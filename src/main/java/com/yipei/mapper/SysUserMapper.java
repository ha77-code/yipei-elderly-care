package com.yipei.mapper;

import com.yipei.entity.SysUser;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface SysUserMapper {
    @Select("SELECT id, username, password, nickname, phone, role, status, created_at AS createAt, updated_at AS updateAt FROM sys_user ORDER BY id DESC")
    List<SysUser> selectAll();

    @Select("SELECT id, username, password, nickname, phone, role, status, created_at AS createAt, updated_at AS updateAt FROM sys_user WHERE id = #{id}")
    SysUser selectById(@Param("id") Long id);

    @Select("SELECT id, username, password, nickname, phone, role, status, created_at AS createAt, updated_at AS updateAt FROM sys_user WHERE username = #{username}")
    SysUser selectByUsername(@Param("username") String username);

    @Insert("INSERT INTO sys_user(username, password, nickname, phone, role, status, created_at, updated_at) " +
            "VALUES(#{username}, #{password}, #{nickname}, #{phone}, #{role}, 1, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(SysUser user);

    @Update("UPDATE sys_user SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") int status);

    @Update("<script>" +
            "UPDATE sys_user" +
            "<set>" +
            "<if test='nickname != null'>nickname = #{nickname},</if>" +
            "<if test='phone != null'>phone = #{phone},</if>" +
            "</set>" +
            "WHERE id = #{id}" +
            "</script>")
    int updateUserInfo(@Param("id") Long id,
                       @Param("nickname") String nickname,
                       @Param("phone") String phone);
}
