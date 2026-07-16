package com.yipei.entity;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EvaluationCreateRequest {
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    @NotNull(message = "被评价人ID不能为空")
    private Long toUserId;

    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "评分范围为1-5")
    @Max(value = 5, message = "评分范围为1-5")
    private Integer score;

    private String content;
}
