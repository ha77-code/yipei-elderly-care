package com.yipei.service;

import com.yipei.entity.OrderCreateRequest;
import com.yipei.entity.OrderDetailVO;
import com.yipei.entity.OrderStatusLog;
import com.yipei.entity.ServiceOrder;
import com.yipei.entity.ServiceRequest;
import com.yipei.entity.SysUser;
import com.yipei.exception.ForbiddenException;
import com.yipei.exception.NotFoundException;
import com.yipei.mapper.OrderStatusLogMapper;
import com.yipei.mapper.ServiceOrderMapper;
import com.yipei.mapper.ServiceRequestMapper;
import com.yipei.mapper.SysUserMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class OrderService {
    private final ServiceOrderMapper serviceOrderMapper;
    private final ServiceRequestMapper serviceRequestMapper;
    private final SysUserMapper sysUserMapper;
    private final OrderStatusLogMapper orderStatusLogMapper;

    private static final BigDecimal PLATFORM_RATE = new BigDecimal("0.1");

    public OrderService(ServiceOrderMapper serviceOrderMapper,
                        ServiceRequestMapper serviceRequestMapper,
                        SysUserMapper sysUserMapper,
                        OrderStatusLogMapper orderStatusLogMapper) {
        this.serviceOrderMapper = serviceOrderMapper;
        this.serviceRequestMapper = serviceRequestMapper;
        this.sysUserMapper = sysUserMapper;
        this.orderStatusLogMapper = orderStatusLogMapper;
    }

    /** 创建订单 */
    public ServiceOrder create(Long customerId, OrderCreateRequest request) {
        ServiceRequest sr = serviceRequestMapper.selectById(request.getRequestId());
        if (sr == null) {
            throw new NotFoundException("服务需求不存在，ID: " + request.getRequestId());
        }
        if (!sr.getCustomerId().equals(customerId)) {
            throw new ForbiddenException("只能基于自己的服务需求创建订单");
        }
        int existCount = serviceRequestMapper.countOrdersByRequestId(request.getRequestId());
        if (existCount > 0) {
            throw new ForbiddenException("该需求已生成订单，不能重复创建");
        }
        SysUser companion = sysUserMapper.selectById(request.getCompanionId());
        if (companion == null || !"COMPANION".equals(companion.getRole())) {
            throw new NotFoundException("陪诊师不存在，ID: " + request.getCompanionId());
        }
        BigDecimal platformFee = request.getServicePrice()
                .multiply(PLATFORM_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal companionIncome = request.getServicePrice().subtract(platformFee);

        ServiceOrder order = new ServiceOrder();
        order.setRequestId(request.getRequestId());
        order.setCustomerId(customerId);
        order.setCompanionId(request.getCompanionId());
        order.setServicePrice(request.getServicePrice());
        order.setPlatformFee(platformFee);
        order.setCompanionIncome(companionIncome);
        order.setStatus("PENDING_ACCEPT");
        serviceOrderMapper.insert(order);

        OrderStatusLog log = new OrderStatusLog();
        log.setOrderId(order.getId());
        log.setToStatus("PENDING_ACCEPT");
        log.setOperatorId(customerId);
        log.setRemark("客户创建订单");
        orderStatusLogMapper.insert(log);

        return order;
    }

    /* ===== 陪诊师操作 ===== */

    /** 接单 */
    public void accept(Long orderId, Long userId) {
        ServiceOrder order = serviceOrderMapper.selectById(orderId);
        if (order == null) {
            throw new NotFoundException("订单不存在，ID: " + orderId);
        }
        // 校验陪诊师身份
        if (!order.getCompanionId().equals(userId)) {
            throw new ForbiddenException("只能接自己被指定的订单");
        }
        if (!"PENDING_ACCEPT".equals(order.getStatus())) {
            throw new ForbiddenException("当前状态不允许接单");
        }
        serviceOrderMapper.accept(orderId, userId);

        OrderStatusLog log = new OrderStatusLog();
        log.setOrderId(orderId);
        log.setFromStatus("PENDING_ACCEPT");
        log.setToStatus("ACCEPTED");
        log.setOperatorId(userId);
        log.setRemark("陪诊师接单");
        orderStatusLogMapper.insert(log);
    }

    /** 拒绝订单 */
    public void reject(Long orderId, Long userId, String reason) {
        ServiceOrder order = serviceOrderMapper.selectById(orderId);
        if (order == null) {
            throw new NotFoundException("订单不存在，ID: " + orderId);
        }
        if (!order.getCompanionId().equals(userId)) {
            throw new ForbiddenException("只能拒绝自己被指定的订单");
        }
        if (!"PENDING_ACCEPT".equals(order.getStatus())) {
            throw new ForbiddenException("当前状态不允许拒绝");
        }
        serviceOrderMapper.updateStatus(orderId, "REJECTED", reason);

        OrderStatusLog log = new OrderStatusLog();
        log.setOrderId(orderId);
        log.setFromStatus("PENDING_ACCEPT");
        log.setToStatus("REJECTED");
        log.setOperatorId(userId);
        log.setRemark(reason != null ? reason : "陪诊师拒绝订单");
        orderStatusLogMapper.insert(log);
    }

    /** 开始服务 */
    public void start(Long orderId, Long userId) {
        ServiceOrder order = serviceOrderMapper.selectById(orderId);
        if (order == null) {
            throw new NotFoundException("订单不存在，ID: " + orderId);
        }
        if (!order.getCompanionId().equals(userId)) {
            throw new ForbiddenException("只能操作自己的订单");
        }
        if (!"ACCEPTED".equals(order.getStatus())) {
            throw new ForbiddenException("当前状态不允许开始服务");
        }
        serviceOrderMapper.start(orderId);

        OrderStatusLog log = new OrderStatusLog();
        log.setOrderId(orderId);
        log.setFromStatus("ACCEPTED");
        log.setToStatus("IN_SERVICE");
        log.setOperatorId(userId);
        log.setRemark("陪诊师开始服务");
        orderStatusLogMapper.insert(log);
    }

    /** 提交服务完成 */
    public void complete(Long orderId, Long userId) {
        ServiceOrder order = serviceOrderMapper.selectById(orderId);
        if (order == null) {
            throw new NotFoundException("订单不存在，ID: " + orderId);
        }
        if (!order.getCompanionId().equals(userId)) {
            throw new ForbiddenException("只能操作自己的订单");
        }
        if (!"IN_SERVICE".equals(order.getStatus())) {
            throw new ForbiddenException("当前状态不允许提交完成");
        }
        serviceOrderMapper.complete(orderId);

        OrderStatusLog log = new OrderStatusLog();
        log.setOrderId(orderId);
        log.setFromStatus("IN_SERVICE");
        log.setToStatus("PENDING_CONFIRM");
        log.setOperatorId(userId);
        log.setRemark("陪诊师提交服务完成，等待客户确认");
        orderStatusLogMapper.insert(log);
    }

    /* ===== 客户操作 ===== */

    /** 取消订单（仅未开始订单） */
    public void cancel(Long orderId, Long userId, String reason) {
        ServiceOrder order = serviceOrderMapper.selectById(orderId);
        if (order == null) {
            throw new NotFoundException("订单不存在，ID: " + orderId);
        }
        if (!order.getCustomerId().equals(userId)) {
            throw new ForbiddenException("只能取消自己的订单");
        }
        if (!"PENDING_ACCEPT".equals(order.getStatus())
                && !"ACCEPTED".equals(order.getStatus())) {
            throw new ForbiddenException("当前状态不允许取消，仅未开始的订单可取消");
        }
        String fromStatus = order.getStatus();
        serviceOrderMapper.cancel(orderId, reason);

        OrderStatusLog log = new OrderStatusLog();
        log.setOrderId(orderId);
        log.setFromStatus(fromStatus);
        log.setToStatus("CANCELLED");
        log.setOperatorId(userId);
        log.setRemark(reason != null ? reason : "客户取消订单");
        orderStatusLogMapper.insert(log);
    }

    /* ===== 查询 ===== */

    public List<OrderDetailVO> listAvailable(String serviceType, String keyword) {
        return serviceOrderMapper.selectAvailable(serviceType, keyword);
    }

    public List<OrderDetailVO> listByRole(Long userId, String role) {
        return serviceOrderMapper.selectByRole(userId, role);
    }

    public OrderDetailVO getDetail(Long id) {
        OrderDetailVO order = serviceOrderMapper.selectDetailById(id);
        if (order == null) {
            throw new NotFoundException("订单不存在，ID: " + id);
        }
        List<OrderStatusLog> logs = orderStatusLogMapper.selectByOrderId(id);
        order.setStatusLogs(logs);
        return order;
    }
}
