package com.yipei.mapper;

import com.yipei.entity.ReportRecord;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

@Mapper
public interface ReportRecordMapper {

    @Insert("INSERT INTO report_record(order_id, reporter_id, reason, content, status, created_at) " +
            "VALUES(#{orderId}, #{reporterId}, #{reason}, #{content}, 'PENDING', NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ReportRecord report);
}
