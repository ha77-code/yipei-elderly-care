package com.yipei.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;

@Mapper
public interface AdminStatisticsMapper {

    /* ---- 用户与人员 ---- */
    @Select("SELECT COUNT(*) FROM sys_user")
    Long countUsers();

    @Select("SELECT COUNT(*) FROM companion_profile")
    Long countCompanions();

    @Select("SELECT COUNT(*) FROM companion_profile WHERE audit_status = 0")
    Long countPendingAudit();

    /* ---- 订单 ---- */
    @Select("SELECT COUNT(*) FROM service_order")
    Long countOrders();

    @Select("SELECT COUNT(*) FROM service_order WHERE status = 'COMPLETED'")
    Long countCompletedOrders();

    @Select("SELECT COUNT(*) FROM service_order WHERE status = 'PENDING_ACCEPT'")
    Long countPendingAcceptOrders();

    @Select("SELECT COUNT(*) FROM service_order WHERE status = 'IN_SERVICE'")
    Long countInServiceOrders();

    /* ---- 投诉 ---- */
    @Select("SELECT COUNT(*) FROM report_record")
    Long countComplaints();

    @Select("SELECT COUNT(*) FROM report_record WHERE status = 'PENDING'")
    Long countPendingComplaints();

    /* ---- 收入（仅统计已完成订单） ---- */
    @Select("SELECT COALESCE(SUM(service_price), 0) FROM service_order WHERE status = 'COMPLETED'")
    BigDecimal sumTotalRevenue();

    @Select("SELECT COALESCE(SUM(platform_fee), 0) FROM service_order WHERE status = 'COMPLETED'")
    BigDecimal sumPlatformRevenue();

    @Select("SELECT COALESCE(SUM(companion_income), 0) FROM service_order WHERE status = 'COMPLETED'")
    BigDecimal sumCompanionIncome();
}
