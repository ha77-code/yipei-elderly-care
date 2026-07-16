package com.yipei.controller;

import com.yipei.entity.ApiResponse;
import com.yipei.entity.ServiceRecord;
import com.yipei.entity.ServiceRecordCreateRequest;
import com.yipei.service.ServiceRecordService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/service-record")
public class ServiceRecordController {
    private final ServiceRecordService serviceRecordService;

    public ServiceRecordController(ServiceRecordService serviceRecordService) {
        this.serviceRecordService = serviceRecordService;
    }

    /** 创建服务记录 */
    @PostMapping("/create")
    public ApiResponse<ServiceRecord> create(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody ServiceRecordCreateRequest request) {
        return ApiResponse.success(serviceRecordService.create(userId, request));
    }

    /** 根据订单 ID 查询服务记录 */
    @GetMapping("/order/{orderId}")
    public ApiResponse<ServiceRecord> getByOrder(@PathVariable Long orderId) {
        return ApiResponse.success(serviceRecordService.getByOrderId(orderId));
    }

    /** 当前陪诊师填写的服务记录 */
    @GetMapping("/my")
    public ApiResponse<List<ServiceRecord>> myRecords(
            @RequestHeader("X-User-Id") Long userId) {
        return ApiResponse.success(serviceRecordService.listMine(userId));
    }
}
