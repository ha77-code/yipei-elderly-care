package com.yipei.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ChatMessage {
    private Long id;
    private Long orderId;
    private Long fromUserId;
    private Long toUserId;
    private String content;
    private Integer isRead;
    private LocalDateTime createdAt;
}
