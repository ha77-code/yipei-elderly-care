package com.yipei.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
public class CompanionProfile {


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


    /**
     * 0 待审核
     * 1 通过
     * 2 拒绝
     */
    private Integer auditStatus;


    private LocalDateTime createdAt;


    private LocalDateTime updatedAt;


}