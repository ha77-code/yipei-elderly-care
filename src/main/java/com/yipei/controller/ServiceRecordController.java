package com.yipei.controller;

import com.yipei.entity.ApiResponse;
import com.yipei.entity.ServiceRecord;
import com.yipei.service.ServiceRecordService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/service-record")
public class ServiceRecordController {
    private final ServiceRecordService serviceRecordService;

    public ServiceRecordController(ServiceRecordService serviceRecordService) {
        this.serviceRecordService = serviceRecordService;
    }

    /** 根据订单 ID 查询服务记录 */
    @GetMapping("/order/{orderId}")
    public ApiResponse<ServiceRecord> getByOrder(@PathVariable Long orderId) {
        return ApiResponse.success(serviceRecordService.getByOrderId(orderId));
    }
}
