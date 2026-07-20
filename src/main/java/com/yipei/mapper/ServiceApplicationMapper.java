package com.yipei.mapper;

import com.yipei.entity.ApplicationVO;
import com.yipei.entity.ServiceApplication;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ServiceApplicationMapper {

    @Insert("INSERT INTO service_application(request_id, companion_id, message, status, created_at, updated_at) " +
            "VALUES(#{requestId}, #{companionId}, #{message}, #{status}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ServiceApplication application);

    @Select("SELECT id, request_id, companion_id, message, status, created_at, updated_at " +
            "FROM service_application WHERE id = #{id}")
    ServiceApplication selectById(@Param("id") Long id);

    @Select("SELECT id, request_id, companion_id, message, status, created_at, updated_at " +
            "FROM service_application WHERE request_id = #{requestId} AND companion_id = #{companionId}")
    ServiceApplication selectByRequestAndCompanion(@Param("requestId") Long requestId,
                                                   @Param("companionId") Long companionId);

    @Update("UPDATE service_application SET status = #{status}, updated_at = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);

    @Update("UPDATE service_application SET message = #{message}, status = 'PENDING', updated_at = NOW() WHERE id = #{id}")
    int resubmit(@Param("id") Long id, @Param("message") String message);

    /** 接受某申请时，把同一需求下其他 PENDING 申请置为 REJECTED */
    @Update("UPDATE service_application SET status = 'REJECTED', updated_at = NOW() " +
            "WHERE request_id = #{requestId} AND id <> #{acceptedId} AND status = 'PENDING'")
    int rejectOthers(@Param("requestId") Long requestId, @Param("acceptedId") Long acceptedId);

    /** 客户查看某需求的申请列表（含陪诊师资料） */
    @Select("SELECT a.id, a.request_id, a.companion_id, a.message, a.status, a.created_at, " +
            "cp.user_id AS companion_user_id, u.nickname AS companion_name, u.avatar AS companion_avatar, " +
            "cp.introduction, cp.service_area, cp.service_types, cp.experience_years, cp.rating, cp.completed_count " +
            "FROM service_application a " +
            "JOIN companion_profile cp ON a.companion_id = cp.id " +
            "JOIN sys_user u ON cp.user_id = u.id " +
            "WHERE a.request_id = #{requestId} " +
            "ORDER BY FIELD(a.status,'ACCEPTED','PENDING','REJECTED','WITHDRAWN'), a.created_at DESC")
    List<ApplicationVO> selectByRequest(@Param("requestId") Long requestId);

    /** 陪诊师查看自己的申请列表（含需求信息） */
    @Select("SELECT a.id, a.request_id, a.companion_id, a.message, a.status, a.created_at, " +
            "sr.service_type, sr.hospital_name, sr.department, sr.service_date, sr.budget, sr.status AS request_status " +
            "FROM service_application a " +
            "JOIN service_request sr ON a.request_id = sr.id " +
            "WHERE a.companion_id = #{companionId} " +
            "ORDER BY a.created_at DESC")
    List<ApplicationVO> selectByCompanion(@Param("companionId") Long companionId);
}
