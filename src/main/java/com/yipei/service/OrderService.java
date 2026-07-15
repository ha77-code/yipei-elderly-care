package com.yipei.service;

import com.yipei.entity.OrderCreateRequest;
import com.yipei.entity.OrderDetailVO;
import com.yipei.entity.ServiceOrder;
import com.yipei.entity.ServiceRequest;
import com.yipei.entity.SysUser;
import com.yipei.exception.ForbiddenException;
import com.yipei.exception.NotFoundException;
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

    private static final BigDecimal PLATFORM_RATE = new BigDecimal("0.1");

    public OrderService(ServiceOrderMapper serviceOrderMapper,
                        ServiceRequestMapper serviceRequestMapper,
                        SysUserMapper sysUserMapper) {
        this.serviceOrderMapper = serviceOrderMapper;
        this.serviceRequestMapper = serviceRequestMapper;
        this.sysUserMapper = sysUserMapper;
    }

    /** 创建订单 */
    public ServiceOrder create(Long customerId, OrderCreateRequest request) {
        // 校验服务需求
        ServiceRequest sr = serviceRequestMapper.selectById(request.getRequestId());
        if (sr == null) {
            throw new NotFoundException("服务需求不存在，ID: " + request.getRequestId());
        }
        if (!sr.getCustomerId().equals(customerId)) {
            throw new ForbiddenException("只能基于自己的服务需求创建订单");
        }
        // 校验不重复创建订单
        int existCount = serviceRequestMapper.countOrdersByRequestId(request.getRequestId());
        if (existCount > 0) {
            throw new ForbiddenException("该需求已生成订单，不能重复创建");
        }
        // 校验陪诊师
        SysUser companion = sysUserMapper.selectById(request.getCompanionId());
        if (companion == null || !"COMPANION".equals(companion.getRole())) {
            throw new NotFoundException("陪诊师不存在，ID: " + request.getCompanionId());
        }
        // 计算平台抽成与陪诊师收入
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
        return order;
    }

    /** 按角色查询订单列表 */
    public List<OrderDetailVO> listByRole(Long userId, String role) {
        return serviceOrderMapper.selectByRole(userId, role);
    }

    /** 订单详情 */
    public OrderDetailVO getDetail(Long id) {
        OrderDetailVO order = serviceOrderMapper.selectDetailById(id);
        if (order == null) {
            throw new NotFoundException("订单不存在，ID: " + id);
        }
        return order;
    }
}
