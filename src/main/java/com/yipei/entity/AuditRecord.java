package com.yipei.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AuditRecord {
    private Long id;
    private String businessType;
    private Long businessId;
    private Long auditorId;
    private Integer auditStatus;
    private String remark;
    private LocalDateTime createdAt;
}
