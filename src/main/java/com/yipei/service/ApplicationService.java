package com.yipei.service;

import com.yipei.constant.RoleConstants;
import com.yipei.entity.ApplicationVO;
import com.yipei.entity.CompanionProfile;
import com.yipei.entity.ServiceApplication;
import com.yipei.entity.ServiceOrder;
import com.yipei.entity.ServiceRequest;
import com.yipei.entity.SysUser;
import com.yipei.exception.ForbiddenException;
import com.yipei.exception.NotFoundException;
import com.yipei.mapper.CompanionProfileMapper;
import com.yipei.mapper.ServiceApplicationMapper;
import com.yipei.mapper.ServiceRequestMapper;
import com.yipei.mapper.SysUserMapper;
import com.yipei.util.SubmitLock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ApplicationService {
    private final ServiceApplicationMapper applicationMapper;
    private final ServiceRequestMapper serviceRequestMapper;
    private final CompanionProfileMapper companionProfileMapper;
    private final SysUserMapper sysUserMapper;
    private final OrderService orderService;
    private final SubmitLock submitLock;
    private final UserNotificationService notificationService;

    public ApplicationService(ServiceApplicationMapper applicationMapper,
                              ServiceRequestMapper serviceRequestMapper,
                              CompanionProfileMapper companionProfileMapper,
                              SysUserMapper sysUserMapper,
                              OrderService orderService,
                              SubmitLock submitLock,
                              UserNotificationService notificationService) {
        this.applicationMapper = applicationMapper;
        this.serviceRequestMapper = serviceRequestMapper;
        this.companionProfileMapper = companionProfileMapper;
        this.sysUserMapper = sysUserMapper;
        this.orderService = orderService;
        this.submitLock = submitLock;
        this.notificationService = notificationService;
    }

    /** 解析当前登录陪诊师的、已通过审核的资料 */
    private CompanionProfile requireApprovedCompanion(Long userId) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null || !RoleConstants.COMPANION.equals(user.getRole())) {
            throw new ForbiddenException("仅陪诊师可操作");
        }
        CompanionProfile profile = companionProfileMapper.selectByUserId(userId);
        if (profile == null) {
            throw new ForbiddenException("请先完善陪诊师资料");
        }
        if (profile.getAuditStatus() == null || profile.getAuditStatus() != 1) {
            throw new ForbiddenException("资料未通过审核，暂不能申请接单");
        }
        return profile;
    }

    /** 陪诊师申请接单 */
    @Transactional
    public ServiceApplication apply(Long userId, Long requestId, String message) {
        CompanionProfile profile = requireApprovedCompanion(userId);
        ServiceRequest sr = serviceRequestMapper.selectById(requestId);
        if (sr == null) {
            throw new NotFoundException("服务需求不存在，ID: " + requestId);
        }
        if (sr.getAuditStatus() == null || sr.getAuditStatus() != 1) {
            throw new ForbiddenException("该需求尚未通过审核，暂不可申请");
        }
        if (!"PENDING".equals(sr.getStatus())) {
            throw new ForbiddenException("该需求已被匹配或关闭，无法申请");
        }
        if (serviceRequestMapper.countOrdersByRequestId(requestId) > 0) {
            throw new ForbiddenException("该需求已生成订单，无法申请");
        }
        ServiceApplication existing = applicationMapper.selectByRequestAndCompanion(requestId, profile.getId());
        if (existing != null) {
            if ("PENDING".equals(existing.getStatus())) {
                throw new ForbiddenException("您已申请过该需求，请勿重复申请");
            }
            if ("ACCEPTED".equals(existing.getStatus())) {
                throw new ForbiddenException("您的申请已被接受");
            }
            // 之前撤回或被拒，允许重新申请
            existing.setMessage(message);
            existing.setStatus("PENDING");
            applicationMapper.resubmit(existing.getId(), message);
            notifyCustomerOfApplication(sr, profile, requestId);
            return existing;
        }
        ServiceApplication application = new ServiceApplication();
        application.setRequestId(requestId);
        application.setCompanionId(profile.getId());
        application.setMessage(message);
        application.setStatus("PENDING");
        applicationMapper.insert(application);
        notifyCustomerOfApplication(sr, profile, requestId);
        return application;
    }

    private String profileName(Long userId) {
        SysUser user = sysUserMapper.selectById(userId);
        return user == null ? null : user.getNickname();
    }

    private void notifyCustomerOfApplication(ServiceRequest request, CompanionProfile profile, Long requestId) {
        String companionName = profileName(profile.getUserId());
        notificationService.send(request.getCustomerId(), "APPLICATION", "收到新的陪诊申请",
                (companionName == null ? "一位陪诊师" : companionName) + "申请了您的陪诊需求，请及时查看。", requestId);
    }

    /** 陪诊师撤回申请 */
    @Transactional
    public void withdraw(Long userId, Long applicationId) {
        CompanionProfile profile = requireApprovedCompanion(userId);
        ServiceApplication application = applicationMapper.selectById(applicationId);
        if (application == null) {
            throw new NotFoundException("申请不存在，ID: " + applicationId);
        }
        if (!application.getCompanionId().equals(profile.getId())) {
            throw new ForbiddenException("只能撤回自己的申请");
        }
        if (!"PENDING".equals(application.getStatus())) {
            throw new ForbiddenException("当前状态不允许撤回");
        }
        applicationMapper.updateStatus(applicationId, "WITHDRAWN");
        ServiceRequest request = serviceRequestMapper.selectById(application.getRequestId());
        if (request != null) {
            String companionName = profileName(profile.getUserId());
            notificationService.send(request.getCustomerId(), "APPLICATION_WITHDRAWN", "陪诊申请已撤回",
                    (companionName == null ? "一位陪诊师" : companionName) + "撤回了对您需求的申请。", request.getId());
        }
    }

    /** 陪诊师查看自己的申请 */
    public List<ApplicationVO> listMine(Long userId) {
        CompanionProfile profile = requireApprovedCompanion(userId);
        return applicationMapper.selectByCompanion(profile.getId());
    }

    /** 客户查看某需求的申请列表 */
    public List<ApplicationVO> listByRequest(Long userId, Long requestId) {
        ServiceRequest sr = serviceRequestMapper.selectById(requestId);
        if (sr == null) {
            throw new NotFoundException("服务需求不存在，ID: " + requestId);
        }
        if (!sr.getCustomerId().equals(userId)) {
            throw new ForbiddenException("只能查看自己需求的申请");
        }
        return applicationMapper.selectByRequest(requestId);
    }

    /** 客户接受某申请：生成订单、拒绝其余申请。返回订单以便开启聊天。 */
    @Transactional
    public ServiceOrder accept(Long userId, Long applicationId, BigDecimal servicePrice) {
        if (!submitLock.tryLock("application_accept", userId, 10)) {
            throw new ForbiddenException("请勿重复提交，稍后再试");
        }
        try {
            ServiceApplication application = applicationMapper.selectById(applicationId);
            if (application == null) {
                throw new NotFoundException("申请不存在，ID: " + applicationId);
            }
            if (!"PENDING".equals(application.getStatus())) {
                throw new ForbiddenException("该申请当前状态不可接受");
            }
            List<ApplicationVO> candidates = applicationMapper.selectByRequest(application.getRequestId());
            ServiceOrder order = orderService.createFromApplication(
                    userId, application.getRequestId(), application.getCompanionId(), servicePrice);
            applicationMapper.updateStatus(applicationId, "ACCEPTED");
            applicationMapper.rejectOthers(application.getRequestId(), applicationId);
            CompanionProfile profile = companionProfileMapper.selectById(application.getCompanionId());
            if (profile != null) {
                notificationService.send(profile.getUserId(), "APPLICATION_ACCEPTED", "申请已被客户接受",
                        "客户已接受您的陪诊申请，订单已生成，现在可以开始私信沟通。", order.getId());
            }
            for (ApplicationVO candidate : candidates) {
                if (!candidate.getId().equals(applicationId) && "PENDING".equals(candidate.getStatus())
                        && candidate.getCompanionUserId() != null) {
                    notificationService.send(candidate.getCompanionUserId(), "APPLICATION_AUTO_REJECTED", "本次申请未被选中",
                            "该需求已选择其他陪诊师，您可以继续查看需求广场。", application.getRequestId());
                }
            }
            return order;
        } finally {
            submitLock.unlock("application_accept", userId);
        }
    }

    /** 客户拒绝某申请 */
    @Transactional
    public void reject(Long userId, Long applicationId) {
        ServiceApplication application = applicationMapper.selectById(applicationId);
        if (application == null) {
            throw new NotFoundException("申请不存在，ID: " + applicationId);
        }
        ServiceRequest sr = serviceRequestMapper.selectById(application.getRequestId());
        if (sr == null || !sr.getCustomerId().equals(userId)) {
            throw new ForbiddenException("只能处理自己需求的申请");
        }
        if (!"PENDING".equals(application.getStatus())) {
            throw new ForbiddenException("该申请当前状态不可拒绝");
        }
        applicationMapper.updateStatus(applicationId, "REJECTED");
        CompanionProfile profile = companionProfileMapper.selectById(application.getCompanionId());
        if (profile != null) {
            notificationService.send(profile.getUserId(), "APPLICATION_REJECTED", "陪诊申请未被接受",
                    "客户暂未接受您对该需求的申请，您可以继续查看其他需求。", application.getRequestId());
        }
    }
}
