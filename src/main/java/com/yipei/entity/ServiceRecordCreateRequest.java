package com.yipei.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ServiceRecordCreateRequest {
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    @NotBlank(message = "服务内容不能为空")
    private String content;

    private String importantNotes;
}
