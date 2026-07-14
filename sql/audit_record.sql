USE yipei;

CREATE TABLE audit_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    business_type VARCHAR(50) NOT NULL COMMENT 'COMPANION_PROFILE等业务类型',
    business_id BIGINT NOT NULL,
    auditor_id BIGINT NOT NULL,
    audit_status TINYINT NOT NULL COMMENT '1通过 2拒绝',
    remark VARCHAR(1000),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_audit_record_auditor
        FOREIGN KEY (auditor_id) REFERENCES sys_user (id),
    INDEX idx_audit_business (business_type, business_id),
    INDEX idx_audit_auditor (auditor_id)
);
