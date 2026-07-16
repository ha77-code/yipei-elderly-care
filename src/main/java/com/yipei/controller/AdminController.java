package com.yipei.controller;

import com.yipei.entity.ApiResponse;
import com.yipei.entity.AdminStatisticsVO;
import com.yipei.entity.ServiceRequest;
import com.yipei.mapper.AdminStatisticsMapper;
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

    public AdminController(ServiceRequestService serviceRequestService,
                           AdminStatisticsMapper adminStatisticsMapper) {
        this.serviceRequestService = serviceRequestService;
        this.adminStatisticsMapper = adminStatisticsMapper;
    }

    /** 管理员查看全部服务需求，支持按状态和服务类型筛选 */
    @GetMapping("/service-request/list")
    public ApiResponse<List<ServiceRequest>> listServiceRequests(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String serviceType) {
        return ApiResponse.success(serviceRequestService.listAll(status, serviceType));
    }

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
