package com.yipei.service;

import com.yipei.entity.ServiceRequest;
import com.yipei.exception.ForbiddenException;
import com.yipei.exception.NotFoundException;
import com.yipei.mapper.ServiceRequestMapper;
import org.springframework.stereotype.Service;

@Service
public class ServiceRequestService {
    private final ServiceRequestMapper serviceRequestMapper;

    public ServiceRequestService(ServiceRequestMapper serviceRequestMapper) {
        this.serviceRequestMapper = serviceRequestMapper;
    }

    public ServiceRequest getById(Long id) {
        ServiceRequest request = serviceRequestMapper.selectById(id);
        if (request == null) {
            throw new NotFoundException("服务需求不存在，ID: " + id);
        }
        return request;
    }

    public void cancel(Long id, Long userId) {
        ServiceRequest request = serviceRequestMapper.selectById(id);
        if (request == null) {
            throw new NotFoundException("服务需求不存在，ID: " + id);
        }
        // 校验需求归属
        if (!request.getCustomerId().equals(userId)) {
            throw new ForbiddenException("只能取消自己的服务需求");
        }
        // 已生成订单不能直接取消
        int orderCount = serviceRequestMapper.countOrdersByRequestId(id);
        if (orderCount > 0) {
            throw new ForbiddenException("该需求已生成订单，不能直接取消，请先取消订单");
        }
        // 检查当前状态是否允许取消
        if ("CANCELLED".equals(request.getStatus()) || "CLOSED".equals(request.getStatus())) {
            throw new ForbiddenException("当前状态（" + request.getStatus() + "）不允许取消");
        }
        serviceRequestMapper.updateStatus(id, "CANCELLED");
    }
}
