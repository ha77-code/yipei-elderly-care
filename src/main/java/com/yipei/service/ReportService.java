package com.yipei.service;

import com.yipei.constant.RoleConstants;
import com.yipei.entity.ReportCreateRequest;
import com.yipei.entity.ReportRecord;
import com.yipei.entity.ServiceOrder;
import com.yipei.entity.CompanionProfile;
import com.yipei.exception.ForbiddenException;
import com.yipei.exception.NotFoundException;
import com.yipei.mapper.ReportRecordMapper;
import com.yipei.mapper.ServiceOrderMapper;
import com.yipei.mapper.CompanionProfileMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReportService {
    private final ReportRecordMapper reportRecordMapper;
    private final ServiceOrderMapper serviceOrderMapper;
    private final CompanionProfileMapper companionProfileMapper;
    private final UserNotificationService notificationService;

    public ReportService(ReportRecordMapper reportRecordMapper,
                         ServiceOrderMapper serviceOrderMapper,
                         CompanionProfileMapper companionProfileMapper,
                         UserNotificationService notificationService) {
        this.reportRecordMapper = reportRecordMapper;
        this.serviceOrderMapper = serviceOrderMapper;
        this.companionProfileMapper = companionProfileMapper;
        this.notificationService = notificationService;
    }

    /** 发起投诉 */
    @Transactional
    public ReportRecord create(Long userId, ReportCreateRequest request) {
        ServiceOrder order = serviceOrderMapper.selectById(request.getOrderId());
        if (order == null) {
            throw new NotFoundException("订单不存在，ID: " + request.getOrderId());
        }
        CompanionProfile profile = companionProfileMapper.selectById(order.getCompanionId());
        boolean isCompanion = profile != null && userId.equals(profile.getUserId());
        if (!order.getCustomerId().equals(userId) && !isCompanion) {
            throw new ForbiddenException("只能投诉自己参与的订单");
        }

        ReportRecord report = new ReportRecord();
        report.setOrderId(request.getOrderId());
        report.setReporterId(userId);
        report.setReason(request.getReason());
        report.setContent(request.getContent());
        report.setStatus("PENDING");
        reportRecordMapper.insert(report);
        notificationService.sendToRole(RoleConstants.ADMIN, "REPORT_CREATED", "有新的投诉待处理",
                "订单 #" + request.getOrderId() + " 收到新的投诉，请及时处理。", report.getId());
        return report;
    }

    /** 投诉列表（按状态筛选） */
    public List<ReportRecord> listAll(String status) {
        return reportRecordMapper.selectAll(status);
    }

    /** 处理投诉 */
    @Transactional
    public void handle(Long id, Long handlerId, String status, String remark) {
        ReportRecord report = reportRecordMapper.selectById(id);
        if (report == null) {
            throw new NotFoundException("投诉不存在，ID: " + id);
        }
        if (!"PROCESSING".equals(status) && !"RESOLVED".equals(status) && !"REJECTED".equals(status)) {
            throw new ForbiddenException("处理状态只能为 PROCESSING、RESOLVED 或 REJECTED");
        }
        reportRecordMapper.updateHandle(id, status, handlerId, remark);
        String suffix = remark == null || remark.isBlank() ? "" : " 处理说明：" + remark.trim();
        notificationService.send(report.getReporterId(), "REPORT_STATUS", "投诉处理状态已更新",
                "您对订单 #" + report.getOrderId() + " 的投诉状态已更新为 " + status + "。" + suffix,
                report.getOrderId());
    }
}
