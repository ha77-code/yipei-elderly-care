package com.yipei.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 申请详情视图。
 * - 客户视角：申请 + 陪诊师资料（挑选陪诊师用）。
 * - 陪诊师视角：申请 + 需求信息（查看自己申请了哪些需求）。
 */
@Data
public class ApplicationVO {
    private Long id;
    private Long requestId;
    private Long companionId;
    private String message;
    private String status;
    private LocalDateTime createdAt;

    /* ===== 陪诊师资料（客户挑选用） ===== */
    private Long companionUserId;
    private String companionName;
    private String companionAvatar;
    private String introduction;
    private String serviceArea;
    private String serviceTypes;
    private Integer experienceYears;
    private BigDecimal rating;
    private Integer completedCount;

    /* ===== 需求信息（陪诊师查看自己申请用） ===== */
    private String serviceType;
    private String hospitalName;
    private String department;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime serviceDate;
    private BigDecimal budget;
    private String requestStatus;
}
