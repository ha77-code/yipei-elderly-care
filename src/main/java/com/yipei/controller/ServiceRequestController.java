package com.yipei.controller;

import com.yipei.entity.ApiResponse;
import com.yipei.entity.ServiceRequest;
import com.yipei.service.ServiceRequestService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/service-request")
public class ServiceRequestController {
    private final ServiceRequestService serviceRequestService;

    public ServiceRequestController(ServiceRequestService serviceRequestService) {
        this.serviceRequestService = serviceRequestService;
    }

    /** 查看需求详情 */
    @GetMapping("/{id}")
    public ApiResponse<ServiceRequest> detail(@PathVariable Long id) {
        return ApiResponse.success(serviceRequestService.getById(id));
    }

    /** 取消需求 */
    @PutMapping("/{id}/cancel")
    public ApiResponse<Void> cancel(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId) {
        serviceRequestService.cancel(id, userId);
        return ApiResponse.success();
    }
}
