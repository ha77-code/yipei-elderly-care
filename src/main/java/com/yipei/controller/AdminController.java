package com.yipei.controller;

import com.yipei.entity.AdminStatisticsVO;
import com.yipei.entity.ApiResponse;
import com.yipei.entity.CompanionProfile;
import com.yipei.entity.OrderDetailVO;
import com.yipei.entity.ReportRecord;
import com.yipei.entity.ServiceRequest;
import com.yipei.mapper.AdminStatisticsMapper;
import com.yipei.service.CompanionService;
import com.yipei.service.OrderService;
import com.yipei.service.ReportService;
import com.yipei.service.ServiceRequestService;
import com.yipei.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final ServiceRequestService serviceRequestService;
    private final AdminStatisticsMapper adminStatisticsMapper;
    private final OrderService orderService;
    private final ReportService reportService;
    private final CompanionService companionService;
    private final UserService userService;

    public AdminController(ServiceRequestService serviceRequestService,
                           AdminStatisticsMapper adminStatisticsMapper,
                           OrderService orderService,
                           ReportService reportService,
                           CompanionService companionService,
                           UserService userService) {
        this.serviceRequestService = serviceRequestService;
        this.adminStatisticsMapper = adminStatisticsMapper;
        this.orderService = orderService;
        this.reportService = reportService;
        this.companionService = companionService;
        this.userService = userService;
    }

    /** 服务需求 */
    @GetMapping("/service-request/list")
    public ApiResponse<List<ServiceRequest>> listServiceRequests(
            @RequestHeader("X-User-Id") Long adminId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String serviceType) {
        userService.requireAdmin(adminId);
        return ApiResponse.success(serviceRequestService.listAll(status, serviceType));
    }

    /** 订单 */
    @GetMapping("/orders")
    public ApiResponse<List<OrderDetailVO>> listOrders(
            @RequestHeader("X-User-Id") Long adminId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) Long companionId) {
        userService.requireAdmin(adminId);
        return ApiResponse.success(orderService.listForAdmin(status, customerId, companionId));
    }

    /** 投诉列表 */
    @GetMapping("/report/list")
    public ApiResponse<List<ReportRecord>> listReports(
            @RequestHeader("X-User-Id") Long adminId,
            @RequestParam(required = false) String status) {
        userService.requireAdmin(adminId);
        return ApiResponse.success(reportService.listAll(status));
    }

    /** 处理投诉 */
    @PutMapping("/report/{id}/handle")
    public ApiResponse<Void> handleReport(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long handlerId,
            @RequestBody Map<String, String> body) {
        userService.requireAdmin(handlerId);
        reportService.handle(id, handlerId, body.get("status"), body.get("remark"));
        return ApiResponse.success();
    }

    /** 待审核陪诊师列表 */
    @GetMapping("/companion/pending")
    public ApiResponse<List<CompanionProfile>> listPendingCompanions(
            @RequestHeader("X-User-Id") Long adminId) {
        userService.requireAdmin(adminId);
        return ApiResponse.success(companionService.listPending());
    }

    /** 审核陪诊师 */
    @PutMapping("/companion/{id}/audit")
    public ApiResponse<Void> auditCompanion(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long auditorId,
            @RequestBody Map<String, Object> body) {
        userService.requireAdmin(auditorId);
        Integer auditStatus = (Integer) body.get("auditStatus");
        String remark = (String) body.get("remark");
        companionService.audit(id, auditorId, auditStatus, remark);
        return ApiResponse.success();
    }

    /** 平台统计 */
    @GetMapping("/statistics")
    public ApiResponse<AdminStatisticsVO> statistics(
            @RequestHeader("X-User-Id") Long adminId) {
        userService.requireAdmin(adminId);
        AdminStatisticsVO vo = new AdminStatisticsVO();
        vo.setTotalUsers(adminStatisticsMapper.countUsers());
        vo.setTotalCompanions(adminStatisticsMapper.countCompanions());
        vo.setPendingAuditCount(adminStatisticsMapper.countPendingAudit());
        vo.setTotalOrders(adminStatisticsMapper.countOrders());
        vo.setCompletedOrders(adminStatisticsMapper.countCompletedOrders());
        vo.setPendingAcceptOrders(adminStatisticsMapper.countPendingAcceptOrders());
        vo.setInServiceOrders(adminStatisticsMapper.countInServiceOrders());
        vo.setComplaintCount(adminStatisticsMapper.countComplaints());
        vo.setPendingComplaintCount(adminStatisticsMapper.countPendingComplaints());
        vo.setTotalRevenue(adminStatisticsMapper.sumTotalRevenue());
        vo.setPlatformRevenue(adminStatisticsMapper.sumPlatformRevenue());
        vo.setCompanionIncome(adminStatisticsMapper.sumCompanionIncome());
        return ApiResponse.success(vo);
    }
}
