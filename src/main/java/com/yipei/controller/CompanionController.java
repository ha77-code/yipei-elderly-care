package com.yipei.controller;

import com.yipei.entity.ApiResponse;
import com.yipei.entity.CompanionApplyRequest;
import com.yipei.entity.CompanionProfile;
import com.yipei.entity.CompanionVO;
import com.yipei.service.CompanionService;
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
@RequestMapping("/api/companion")
public class CompanionController {
    private final CompanionService companionService;

    public CompanionController(CompanionService companionService) {
        this.companionService = companionService;
    }

    /** 提交入驻申请 */
    @PostMapping("/apply")
    public ApiResponse<CompanionProfile> apply(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody CompanionApplyRequest request) {
        return ApiResponse.success(companionService.apply(userId, request));
    }

    /** 修改入驻资料 */
    @PutMapping("/profile")
    public ApiResponse<Void> updateProfile(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody CompanionApplyRequest request) {
        companionService.updateProfile(userId, request);
        return ApiResponse.success();
    }

    /** 陪诊师列表（审核通过） */
    @GetMapping("/list")
    public ApiResponse<List<CompanionVO>> list(
            @RequestParam(required = false) String serviceArea,
            @RequestParam(required = false) String serviceType) {
        return ApiResponse.success(companionService.listApproved(serviceArea, serviceType));
    }

    /** 陪诊师详情 */
    @GetMapping("/{id}")
    public ApiResponse<CompanionProfile> detail(@PathVariable Long id) {
        return ApiResponse.success(companionService.getDetail(id));
    }
}
