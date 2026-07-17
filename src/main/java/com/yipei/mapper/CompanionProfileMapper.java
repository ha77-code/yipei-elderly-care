package com.yipei.mapper;

import com.yipei.entity.CompanionProfile;
import com.yipei.entity.CompanionVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface CompanionProfileMapper {

    String SELECT_COLS = "id, user_id, real_name, avatar, introduction, service_area, service_types, " +
            "experience_years, rating, completed_count, traits, audit_status, created_at, updated_at ";

    @Select("SELECT " + SELECT_COLS + "FROM companion_profile WHERE id = #{id}")
    CompanionProfile selectById(@Param("id") Long id);

    @Select("SELECT " + SELECT_COLS + "FROM companion_profile WHERE user_id = #{userId}")
    CompanionProfile selectByUserId(@Param("userId") Long userId);

    @Select("<script>" +
            "SELECT cp.id, cp.user_id, cp.real_name, cp.avatar, cp.introduction, cp.service_area, " +
            "cp.service_types, cp.experience_years, cp.rating, cp.completed_count, cp.traits, cp.audit_status, " +
            "u.nickname, u.phone, cp.created_at " +
            "FROM companion_profile cp " +
            "LEFT JOIN sys_user u ON cp.user_id = u.id " +
            "WHERE cp.audit_status = 1 " +
            "<if test='serviceArea != null and serviceArea != \"\"'> AND cp.service_area LIKE CONCAT('%',#{serviceArea},'%')</if>" +
            "<if test='serviceType != null and serviceType != \"\"'> AND cp.service_types LIKE CONCAT('%',#{serviceType},'%')</if>" +
            "<if test='traits != null and traits != \"\"'> AND cp.traits IS NOT NULL AND cp.traits != ''</if>" +
            " ORDER BY cp.rating DESC, cp.completed_count DESC" +
            "</script>")
    List<CompanionVO> selectApproved(@Param("serviceArea") String serviceArea,
                                     @Param("serviceType") String serviceType,
                                     @Param("traits") String traits);

    @Select("SELECT " + SELECT_COLS + "FROM companion_profile WHERE audit_status = 0 ORDER BY created_at DESC")
    List<CompanionProfile> selectPending();

    @Insert("INSERT INTO companion_profile(user_id, real_name, avatar, introduction, service_area, " +
            "service_types, experience_years, traits, audit_status, created_at, updated_at) " +
            "VALUES(#{userId}, #{realName}, #{avatar}, #{introduction}, #{serviceArea}, " +
            "#{serviceTypes}, #{experienceYears}, #{traits}, 0, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(CompanionProfile profile);

    @Update("UPDATE companion_profile SET real_name = #{realName}, avatar = #{avatar}, " +
            "introduction = #{introduction}, service_area = #{serviceArea}, service_types = #{serviceTypes}, " +
            "traits = #{traits}, experience_years = #{experienceYears}, audit_status = 0, updated_at = NOW() " +
            "WHERE id = #{id}")
    int update(@Param("id") Long id, @Param("realName") String realName, @Param("avatar") String avatar,
               @Param("introduction") String introduction, @Param("serviceArea") String serviceArea,
               @Param("serviceTypes") String serviceTypes, @Param("traits") String traits,
               @Param("experienceYears") Integer experienceYears);

    @Update("UPDATE companion_profile SET audit_status = #{auditStatus}, updated_at = NOW() WHERE id = #{id}")
    int updateAuditStatus(@Param("id") Long id, @Param("auditStatus") Integer auditStatus);

    @Update("UPDATE companion_profile SET rating = #{rating} WHERE id = #{id}")
    int updateRating(@Param("id") Long id, @Param("rating") double rating);
}
