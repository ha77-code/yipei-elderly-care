package com.yipei.mapper;

import com.yipei.entity.ServiceRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ServiceRequestMapper {

    @Select("SELECT id, customer_id, service_type, service_date, hospital_name, department, " +
            "requirement, special_notes, contact_name, contact_phone, budget, status, " +
            "created_at, updated_at FROM service_request WHERE id = #{id}")
    ServiceRequest selectById(@Param("id") Long id);

    @org.apache.ibatis.annotations.Update(
            "UPDATE service_request SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);

    @Select("SELECT COUNT(*) FROM service_order WHERE request_id = #{requestId}")
    int countOrdersByRequestId(@Param("requestId") Long requestId);
}
