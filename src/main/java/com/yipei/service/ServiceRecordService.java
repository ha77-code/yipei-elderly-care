package com.yipei.service;

import com.yipei.entity.ServiceRecord;
import com.yipei.mapper.ServiceRecordMapper;
import org.springframework.stereotype.Service;

@Service
public class ServiceRecordService {
    private final ServiceRecordMapper serviceRecordMapper;

    public ServiceRecordService(ServiceRecordMapper serviceRecordMapper) {
        this.serviceRecordMapper = serviceRecordMapper;
    }

    /** 根据订单 ID 查询服务记录，不存在返回 null */
    public ServiceRecord getByOrderId(Long orderId) {
        return serviceRecordMapper.selectByOrderId(orderId);
    }
}
