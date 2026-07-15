package com.yipei.mapper;

import com.yipei.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface SysUserMapper {
    @Select("SELECT id, username, nickname, phone, role, status, created_at AS createAt, updated_at AS updateAt FROM sys_user ORDER BY id DESC")
    List<SysUser> selectAll();

    @Select("SELECT id, username, nickname, phone, role, status, created_at AS createAt, updated_at AS updateAt FROM sys_user WHERE id = #{id}")
    SysUser selectById(@Param("id") Long id);

    @Update("UPDATE sys_user SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") int status);
}
