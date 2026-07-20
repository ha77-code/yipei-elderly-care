package com.yipei.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserNotification {
    private Long id;
    private Long userId;
    private String type;
    private String title;
    private String content;
    private Long relatedId;
    private Integer isRead;
    private LocalDateTime createdAt;
}
