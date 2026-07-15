USE yipei;
-- 系统用户表，用于保存老人、家属、陪诊师、管理员的账号信息、密码、角色和状态等
CREATE TABLE sys_user (
                          id BIGINT PRIMARY KEY AUTO_INCREMENT,
                          username VARCHAR(50) NOT NULL UNIQUE,
                          password VARCHAR(255) NOT NULL,
                          nickname VARCHAR(100),
                          phone VARCHAR(20),
                          role VARCHAR(20) NOT NULL DEFAULT 'CUSTOMER',
                          status TINYINT NOT NULL DEFAULT 1,
                          created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                          updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
