package com.yipei.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Evaluation {
    private Long id;
    private Long orderId;
    private Long fromUserId;
    private Long toUserId;
    private Integer score;
    private String content;
    private LocalDateTime createdAt;
}
