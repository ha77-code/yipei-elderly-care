package com.yipei.mapper;

import com.yipei.entity.ReportRecord;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ReportRecordMapper {

    @Insert("INSERT INTO report_record(order_id, reporter_id, reason, content, status, created_at) " +
            "VALUES(#{orderId}, #{reporterId}, #{reason}, #{content}, 'PENDING', NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ReportRecord report);

    @Select("<script>" +
            "SELECT id, order_id, reporter_id, reason, content, status, " +
            "handled_by, handled_at, handle_remark, created_at " +
            "FROM report_record WHERE 1=1 " +
            "<if test='status != null and status != \"\"'> AND status = #{status}</if>" +
            " ORDER BY created_at DESC" +
            "</script>")
    List<ReportRecord> selectAll(@Param("status") String status);

    @Select("SELECT id, order_id, reporter_id, reason, content, status, " +
            "handled_by, handled_at, handle_remark, created_at " +
            "FROM report_record WHERE id = #{id}")
    ReportRecord selectById(@Param("id") Long id);

    @Update("UPDATE report_record SET status = #{status}, handled_by = #{handlerId}, " +
            "handled_at = NOW(), handle_remark = #{remark} WHERE id = #{id}")
    int updateHandle(@Param("id") Long id, @Param("status") String status,
                     @Param("handlerId") Long handlerId, @Param("remark") String remark);
}
