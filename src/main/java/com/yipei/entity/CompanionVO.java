package com.yipei.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CompanionVO {
    private Long id;
    private Long userId;
    private String realName;
    private String avatar;
    private String introduction;
    private String serviceArea;
    private String serviceTypes;
    private Integer experienceYears;
    private BigDecimal rating;
    private Integer completedCount;
    private Integer auditStatus;
    private String traits;
    private String nickname;
    private String phone;
    private LocalDateTime createdAt;
}
