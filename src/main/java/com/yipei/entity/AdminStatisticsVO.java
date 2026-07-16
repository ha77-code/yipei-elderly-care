package com.yipei.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AdminStatisticsVO {
    /* 用户与人员 */
    private Long totalUsers;
    private Long totalCompanions;
    private Long pendingAuditCount;

    /* 订单 */
    private Long totalOrders;
    private Long completedOrders;
    private Long pendingAcceptOrders;
    private Long inServiceOrders;

    /* 投诉 */
    private Long complaintCount;
    private Long pendingComplaintCount;

    /* 收入 */
    private BigDecimal totalRevenue;
    private BigDecimal platformRevenue;
    private BigDecimal companionIncome;
}
