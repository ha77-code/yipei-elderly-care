package com.yipei.mapper;

import com.yipei.entity.ServiceRecord;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ServiceRecordMapper {

    @Select("SELECT id, order_id, content, important_notes, completed_by, created_at " +
            "FROM service_record WHERE order_id = #{orderId}")
    ServiceRecord selectByOrderId(@Param("orderId") Long orderId);

    @Insert("INSERT INTO service_record(order_id, content, important_notes, completed_by, created_at) " +
            "VALUES(#{orderId}, #{content}, #{importantNotes}, #{completedBy}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ServiceRecord record);
}
