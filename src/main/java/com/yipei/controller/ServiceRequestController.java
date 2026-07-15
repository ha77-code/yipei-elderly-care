package com.yipei.controller;

import com.yipei.entity.ApiResponse;
import com.yipei.entity.ServiceRequest;
import com.yipei.exception.ForbiddenException;
import com.yipei.service.ServiceRequestService;
import com.yipei.service.UserService;
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
    private final UserService userService;

    public ServiceRequestController(ServiceRequestService serviceRequestService,
                                    UserService userService) {
        this.serviceRequestService = serviceRequestService;
        this.userService = userService;
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
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestHeader(value = "X-User-Id", required = false) Long headerUserId) {
        Long userId = userService.resolveUserId(authHeader);
        if (userId == null) userId = headerUserId;
        if (userId == null) throw new ForbiddenException("请先登录");
        serviceRequestService.cancel(id, userId);
        return ApiResponse.success();
    }
}
