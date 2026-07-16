package com.yipei.mapper;

import com.yipei.entity.CompanionProfile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CompanionProfileMapper {

    @Select("SELECT id, user_id, real_name, avatar, introduction, service_area, service_types, " +
            "experience_years, rating, completed_count, audit_status, created_at, updated_at " +
            "FROM companion_profile WHERE id = #{id}")
    CompanionProfile selectById(@Param("id") Long id);
}
