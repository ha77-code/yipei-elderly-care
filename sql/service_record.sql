USE yipei;

CREATE TABLE service_record (
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
