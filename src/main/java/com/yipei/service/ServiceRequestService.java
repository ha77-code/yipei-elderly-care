package com.yipei.service;

import com.yipei.entity.AuditRecord;
import com.yipei.entity.RequestPoolVO;
import com.yipei.entity.ServiceRequest;
import com.yipei.constant.RoleConstants;
import com.yipei.entity.ServiceRequestCreateRequest;
import com.yipei.entity.SysUser;
import com.yipei.exception.ForbiddenException;
import com.yipei.exception.NotFoundException;
import com.yipei.mapper.AuditRecordMapper;
import com.yipei.mapper.ServiceRequestMapper;
import com.yipei.mapper.SysUserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ServiceRequestService {
    private final ServiceRequestMapper serviceRequestMapper;
    private final SysUserMapper sysUserMapper;
    private final AiSummaryService aiSummaryService;
    private final AuditRecordMapper auditRecordMapper;
    private final OrderService orderService;
    private final UserNotificationService notificationService;

    public ServiceRequestService(ServiceRequestMapper serviceRequestMapper,
                                 SysUserMapper sysUserMapper,
                                 AiSummaryService aiSummaryService,
                                 AuditRecordMapper auditRecordMapper,
                                 OrderService orderService,
                                 UserNotificationService notificationService) {
        this.serviceRequestMapper = serviceRequestMapper;
        this.sysUserMapper = sysUserMapper;
        this.aiSummaryService = aiSummaryService;
        this.auditRecordMapper = auditRecordMapper;
        this.orderService = orderService;
        this.notificationService = notificationService;
    }

    /** 发布服务需求 */
    @Transactional
    public ServiceRequest create(Long customerId, ServiceRequestCreateRequest request) {
        SysUser user = sysUserMapper.selectById(customerId);
        if (user == null) {
            throw new NotFoundException("用户不存在，ID: " + customerId);
        }
        if (!RoleConstants.CUSTOMER.equals(user.getRole())) {
            throw new ForbiddenException("仅客户角色可发布服务需求");
        }
        ServiceRequest sr = new ServiceRequest();
        sr.setCustomerId(customerId);
        sr.setServiceType(request.getServiceType());
        sr.setServiceDate(request.getServiceDate());
        sr.setHospitalName(request.getHospitalName());
        sr.setDepartment(request.getDepartment());
        sr.setRequirement(request.getRequirement());
        sr.setSpecialNotes(request.getSpecialNotes());
        sr.setAiSummary(normalizeAiSummary(request.getAiSummary()));
        sr.setPreferredTraits(request.getPreferredTraits());
        sr.setNeedPickup(request.getNeedPickup());
        sr.setContactName(request.getContactName());
        sr.setContactPhone(request.getContactPhone());
        sr.setBudget(request.getBudget());
        sr.setStatus("PENDING");
        // 所有需求（含指定下单）统一进入待审核队列；通道B 记录客户指定的陪诊师，
        // 审核通过后据此自动生成待接单订单。
        sr.setAuditStatus(0);
        sr.setPreferredCompanionId(request.getPreferredCompanionId());
        serviceRequestMapper.insert(sr);
        if (sr.getAiSummary() == null) {
            aiSummaryService.generate(sr).ifPresent(summary -> {
                sr.setAiSummary(summary);
                serviceRequestMapper.updateAiSummary(sr.getId(), summary);
            });
        }
        notificationService.sendToRole(RoleConstants.ADMIN, "REQUEST_AUDIT_PENDING", "有新的需求待审核",
                "需求 #" + sr.getId() + "（" + sr.getServiceType() + "）已提交，请及时审核。", sr.getId());
        return sr;
    }

    private String normalizeAiSummary(String summary) {
        if (summary == null || summary.isBlank()) {
            return null;
        }
        String normalized = summary.replaceAll("\\s+", " ").trim();
        return normalized.length() > 800 ? normalized.substring(0, 800) : normalized;
    }

    /** 查看我发布的需求列表 */
    public List<ServiceRequest> listByCustomerId(Long customerId) {
        return serviceRequestMapper.selectByCustomerId(customerId);
    }

    /** 管理员查看全部需求，支持按状态、服务类型、审核状态筛选 */
    public List<ServiceRequest> listAll(String status, String serviceType, Integer auditStatus) {
        return serviceRequestMapper.selectAll(status, serviceType, auditStatus);
    }

    /** 管理员待审核需求列表 */
    public List<ServiceRequest> listPendingAudit() {
        return serviceRequestMapper.selectAll(null, null, 0);
    }

    /** 管理员审核需求（auditStatus：1 通过 / 2 拒绝） */
    @Transactional
    public void audit(Long id, Long auditorId, Integer auditStatus, String remark) {
        ServiceRequest request = serviceRequestMapper.selectById(id);
        if (request == null) {
            throw new NotFoundException("服务需求不存在，ID: " + id);
        }
        if (request.getAuditStatus() == null || request.getAuditStatus() != 0) {
            throw new ForbiddenException("该需求当前不在待审核状态，无法审核");
        }
        if (auditStatus == null || (auditStatus != 1 && auditStatus != 2)) {
            throw new ForbiddenException("审核状态只能为 1（通过）或 2（拒绝）");
        }
        serviceRequestMapper.updateAuditStatus(id, auditStatus, remark);
        if (auditStatus == 2) {
            serviceRequestMapper.updateStatus(id, "CLOSED");
        }

        AuditRecord record = new AuditRecord();
        record.setBusinessType("service_request");
        record.setBusinessId(id);
        record.setAuditorId(auditorId);
        record.setAuditStatus(auditStatus);
        record.setRemark(remark);
        auditRecordMapper.insert(record);

        // 通道B：指定下单需求审核通过后，自动为客户选定的陪诊师生成待接单订单
        if (auditStatus == 1 && request.getPreferredCompanionId() != null) {
            orderService.createDirectedOrder(
                    request.getCustomerId(), id, request.getPreferredCompanionId(), request.getBudget());
        }

        if (auditStatus == 1) {
            String content = request.getPreferredCompanionId() == null
                    ? "您的陪诊需求已通过审核，现已进入需求广场。"
                    : "您的指定陪诊需求已通过审核，订单已发送给指定陪诊师。";
            notificationService.send(request.getCustomerId(), "REQUEST_AUDIT_APPROVED", "需求审核已通过", content, id);
        } else {
            String suffix = remark == null || remark.isBlank() ? "" : " 原因：" + remark.trim();
            notificationService.send(request.getCustomerId(), "REQUEST_AUDIT_REJECTED", "需求审核未通过",
                    "您的陪诊需求未通过审核。" + suffix, id);
        }
    }

    /** 陪诊师需求广场：已通过审核、仍待匹配的需求 */
    public List<RequestPoolVO> listPool(Long companionProfileId, String serviceType, String keyword) {
        return serviceRequestMapper.selectPool(companionProfileId, serviceType, keyword);
    }

    /** 查看需求详情 */
    public ServiceRequest getById(Long id) {
        ServiceRequest request = serviceRequestMapper.selectById(id);
        if (request == null) {
            throw new NotFoundException("服务需求不存在，ID: " + id);
        }
        return request;
    }

    /** 取消需求 */
    public void cancel(Long id, Long userId) {
        ServiceRequest request = serviceRequestMapper.selectById(id);
        if (request == null) {
            throw new NotFoundException("服务需求不存在，ID: " + id);
        }
        if (!request.getCustomerId().equals(userId)) {
            throw new ForbiddenException("只能取消自己的服务需求");
        }
        int orderCount = serviceRequestMapper.countOrdersByRequestId(id);
        if (orderCount > 0) {
            throw new ForbiddenException("该需求已生成订单，不能直接取消，请先取消订单");
        }
        if ("CANCELLED".equals(request.getStatus()) || "CLOSED".equals(request.getStatus())) {
            throw new ForbiddenException("当前状态（" + request.getStatus() + "）不允许取消");
        }
        serviceRequestMapper.updateStatus(id, "CANCELLED");
    }
}
