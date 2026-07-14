USE yipei;
#陪诊师资料表，用于保存陪诊师姓名、头像、介绍、服务区域、服务类型、经验、评分和审核状态
CREATE TABLE companion_profile (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL UNIQUE,
    real_name VARCHAR(100) NOT NULL,
    avatar VARCHAR(500),
    introduction VARCHAR(1000),
    service_area VARCHAR(100),
    service_types VARCHAR(255),
    experience_years INT NOT NULL DEFAULT 0,
    rating DECIMAL(3,2) NOT NULL DEFAULT 0.00,
    completed_count INT NOT NULL DEFAULT 0,
    audit_status TINYINT NOT NULL DEFAULT 0 COMMENT '0待审核 1通过 2拒绝',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_companion_profile_user
        FOREIGN KEY (user_id) REFERENCES sys_user (id),
    INDEX idx_companion_audit_status (audit_status),
    INDEX idx_companion_service_area (service_area),
    INDEX idx_companion_rating (rating)
);
