package com.yipei.entity;

import java.math.BigDecimal;

public class AdminStatisticsVO {
    private Long totalUsers;
    private Long totalCompanions;
    private Long totalOrders;
    private Long completedOrders;
    private Long complaintCount;
    private BigDecimal platformRevenue;

    public Long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(Long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public Long getTotalCompanions() {
        return totalCompanions;
    }

    public void setTotalCompanions(Long totalCompanions) {
        this.totalCompanions = totalCompanions;
    }

    public Long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(Long totalOrders) {
        this.totalOrders = totalOrders;
    }

    public Long getCompletedOrders() {
        return completedOrders;
    }

    public void setCompletedOrders(Long completedOrders) {
        this.completedOrders = completedOrders;
    }

    public Long getComplaintCount() {
        return complaintCount;
    }

    public void setComplaintCount(Long complaintCount) {
        this.complaintCount = complaintCount;
    }

    public BigDecimal getPlatformRevenue() {
        return platformRevenue;
    }

    public void setPlatformRevenue(BigDecimal platformRevenue) {
        this.platformRevenue = platformRevenue;
    }
}
