package com.yipei.mapper;

import com.yipei.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysUserMapper {
    @Select("SELECT id, username, password, nickname, phone, role, status, created_at AS createAt, updated_at AS updateAt FROM sys_user ORDER BY id DESC")
    List<SysUser> selectAll();
}
