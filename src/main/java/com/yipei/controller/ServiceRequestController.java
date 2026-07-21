package com.yipei.controller;

import com.yipei.entity.ApiResponse;
import com.yipei.entity.CompanionVO;
import com.yipei.entity.ServiceRequest;
import com.yipei.entity.ServiceRequestCreateRequest;
import com.yipei.service.ServiceRequestService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody ServiceRequestCreateRequest request) {
        return ApiResponse.success(serviceRequestService.create(userId, request));
    }

    /** 查看我发布的需求列表 */
    @GetMapping("/list")
    public ApiResponse<List<ServiceRequest>> list(
            @RequestHeader("X-User-Id") Long userId) {
        return ApiResponse.success(serviceRequestService.listByCustomerId(userId));
    }

    /** 查看需求详情 */
    @GetMapping("/{id}")
    public ApiResponse<ServiceRequest> detail(@PathVariable Long id) {
        return ApiResponse.success(serviceRequestService.getById(id));
    }

    /** 为需求推荐适配的陪诊师（最多 3 个，无合适则返回空数组） */
    @GetMapping("/{id}/matches")
    public ApiResponse<List<CompanionVO>> matches(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId) {
        return ApiResponse.success(serviceRequestService.matchCompanions(id, userId, 3));
    }

    /** 发布表单“智能推荐”：按当前填写内容即时推荐陪诊师，不落库 */
    @PostMapping("/recommend")
    public ApiResponse<List<CompanionVO>> recommend(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody ServiceRequestCreateRequest body) {
        ServiceRequest draft = new ServiceRequest();
        draft.setServiceType(body.getServiceType());
        draft.setServiceDate(body.getServiceDate());
        draft.setHospitalName(body.getHospitalName());
        draft.setDepartment(body.getDepartment());
        draft.setRequirement(body.getRequirement());
        draft.setSpecialNotes(body.getSpecialNotes());
        draft.setPreferredTraits(body.getPreferredTraits());
        return ApiResponse.success(serviceRequestService.previewMatches(draft, 3));
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
