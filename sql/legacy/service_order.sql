USE yipei;
-- 服务订单表，用于保存服务需求匹配后的客户、陪诊师、价格、收入和订单状态信息
CREATE TABLE service_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    request_id BIGINT NOT NULL UNIQUE,
    customer_id BIGINT NOT NULL,
    companion_id BIGINT NOT NULL,
    service_price DECIMAL(10,2) NOT NULL,
    platform_fee DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    companion_income DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING_ACCEPT' COMMENT 'PENDING_ACCEPT待接单 ACCEPTED已接单 IN_SERVICE服务中 PENDING_CONFIRM待确认 COMPLETED已完成 CANCELLED已取消 REJECTED已拒绝 COMPLAINT已投诉',
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
