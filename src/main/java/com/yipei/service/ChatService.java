package com.yipei.service;

import com.yipei.constant.RoleConstants;
import com.yipei.entity.ChatConversationVO;
import com.yipei.entity.ChatMessage;
import com.yipei.entity.ChatMessageVO;
import com.yipei.entity.CompanionProfile;
import com.yipei.entity.ServiceOrder;
import com.yipei.entity.SysUser;
import com.yipei.exception.ForbiddenException;
import com.yipei.exception.NotFoundException;
import com.yipei.mapper.ChatMessageMapper;
import com.yipei.mapper.CompanionProfileMapper;
import com.yipei.mapper.ServiceOrderMapper;
import com.yipei.mapper.SysUserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public class ChatService {
    private final ChatMessageMapper chatMessageMapper;
    private final ServiceOrderMapper serviceOrderMapper;
    private final CompanionProfileMapper companionProfileMapper;
    private final SysUserMapper sysUserMapper;
    private final UserNotificationService notificationService;

    /** 聊天开启的订单状态：撮合达成后、服务结束前 */
    private static final Set<String> OPEN_STATUSES = Set.of("ACCEPTED", "IN_SERVICE", "PENDING_CONFIRM");

    public ChatService(ChatMessageMapper chatMessageMapper,
                       ServiceOrderMapper serviceOrderMapper,
                       CompanionProfileMapper companionProfileMapper,
                       SysUserMapper sysUserMapper,
                       UserNotificationService notificationService) {
        this.chatMessageMapper = chatMessageMapper;
        this.serviceOrderMapper = serviceOrderMapper;
        this.companionProfileMapper = companionProfileMapper;
        this.sysUserMapper = sysUserMapper;
        this.notificationService = notificationService;
    }

    private ServiceOrder requireOrder(Long orderId) {
        ServiceOrder order = serviceOrderMapper.selectById(orderId);
        if (order == null) {
            throw new NotFoundException("订单不存在，ID: " + orderId);
        }
        return order;
    }

    /** 返回订单里对方的 userId；参数 userId 必须是该订单的客户或陪诊师 */
    private Long resolveCounterpart(ServiceOrder order, Long userId) {
        Long companionUserId = null;
        CompanionProfile profile = companionProfileMapper.selectById(order.getCompanionId());
        if (profile != null) {
            companionUserId = profile.getUserId();
        }
        if (order.getCustomerId().equals(userId)) {
            return companionUserId;
        }
        if (companionUserId != null && companionUserId.equals(userId)) {
            return order.getCustomerId();
        }
        return null; // 非参与者
    }

    private boolean isParticipant(ServiceOrder order, Long userId) {
        return resolveCounterpart(order, userId) != null;
    }

    public boolean isOpen(ServiceOrder order) {
        return OPEN_STATUSES.contains(order.getStatus());
    }

    /** 发送消息（仅参与者、仅聊天开启状态） */
    @Transactional
    public ChatMessageVO send(Long orderId, Long fromUserId, String content) {
        if (content == null || content.isBlank()) {
            throw new ForbiddenException("消息内容不能为空");
        }
        if (content.length() > 1000) {
            throw new ForbiddenException("消息内容过长");
        }
        ServiceOrder order = requireOrder(orderId);
        Long toUserId = resolveCounterpart(order, fromUserId);
        if (toUserId == null) {
            throw new ForbiddenException("只能在自己参与的订单中聊天");
        }
        if (!isOpen(order)) {
            throw new ForbiddenException("服务已结束，聊天通道已关闭");
        }
        ChatMessage message = new ChatMessage();
        message.setOrderId(orderId);
        message.setFromUserId(fromUserId);
        message.setToUserId(toUserId);
        message.setContent(content.trim());
        chatMessageMapper.insert(message);

        ChatMessageVO vo = new ChatMessageVO();
        vo.setId(message.getId());
        vo.setOrderId(orderId);
        vo.setFromUserId(fromUserId);
        vo.setToUserId(toUserId);
        vo.setContent(message.getContent());
        vo.setIsRead(0);
        SysUser sender = sysUserMapper.selectById(fromUserId);
        if (sender != null) {
            vo.setFromName(sender.getNickname());
            vo.setFromAvatar(sender.getAvatar());
        }
        String senderName = sender != null && sender.getNickname() != null
                ? sender.getNickname() : "订单联系人";
        String preview = message.getContent().length() > 80
                ? message.getContent().substring(0, 80) + "..." : message.getContent();
        notificationService.send(toUserId, "CHAT_MESSAGE", "收到新的私信",
                senderName + "在订单 #" + orderId + " 中发来消息：" + preview, orderId);
        return vo;
    }

    /** 读取某订单聊天记录（参与者或管理员），并把发给自己的消息标记已读 */
    public List<ChatMessageVO> listMessages(Long orderId, Long userId) {
        ServiceOrder order = requireOrder(orderId);
        SysUser user = sysUserMapper.selectById(userId);
        boolean admin = user != null && RoleConstants.ADMIN.equals(user.getRole());
        if (!admin && !isParticipant(order, userId)) {
            throw new ForbiddenException("只能查看自己参与的订单聊天");
        }
        if (!admin) {
            chatMessageMapper.markRead(orderId, userId);
        }
        return chatMessageMapper.selectByOrder(orderId);
    }

    /** 某订单聊天是否开启（供前端决定是否显示输入框） */
    public boolean orderChatOpen(Long orderId, Long userId) {
        ServiceOrder order = requireOrder(orderId);
        if (!isParticipant(order, userId)) {
            throw new ForbiddenException("只能查看自己参与的订单");
        }
        return isOpen(order);
    }

    /** 总未读数（用于导航红点） */
    public int unreadTotal(Long userId) {
        return chatMessageMapper.countUnreadTotal(userId);
    }

    /** 当前用户的会话列表（聊天中心用），并标注每条会话聊天是否仍开启 */
    public List<ChatConversationVO> listConversations(Long userId) {
        List<ChatConversationVO> list = chatMessageMapper.selectConversations(userId);
        for (ChatConversationVO vo : list) {
            vo.setChatOpen(OPEN_STATUSES.contains(vo.getOrderStatus()));
        }
        return list;
    }
}
