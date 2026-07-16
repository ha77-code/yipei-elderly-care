package com.yipei.controller;

import com.yipei.entity.AdminStatisticsVO;
import com.yipei.entity.ApiResponse;
import com.yipei.entity.OrderDetailVO;
import com.yipei.entity.ServiceRequest;
import com.yipei.mapper.AdminStatisticsMapper;
import com.yipei.service.OrderService;
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
    private final AdminStatisticsMapper adminStatisticsMapper;
    private final OrderService orderService;

    public AdminController(ServiceRequestService serviceRequestService,
                           AdminStatisticsMapper adminStatisticsMapper,
                           OrderService orderService) {
        this.serviceRequestService = serviceRequestService;
        this.adminStatisticsMapper = adminStatisticsMapper;
        this.orderService = orderService;
    }

    /** 管理员查看全部服务需求 */
    @GetMapping("/service-request/list")
    public ApiResponse<List<ServiceRequest>> listServiceRequests(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String serviceType) {
        return ApiResponse.success(serviceRequestService.listAll(status, serviceType));
    }

    /** 管理员查看全部订单 */
    @GetMapping("/orders")
    public ApiResponse<List<OrderDetailVO>> listOrders(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) Long companionId) {
        return ApiResponse.success(orderService.listForAdmin(status, customerId, companionId));
    }

    /** 平台统计 */
    @GetMapping("/statistics")
    public ApiResponse<AdminStatisticsVO> statistics() {
        AdminStatisticsVO statistics = new AdminStatisticsVO();
        statistics.setTotalUsers(adminStatisticsMapper.countUsers());
        statistics.setTotalCompanions(adminStatisticsMapper.countCompanions());
        statistics.setTotalOrders(adminStatisticsMapper.countOrders());
        statistics.setCompletedOrders(adminStatisticsMapper.countCompletedOrders());
        statistics.setComplaintCount(adminStatisticsMapper.countComplaints());
        statistics.setPlatformRevenue(adminStatisticsMapper.sumPlatformRevenue());
        return ApiResponse.success(statistics);
    }
}
