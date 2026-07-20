USE yipei;
-- 陪诊需求表，保存老人或家属发布的服务需求，例如医院、科室、服务日期、需求内容、预算和联系人
CREATE TABLE service_request (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_id BIGINT NOT NULL,
    service_type VARCHAR(50) NOT NULL,
    service_date DATETIME NOT NULL,
    hospital_name VARCHAR(200),
    department VARCHAR(100),
    requirement VARCHAR(2000) NOT NULL,
    special_notes VARCHAR(1000),
    ai_summary VARCHAR(1000),
    contact_name VARCHAR(100) NOT NULL,
    contact_phone VARCHAR(20) NOT NULL,
    budget DECIMAL(10,2),
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING待处理 MATCHED已匹配 CANCELLED已取消 CLOSED已关闭',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_service_request_customer
        FOREIGN KEY (customer_id) REFERENCES sys_user (id),
    INDEX idx_request_customer (customer_id),
    INDEX idx_request_status_date (status, service_date),
    INDEX idx_request_service_type (service_type)
);
