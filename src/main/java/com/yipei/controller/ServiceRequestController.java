package com.yipei.controller;

import com.yipei.entity.ApiResponse;
import com.yipei.entity.ServiceRequest;
import com.yipei.entity.ServiceRequestCreateRequest;
import com.yipei.security.SecurityUtils;
import com.yipei.service.ServiceRequestService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/service-request")
public class ServiceRequestController {
    private final ServiceRequestService serviceRequestService;

    public ServiceRequestController(ServiceRequestService serviceRequestService) {
        this.serviceRequestService = serviceRequestService;
    }

    /** 发布服务需求 */
    @PostMapping("/create")
    public ApiResponse<ServiceRequest> create(
            @Valid @RequestBody ServiceRequestCreateRequest request) {
        Long userId = SecurityUtils.requireLoginUserId();
        return ApiResponse.success(serviceRequestService.create(userId, request));
    }

    /** 查看我发布的需求列表 */
    @GetMapping("/list")
    public ApiResponse<List<ServiceRequest>> list() {
        Long userId = SecurityUtils.requireLoginUserId();
        return ApiResponse.success(serviceRequestService.listByCustomerId(userId));
    }

    /** 查看需求详情（需登录） */
    @GetMapping("/{id}")
    public ApiResponse<ServiceRequest> detail(@PathVariable Long id) {
        SecurityUtils.requireLogin();
        return ApiResponse.success(serviceRequestService.getById(id));
    }

    /** 取消需求 */
    @PutMapping("/{id}/cancel")
    public ApiResponse<Void> cancel(@PathVariable Long id) {
        Long userId = SecurityUtils.requireLoginUserId();
        serviceRequestService.cancel(id, userId);
        return ApiResponse.success();
    }
}