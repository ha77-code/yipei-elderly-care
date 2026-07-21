package com.yipei.service;

import com.yipei.entity.ChatMessage;
import com.yipei.entity.CompanionProfile;
import com.yipei.entity.ServiceOrder;
import com.yipei.entity.SysUser;
import com.yipei.exception.ForbiddenException;
import com.yipei.mapper.ChatMessageMapper;
import com.yipei.mapper.CompanionProfileMapper;
import com.yipei.mapper.ServiceOrderMapper;
import com.yipei.mapper.SysUserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock private ChatMessageMapper chatMessageMapper;
    @Mock private ServiceOrderMapper serviceOrderMapper;
    @Mock private CompanionProfileMapper companionProfileMapper;
    @Mock private SysUserMapper sysUserMapper;
    @Mock private UserNotificationService notificationService;

    private ChatService chatService;

    @BeforeEach
    void setUp() {
        chatService = new ChatService(chatMessageMapper, serviceOrderMapper,
                companionProfileMapper, sysUserMapper, notificationService);
    }

    @Test
    void sendCreatesOrderChatNotificationAfterBothSidesAgree() {
        ServiceOrder order = order("ACCEPTED");
        CompanionProfile profile = new CompanionProfile();
        profile.setId(20L);
        profile.setUserId(2L);
        SysUser sender = new SysUser();
        sender.setId(1L);
        sender.setNickname("客户张先生");

        when(serviceOrderMapper.selectById(100L)).thenReturn(order);
        when(companionProfileMapper.selectById(20L)).thenReturn(profile);
        when(sysUserMapper.selectById(1L)).thenReturn(sender);

        var message = chatService.send(100L, 1L, "请明天提前十分钟到医院门口");

        assertEquals(2L, message.getToUserId());
        verify(chatMessageMapper).insert(org.mockito.ArgumentMatchers.any(ChatMessage.class));
        verify(notificationService).send(eq(2L), eq("CHAT_MESSAGE"), eq("收到新的私信"),
                contains("订单 #100"), eq(100L));
    }

    @Test
    void sendIsRejectedAfterServiceEndsButHistoryRemainsReadable() {
        ServiceOrder order = order("COMPLETED");
        CompanionProfile profile = new CompanionProfile();
        profile.setId(20L);
        profile.setUserId(2L);
        when(serviceOrderMapper.selectById(100L)).thenReturn(order);
        when(companionProfileMapper.selectById(20L)).thenReturn(profile);

        assertThrows(ForbiddenException.class,
                () -> chatService.send(100L, 1L, "服务结束后不应再发送"));
        verify(chatMessageMapper, never()).insert(org.mockito.ArgumentMatchers.any());
        verify(notificationService, never()).send(org.mockito.ArgumentMatchers.anyLong(),
                org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyLong());
    }

    private ServiceOrder order(String status) {
        ServiceOrder order = new ServiceOrder();
        order.setId(100L);
        order.setCustomerId(1L);
        order.setCompanionId(20L);
        order.setStatus(status);
        return order;
    }
}
