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

    String COLS = "id, customer_id, service_type, service_date, hospital_name, department, " +
            "requirement, special_notes, ai_summary, preferred_traits, need_pickup, contact_name, " +
            "contact_phone, budget, status, audit_status, audit_remark, preferred_companion_id, created_at, updated_at ";

    @Select("SELECT " + COLS + "FROM service_request WHERE id = #{id}")
    ServiceRequest selectById(@Param("id") Long id);

    @Select("SELECT " + COLS + "FROM service_request WHERE customer_id = #{customerId} " +
            "ORDER BY created_at DESC")
    List<ServiceRequest> selectByCustomerId(@Param("customerId") Long customerId);

    @Insert("INSERT INTO service_request(customer_id, service_type, service_date, hospital_name, " +
            "department, requirement, special_notes, ai_summary, preferred_traits, need_pickup, contact_name, contact_phone, budget, status, audit_status, preferred_companion_id, " +
            "created_at, updated_at) VALUES(#{customerId}, #{serviceType}, #{serviceDate}, " +
            "#{hospitalName}, #{department}, #{requirement}, #{specialNotes}, #{aiSummary}, #{preferredTraits}, #{needPickup}, #{contactName}, " +
            "#{contactPhone}, #{budget}, #{status}, #{auditStatus}, #{preferredCompanionId}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ServiceRequest request);

    @Update("UPDATE service_request SET ai_summary = #{summary} WHERE id = #{id}")
    int updateAiSummary(@Param("id") Long id, @Param("summary") String summary);

    @Update("UPDATE service_request SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);

    @Update("UPDATE service_request SET audit_status = #{auditStatus}, audit_remark = #{remark}, updated_at = NOW() WHERE id = #{id}")
    int updateAuditStatus(@Param("id") Long id, @Param("auditStatus") Integer auditStatus,
                          @Param("remark") String remark);

    @Select("<script>" +
            "SELECT sr.id, sr.customer_id, sr.service_type, sr.service_date, sr.hospital_name, sr.department, " +
            "sr.requirement, sr.special_notes, sr.ai_summary, sr.preferred_traits, sr.need_pickup, sr.contact_name, " +
            "sr.contact_phone, sr.budget, sr.status, sr.audit_status, sr.audit_remark, sr.preferred_companion_id, sr.created_at, sr.updated_at, " +
            "u.nickname AS preferred_companion_name " +
            "FROM service_request sr " +
            "LEFT JOIN companion_profile cp ON sr.preferred_companion_id = cp.id " +
            "LEFT JOIN sys_user u ON cp.user_id = u.id WHERE 1=1" +
            "<if test='status != null and status != \"\"'> AND sr.status = #{status}</if>" +
            "<if test='serviceType != null and serviceType != \"\"'> AND sr.service_type = #{serviceType}</if>" +
            "<if test='auditStatus != null'> AND sr.audit_status = #{auditStatus}</if>" +
            " ORDER BY sr.created_at DESC" +
            "</script>")
    List<ServiceRequest> selectAll(@Param("status") String status,
                                   @Param("serviceType") String serviceType,
                                   @Param("auditStatus") Integer auditStatus);

    /** 陪诊师可申请的需求池：已通过审核、仍待匹配、无订单。附带申请数与当前陪诊师是否已申请。 */
    @Select("<script>" +
            "SELECT sr.id, sr.customer_id, sr.service_type, sr.service_date, sr.hospital_name, sr.department, " +
            "sr.requirement, sr.ai_summary, sr.preferred_traits, sr.need_pickup, sr.budget, sr.status, sr.created_at, " +
            "(SELECT COUNT(*) FROM service_application a WHERE a.request_id = sr.id AND a.status = 'PENDING') AS applicationCount, " +
            "(SELECT a2.status FROM service_application a2 WHERE a2.request_id = sr.id AND a2.companion_id = #{companionId} LIMIT 1) AS myApplicationStatus " +
            "FROM service_request sr " +
            "WHERE sr.audit_status = 1 AND sr.status = 'PENDING' " +
            "AND NOT EXISTS (SELECT 1 FROM service_order o WHERE o.request_id = sr.id) " +
            "<if test='serviceType != null and serviceType != \"\"'> AND sr.service_type = #{serviceType}</if>" +
            "<if test='keyword != null and keyword != \"\"'> AND (sr.hospital_name LIKE CONCAT('%',#{keyword},'%') OR sr.department LIKE CONCAT('%',#{keyword},'%'))</if>" +
            " ORDER BY sr.created_at DESC" +
            "</script>")
    List<com.yipei.entity.RequestPoolVO> selectPool(@Param("companionId") Long companionId,
                                                    @Param("serviceType") String serviceType,
                                                    @Param("keyword") String keyword);

    @Select("SELECT COUNT(*) FROM service_order WHERE request_id = #{requestId}")
    int countOrdersByRequestId(@Param("requestId") Long requestId);
}
