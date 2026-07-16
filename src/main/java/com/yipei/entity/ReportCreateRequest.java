package com.yipei.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReportCreateRequest {
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    @NotBlank(message = "投诉原因不能为空")
    private String reason;

    @NotBlank(message = "投诉内容不能为空")
    private String content;
}
