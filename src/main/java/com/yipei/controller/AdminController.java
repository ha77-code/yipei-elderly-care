package com.yipei.controller;

import com.yipei.entity.ApiResponse;
import com.yipei.entity.ServiceRequest;
import com.yipei.service.ServiceRequestService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final ServiceRequestService serviceRequestService;

    public AdminController(ServiceRequestService serviceRequestService) {
        this.serviceRequestService = serviceRequestService;
    }

    /** 管理员查看全部服务需求，支持按状态和服务类型筛选 */
    @GetMapping("/service-request/list")
    public ApiResponse<List<ServiceRequest>> listServiceRequests(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String serviceType) {
        return ApiResponse.success(serviceRequestService.listAll(status, serviceType));
    }
}
