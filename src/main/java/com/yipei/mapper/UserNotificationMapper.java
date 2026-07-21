package com.yipei.mapper;

import com.yipei.entity.UserNotification;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface UserNotificationMapper {

    @Insert("INSERT INTO user_notification(user_id, type, title, content, related_id, is_read, created_at) " +
            "VALUES(#{userId}, #{type}, #{title}, #{content}, #{relatedId}, 0, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UserNotification notification);

    @Select("SELECT id, user_id, type, title, content, related_id, is_read, created_at " +
            "FROM user_notification WHERE user_id = #{userId} ORDER BY created_at DESC, id DESC LIMIT 100")
    List<UserNotification> selectByUser(@Param("userId") Long userId);

    @Select("SELECT COUNT(*) FROM user_notification WHERE user_id = #{userId} AND is_read = 0")
    int countUnread(@Param("userId") Long userId);

    @Update("UPDATE user_notification SET is_read = 1 WHERE id = #{id} AND user_id = #{userId}")
    int markRead(@Param("id") Long id, @Param("userId") Long userId);

    @Update("UPDATE user_notification SET is_read = 1 WHERE user_id = #{userId} AND is_read = 0")
    int markAllRead(@Param("userId") Long userId);
}
