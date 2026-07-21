package com.yipei.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/** 陪诊师需求广场卡片：需求信息 + 申请数 + 当前陪诊师申请状态 */
@Data
public class RequestPoolVO {
    private Long id;
    private Long customerId;
    private String serviceType;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime serviceDate;
    private String hospitalName;
    private String department;
    private String requirement;
    private String aiSummary;
    private String preferredTraits;
    private Boolean needPickup;
    private BigDecimal budget;
    private String status;
    private LocalDateTime createdAt;

    /** 当前待处理申请数 */
    private Integer applicationCount;
    /** 当前陪诊师对该需求的申请状态（PENDING/ACCEPTED/REJECTED/WITHDRAWN），未申请为 null */
    private String myApplicationStatus;
}
