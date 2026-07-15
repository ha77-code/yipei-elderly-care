package com.yipei.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ServiceOrder {
    private Long id;
    private Long requestId;
    private Long customerId;
    private Long companionId;
    private BigDecimal servicePrice;
    private BigDecimal platformFee;
    private BigDecimal companionIncome;
    private String status;
    private LocalDateTime acceptedAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private String cancelReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
