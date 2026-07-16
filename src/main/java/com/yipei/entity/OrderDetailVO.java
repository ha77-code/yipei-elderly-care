package com.yipei.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDetailVO {
    private Long id;
    private Long requestId;
    private Long customerId;
    private String customerName;
    private Long companionId;
    private Long companionUserId;
    private String companionName;
    private BigDecimal servicePrice;
    private BigDecimal platformFee;
    private BigDecimal companionIncome;
    private String status;
    private LocalDateTime acceptedAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private String cancelReason;
    private String serviceType;
    private String hospitalName;
    private String department;
    private LocalDateTime serviceDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /** 状态变更记录 */
    private List<OrderStatusLog> statusLogs;
}
