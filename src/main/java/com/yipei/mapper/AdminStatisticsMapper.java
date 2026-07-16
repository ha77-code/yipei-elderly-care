package com.yipei.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;

@Mapper
public interface AdminStatisticsMapper {
    @Select("SELECT COUNT(*) FROM sys_user")
    Long countUsers();

    @Select("SELECT COUNT(*) FROM companion_profile")
    Long countCompanions();

    @Select("SELECT COUNT(*) FROM service_order")
    Long countOrders();

    @Select("SELECT COUNT(*) FROM service_order WHERE status = 'COMPLETED'")
    Long countCompletedOrders();

    @Select("SELECT COUNT(*) FROM report_record")
    Long countComplaints();

    @Select("SELECT COALESCE(SUM(platform_fee), 0) FROM service_order WHERE status = 'COMPLETED'")
    BigDecimal sumPlatformRevenue();
}
