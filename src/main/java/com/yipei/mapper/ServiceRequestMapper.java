package com.yipei.mapper;

import com.yipei.entity.ServiceRequest;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ServiceRequestMapper {

    @Select("SELECT id, customer_id, service_type, service_date, hospital_name, department, " +
            "requirement, special_notes, contact_name, contact_phone, budget, status, " +
            "created_at, updated_at FROM service_request WHERE id = #{id}")
    ServiceRequest selectById(@Param("id") Long id);

    @Select("SELECT id, customer_id, service_type, service_date, hospital_name, department, " +
            "requirement, special_notes, contact_name, contact_phone, budget, status, " +
            "created_at, updated_at FROM service_request WHERE customer_id = #{customerId} " +
            "ORDER BY created_at DESC")
    List<ServiceRequest> selectByCustomerId(@Param("customerId") Long customerId);

    @Insert("INSERT INTO service_request(customer_id, service_type, service_date, hospital_name, " +
            "department, requirement, special_notes, contact_name, contact_phone, budget, status, " +
            "created_at, updated_at) VALUES(#{customerId}, #{serviceType}, #{serviceDate}, " +
            "#{hospitalName}, #{department}, #{requirement}, #{specialNotes}, #{contactName}, " +
            "#{contactPhone}, #{budget}, #{status}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ServiceRequest request);

    @Update("UPDATE service_request SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);

    @Select("SELECT COUNT(*) FROM service_order WHERE request_id = #{requestId}")
    int countOrdersByRequestId(@Param("requestId") Long requestId);
}
