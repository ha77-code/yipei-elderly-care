package com.yipei.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CompanionApplyRequest {
    @NotBlank(message = "真实姓名不能为空")
    private String realName;

    private String avatar;

    @NotBlank(message = "个人介绍不能为空")
    private String introduction;

    @NotBlank(message = "服务区域不能为空")
    private String serviceArea;

    @NotBlank(message = "服务类型不能为空")
    private String serviceTypes;

    private String traits;

    @NotNull(message = "经验年限不能为空")
    private Integer experienceYears;
}
