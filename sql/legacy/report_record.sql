USE yipei;
-- 投诉记录表，用于保存订单投诉原因、投诉内容、处理状态和处理结果
CREATE TABLE report_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    reporter_id BIGINT NOT NULL,
    reason VARCHAR(100) NOT NULL,
    content VARCHAR(2000) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING待处理 PROCESSING处理中 RESOLVED已处理 REJECTED驳回',
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
