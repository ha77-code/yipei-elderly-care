-- ============================================================================
-- 撮合流程 + 私信聊天：需求审核字段 + 申请表 + 聊天表
-- 适用于已有旧库的增量升级；全新库请直接用 sql/init.sql（已含以下内容）。
-- 可重复执行。
-- ============================================================================
USE yipei;

-- 1) service_request 增加审核字段：audit_status(0待审/1通过/2拒绝) + audit_remark
SET @c := (SELECT COUNT(*) FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = 'service_request' AND column_name = 'audit_status');
SET @s := IF(@c = 0,
    'ALTER TABLE service_request ADD COLUMN audit_status TINYINT NOT NULL DEFAULT 0 COMMENT ''0待审 1通过 2拒绝'' AFTER status',
    'SELECT 1');
PREPARE st FROM @s; EXECUTE st; DEALLOCATE PREPARE st;

SET @c := (SELECT COUNT(*) FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = 'service_request' AND column_name = 'audit_remark');
SET @s := IF(@c = 0,
    'ALTER TABLE service_request ADD COLUMN audit_remark VARCHAR(500) AFTER audit_status',
    'SELECT 1');
PREPARE st FROM @s; EXECUTE st; DEALLOCATE PREPARE st;

-- 指定下单（通道B）时客户选定的陪诊师，审核通过后据此自动生成订单；广场需求为 NULL
SET @c := (SELECT COUNT(*) FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = 'service_request' AND column_name = 'preferred_companion_id');
SET @s := IF(@c = 0,
    'ALTER TABLE service_request ADD COLUMN preferred_companion_id BIGINT AFTER audit_remark',
    'SELECT 1');
PREPARE st FROM @s; EXECUTE st; DEALLOCATE PREPARE st;

-- 已有历史需求视为已通过审核，避免升级后旧需求全部卡在待审
UPDATE service_request SET audit_status = 1 WHERE audit_status = 0 AND status <> 'PENDING';

-- 2) 陪诊师申请表：一个陪诊师对一个需求只能申请一次
CREATE TABLE IF NOT EXISTS service_application (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    request_id BIGINT NOT NULL,
    companion_id BIGINT NOT NULL COMMENT 'companion_profile.id',
    message VARCHAR(500),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING, ACCEPTED, REJECTED, WITHDRAWN',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_application_request FOREIGN KEY (request_id) REFERENCES service_request (id),
    CONSTRAINT fk_application_companion FOREIGN KEY (companion_id) REFERENCES companion_profile (id),
    UNIQUE KEY uk_application_request_companion (request_id, companion_id),
    INDEX idx_application_request (request_id, status),
    INDEX idx_application_companion (companion_id, status)
);

-- 3) 私信聊天表：绑定订单，服务结束后只读历史
CREATE TABLE IF NOT EXISTS chat_message (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    from_user_id BIGINT NOT NULL,
    to_user_id BIGINT NOT NULL,
    content VARCHAR(1000) NOT NULL,
    is_read TINYINT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_chat_order FOREIGN KEY (order_id) REFERENCES service_order (id),
    CONSTRAINT fk_chat_from FOREIGN KEY (from_user_id) REFERENCES sys_user (id),
    CONSTRAINT fk_chat_to FOREIGN KEY (to_user_id) REFERENCES sys_user (id),
    INDEX idx_chat_order (order_id, created_at),
    INDEX idx_chat_to_unread (to_user_id, is_read)
);
