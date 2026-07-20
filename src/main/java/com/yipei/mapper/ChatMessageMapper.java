package com.yipei.mapper;

import com.yipei.entity.ChatMessage;
import com.yipei.entity.ChatMessageVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ChatMessageMapper {

    @Insert("INSERT INTO chat_message(order_id, from_user_id, to_user_id, content, is_read, created_at) " +
            "VALUES(#{orderId}, #{fromUserId}, #{toUserId}, #{content}, 0, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ChatMessage message);

    /** 某订单的完整聊天记录（含发送者昵称/头像） */
    @Select("SELECT m.id, m.order_id, m.from_user_id, u.nickname AS from_name, u.avatar AS from_avatar, " +
            "m.to_user_id, m.content, m.is_read, m.created_at " +
            "FROM chat_message m LEFT JOIN sys_user u ON m.from_user_id = u.id " +
            "WHERE m.order_id = #{orderId} ORDER BY m.created_at ASC, m.id ASC")
    List<ChatMessageVO> selectByOrder(@Param("orderId") Long orderId);

    /** 把发给某用户的、某订单下的未读消息标记为已读 */
    @Update("UPDATE chat_message SET is_read = 1 WHERE order_id = #{orderId} AND to_user_id = #{userId} AND is_read = 0")
    int markRead(@Param("orderId") Long orderId, @Param("userId") Long userId);

    /** 某用户在某订单下的未读数 */
    @Select("SELECT COUNT(*) FROM chat_message WHERE order_id = #{orderId} AND to_user_id = #{userId} AND is_read = 0")
    int countUnreadByOrder(@Param("orderId") Long orderId, @Param("userId") Long userId);

    /** 某用户的总未读数 */
    @Select("SELECT COUNT(*) FROM chat_message WHERE to_user_id = #{userId} AND is_read = 0")
    int countUnreadTotal(@Param("userId") Long userId);
}
