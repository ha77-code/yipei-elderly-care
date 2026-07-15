package com.yipei.entity;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderCreateRequest {
    @NotNull(message = "需求ID不能为空")
    private Long requestId;

    @NotNull(message = "陪诊师ID不能为空")
    private Long companionId;

    @NotNull(message = "服务价格不能为空")
    private BigDecimal servicePrice;
}
