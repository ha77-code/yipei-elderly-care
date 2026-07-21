package com.yipei.controller;

import com.yipei.constant.RoleConstants;
import com.yipei.entity.ApiResponse;
import com.yipei.entity.ApplicationVO;
import com.yipei.entity.CompanionProfile;
import com.yipei.entity.RequestPoolVO;
import com.yipei.entity.ServiceApplication;
import com.yipei.entity.ServiceOrder;
import com.yipei.entity.SysUser;
import com.yipei.exception.ForbiddenException;
import com.yipei.mapper.CompanionProfileMapper;
import com.yipei.mapper.SysUserMapper;
import com.yipei.service.ApplicationService;
import com.yipei.service.ServiceRequestService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/application")
public class ApplicationController {
    private final ApplicationService applicationService;
    private final ServiceRequestService serviceRequestService;
    private final CompanionProfileMapper companionProfileMapper;
    private final SysUserMapper sysUserMapper;

    public ApplicationController(ApplicationService applicationService,
                                 ServiceRequestService serviceRequestService,
                                 CompanionProfileMapper companionProfileMapper,
                                 SysUserMapper sysUserMapper) {
        this.applicationService = applicationService;
        this.serviceRequestService = serviceRequestService;
        this.companionProfileMapper = companionProfileMapper;
        this.sysUserMapper = sysUserMapper;
    }

    /** 需求广场：陪诊师可申请的需求池 */
    @GetMapping("/pool")
    public ApiResponse<List<RequestPoolVO>> pool(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(required = false) String serviceType,
            @RequestParam(required = false) String keyword) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null || !RoleConstants.COMPANION.equals(user.getRole())) {
            throw new ForbiddenException("仅陪诊师可查看需求广场");
        }
        CompanionProfile profile = companionProfileMapper.selectByUserId(userId);
        if (profile == null) {
            throw new ForbiddenException("请先完善陪诊师资料");
        }
        return ApiResponse.success(serviceRequestService.listPool(profile.getId(), serviceType, keyword));
    }

    /** 陪诊师申请接单 */
    @PostMapping("/apply")
    public ApiResponse<ServiceApplication> apply(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody Map<String, Object> body) {
        Object rawId = body == null ? null : body.get("requestId");
        if (rawId == null || String.valueOf(rawId).isBlank()) {
            throw new ForbiddenException("缺少需求 ID，无法申请");
        }
        long requestId;
        try {
            requestId = Long.parseLong(String.valueOf(rawId).trim());
        } catch (NumberFormatException e) {
            throw new ForbiddenException("需求 ID 格式不正确");
        }
        String message = body.get("message") != null ? String.valueOf(body.get("message")) : null;
        return ApiResponse.success(applicationService.apply(userId, requestId, message));
    }

    /** 陪诊师撤回申请 */
    @PutMapping("/{id}/withdraw")
    public ApiResponse<Void> withdraw(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId) {
        applicationService.withdraw(userId, id);
        return ApiResponse.success();
    }

    /** 陪诊师查看自己的申请 */
    @GetMapping("/mine")
    public ApiResponse<List<ApplicationVO>> mine(
            @RequestHeader("X-User-Id") Long userId) {
        return ApiResponse.success(applicationService.listMine(userId));
    }

    /** 客户查看某需求的申请列表 */
    @GetMapping("/by-request/{requestId}")
    public ApiResponse<List<ApplicationVO>> byRequest(
            @PathVariable Long requestId,
            @RequestHeader("X-User-Id") Long userId) {
        return ApiResponse.success(applicationService.listByRequest(userId, requestId));
    }

    /** 客户接受某申请 */
    @PutMapping("/{id}/accept")
    public ApiResponse<ServiceOrder> accept(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody(required = false) Map<String, Object> body) {
        BigDecimal servicePrice = null;
        if (body != null && body.get("servicePrice") != null) {
            servicePrice = new BigDecimal(String.valueOf(body.get("servicePrice")));
        }
        return ApiResponse.success(applicationService.accept(userId, id, servicePrice));
    }

    /** 客户拒绝某申请 */
    @PutMapping("/{id}/reject")
    public ApiResponse<Void> reject(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId) {
        applicationService.reject(userId, id);
        return ApiResponse.success();
    }
}
