package com.yipei.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ServiceRequest {
    private Long id;
    private Long customerId;
    private String serviceType;
    private LocalDateTime serviceDate;
    private String hospitalName;
    private String department;
    private String requirement;
    private String specialNotes;
    private String aiSummary;
    private String contactName;
    private String contactPhone;
    private BigDecimal budget;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
