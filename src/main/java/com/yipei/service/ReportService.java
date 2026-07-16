package com.yipei.service;

import com.yipei.entity.ReportCreateRequest;
import com.yipei.entity.ReportRecord;
import com.yipei.entity.ServiceOrder;
import com.yipei.exception.ForbiddenException;
import com.yipei.exception.NotFoundException;
import com.yipei.mapper.ReportRecordMapper;
import com.yipei.mapper.ServiceOrderMapper;
import org.springframework.stereotype.Service;

@Service
public class ReportService {
    private final ReportRecordMapper reportRecordMapper;
    private final ServiceOrderMapper serviceOrderMapper;

    public ReportService(ReportRecordMapper reportRecordMapper,
                         ServiceOrderMapper serviceOrderMapper) {
        this.reportRecordMapper = reportRecordMapper;
        this.serviceOrderMapper = serviceOrderMapper;
    }

    /** 发起投诉 */
    public ReportRecord create(Long userId, ReportCreateRequest request) {
        // 校验订单存在
        ServiceOrder order = serviceOrderMapper.selectById(request.getOrderId());
        if (order == null) {
            throw new NotFoundException("订单不存在，ID: " + request.getOrderId());
        }
        // 校验投诉人是订单相关方
        if (!order.getCustomerId().equals(userId) && !order.getCompanionId().equals(userId)) {
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
}
