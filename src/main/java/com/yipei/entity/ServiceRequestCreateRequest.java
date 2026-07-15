package com.yipei.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ServiceRequestCreateRequest {
    @NotBlank(message = "服务类型不能为空")
    private String serviceType;

    @NotNull(message = "服务日期不能为空")
    private LocalDateTime serviceDate;

    @NotBlank(message = "医院名称不能为空")
    private String hospitalName;

    @NotBlank(message = "科室不能为空")
    private String department;

    @NotBlank(message = "需求内容不能为空")
    private String requirement;

    private String specialNotes;

    @NotBlank(message = "联系人不能为空")
    private String contactName;

    @NotBlank(message = "联系电话不能为空")
    private String contactPhone;

    private BigDecimal budget;
}
