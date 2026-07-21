package com.yipei.controller;

import com.yipei.entity.ApiResponse;
import com.yipei.entity.OrderCreateRequest;
import com.yipei.entity.OrderDetailVO;
import com.yipei.entity.OrderStatusLog;
import com.yipei.entity.ServiceOrder;
import com.yipei.security.SecurityUtils;
import com.yipei.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/order")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /** 创建订单 */
    @PostMapping("/create")
    public ApiResponse<ServiceOrder> create(
            @Valid @RequestBody OrderCreateRequest request) {
        Long userId = SecurityUtils.requireLoginUserId();
        return ApiResponse.success(orderService.create(userId, request));
    }

    /** 订单列表 */
    @GetMapping("/list")
    public ApiResponse<List<OrderDetailVO>> list(
            @RequestParam(required = false) String role) {
        Long userId = SecurityUtils.requireLoginUserId();
        return ApiResponse.success(orderService.listByRole(userId, role != null ? role : ""));
    }

    /** 当前登录用户的订单 */
    @GetMapping("/my")
    public ApiResponse<List<OrderDetailVO>> myOrders() {
        Long userId = SecurityUtils.requireLoginUserId();
        return ApiResponse.success(orderService.listMine(userId));
    }

    /** 可接订单（陪诊师查看） */
    @GetMapping("/available")
    public ApiResponse<List<OrderDetailVO>> available(
            @RequestParam(required = false) String serviceType,
            @RequestParam(required = false) String keyword) {
        Long userId = SecurityUtils.requireLoginUserId();
        return ApiResponse.success(orderService.listAvailable(userId, serviceType, keyword));
    }

    /** 订单详情 */
    @GetMapping("/{id}")
    public ApiResponse<OrderDetailVO> detail(@PathVariable Long id) {
        Long userId = SecurityUtils.requireLoginUserId();
        return ApiResponse.success(orderService.getDetail(id, userId));
    }

    /** 接单 */
    @PutMapping("/{id}/accept")
    public ApiResponse<Void> accept(@PathVariable Long id) {
        Long userId = SecurityUtils.requireLoginUserId();
        orderService.accept(id, userId);
        return ApiResponse.success();
    }

    /** 拒绝订单 */
    @PutMapping("/{id}/reject")
    public ApiResponse<Void> reject(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        Long userId = SecurityUtils.requireLoginUserId();
        orderService.reject(id, userId, body.get("reason"));
        return ApiResponse.success();
    }

    /** 开始服务 */
    @PutMapping("/{id}/start")
    public ApiResponse<Void> start(@PathVariable Long id) {
        Long userId = SecurityUtils.requireLoginUserId();
        orderService.start(id, userId);
        return ApiResponse.success();
    }

    /** 提交服务完成 */
    @PutMapping("/{id}/complete")
    public ApiResponse<Void> complete(@PathVariable Long id) {
        Long userId = SecurityUtils.requireLoginUserId();
        orderService.complete(id, userId);
        return ApiResponse.success();
    }

    /** 确认完成 */
    @PutMapping("/{id}/confirm")
    public ApiResponse<Void> confirm(@PathVariable Long id) {
        Long userId = SecurityUtils.requireLoginUserId();
        orderService.confirm(id, userId);
        return ApiResponse.success();
    }

    /** 取消订单 */
    @PutMapping("/{id}/cancel")
    public ApiResponse<Void> cancel(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        Long userId = SecurityUtils.requireLoginUserId();
        orderService.cancel(id, userId, body.get("cancelReason"));
        return ApiResponse.success();
    }

    /** 订单状态变更记录 */
    @GetMapping("/{id}/status-log")
    public ApiResponse<List<OrderStatusLog>> statusLog(@PathVariable Long id) {
        Long userId = SecurityUtils.requireLoginUserId();
        return ApiResponse.success(orderService.getStatusLogs(id, userId));
    }
}