USE yipei;
-- 订单状态记录表，用于保存订单状态变化、操作人、操作时间和变更备注
CREATE TABLE order_status_log (
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
