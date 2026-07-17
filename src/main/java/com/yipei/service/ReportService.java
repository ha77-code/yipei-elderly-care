package com.yipei.service;

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

import java.util.List;

@Service
public class ReportService {
    private final ReportRecordMapper reportRecordMapper;
    private final ServiceOrderMapper serviceOrderMapper;
    private final CompanionProfileMapper companionProfileMapper;

    public ReportService(ReportRecordMapper reportRecordMapper,
                         ServiceOrderMapper serviceOrderMapper,
                         CompanionProfileMapper companionProfileMapper) {
        this.reportRecordMapper = reportRecordMapper;
        this.serviceOrderMapper = serviceOrderMapper;
        this.companionProfileMapper = companionProfileMapper;
    }

    /** 发起投诉 */
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
        return report;
    }

    /** 投诉列表（按状态筛选） */
    public List<ReportRecord> listAll(String status) {
        return reportRecordMapper.selectAll(status);
    }

    /** 处理投诉 */
    public void handle(Long id, Long handlerId, String status, String remark) {
        ReportRecord report = reportRecordMapper.selectById(id);
        if (report == null) {
            throw new NotFoundException("投诉不存在，ID: " + id);
        }
        if (!"PROCESSING".equals(status) && !"RESOLVED".equals(status) && !"REJECTED".equals(status)) {
            throw new ForbiddenException("处理状态只能为 PROCESSING、RESOLVED 或 REJECTED");
        }
        reportRecordMapper.updateHandle(id, status, handlerId, remark);
    }
}
