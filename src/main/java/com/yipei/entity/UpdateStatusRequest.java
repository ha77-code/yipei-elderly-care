package com.yipei.entity;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateStatusRequest {
    @NotNull(message = "status不能为空")
    private Integer status;
}