USE yipei;
#评价表，用于保存客户和陪诊师对服务参与方的评分及评价内容
CREATE TABLE evaluation (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    from_user_id BIGINT NOT NULL,
    to_user_id BIGINT NOT NULL,
    score TINYINT NOT NULL,
    content VARCHAR(1000),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_evaluation_score CHECK (score BETWEEN 1 AND 5),
    CONSTRAINT fk_evaluation_order
        FOREIGN KEY (order_id) REFERENCES service_order (id),
    CONSTRAINT fk_evaluation_from_user
        FOREIGN KEY (from_user_id) REFERENCES sys_user (id),
    CONSTRAINT fk_evaluation_to_user
        FOREIGN KEY (to_user_id) REFERENCES sys_user (id),
    UNIQUE KEY uk_evaluation_order_from_user (order_id, from_user_id),
    INDEX idx_evaluation_to_user (to_user_id)
);
