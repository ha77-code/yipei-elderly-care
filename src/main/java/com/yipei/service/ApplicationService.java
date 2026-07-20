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
            applicationMapper.updateStatus(existing.getId(), "PENDING");
            return existing;
        }
        ServiceApplication application = new ServiceApplication();
        application.setRequestId(requestId);
        application.setCompanionId(profile.getId());
        application.setMessage(message);
        application.setStatus("PENDING");
        applicationMapper.insert(application);
        String companionName = profileName(profile.getUserId());
        notificationService.send(sr.getCustomerId(), "APPLICATION", "\u6536\u5230\u65b0\u7684\u966a\u8bca\u7533\u8bf7",
                (companionName == null ? "\u4e00\u4f4d\u966a\u8bca\u5e08" : companionName) + "\u7533\u8bf7\u4e86\u60a8\u7684\u966a\u8bca\u9700\u6c42\uff0c\u8bf7\u53ca\u65f6\u67e5\u770b\u3002", application.getId());
        return application;
    }

    private String profileName(Long userId) {
        SysUser user = sysUserMapper.selectById(userId);
        return user == null ? null : user.getNickname();
    }

    /** 陪诊师撤回申请 */
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
            ServiceOrder order = orderService.createFromApplication(
                    userId, application.getRequestId(), application.getCompanionId(), servicePrice);
            applicationMapper.updateStatus(applicationId, "ACCEPTED");
            applicationMapper.rejectOthers(application.getRequestId(), applicationId);
            CompanionProfile profile = companionProfileMapper.selectById(application.getCompanionId());
            if (profile != null) {
                notificationService.send(profile.getUserId(), "APPLICATION_ACCEPTED", "\u7533\u8bf7\u5df2\u88ab\u60a3\u8005\u63a5\u53d7",
                        "\u60a3\u8005\u5df2\u63a5\u53d7\u60a8\u7684\u966a\u8bca\u7533\u8bf7\uff0c\u8ba2\u5355\u5df2\u751f\u6210\uff0c\u8bf7\u53ca\u65f6\u67e5\u770b\u8ba2\u5355\u3002", order.getId());
            }
            return order;
        } finally {
            submitLock.unlock("application_accept", userId);
        }
    }

    /** 客户拒绝某申请 */
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
    }
}
