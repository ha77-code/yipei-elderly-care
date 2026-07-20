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
    String COLS = "id, username, password, nickname, phone, avatar, pending_avatar AS pendingAvatar, " +
            "avatar_audit_status AS avatarAuditStatus, role, status, created_at AS createAt, updated_at AS updateAt ";

    @Select("SELECT " + COLS + "FROM sys_user ORDER BY id DESC")
    List<SysUser> selectAll();

    @Select("SELECT " + COLS + "FROM sys_user WHERE id = #{id}")
    SysUser selectById(@Param("id") Long id);

    @Select("SELECT " + COLS + "FROM sys_user WHERE username = #{username}")
    SysUser selectByUsername(@Param("username") String username);

    @Select("SELECT id FROM sys_user WHERE role = #{role} AND status = 1 ORDER BY id")
    List<Long> selectActiveIdsByRole(@Param("role") String role);

    /** 待审核头像的用户列表 */
    @Select("SELECT " + COLS + "FROM sys_user WHERE avatar_audit_status = 0 ORDER BY updated_at DESC")
    List<SysUser> selectPendingAvatars();

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
            "<if test='avatar != null'>avatar = #{avatar},</if>" +
            "</set>" +
            "WHERE id = #{id}" +
            "</script>")
    int updateUserInfo(@Param("id") Long id,
                       @Param("nickname") String nickname,
                       @Param("phone") String phone,
                       @Param("avatar") String avatar);

    /** 提交新头像进入待审核：写入 pending_avatar，状态置 0，不动已展示的 avatar */
    @Update("UPDATE sys_user SET pending_avatar = #{avatar}, avatar_audit_status = 0 WHERE id = #{id}")
    int updatePendingAvatar(@Param("id") Long id, @Param("avatar") String avatar);

    /** 审核通过：pending_avatar 搬迁为正式 avatar，清空待审，状态置 1 */
    @Update("UPDATE sys_user SET avatar = pending_avatar, pending_avatar = NULL, avatar_audit_status = 1 WHERE id = #{id}")
    int approveAvatar(@Param("id") Long id);

    /** 审核拒绝：清空待审头像，状态置 2，保留原 avatar */
    @Update("UPDATE sys_user SET pending_avatar = NULL, avatar_audit_status = 2 WHERE id = #{id}")
    int rejectAvatar(@Param("id") Long id);

    @Update("UPDATE sys_user SET password = #{password} WHERE id = #{id}")
    int updatePassword(@Param("id") Long id, @Param("password") String password);
}
