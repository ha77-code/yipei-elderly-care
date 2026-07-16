package com.yipei.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReportRecord {
    private Long id;
    private Long orderId;
    private Long reporterId;
    private String reason;
    private String content;
    private String status;
    private Long handledBy;
    private LocalDateTime handledAt;
    private String handleRemark;
    private LocalDateTime createdAt;
}
