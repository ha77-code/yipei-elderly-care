package com.yipei.mapper;

import com.yipei.entity.ServiceRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ServiceRecordMapper {

    @Select("SELECT id, order_id, content, important_notes, completed_by, created_at " +
            "FROM service_record WHERE order_id = #{orderId}")
    ServiceRecord selectByOrderId(@Param("orderId") Long orderId);
}
