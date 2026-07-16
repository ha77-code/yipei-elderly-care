package com.yipei.mapper;

import com.yipei.entity.OrderStatusLog;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderStatusLogMapper {

    @Select("SELECT id, order_id, from_status, to_status, operator_id, remark, created_at " +
            "FROM order_status_log WHERE order_id = #{orderId} ORDER BY created_at ASC")
    List<OrderStatusLog> selectByOrderId(@Param("orderId") Long orderId);

    @Insert("INSERT INTO order_status_log(order_id, from_status, to_status, operator_id, remark, created_at) " +
            "VALUES(#{orderId}, #{fromStatus}, #{toStatus}, #{operatorId}, #{remark}, NOW())")
    int insert(OrderStatusLog log);
}
