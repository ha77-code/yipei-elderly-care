package com.yipei.controller;

import com.yipei.entity.ApiResponse;
import com.yipei.entity.ReportCreateRequest;
import com.yipei.entity.ReportRecord;
import com.yipei.security.SecurityUtils;
import com.yipei.service.ReportService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/report")
public class ReportController {
    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    /** 发起投诉 */
    @PostMapping("/create")
    public ApiResponse<ReportRecord> create(
            @Valid @RequestBody ReportCreateRequest request) {
        Long userId = SecurityUtils.requireLoginUserId();
        return ApiResponse.success(reportService.create(userId, request));
    }
}