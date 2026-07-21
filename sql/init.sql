-- ============================================================================
-- YiPei 数据库一键初始化脚本（全新克隆后运行此文件即可）
--   mysql -uroot -p < sql/init.sql
--
-- 包含：建库 + 全部表结构（已含所有最新字段）+ 种子账号/演示数据。
-- 可重复执行：表用 CREATE TABLE IF NOT EXISTS，种子数据用 upsert / 存在即跳过。
--
-- 说明：
--   - 全新环境：只需本文件，无需再跑 sql/*.sql 或 sql/migrations/*.sql。
--   - 已有旧库升级：本文件不改动已存在的表结构，请改用 sql/migrations/ 下的增量脚本。
-- 种子账号密码统一为 123456（customer1 / companion1 / admin1）。
-- ============================================================================

CREATE DATABASE IF NOT EXISTS yipei
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE yipei;

CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(100),
    phone VARCHAR(20),
    avatar VARCHAR(500),
    pending_avatar VARCHAR(500),
    avatar_audit_status TINYINT,
    role VARCHAR(20) NOT NULL DEFAULT 'CUSTOMER',
    status TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS companion_profile (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL UNIQUE,
    real_name VARCHAR(100) NOT NULL,
    avatar VARCHAR(500),
    introduction VARCHAR(1000),
    service_area VARCHAR(100),
    service_types VARCHAR(255),
    traits VARCHAR(255),
    experience_years INT NOT NULL DEFAULT 0,
    rating DECIMAL(3,2) NOT NULL DEFAULT 0.00,
    completed_count INT NOT NULL DEFAULT 0,
    audit_status TINYINT NOT NULL DEFAULT 0 COMMENT '0 pending, 1 approved, 2 rejected',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_companion_profile_user
        FOREIGN KEY (user_id) REFERENCES sys_user (id),
    INDEX idx_companion_audit_status (audit_status),
    INDEX idx_companion_service_area (service_area),
    INDEX idx_companion_rating (rating)
);

CREATE TABLE IF NOT EXISTS service_request (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_id BIGINT NOT NULL,
    service_type VARCHAR(50) NOT NULL,
    service_date DATETIME NOT NULL,
    hospital_name VARCHAR(200),
    department VARCHAR(100),
    requirement VARCHAR(2000) NOT NULL,
    special_notes VARCHAR(1000),
    ai_summary VARCHAR(1000),
    preferred_traits VARCHAR(255),
    need_pickup TINYINT NOT NULL DEFAULT 0,
    contact_name VARCHAR(100) NOT NULL,
    contact_phone VARCHAR(20) NOT NULL,
    budget DECIMAL(10,2),
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING, MATCHED, CANCELLED, CLOSED',
    audit_status TINYINT NOT NULL DEFAULT 0 COMMENT '0待审 1通过 2拒绝',
    audit_remark VARCHAR(500),
    preferred_companion_id BIGINT COMMENT '通道B客户指定的陪诊师(companion_profile.id)，审核通过后据此生成订单',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_service_request_customer
        FOREIGN KEY (customer_id) REFERENCES sys_user (id),
    INDEX idx_request_customer (customer_id),
    INDEX idx_request_status_date (status, service_date),
    INDEX idx_request_service_type (service_type)
);

CREATE TABLE IF NOT EXISTS service_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    request_id BIGINT NOT NULL UNIQUE,
    customer_id BIGINT NOT NULL,
    companion_id BIGINT NOT NULL,
    service_price DECIMAL(10,2) NOT NULL,
    platform_fee DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    companion_income DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING_ACCEPT' COMMENT 'PENDING_ACCEPT, ACCEPTED, IN_SERVICE, PENDING_CONFIRM, COMPLETED, CANCELLED, REJECTED, COMPLAINT',
    accepted_at DATETIME,
    started_at DATETIME,
    completed_at DATETIME,
    confirmed_at DATETIME,
    cancelled_at DATETIME,
    cancel_reason VARCHAR(500),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_service_order_request
        FOREIGN KEY (request_id) REFERENCES service_request (id),
    CONSTRAINT fk_service_order_customer
        FOREIGN KEY (customer_id) REFERENCES sys_user (id),
    CONSTRAINT fk_service_order_companion
        FOREIGN KEY (companion_id) REFERENCES companion_profile (id),
    INDEX idx_order_customer_status (customer_id, status),
    INDEX idx_order_companion_status (companion_id, status),
    INDEX idx_order_status_created (status, created_at)
);

CREATE TABLE IF NOT EXISTS order_status_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    from_status VARCHAR(30),
    to_status VARCHAR(30) NOT NULL,
    operator_id BIGINT NOT NULL,
    remark VARCHAR(500),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_order_status_log_order
        FOREIGN KEY (order_id) REFERENCES service_order (id),
    CONSTRAINT fk_order_status_log_operator
        FOREIGN KEY (operator_id) REFERENCES sys_user (id),
    INDEX idx_order_status_log_order (order_id, created_at),
    INDEX idx_order_status_log_operator (operator_id)
);

CREATE TABLE IF NOT EXISTS service_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL UNIQUE,
    content VARCHAR(3000) NOT NULL,
    important_notes VARCHAR(2000),
    completed_by BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_service_record_order
        FOREIGN KEY (order_id) REFERENCES service_order (id),
    CONSTRAINT fk_service_record_completed_by
        FOREIGN KEY (completed_by) REFERENCES sys_user (id),
    INDEX idx_service_record_completed_by (completed_by)
);

CREATE TABLE IF NOT EXISTS evaluation (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    from_user_id BIGINT NOT NULL,
    to_user_id BIGINT NOT NULL,
    score TINYINT NOT NULL,
    content VARCHAR(1000),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_evaluation_order
        FOREIGN KEY (order_id) REFERENCES service_order (id),
    CONSTRAINT fk_evaluation_from_user
        FOREIGN KEY (from_user_id) REFERENCES sys_user (id),
    CONSTRAINT fk_evaluation_to_user
        FOREIGN KEY (to_user_id) REFERENCES sys_user (id),
    UNIQUE KEY uk_evaluation_order_from_user (order_id, from_user_id),
    INDEX idx_evaluation_to_user (to_user_id)
);

CREATE TABLE IF NOT EXISTS audit_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    business_type VARCHAR(50) NOT NULL COMMENT 'Business type, such as COMPANION_PROFILE',
    business_id BIGINT NOT NULL,
    auditor_id BIGINT NOT NULL,
    audit_status TINYINT NOT NULL COMMENT '1 approved, 2 rejected',
    remark VARCHAR(1000),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_audit_record_auditor
        FOREIGN KEY (auditor_id) REFERENCES sys_user (id),
    INDEX idx_audit_business (business_type, business_id),
    INDEX idx_audit_auditor (auditor_id)
);

CREATE TABLE IF NOT EXISTS report_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    reporter_id BIGINT NOT NULL,
    reason VARCHAR(100) NOT NULL,
    content VARCHAR(2000) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING, PROCESSING, RESOLVED, REJECTED',
    handled_by BIGINT,
    handled_at DATETIME,
    handle_remark VARCHAR(1000),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_report_record_order
        FOREIGN KEY (order_id) REFERENCES service_order (id),
    CONSTRAINT fk_report_record_reporter
        FOREIGN KEY (reporter_id) REFERENCES sys_user (id),
    CONSTRAINT fk_report_record_handler
        FOREIGN KEY (handled_by) REFERENCES sys_user (id),
    INDEX idx_report_status_created (status, created_at),
    INDEX idx_report_order (order_id),
    INDEX idx_report_reporter (reporter_id)
);

CREATE TABLE IF NOT EXISTS service_application (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    request_id BIGINT NOT NULL,
    companion_id BIGINT NOT NULL COMMENT 'companion_profile.id',
    message VARCHAR(500),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING, ACCEPTED, REJECTED, WITHDRAWN',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_application_request
        FOREIGN KEY (request_id) REFERENCES service_request (id),
    CONSTRAINT fk_application_companion
        FOREIGN KEY (companion_id) REFERENCES companion_profile (id),
    UNIQUE KEY uk_application_request_companion (request_id, companion_id),
    INDEX idx_application_request (request_id, status),
    INDEX idx_application_companion (companion_id, status)
);

CREATE TABLE IF NOT EXISTS chat_message (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    from_user_id BIGINT NOT NULL,
    to_user_id BIGINT NOT NULL,
    content VARCHAR(1000) NOT NULL,
    is_read TINYINT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_chat_order
        FOREIGN KEY (order_id) REFERENCES service_order (id),
    CONSTRAINT fk_chat_from
        FOREIGN KEY (from_user_id) REFERENCES sys_user (id),
    CONSTRAINT fk_chat_to
        FOREIGN KEY (to_user_id) REFERENCES sys_user (id),
    INDEX idx_chat_order (order_id, created_at),
    INDEX idx_chat_to_unread (to_user_id, is_read)
);

-- Password for all seed users: 123456
-- Format matches com.yipei.util.PasswordUtil: base64(salt):base64(sha256(salt + rawPassword)).
CREATE TABLE IF NOT EXISTS user_notification (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    type VARCHAR(30) NOT NULL,
    title VARCHAR(100) NOT NULL,
    content VARCHAR(500) NOT NULL,
    related_id BIGINT,
    is_read TINYINT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notification_user FOREIGN KEY (user_id) REFERENCES sys_user (id),
    INDEX idx_notification_user_read (user_id, is_read, created_at)
);

INSERT INTO sys_user(username, password, nickname, phone, role, status, created_at, updated_at)
VALUES
    ('customer1', 'eWlwZWktY3VzdG9tZXItMQ==:eJtfcMKaWA9YU1earQo/dYCpEXeKwJRa2zONf8N8pa8=', 'Zhang San', '13800000001', 'CUSTOMER', 1, NOW(), NOW()),
    ('companion1', 'eWlwZWktY29tcGFuaW9u:GJrEqXXIMUMpvW6j0iT5UGZf86Q2DXpSsXONwJ1Hqyo=', 'Li Peizhen', '13800000002', 'COMPANION', 1, NOW(), NOW()),
    ('admin1', 'eWlwZWktYWRtaW4tMDAw:+TZrSOeqcqAYBARD1OBrheJbXWC4T2Mt9RWFndsMtyo=', 'Admin', '13800000003', 'ADMIN', 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE
    password = VALUES(password),
    nickname = VALUES(nickname),
    phone = VALUES(phone),
    role = VALUES(role),
    status = VALUES(status),
    updated_at = NOW();
-- Quick-experience companion profile. This is inserted only when missing so it does not overwrite local edits.
INSERT INTO companion_profile(
    user_id, real_name, avatar, introduction, service_area, service_types, traits,
    experience_years, rating, completed_count, audit_status, created_at, updated_at
)
SELECT
    u.id, 'Li Peizhen', NULL,
    'Experienced companion for outpatient visits, inpatient care, and examinations.',
    'Beijing Chaoyang District', 'Outpatient,Inpatient,Examination', 'patient,communicative,experienced',
    5, 4.80, 12, 1, NOW(), NOW()
FROM sys_user u
WHERE u.username = 'companion1'
ON DUPLICATE KEY UPDATE user_id = companion_profile.user_id;
