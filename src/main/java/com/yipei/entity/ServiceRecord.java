package com.yipei.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ServiceRecord {
    private Long id;
    private Long orderId;
    private String content;
    private String importantNotes;
    private Long completedBy;
    private LocalDateTime createdAt;
}
