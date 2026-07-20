package com.yipei.mapper;

import com.yipei.entity.ChatConversationVO;
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

    /**
     * 某用户的会话列表：其作为客户或陪诊师参与、且聊天可用（撮合达成后）的订单，
     * 附带对方信息、最近一条消息与未读数。有消息或聊天进行中的会话优先、按最近消息倒序。
     */
    @Select("SELECT o.id AS orderId, o.status AS orderStatus, " +
            "cu.id AS counterpartUserId, cu.nickname AS counterpartName, cu.avatar AS counterpartAvatar, " +
            "sr.hospital_name AS hospitalName, sr.service_type AS serviceType, sr.service_date AS serviceDate, " +
            "lm.content AS lastMessage, lm.created_at AS lastTime, " +
            "(SELECT COUNT(*) FROM chat_message m WHERE m.order_id = o.id AND m.to_user_id = #{userId} AND m.is_read = 0) AS unreadCount " +
            "FROM service_order o " +
            "LEFT JOIN companion_profile cp ON o.companion_id = cp.id " +
            "LEFT JOIN sys_user cu ON cu.id = CASE WHEN o.customer_id = #{userId} THEN cp.user_id ELSE o.customer_id END " +
            "LEFT JOIN service_request sr ON o.request_id = sr.id " +
            "LEFT JOIN chat_message lm ON lm.id = " +
            "(SELECT m2.id FROM chat_message m2 WHERE m2.order_id = o.id ORDER BY m2.created_at DESC, m2.id DESC LIMIT 1) " +
            "WHERE (o.customer_id = #{userId} OR cp.user_id = #{userId}) " +
            "AND o.status IN ('ACCEPTED','IN_SERVICE','PENDING_CONFIRM','COMPLETED','CANCELLED','COMPLAINT') " +
            "ORDER BY (lm.created_at IS NULL) ASC, lm.created_at DESC, o.id DESC")
    List<ChatConversationVO> selectConversations(@Param("userId") Long userId);
}
