package com.yipei.service;

import com.yipei.entity.CompanionProfile;
import com.yipei.constant.RoleConstants;
import com.yipei.entity.OrderCreateRequest;
import com.yipei.entity.OrderDetailVO;
import com.yipei.entity.OrderStatusLog;
import com.yipei.entity.ServiceOrder;
import com.yipei.entity.ServiceRequest;
import com.yipei.entity.SysUser;
import com.yipei.exception.ForbiddenException;
import com.yipei.exception.NotFoundException;
import com.yipei.mapper.CompanionProfileMapper;
import com.yipei.mapper.OrderStatusLogMapper;
import com.yipei.mapper.ServiceOrderMapper;
import com.yipei.mapper.ServiceRequestMapper;
import com.yipei.mapper.SysUserMapper;
import com.yipei.util.SubmitLock;
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
    private final CompanionProfileMapper companionProfileMapper;
    private final SubmitLock submitLock;
    private final UserNotificationService notificationService;

    private static final BigDecimal PLATFORM_RATE = new BigDecimal("0.1");

    public OrderService(ServiceOrderMapper serviceOrderMapper,
                        ServiceRequestMapper serviceRequestMapper,
                        SysUserMapper sysUserMapper,
                        OrderStatusLogMapper orderStatusLogMapper,
                        CompanionProfileMapper companionProfileMapper,
                        SubmitLock submitLock,
                        UserNotificationService notificationService) {
        this.serviceOrderMapper = serviceOrderMapper;
        this.serviceRequestMapper = serviceRequestMapper;
        this.sysUserMapper = sysUserMapper;
        this.orderStatusLogMapper = orderStatusLogMapper;
        this.companionProfileMapper = companionProfileMapper;
        this.submitLock = submitLock;
        this.notificationService = notificationService;
    }

    /** 创建订单 */
    public ServiceOrder create(Long customerId, OrderCreateRequest request) {
        if (!submitLock.tryLock("order_create", customerId, 10)) {
            throw new ForbiddenException("请勿重复提交，稍后再试");
        }
        try {
            return doCreate(customerId, request);
        } finally {
            submitLock.unlock("order_create", customerId);
        }
    }

    private ServiceOrder doCreate(Long customerId, OrderCreateRequest request) {
        ServiceRequest sr = serviceRequestMapper.selectById(request.getRequestId());
        if (sr == null) {
            throw new NotFoundException("服务需求不存在，ID: " + request.getRequestId());
        }
        if (!sr.getCustomerId().equals(customerId)) {
            throw new ForbiddenException("只能基于自己的服务需求创建订单");
        }
        if (sr.getAuditStatus() == null || sr.getAuditStatus() != 1) {
            throw new ForbiddenException("该需求尚未通过审核，暂不能创建订单");
        }
        return buildPendingOrder(customerId, request.getRequestId(), request.getCompanionId(),
                request.getServicePrice(), "客户创建订单");
    }

    /**
     * 通道B：指定下单需求审核通过后，自动为客户选定的陪诊师生成待接单订单。
     * 价格取需求预算；预算为空时以 0 建单（与旧的指定下单默认行为一致）。
     */
    public ServiceOrder createDirectedOrder(Long customerId, Long requestId, Long companionProfileId,
                                            BigDecimal budget) {
        BigDecimal price = budget != null ? budget : BigDecimal.ZERO;
        return buildPendingOrder(customerId, requestId, companionProfileId, price,
                "指定需求审核通过，自动生成订单");
    }

    /** 生成 PENDING_ACCEPT 订单的共用逻辑（通道B 客户指定下单） */
    private ServiceOrder buildPendingOrder(Long customerId, Long requestId, Long companionProfileId,
                                           BigDecimal servicePrice, String logRemark) {
        int existCount = serviceRequestMapper.countOrdersByRequestId(requestId);
        if (existCount > 0) {
            throw new ForbiddenException("该需求已生成订单，不能重复创建");
        }
        // companionId 是 companion_profile.id
        CompanionProfile profile = companionProfileMapper.selectById(companionProfileId);
        if (profile == null || profile.getAuditStatus() == null || profile.getAuditStatus() != 1) {
            throw new NotFoundException("陪诊师不存在或未通过审核，ID: " + companionProfileId);
        }
        SysUser companion = sysUserMapper.selectById(profile.getUserId());
        if (companion == null || !RoleConstants.COMPANION.equals(companion.getRole())) {
            throw new NotFoundException("陪诊师用户异常，ID: " + profile.getUserId());
        }
        BigDecimal platformFee = servicePrice
                .multiply(PLATFORM_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal companionIncome = servicePrice.subtract(platformFee);

        ServiceOrder order = new ServiceOrder();
        order.setRequestId(requestId);
        order.setCustomerId(customerId);
        order.setCompanionId(profile.getId());
        order.setServicePrice(servicePrice);
        order.setPlatformFee(platformFee);
        order.setCompanionIncome(companionIncome);
        order.setStatus("PENDING_ACCEPT");
        serviceOrderMapper.insert(order);
        serviceRequestMapper.updateStatus(requestId, "MATCHED");

        OrderStatusLog log = new OrderStatusLog();
        log.setOrderId(order.getId());
        log.setToStatus("PENDING_ACCEPT");
        log.setOperatorId(customerId);
        log.setRemark(logRemark);
        orderStatusLogMapper.insert(log);

        return order;
    }

    /**
     * 通道A：客户接受陪诊师申请，直接生成「已接单」订单（双方已达成，无需再走 PENDING_ACCEPT）。
     * 价格取客户预算；若传入 servicePrice 则以其为准（用于预算为空时客户补价）。
     * 返回创建的订单，供上层开启聊天等后续处理。
     */
    public ServiceOrder createFromApplication(Long customerId, Long requestId, Long companionProfileId,
                                              BigDecimal servicePrice) {
        ServiceRequest sr = serviceRequestMapper.selectById(requestId);
        if (sr == null) {
            throw new NotFoundException("服务需求不存在，ID: " + requestId);
        }
        if (!sr.getCustomerId().equals(customerId)) {
            throw new ForbiddenException("只能基于自己的服务需求接受申请");
        }
        int existCount = serviceRequestMapper.countOrdersByRequestId(requestId);
        if (existCount > 0) {
            throw new ForbiddenException("该需求已生成订单，不能重复接受");
        }
        CompanionProfile profile = companionProfileMapper.selectById(companionProfileId);
        if (profile == null || profile.getAuditStatus() == null || profile.getAuditStatus() != 1) {
            throw new NotFoundException("陪诊师不存在或未通过审核，ID: " + companionProfileId);
        }
        BigDecimal price = servicePrice != null ? servicePrice : sr.getBudget();
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ForbiddenException("请先为该需求设置有效的服务金额");
        }
        BigDecimal platformFee = price.multiply(PLATFORM_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal companionIncome = price.subtract(platformFee);

        ServiceOrder order = new ServiceOrder();
        order.setRequestId(requestId);
        order.setCustomerId(customerId);
        order.setCompanionId(profile.getId());
        order.setServicePrice(price);
        order.setPlatformFee(platformFee);
        order.setCompanionIncome(companionIncome);
        order.setStatus("ACCEPTED");
        serviceOrderMapper.insert(order);
        serviceOrderMapper.markAcceptedNow(order.getId());
        serviceRequestMapper.updateStatus(requestId, "MATCHED");

        OrderStatusLog log = new OrderStatusLog();
        log.setOrderId(order.getId());
        log.setToStatus("ACCEPTED");
        log.setOperatorId(customerId);
        log.setRemark("客户接受陪诊师申请，订单达成");
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
        // 通过 companion_profile 校验当前用户是否是订单指定的陪诊师
        CompanionProfile profile = companionProfileMapper.selectById(order.getCompanionId());
        if (profile == null || !profile.getUserId().equals(userId)) {
            throw new ForbiddenException("只能接自己被指定的订单");
        }
        if (!"PENDING_ACCEPT".equals(order.getStatus())) {
            throw new ForbiddenException("当前状态不允许接单");
        }
        serviceOrderMapper.accept(orderId, order.getCompanionId());

        OrderStatusLog log = new OrderStatusLog();
        log.setOrderId(orderId);
        log.setFromStatus("PENDING_ACCEPT");
        log.setToStatus("ACCEPTED");
        log.setOperatorId(userId);
        log.setRemark("陪诊师接单");
        orderStatusLogMapper.insert(log);
        notificationService.send(order.getCustomerId(), "ORDER_ACCEPTED", "\u966a\u8bca\u5e08\u5df2\u63a5\u5355",
                "\u60a8\u6307\u5b9a\u7684\u966a\u8bca\u5e08\u5df2\u63a5\u5355\uff0c\u8bf7\u5728\u8ba2\u5355\u4e2d\u67e5\u770b\u670d\u52a1\u5b89\u6392\u3002", orderId);

    }

    /** 拒绝订单 */
    public void reject(Long orderId, Long userId, String reason) {
        ServiceOrder order = serviceOrderMapper.selectById(orderId);
        if (order == null) {
            throw new NotFoundException("订单不存在，ID: " + orderId);
        }
        CompanionProfile profile = companionProfileMapper.selectById(order.getCompanionId());
        if (profile == null || !profile.getUserId().equals(userId)) {
            throw new ForbiddenException("只能拒绝自己被指定的订单");
        }
        if (!"PENDING_ACCEPT".equals(order.getStatus())) {
            throw new ForbiddenException("当前状态不允许拒绝");
        }
        serviceOrderMapper.updateStatus(orderId, "REJECTED", reason);
        serviceRequestMapper.updateStatus(order.getRequestId(), "PENDING");

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
        CompanionProfile profile = companionProfileMapper.selectById(order.getCompanionId());
        if (profile == null || !profile.getUserId().equals(userId)) {
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
        CompanionProfile profile = companionProfileMapper.selectById(order.getCompanionId());
        if (profile == null || !profile.getUserId().equals(userId)) {
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

    /** 确认完成 */
    public void confirm(Long orderId, Long userId) {
        ServiceOrder order = serviceOrderMapper.selectById(orderId);
        if (order == null) {
            throw new NotFoundException("订单不存在，ID: " + orderId);
        }
        if (!order.getCustomerId().equals(userId)) {
            throw new ForbiddenException("只能确认自己的订单");
        }
        if (!"PENDING_CONFIRM".equals(order.getStatus())) {
            throw new ForbiddenException("当前状态不允许确认完成");
        }
        serviceOrderMapper.confirm(orderId);
        serviceRequestMapper.updateStatus(order.getRequestId(), "CLOSED");
        companionProfileMapper.incrementCompletedCount(order.getCompanionId());

        OrderStatusLog log = new OrderStatusLog();
        log.setOrderId(orderId);
        log.setFromStatus("PENDING_CONFIRM");
        log.setToStatus("COMPLETED");
        log.setOperatorId(userId);
        log.setRemark("客户确认服务完成");
        orderStatusLogMapper.insert(log);
    }

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
        serviceRequestMapper.updateStatus(order.getRequestId(), "CANCELLED");

        OrderStatusLog log = new OrderStatusLog();
        log.setOrderId(orderId);
        log.setFromStatus(fromStatus);
        log.setToStatus("CANCELLED");
        log.setOperatorId(userId);
        log.setRemark(reason != null ? reason : "客户取消订单");
        orderStatusLogMapper.insert(log);
    }

    /* ===== 管理员操作 ===== */

    public List<OrderDetailVO> listForAdmin(String status, Long customerId, Long companionId) {
        return serviceOrderMapper.selectForAdmin(status, customerId, companionId);
    }

    /* ===== 状态记录 ===== */

    public List<OrderStatusLog> getStatusLogs(Long orderId, Long userId) {
        return getDetail(orderId, userId).getStatusLogs();
    }

    /* ===== 查询 ===== */

    public List<OrderDetailVO> listAvailable(Long userId, String serviceType, String keyword) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null || !RoleConstants.COMPANION.equals(user.getRole())) {
            throw new ForbiddenException("仅陪诊师可以查看可接订单");
        }
        return serviceOrderMapper.selectAvailable(userId, serviceType, keyword);
    }

    public List<OrderDetailVO> listByRole(Long userId, String role) {
        return serviceOrderMapper.selectByRole(userId, role);
    }

    public List<OrderDetailVO> listMine(Long userId) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new NotFoundException("用户不存在，ID: " + userId);
        }
        if (RoleConstants.ADMIN.equals(user.getRole())) {
            return listForAdmin(null, null, null);
        }
        return serviceOrderMapper.selectByRole(userId, user.getRole());
    }

    public OrderDetailVO getDetail(Long id, Long userId) {
        OrderDetailVO order = serviceOrderMapper.selectDetailById(id);
        if (order == null) {
            throw new NotFoundException("订单不存在，ID: " + id);
        }
        SysUser user = sysUserMapper.selectById(userId);
        boolean isParticipant = order.getCustomerId().equals(userId)
                || (order.getCompanionUserId() != null
                && order.getCompanionUserId().equals(userId));
        if (user == null || (!RoleConstants.ADMIN.equals(user.getRole()) && !isParticipant)) {
            throw new ForbiddenException("只能查看自己参与的订单");
        }
        List<OrderStatusLog> logs = orderStatusLogMapper.selectByOrderId(id);
        order.setStatusLogs(logs);
        return order;
    }
}
