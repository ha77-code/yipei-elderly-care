package com.yipei.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 聊天会话列表项：一条会话对应一个订单，展示对方信息、最近一条消息与未读数。
 */
@Data
public class ChatConversationVO {
    private Long orderId;
    private String orderStatus;
    /** 对方用户ID/昵称/头像 */
    private Long counterpartUserId;
    private String counterpartName;
    private String counterpartAvatar;
    /** 订单上下文，便于区分同一对话人的不同订单 */
    private String hospitalName;
    private String serviceType;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime serviceDate;
    /** 最近一条消息内容与时间 */
    private String lastMessage;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastTime;
    private Integer unreadCount;
    /** 聊天是否仍开启（服务结束后为只读） */
    private Boolean chatOpen;
}
