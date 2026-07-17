package com.yipei.service;

import com.yipei.constant.RoleConstants;
import com.yipei.entity.OrderDetailVO;
import com.yipei.entity.SysUser;
import com.yipei.exception.ForbiddenException;
import com.yipei.mapper.CompanionProfileMapper;
import com.yipei.mapper.OrderStatusLogMapper;
import com.yipei.mapper.ServiceOrderMapper;
import com.yipei.mapper.ServiceRequestMapper;
import com.yipei.mapper.SysUserMapper;
import com.yipei.util.SubmitLock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private ServiceOrderMapper serviceOrderMapper;

    @Mock
    private ServiceRequestMapper serviceRequestMapper;

    @Mock
    private SysUserMapper sysUserMapper;

    @Mock
    private OrderStatusLogMapper orderStatusLogMapper;

    @Mock
    private CompanionProfileMapper companionProfileMapper;

    @Mock
    private SubmitLock submitLock;

    @InjectMocks
    private OrderService orderService;

    @Test
    void getDetailAllowsOrderParticipant() {
        OrderDetailVO order = order(11L, 22L);
        when(serviceOrderMapper.selectDetailById(100L)).thenReturn(order);
        when(sysUserMapper.selectById(22L)).thenReturn(user(22L, RoleConstants.COMPANION));
        when(orderStatusLogMapper.selectByOrderId(100L)).thenReturn(List.of());

        OrderDetailVO result = orderService.getDetail(100L, 22L);

        assertSame(order, result);
    }

    @Test
    void getDetailRejectsNonParticipant() {
        when(serviceOrderMapper.selectDetailById(100L)).thenReturn(order(11L, 22L));
        when(sysUserMapper.selectById(33L)).thenReturn(user(33L, RoleConstants.CUSTOMER));

        assertThrows(ForbiddenException.class, () -> orderService.getDetail(100L, 33L));
    }

    @Test
    void getDetailAllowsAdministrator() {
        OrderDetailVO order = order(11L, 22L);
        when(serviceOrderMapper.selectDetailById(100L)).thenReturn(order);
        when(sysUserMapper.selectById(99L)).thenReturn(user(99L, RoleConstants.ADMIN));
        when(orderStatusLogMapper.selectByOrderId(100L)).thenReturn(List.of());

        OrderDetailVO result = orderService.getDetail(100L, 99L);

        assertSame(order, result);
    }

    private OrderDetailVO order(Long customerId, Long companionUserId) {
        OrderDetailVO order = new OrderDetailVO();
        order.setCustomerId(customerId);
        order.setCompanionUserId(companionUserId);
        return order;
    }

    private SysUser user(Long id, String role) {
        SysUser user = new SysUser();
        user.setId(id);
        user.setRole(role);
        return user;
    }
}