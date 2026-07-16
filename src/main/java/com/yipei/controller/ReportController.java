package com.yipei.controller;

import com.yipei.entity.ApiResponse;
import com.yipei.entity.ReportCreateRequest;
import com.yipei.entity.ReportRecord;
import com.yipei.service.ReportService;
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
@RequestMapping("/api/report")
public class ReportController {
    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    /** 发起投诉 */
    @PostMapping("/create")
    public ApiResponse<ReportRecord> create(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody ReportCreateRequest request) {
        return ApiResponse.success(reportService.create(userId, request));
    }

    /** 当前用户发起的投诉 */
    @GetMapping("/my")
    public ApiResponse<List<ReportRecord>> myReports(
            @RequestHeader("X-User-Id") Long userId) {
        return ApiResponse.success(reportService.listMine(userId));
    }

    /** 投诉详情 */
    @GetMapping("/{id}")
    public ApiResponse<ReportRecord> detail(@PathVariable Long id) {
        return ApiResponse.success(reportService.getById(id));
    }
}
