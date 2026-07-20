package com.yipei.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ServiceRequest {
    private Long id;
    private Long customerId;
    private String serviceType;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime serviceDate;
    private String hospitalName;
    private String department;
    private String requirement;
    private String specialNotes;
    private String aiSummary;
    private String preferredTraits;
    private Boolean needPickup;
    private String contactName;
    private String contactPhone;
    private BigDecimal budget;
    private String status;
    private Integer auditStatus;
    private String auditRemark;
    /** 通道B：客户指定的陪诊师 companion_profile.id，审核通过后据此生成订单 */
    private Long preferredCompanionId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /** 非持久字段：指定陪诊师昵称，供客户和管理员页面展示 */
    private String preferredCompanionName;
}
