package com.yipei.mapper;

import com.yipei.entity.OrderDetailVO;
import com.yipei.entity.ServiceOrder;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ServiceOrderMapper {

    @Insert("INSERT INTO service_order(request_id, customer_id, companion_id, service_price, " +
            "platform_fee, companion_income, status, created_at, updated_at) " +
            "VALUES(#{requestId}, #{customerId}, #{companionId}, #{servicePrice}, " +
            "#{platformFee}, #{companionIncome}, #{status}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ServiceOrder order);

    /** 按角色查询订单列表 */
    @Select("<script>" +
            "SELECT o.id, o.request_id, o.customer_id, cu.nickname AS customer_name, " +
            "o.companion_id, co.nickname AS companion_name, " +
            "o.service_price, o.platform_fee, o.companion_income, o.status, " +
            "o.accepted_at, o.started_at, o.completed_at, o.cancel_reason, " +
            "o.created_at, o.updated_at " +
            "FROM service_order o " +
            "LEFT JOIN sys_user cu ON o.customer_id = cu.id " +
            "LEFT JOIN companion_profile cp ON o.companion_id = cp.id " +
            "LEFT JOIN sys_user co ON cp.user_id = co.id " +
            "WHERE 1=1 " +
            "<if test='role == \"CUSTOMER\"'> AND o.customer_id = #{userId}</if>" +
            "<if test='role == \"COMPANION\"'> AND cp.user_id = #{userId}</if>" +
            " ORDER BY o.created_at DESC" +
            "</script>")
    List<OrderDetailVO> selectByRole(@Param("userId") Long userId,
                                     @Param("role") String role);

    /** 可接订单（PENDING_ACCEPT），含需求详情 */
    @Select("<script>" +
            "SELECT o.id, o.request_id, o.customer_id, o.companion_id, " +
            "o.service_price, o.platform_fee, o.companion_income, o.status, " +
            "sr.service_type, sr.hospital_name, sr.department, sr.service_date, " +
            "sr.requirement, sr.contact_name, sr.contact_phone, " +
            "o.created_at, o.updated_at " +
            "FROM service_order o " +
            "JOIN service_request sr ON o.request_id = sr.id " +
            "WHERE o.status = 'PENDING_ACCEPT' " +
            "<if test='serviceType != null and serviceType != \"\"'> AND sr.service_type = #{serviceType}</if>" +
            "<if test='keyword != null and keyword != \"\"'> AND (sr.hospital_name LIKE CONCAT('%',#{keyword},'%') OR sr.department LIKE CONCAT('%',#{keyword},'%'))</if>" +
            " ORDER BY o.created_at DESC" +
            "</script>")
    List<OrderDetailVO> selectAvailable(@Param("serviceType") String serviceType,
                                        @Param("keyword") String keyword);

    /** 订单详情，含请求信息 */
    @Select("SELECT o.id, o.request_id, o.customer_id, cu.nickname AS customer_name, " +
            "o.companion_id, co.nickname AS companion_name, " +
            "o.service_price, o.platform_fee, o.companion_income, o.status, " +
            "o.accepted_at, o.started_at, o.completed_at, o.cancel_reason, " +
            "sr.service_type, sr.hospital_name, sr.department, sr.service_date, " +
            "o.created_at, o.updated_at " +
            "FROM service_order o " +
            "LEFT JOIN sys_user cu ON o.customer_id = cu.id " +
            "LEFT JOIN companion_profile cp ON o.companion_id = cp.id " +
            "LEFT JOIN sys_user co ON cp.user_id = co.id " +
            "LEFT JOIN service_request sr ON o.request_id = sr.id " +
            "WHERE o.id = #{id}")
    OrderDetailVO selectDetailById(@Param("id") Long id);

    @Select("SELECT id, request_id, customer_id, companion_id, service_price, " +
            "platform_fee, companion_income, status, accepted_at, started_at, " +
            "completed_at, cancel_reason, created_at, updated_at " +
            "FROM service_order WHERE id = #{id}")
    ServiceOrder selectById(@Param("id") Long id);

    /** 接单 */
    @Update("UPDATE service_order SET status = 'ACCEPTED', companion_id = #{companionId}, " +
            "accepted_at = NOW() WHERE id = #{id} AND status = 'PENDING_ACCEPT'")
    int accept(@Param("id") Long id, @Param("companionId") Long companionId);

    /** 开始服务 */
    @Update("UPDATE service_order SET status = 'IN_SERVICE', started_at = NOW() " +
            "WHERE id = #{id} AND status = 'ACCEPTED'")
    int start(@Param("id") Long id);

    /** 完成服务 */
    @Update("UPDATE service_order SET status = 'PENDING_CONFIRM', completed_at = NOW() " +
            "WHERE id = #{id} AND status = 'IN_SERVICE'")
    int complete(@Param("id") Long id);

    /** 取消订单 */
    @Update("UPDATE service_order SET status = 'CANCELLED', cancel_reason = #{cancelReason} " +
            "WHERE id = #{id}")
    int cancel(@Param("id") Long id, @Param("cancelReason") String cancelReason);

    /** 管理员订单列表 */
    @Select("<script>" +
            "SELECT o.id, o.request_id, o.customer_id, cu.nickname AS customer_name, " +
            "o.companion_id, co.nickname AS companion_name, " +
            "o.service_price, o.platform_fee, o.companion_income, o.status, " +
            "o.accepted_at, o.started_at, o.completed_at, o.cancel_reason, " +
            "o.created_at, o.updated_at " +
            "FROM service_order o " +
            "LEFT JOIN sys_user cu ON o.customer_id = cu.id " +
            "LEFT JOIN companion_profile cp ON o.companion_id = cp.id " +
            "LEFT JOIN sys_user co ON cp.user_id = co.id " +
            "WHERE 1=1 " +
            "<if test='status != null and status != \"\"'> AND o.status = #{status}</if>" +
            "<if test='customerId != null'> AND o.customer_id = #{customerId}</if>" +
            "<if test='companionId != null'> AND o.companion_id = #{companionId}</if>" +
            " ORDER BY o.created_at DESC" +
            "</script>")
    List<OrderDetailVO> selectForAdmin(@Param("status") String status,
                                       @Param("customerId") Long customerId,
                                       @Param("companionId") Long companionId);

    /** 更新状态和拒绝原因 */
    @Update("UPDATE service_order SET status = #{status}, cancel_reason = #{cancelReason} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status,
                     @Param("cancelReason") String cancelReason);
}
