package com.yipei.service;

import com.yipei.entity.CompanionProfile;
import com.yipei.entity.ServiceOrder;
import com.yipei.entity.ServiceRecord;
import com.yipei.entity.ServiceRecordCreateRequest;
import com.yipei.exception.ForbiddenException;
import com.yipei.exception.NotFoundException;
import com.yipei.mapper.CompanionProfileMapper;
import com.yipei.mapper.ServiceOrderMapper;
import com.yipei.mapper.ServiceRecordMapper;
import org.springframework.stereotype.Service;

@Service
public class ServiceRecordService {
    private final ServiceRecordMapper serviceRecordMapper;
    private final ServiceOrderMapper serviceOrderMapper;
    private final CompanionProfileMapper companionProfileMapper;

    public ServiceRecordService(ServiceRecordMapper serviceRecordMapper,
                                ServiceOrderMapper serviceOrderMapper,
                                CompanionProfileMapper companionProfileMapper) {
        this.serviceRecordMapper = serviceRecordMapper;
        this.serviceOrderMapper = serviceOrderMapper;
        this.companionProfileMapper = companionProfileMapper;
    }

    /** 创建服务记录 */
    public ServiceRecord create(Long userId, ServiceRecordCreateRequest request) {
        ServiceOrder order = serviceOrderMapper.selectById(request.getOrderId());
        if (order == null) {
            throw new NotFoundException("订单不存在，ID: " + request.getOrderId());
        }
        // 校验是订单的陪诊师
        CompanionProfile profile = companionProfileMapper.selectById(order.getCompanionId());
        if (profile == null || !profile.getUserId().equals(userId)) {
            throw new ForbiddenException("只有该订单的陪诊师可以填写服务记录");
        }

        ServiceRecord record = new ServiceRecord();
        record.setOrderId(request.getOrderId());
        record.setContent(request.getContent());
        record.setImportantNotes(request.getImportantNotes());
        record.setCompletedBy(userId);
        serviceRecordMapper.insert(record);
        return record;
    }

    /** 根据订单 ID 查询服务记录 */
    public ServiceRecord getByOrderId(Long orderId) {
        return serviceRecordMapper.selectByOrderId(orderId);
    }
}
