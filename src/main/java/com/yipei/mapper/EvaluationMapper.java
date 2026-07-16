package com.yipei.mapper;

import com.yipei.entity.Evaluation;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface EvaluationMapper {

    @Insert("INSERT INTO evaluation(order_id, from_user_id, to_user_id, score, content, created_at) " +
            "VALUES(#{orderId}, #{fromUserId}, #{toUserId}, #{score}, #{content}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Evaluation evaluation);

    @Select("SELECT id, order_id, from_user_id, to_user_id, score, content, created_at " +
            "FROM evaluation WHERE order_id = #{orderId} ORDER BY created_at DESC")
    List<Evaluation> selectByOrderId(@Param("orderId") Long orderId);

    /** 查陪诊师（companion_profile.user_id）收到的评价 */
    @Select("SELECT e.id, e.order_id, e.from_user_id, e.to_user_id, e.score, e.content, e.created_at " +
            "FROM evaluation e " +
            "JOIN companion_profile cp ON e.to_user_id = cp.user_id " +
            "WHERE cp.id = #{companionId} ORDER BY e.created_at DESC")
    List<Evaluation> selectByCompanionId(@Param("companionId") Long companionId);

    @Select("SELECT COUNT(*) FROM evaluation WHERE order_id = #{orderId} AND from_user_id = #{userId}")
    int countByOrderAndUser(@Param("orderId") Long orderId, @Param("userId") Long userId);

    /** 计算某个用户（sys_user.id）作为被评价人收到的平均分 */
    @Select("SELECT COALESCE(AVG(score), 0) FROM evaluation WHERE to_user_id = #{userId}")
    double avgScoreByUserId(@Param("userId") Long userId);
}
