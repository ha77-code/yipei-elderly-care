package com.yipei.controller;

import com.yipei.entity.ApiResponse;
import com.yipei.entity.OrderCreateRequest;
import com.yipei.entity.OrderDetailVO;
import com.yipei.entity.ServiceOrder;
import com.yipei.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody OrderCreateRequest request) {
        return ApiResponse.success(orderService.create(userId, request));
    }

    /** 订单列表 */
    @GetMapping("/list")
    public ApiResponse<List<OrderDetailVO>> list(
            @RequestParam("userId") Long userId,
            @RequestParam("role") String role) {
        return ApiResponse.success(orderService.listByRole(userId, role));
    }

    /** 可接订单（陪诊师查看） */
    @GetMapping("/available")
    public ApiResponse<List<OrderDetailVO>> available(
            @RequestParam(required = false) String serviceType,
            @RequestParam(required = false) String keyword) {
        return ApiResponse.success(orderService.listAvailable(serviceType, keyword));
    }

    /** 订单详情 */
    @GetMapping("/{id}")
    public ApiResponse<OrderDetailVO> detail(@PathVariable Long id) {
        return ApiResponse.success(orderService.getDetail(id));
    }
}
