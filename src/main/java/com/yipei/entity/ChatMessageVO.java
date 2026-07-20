package com.yipei.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ChatMessageVO {
    private Long id;
    private Long orderId;
    private Long fromUserId;
    private String fromName;
    private String fromAvatar;
    private Long toUserId;
    private String content;
    private Integer isRead;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
