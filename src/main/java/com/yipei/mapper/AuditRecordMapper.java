package com.yipei.mapper;

import com.yipei.entity.AuditRecord;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

@Mapper
public interface AuditRecordMapper {

    @Insert("INSERT INTO audit_record(business_type, business_id, auditor_id, audit_status, remark, created_at) " +
            "VALUES(#{businessType}, #{businessId}, #{auditorId}, #{auditStatus}, #{remark}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(AuditRecord record);
}
