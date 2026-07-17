-- ============================================================================
-- 医陪平台 - 演示测试数据（纯数据插入，不修改表结构）
-- ============================================================================
-- 前置条件：已执行 sql/init.sql 创建数据库和表
-- 所有测试账号密码均为：123456
-- 安全运行：可重复执行，使用 ON DUPLICATE KEY UPDATE 避免重复插入
-- ============================================================================

USE yipei;

-- 设置客户端字符集为 utf8mb4，避免中文乱码和数据插入报错
SET NAMES utf8mb4;

-- 通用密码哈希（密码均为 123456）
SET @pwd = 'eWlwZWktY3VzdG9tZXItMQ==:eJtfcMKaWA9YU1earQo/dYCpEXeKwJRa2zONf8N8pa8=';

-- ============================================================================
-- 第一部分：测试用户账号
-- ============================================================================

-- 1.1 普通用户 CUSTOMER（老人/家属），共8个
INSERT INTO sys_user(username, password, nickname, phone, role, status, created_at, updated_at) VALUES
('customer1', @pwd, '张三',   '13800000001', 'CUSTOMER', 1, '2025-06-01 08:00:00', '2025-06-01 08:00:00'),
('customer2', @pwd, '李四',   '13800000002', 'CUSTOMER', 1, '2025-06-05 10:30:00', '2025-06-05 10:30:00'),
('customer3', @pwd, '王五',   '13800000003', 'CUSTOMER', 1, '2025-06-10 14:00:00', '2025-06-10 14:00:00'),
('customer4', @pwd, '赵六',   '13800000004', 'CUSTOMER', 1, '2025-06-15 09:20:00', '2025-06-15 09:20:00'),
('customer5', @pwd, '孙七',   '13800000005', 'CUSTOMER', 1, '2025-07-01 16:00:00', '2025-07-01 16:00:00'),
('elder01',   @pwd, '刘奶奶', '13900000001', 'CUSTOMER', 1, '2025-05-01 07:00:00', '2025-05-01 07:00:00'),
('elder02',   @pwd, '陈爷爷', '13900000002', 'CUSTOMER', 1, '2025-05-10 08:30:00', '2025-05-10 08:30:00'),
('family01',  @pwd, '周先生', '13900000003', 'CUSTOMER', 1, '2025-06-20 11:00:00', '2025-06-20 11:00:00')
ON DUPLICATE KEY UPDATE password=VALUES(password), nickname=VALUES(nickname), phone=VALUES(phone), role=VALUES(role), status=VALUES(status), updated_at=VALUES(updated_at);

-- 1.2 陪诊师 COMPANION，共5个
INSERT INTO sys_user(username, password, nickname, phone, role, status, created_at, updated_at) VALUES
('companion1', @pwd, '李陪诊', '13600000001', 'COMPANION', 1, '2025-05-15 09:00:00', '2025-05-15 09:00:00'),
('companion2', @pwd, '王小明', '13600000002', 'COMPANION', 1, '2025-05-20 10:00:00', '2025-05-20 10:00:00'),
('companion3', @pwd, '陈丽丽', '13600000003', 'COMPANION', 1, '2025-06-01 11:00:00', '2025-06-01 11:00:00'),
('companion4', @pwd, '刘建国', '13600000004', 'COMPANION', 1, '2025-06-10 14:00:00', '2025-06-10 14:00:00'),
('companion5', @pwd, '周敏',   '13600000005', 'COMPANION', 1, '2025-07-01 08:00:00', '2025-07-01 08:00:00')
ON DUPLICATE KEY UPDATE password=VALUES(password), nickname=VALUES(nickname), phone=VALUES(phone), role=VALUES(role), status=VALUES(status), updated_at=VALUES(updated_at);

-- 1.3 管理员 ADMIN，共2个
INSERT INTO sys_user(username, password, nickname, phone, role, status, created_at, updated_at) VALUES
('admin1', @pwd, '系统管理员', '13700000001', 'ADMIN', 1, '2025-01-01 00:00:00', '2025-01-01 00:00:00'),
('admin2', @pwd, '审核管理员', '13700000002', 'ADMIN', 1, '2025-03-01 10:00:00', '2025-03-01 10:00:00')
ON DUPLICATE KEY UPDATE password=VALUES(password), nickname=VALUES(nickname), phone=VALUES(phone), role=VALUES(role), status=VALUES(status), updated_at=VALUES(updated_at);


-- ============================================================================
-- 第二部分：陪诊师资料
-- ============================================================================

INSERT INTO companion_profile(user_id, real_name, avatar, introduction, service_area, service_types, experience_years, rating, completed_count, audit_status, created_at, updated_at)
SELECT su.id, '李小华', '/uploads/avatars/companion1.jpg',
       '从事陪诊服务5年，熟悉北京各大医院就诊流程，曾帮助数百位老年患者完成就医。耐心细致，擅长与老年人沟通。',
       '北京市朝阳区', '陪诊,取药,挂号,住院办理',
       5, 4.80, 156, 1, '2025-05-15 09:00:00', '2025-05-15 09:00:00'
FROM sys_user su WHERE su.username = 'companion1'
ON DUPLICATE KEY UPDATE real_name=VALUES(real_name), introduction=VALUES(introduction), service_area=VALUES(service_area), service_types=VALUES(service_types), experience_years=VALUES(experience_years), rating=VALUES(rating), completed_count=VALUES(completed_count), audit_status=VALUES(audit_status), updated_at=VALUES(updated_at);

INSERT INTO companion_profile(user_id, real_name, avatar, introduction, service_area, service_types, experience_years, rating, completed_count, audit_status, created_at, updated_at)
SELECT su.id, '王小明', '/uploads/avatars/companion2.jpg',
       '3年三甲医院陪诊经验，服务过200+客户。熟悉协和、301等医院挂号及就诊流程，擅长急症陪诊和检查引导。',
       '北京市海淀区', '陪诊,挂号,检查,急诊陪同',
       3, 4.50, 98, 1, '2025-05-20 10:00:00', '2025-05-20 10:00:00'
FROM sys_user su WHERE su.username = 'companion2'
ON DUPLICATE KEY UPDATE real_name=VALUES(real_name), introduction=VALUES(introduction), service_area=VALUES(service_area), service_types=VALUES(service_types), experience_years=VALUES(experience_years), rating=VALUES(rating), completed_count=VALUES(completed_count), audit_status=VALUES(audit_status), updated_at=VALUES(updated_at);

INSERT INTO companion_profile(user_id, real_name, avatar, introduction, service_area, service_types, experience_years, rating, completed_count, audit_status, created_at, updated_at)
SELECT su.id, '陈丽丽', '/uploads/avatars/companion3.jpg',
       '资深护理专业毕业，2年陪诊经验。温柔细心，擅长儿童就医陪护及慢性病患者长期跟诊管理。',
       '北京市西城区', '陪诊,取药,检查,慢性病管理',
       2, 4.20, 45, 1, '2025-06-01 11:00:00', '2025-06-01 11:00:00'
FROM sys_user su WHERE su.username = 'companion3'
ON DUPLICATE KEY UPDATE real_name=VALUES(real_name), introduction=VALUES(introduction), service_area=VALUES(service_area), service_types=VALUES(service_types), experience_years=VALUES(experience_years), rating=VALUES(rating), completed_count=VALUES(completed_count), audit_status=VALUES(audit_status), updated_at=VALUES(updated_at);

INSERT INTO companion_profile(user_id, real_name, avatar, introduction, service_area, service_types, experience_years, rating, completed_count, audit_status, created_at, updated_at)
SELECT su.id, '刘建国', '/uploads/avatars/companion4.jpg',
       '退休医护人员，8年陪诊经验。精通各类就医流程，对医保报销政策非常了解，擅长帮助老年患者办理各项手续。',
       '北京市丰台区', '陪诊,取药,挂号,医保办理,住院办理',
       8, 4.90, 320, 1, '2025-06-10 14:00:00', '2025-06-10 14:00:00'
FROM sys_user su WHERE su.username = 'companion4'
ON DUPLICATE KEY UPDATE real_name=VALUES(real_name), introduction=VALUES(introduction), service_area=VALUES(service_area), service_types=VALUES(service_types), experience_years=VALUES(experience_years), rating=VALUES(rating), completed_count=VALUES(completed_count), audit_status=VALUES(audit_status), updated_at=VALUES(updated_at);

INSERT INTO companion_profile(user_id, real_name, avatar, introduction, service_area, service_types, experience_years, rating, completed_count, audit_status, created_at, updated_at)
SELECT su.id, '周敏', '/uploads/avatars/companion5.jpg',
       '护理学本科，1年陪诊经验。年轻有活力，擅长使用手机APP帮助患者在线挂号、缴费，对互联网医院操作熟练。',
       '北京市东城区', '陪诊,挂号,取药,在线问诊指导',
       1, 4.0, 12, 0, '2025-07-01 08:00:00', '2025-07-01 08:00:00'
FROM sys_user su WHERE su.username = 'companion5'
ON DUPLICATE KEY UPDATE real_name=VALUES(real_name), introduction=VALUES(introduction), service_area=VALUES(service_area), service_types=VALUES(service_types), experience_years=VALUES(experience_years), rating=VALUES(rating), completed_count=VALUES(completed_count), audit_status=VALUES(audit_status), updated_at=VALUES(updated_at);


-- ============================================================================
-- 第三部分：服务需求（共9条）
-- ============================================================================

-- PENDING（待匹配）3条 --
INSERT INTO service_request(customer_id, service_type, service_date, hospital_name, department, requirement, special_notes, contact_name, contact_phone, budget, status, created_at, updated_at)
SELECT id, '陪诊', '2026-07-20 08:30:00', '北京协和医院', '心内科', '老人需要做心脏彩超和心电图检查，需要陪诊师陪同完成挂号、候诊、检查全过程。', '老人听力不太好，请陪诊师耐心沟通。', '张三', '13800000001', 300.00, 'PENDING', '2026-07-15 09:00:00', '2026-07-15 09:00:00' FROM sys_user WHERE username='customer1' ON DUPLICATE KEY UPDATE status=VALUES(status), updated_at=VALUES(updated_at);

INSERT INTO service_request(customer_id, service_type, service_date, hospital_name, department, requirement, special_notes, contact_name, contact_phone, budget, status, created_at, updated_at)
SELECT id, '取药', '2026-07-22 10:00:00', '北京大学第一医院', '内分泌科', '帮老人取慢性病药物，需要携带医保卡和处方单，取药后送到家中。', '药品清单共5种药，请核对清楚。', '李四', '13800000002', 150.00, 'PENDING', '2026-07-15 10:30:00', '2026-07-15 10:30:00' FROM sys_user WHERE username='customer2' ON DUPLICATE KEY UPDATE status=VALUES(status), updated_at=VALUES(updated_at);

INSERT INTO service_request(customer_id, service_type, service_date, hospital_name, department, requirement, special_notes, contact_name, contact_phone, budget, status, created_at, updated_at)
SELECT id, '挂号', '2026-07-25 06:00:00', '北京同仁医院', '眼科', '老人眼睛不适需要看专家门诊，帮忙提前排队挂号。', '需要挂专家号王医生。', '赵六', '13800000004', 200.00, 'PENDING', '2026-07-16 08:00:00', '2026-07-16 08:00:00' FROM sys_user WHERE username='customer4' ON DUPLICATE KEY UPDATE status=VALUES(status), updated_at=VALUES(updated_at);

-- MATCHED（已匹配）2条 --
INSERT INTO service_request(customer_id, service_type, service_date, hospital_name, department, requirement, special_notes, contact_name, contact_phone, budget, status, created_at, updated_at)
SELECT id, '陪诊', '2026-07-10 09:00:00', '北京协和医院', '消化内科', '老人做胃镜检查，需要全程陪同：挂号、候诊、检查前后照护、取报告。', '需要做无痛胃镜，检查后需观察半小时。', '王五', '13800000003', 400.00, 'MATCHED', '2026-07-05 14:00:00', '2026-07-05 14:00:00' FROM sys_user WHERE username='customer3' ON DUPLICATE KEY UPDATE status=VALUES(status), updated_at=VALUES(updated_at);

INSERT INTO service_request(customer_id, service_type, service_date, hospital_name, department, requirement, special_notes, contact_name, contact_phone, budget, status, created_at, updated_at)
SELECT id, '检查', '2026-07-12 08:00:00', '中国人民解放军总医院', '神经内科', '老人需要做脑部CT和核磁共振，需要陪诊师帮忙排队、引导检查、取结果。', '核磁已预约好，按预约时间带老人去即可。', '刘奶奶', '13900000001', 500.00, 'MATCHED', '2026-07-08 09:00:00', '2026-07-08 09:00:00' FROM sys_user WHERE username='elder01' ON DUPLICATE KEY UPDATE status=VALUES(status), updated_at=VALUES(updated_at);

-- CANCELLED（已取消）1条 --
INSERT INTO service_request(customer_id, service_type, service_date, hospital_name, department, requirement, special_notes, contact_name, contact_phone, budget, status, created_at, updated_at)
SELECT id, '陪诊', '2026-07-05 14:00:00', '北京安贞医院', '心内科', '老人心脏不适，需要陪诊师陪同看心内科专家门诊。', '无特殊要求。', '孙七', '13800000005', 350.00, 'CANCELLED', '2026-06-28 10:00:00', '2026-07-03 16:00:00' FROM sys_user WHERE username='customer5' ON DUPLICATE KEY UPDATE status=VALUES(status), updated_at=VALUES(updated_at);

-- CLOSED（已完成）3条 --
INSERT INTO service_request(customer_id, service_type, service_date, hospital_name, department, requirement, special_notes, contact_name, contact_phone, budget, status, created_at, updated_at)
SELECT id, '住院办理', '2026-07-01 09:00:00', '北京大学第三医院', '骨科', '老人髋关节置换手术住院，需要帮办住院手续、缴费、领取住院用品。', '已提前联系好病房。', '陈爷爷', '13900000002', 300.00, 'CLOSED', '2026-06-25 08:00:00', '2026-07-02 16:00:00' FROM sys_user WHERE username='elder02' ON DUPLICATE KEY UPDATE status=VALUES(status), updated_at=VALUES(updated_at);

INSERT INTO service_request(customer_id, service_type, service_date, hospital_name, department, requirement, special_notes, contact_name, contact_phone, budget, status, created_at, updated_at)
SELECT id, '陪诊', '2026-07-08 08:30:00', '北京协和医院', '风湿免疫科', '父亲痛风发作行动不便，需要陪诊师陪同全程就诊，包括轮椅推送。', '需要陪诊师帮忙借轮椅。', '周先生', '13900000003', 350.00, 'CLOSED', '2026-07-03 09:00:00', '2026-07-09 10:00:00' FROM sys_user WHERE username='family01' ON DUPLICATE KEY UPDATE status=VALUES(status), updated_at=VALUES(updated_at);

INSERT INTO service_request(customer_id, service_type, service_date, hospital_name, department, requirement, special_notes, contact_name, contact_phone, budget, status, created_at, updated_at)
SELECT id, '陪诊', '2026-07-10 08:30:00', '北京协和医院', '消化内科', '老人做胃镜复查，需要陪诊师陪同完成挂号、候诊、检查全过程。', '老人行动不便，需要协助上下楼。', '王五', '13800000003', 400.00, 'CLOSED', '2026-07-05 15:00:00', '2026-07-11 10:00:00' FROM sys_user WHERE username='customer3' ON DUPLICATE KEY UPDATE status=VALUES(status), updated_at=VALUES(updated_at);


-- ============================================================================
-- 第四部分：服务订单（共6个，覆盖全部状态）
-- ============================================================================

-- 订单1: COMPLETED - 完整流程（elder02 的 住院办理 CLOSED 需求）
INSERT INTO service_order(request_id, customer_id, companion_id, service_price, platform_fee, companion_income, status, accepted_at, started_at, completed_at, confirmed_at, created_at, updated_at)
SELECT
    (SELECT id FROM service_request WHERE customer_id=(SELECT id FROM sys_user WHERE username='elder02') AND status='CLOSED' AND service_type='住院办理' LIMIT 1),
    (SELECT id FROM sys_user WHERE username='elder02'),
    (SELECT cp.id FROM companion_profile cp JOIN sys_user su ON cp.user_id=su.id WHERE su.username='companion1'),
    300.00, 30.00, 270.00, 'COMPLETED',
    '2026-07-01 09:30:00', '2026-07-01 10:00:00', '2026-07-01 14:00:00', '2026-07-01 16:00:00',
    '2026-06-25 09:00:00', '2026-07-02 16:00:00'
ON DUPLICATE KEY UPDATE status=VALUES(status), updated_at=VALUES(updated_at);

-- 订单2: COMPLETED - 完整流程（family01 的 陪诊 CLOSED 需求）
INSERT INTO service_order(request_id, customer_id, companion_id, service_price, platform_fee, companion_income, status, accepted_at, started_at, completed_at, confirmed_at, created_at, updated_at)
SELECT
    (SELECT id FROM service_request WHERE customer_id=(SELECT id FROM sys_user WHERE username='family01') AND status='CLOSED' AND service_type='陪诊' LIMIT 1),
    (SELECT id FROM sys_user WHERE username='family01'),
    (SELECT cp.id FROM companion_profile cp JOIN sys_user su ON cp.user_id=su.id WHERE su.username='companion2'),
    350.00, 35.00, 315.00, 'COMPLETED',
    '2026-07-08 09:00:00', '2026-07-08 09:30:00', '2026-07-08 12:30:00', '2026-07-08 16:00:00',
    '2026-07-03 10:00:00', '2026-07-09 10:00:00'
ON DUPLICATE KEY UPDATE status=VALUES(status), updated_at=VALUES(updated_at);

-- 订单3: IN_SERVICE - 服务中（customer3 的 陪诊 MATCHED 需求）
INSERT INTO service_order(request_id, customer_id, companion_id, service_price, platform_fee, companion_income, status, accepted_at, started_at, created_at, updated_at)
SELECT
    (SELECT id FROM service_request WHERE customer_id=(SELECT id FROM sys_user WHERE username='customer3') AND status='MATCHED' AND service_type='陪诊' LIMIT 1),
    (SELECT id FROM sys_user WHERE username='customer3'),
    (SELECT cp.id FROM companion_profile cp JOIN sys_user su ON cp.user_id=su.id WHERE su.username='companion4'),
    400.00, 40.00, 360.00, 'IN_SERVICE',
    '2026-07-10 08:30:00', '2026-07-10 09:00:00',
    '2026-07-05 15:00:00', '2026-07-10 09:00:00'
ON DUPLICATE KEY UPDATE status=VALUES(status), updated_at=VALUES(updated_at);

-- 订单4: ACCEPTED - 已接单待服务（elder01 的 检查 MATCHED 需求）
INSERT INTO service_order(request_id, customer_id, companion_id, service_price, platform_fee, companion_income, status, accepted_at, created_at, updated_at)
SELECT
    (SELECT id FROM service_request WHERE customer_id=(SELECT id FROM sys_user WHERE username='elder01') AND status='MATCHED' AND service_type='检查' LIMIT 1),
    (SELECT id FROM sys_user WHERE username='elder01'),
    (SELECT cp.id FROM companion_profile cp JOIN sys_user su ON cp.user_id=su.id WHERE su.username='companion3'),
    500.00, 50.00, 450.00, 'ACCEPTED',
    '2026-07-11 10:00:00',
    '2026-07-08 10:00:00', '2026-07-11 10:00:00'
ON DUPLICATE KEY UPDATE status=VALUES(status), updated_at=VALUES(updated_at);

-- 订单5: CANCELLED - 已取消（customer5 的 陪诊 CANCELLED 需求）
INSERT INTO service_order(request_id, customer_id, companion_id, service_price, platform_fee, companion_income, status, accepted_at, cancelled_at, cancel_reason, created_at, updated_at)
SELECT
    (SELECT id FROM service_request WHERE customer_id=(SELECT id FROM sys_user WHERE username='customer5') AND status='CANCELLED' AND service_type='陪诊' LIMIT 1),
    (SELECT id FROM sys_user WHERE username='customer5'),
    (SELECT cp.id FROM companion_profile cp JOIN sys_user su ON cp.user_id=su.id WHERE su.username='companion5'),
    350.00, 35.00, 315.00, 'CANCELLED',
    '2026-06-28 15:00:00', '2026-07-03 16:00:00',
    '老人身体状况好转，暂时不需要陪诊服务了。',
    '2026-06-28 15:00:00', '2026-07-03 16:00:00'
ON DUPLICATE KEY UPDATE status=VALUES(status), updated_at=VALUES(updated_at);

-- 订单6: COMPLAINT - 已投诉（customer3 的 陪诊 CLOSED 需求 - 第二个陪诊需求）
INSERT INTO service_order(request_id, customer_id, companion_id, service_price, platform_fee, companion_income, status, accepted_at, started_at, completed_at, created_at, updated_at)
SELECT
    (SELECT id FROM service_request WHERE customer_id=(SELECT id FROM sys_user WHERE username='customer3') AND status='CLOSED' AND service_type='陪诊' AND service_date='2026-07-10 08:30:00' LIMIT 1),
    (SELECT id FROM sys_user WHERE username='customer3'),
    (SELECT cp.id FROM companion_profile cp JOIN sys_user su ON cp.user_id=su.id WHERE su.username='companion4'),
    400.00, 40.00, 360.00, 'COMPLAINT',
    '2026-07-10 08:30:00', '2026-07-10 09:00:00', '2026-07-10 12:00:00',
    '2026-07-05 15:00:00', '2026-07-10 16:00:00'
ON DUPLICATE KEY UPDATE status=VALUES(status), updated_at=VALUES(updated_at);


-- ============================================================================
-- 第五部分：服务记录（2条，对应已完成订单）
-- ============================================================================

INSERT INTO service_record(order_id, content, important_notes, completed_by, created_at)
SELECT so.id,
       '1. 9:30到达北京大学第三医院住院部\n2. 帮助老人填写住院登记表，办理住院手续\n3. 缴纳住院押金，领取住院手环和病号服\n4. 协助老人入住骨科病房305室\n5. 向护士站确认手术安排和术前注意事项\n6. 帮助购买住院日用品（脸盆、毛巾、拖鞋等）',
       '老人对手术有些紧张，建议家属多安抚；住院押金收据已交给家属。',
       (SELECT id FROM sys_user WHERE username='companion1'), '2026-07-01 14:00:00'
FROM service_order so
WHERE so.customer_id=(SELECT id FROM sys_user WHERE username='elder02')
  AND so.status='COMPLETED'
  AND so.companion_id=(SELECT cp.id FROM companion_profile cp JOIN sys_user su ON cp.user_id=su.id WHERE su.username='companion1')
ON DUPLICATE KEY UPDATE content=VALUES(content), important_notes=VALUES(important_notes);

INSERT INTO service_record(order_id, content, important_notes, completed_by, created_at)
SELECT so.id,
       '1. 9:30到北京协和医院门诊大厅集合\n2. 帮助借轮椅，推送老人到风湿免疫科候诊\n3. 陪同医生问诊，记录医嘱和用药调整\n4. 排队缴费、取药\n5. 帮助老人完成血常规和尿酸检测\n6. 将检查报告拍照发给家属，药品核对后交给家属',
       '医生调整了降尿酸药物剂量，请家属注意按时服药；下次复诊8月15日。',
       (SELECT id FROM sys_user WHERE username='companion2'), '2026-07-08 12:30:00'
FROM service_order so
WHERE so.customer_id=(SELECT id FROM sys_user WHERE username='family01')
  AND so.status='COMPLETED'
  AND so.companion_id=(SELECT cp.id FROM companion_profile cp JOIN sys_user su ON cp.user_id=su.id WHERE su.username='companion2')
ON DUPLICATE KEY UPDATE content=VALUES(content), important_notes=VALUES(important_notes);


-- ============================================================================
-- 第六部分：评价记录（5条）
-- ============================================================================

-- 订单1: 客户评价陪诊师 5星
INSERT INTO evaluation(order_id, from_user_id, to_user_id, score, content, created_at)
SELECT
    (SELECT id FROM service_order WHERE customer_id=(SELECT id FROM sys_user WHERE username='elder02') AND status='COMPLETED' LIMIT 1),
    (SELECT id FROM sys_user WHERE username='elder02'),
    (SELECT id FROM sys_user WHERE username='companion1'),
    5, '李小华陪诊师非常专业！帮我办理住院手续很顺利，还主动帮忙买住院用品。态度特别好，老人很安心。强烈推荐！',
    '2026-07-01 16:30:00'
ON DUPLICATE KEY UPDATE score=VALUES(score), content=VALUES(content);

-- 订单1: 陪诊师评价客户 5星
INSERT INTO evaluation(order_id, from_user_id, to_user_id, score, content, created_at)
SELECT
    (SELECT id FROM service_order WHERE customer_id=(SELECT id FROM sys_user WHERE username='elder02') AND status='COMPLETED' LIMIT 1),
    (SELECT id FROM sys_user WHERE username='companion1'),
    (SELECT id FROM sys_user WHERE username='elder02'),
    5, '陈爷爷非常和蔼可亲，沟通顺畅，家属配合度高。祝陈爷爷早日康复！',
    '2026-07-01 17:00:00'
ON DUPLICATE KEY UPDATE score=VALUES(score), content=VALUES(content);

-- 订单2: 客户评价陪诊师 4星
INSERT INTO evaluation(order_id, from_user_id, to_user_id, score, content, created_at)
SELECT
    (SELECT id FROM service_order WHERE customer_id=(SELECT id FROM sys_user WHERE username='family01') AND status='COMPLETED' LIMIT 1),
    (SELECT id FROM sys_user WHERE username='family01'),
    (SELECT id FROM sys_user WHERE username='companion2'),
    4, '王小明陪诊师服务态度好，做事麻利。就是时间稍微有点赶，如果能多跟医生问几句就更好了。总体满意。',
    '2026-07-08 17:00:00'
ON DUPLICATE KEY UPDATE score=VALUES(score), content=VALUES(content);

-- 订单2: 陪诊师评价客户 5星
INSERT INTO evaluation(order_id, from_user_id, to_user_id, score, content, created_at)
SELECT
    (SELECT id FROM service_order WHERE customer_id=(SELECT id FROM sys_user WHERE username='family01') AND status='COMPLETED' LIMIT 1),
    (SELECT id FROM sys_user WHERE username='companion2'),
    (SELECT id FROM sys_user WHERE username='family01'),
    5, '周先生作为家属非常负责，提前准备好了所有就医材料，沟通清晰，配合度很高。',
    '2026-07-08 18:00:00'
ON DUPLICATE KEY UPDATE score=VALUES(score), content=VALUES(content);

-- 订单6: 投诉订单评价 2星差评
INSERT INTO evaluation(order_id, from_user_id, to_user_id, score, content, created_at)
SELECT
    (SELECT id FROM service_order WHERE customer_id=(SELECT id FROM sys_user WHERE username='customer3') AND status='COMPLAINT' LIMIT 1),
    (SELECT id FROM sys_user WHERE username='customer3'),
    (SELECT id FROM sys_user WHERE username='companion4'),
    2, '陪诊师迟到半小时，服务过程中一直在看手机，没有认真陪同。虽然最终完成了就医，但体验非常不好。',
    '2026-07-10 15:00:00'
ON DUPLICATE KEY UPDATE score=VALUES(score), content=VALUES(content);


-- ============================================================================
-- 第七部分：订单状态记录
-- ============================================================================

-- 订单1 完整状态流转（PENDING_ACCEPT → ACCEPTED → IN_SERVICE → PENDING_CONFIRM → COMPLETED）
INSERT INTO order_status_log(order_id, from_status, to_status, operator_id, remark, created_at)
SELECT so.id, NULL, 'PENDING_ACCEPT', (SELECT id FROM sys_user WHERE username='admin1'), '系统自动创建订单，等待陪诊师接单', '2026-06-25 09:00:00'
FROM service_order so WHERE so.customer_id=(SELECT id FROM sys_user WHERE username='elder02') AND so.status='COMPLETED'
AND NOT EXISTS (SELECT 1 FROM order_status_log osl WHERE osl.order_id=so.id AND osl.to_status='PENDING_ACCEPT' AND osl.from_status IS NULL);

INSERT INTO order_status_log(order_id, from_status, to_status, operator_id, remark, created_at)
SELECT so.id, 'PENDING_ACCEPT', 'ACCEPTED', (SELECT id FROM sys_user WHERE username='companion1'), '陪诊师接单', '2026-07-01 09:30:00'
FROM service_order so WHERE so.customer_id=(SELECT id FROM sys_user WHERE username='elder02') AND so.status='COMPLETED'
AND NOT EXISTS (SELECT 1 FROM order_status_log osl WHERE osl.order_id=so.id AND osl.to_status='ACCEPTED');

INSERT INTO order_status_log(order_id, from_status, to_status, operator_id, remark, created_at)
SELECT so.id, 'ACCEPTED', 'IN_SERVICE', (SELECT id FROM sys_user WHERE username='companion1'), '陪诊师到达医院，开始服务', '2026-07-01 10:00:00'
FROM service_order so WHERE so.customer_id=(SELECT id FROM sys_user WHERE username='elder02') AND so.status='COMPLETED'
AND NOT EXISTS (SELECT 1 FROM order_status_log osl WHERE osl.order_id=so.id AND osl.to_status='IN_SERVICE');

INSERT INTO order_status_log(order_id, from_status, to_status, operator_id, remark, created_at)
SELECT so.id, 'IN_SERVICE', 'PENDING_CONFIRM', (SELECT id FROM sys_user WHERE username='companion1'), '服务完成，已提交服务记录，等待客户确认', '2026-07-01 14:00:00'
FROM service_order so WHERE so.customer_id=(SELECT id FROM sys_user WHERE username='elder02') AND so.status='COMPLETED'
AND NOT EXISTS (SELECT 1 FROM order_status_log osl WHERE osl.order_id=so.id AND osl.to_status='PENDING_CONFIRM');

INSERT INTO order_status_log(order_id, from_status, to_status, operator_id, remark, created_at)
SELECT so.id, 'PENDING_CONFIRM', 'COMPLETED', (SELECT id FROM sys_user WHERE username='elder02'), '客户确认服务完成', '2026-07-01 16:00:00'
FROM service_order so WHERE so.customer_id=(SELECT id FROM sys_user WHERE username='elder02') AND so.status='COMPLETED'
AND NOT EXISTS (SELECT 1 FROM order_status_log osl WHERE osl.order_id=so.id AND osl.to_status='COMPLETED');

-- 订单5 取消状态流转
INSERT INTO order_status_log(order_id, from_status, to_status, operator_id, remark, created_at)
SELECT so.id, NULL, 'PENDING_ACCEPT', (SELECT id FROM sys_user WHERE username='admin1'), '系统自动创建订单', '2026-06-28 15:00:00'
FROM service_order so WHERE so.customer_id=(SELECT id FROM sys_user WHERE username='customer5') AND so.status='CANCELLED'
AND NOT EXISTS (SELECT 1 FROM order_status_log osl WHERE osl.order_id=so.id AND osl.to_status='PENDING_ACCEPT' AND osl.from_status IS NULL);

INSERT INTO order_status_log(order_id, from_status, to_status, operator_id, remark, created_at)
SELECT so.id, 'PENDING_ACCEPT', 'CANCELLED', (SELECT id FROM sys_user WHERE username='customer5'), '客户取消：老人身体状况好转，暂时不需要陪诊服务', '2026-07-03 16:00:00'
FROM service_order so WHERE so.customer_id=(SELECT id FROM sys_user WHERE username='customer5') AND so.status='CANCELLED'
AND NOT EXISTS (SELECT 1 FROM order_status_log osl WHERE osl.order_id=so.id AND osl.to_status='CANCELLED');


-- ============================================================================
-- 第八部分：审核记录（4条）
-- ============================================================================

INSERT INTO audit_record(business_type, business_id, auditor_id, audit_status, remark, created_at)
SELECT 'COMPANION_PROFILE', cp1.id, (SELECT id FROM sys_user WHERE username='admin1'), 1, '资料齐全，经验丰富，审核通过。', '2025-05-16 10:00:00'
FROM companion_profile cp1 JOIN sys_user su1 ON cp1.user_id=su1.id WHERE su1.username='companion1'
AND NOT EXISTS (SELECT 1 FROM audit_record ar WHERE ar.business_type='COMPANION_PROFILE' AND ar.business_id=cp1.id);

INSERT INTO audit_record(business_type, business_id, auditor_id, audit_status, remark, created_at)
SELECT 'COMPANION_PROFILE', cp2.id, (SELECT id FROM sys_user WHERE username='admin1'), 1, '资质符合要求，审核通过。', '2025-05-21 10:00:00'
FROM companion_profile cp2 JOIN sys_user su2 ON cp2.user_id=su2.id WHERE su2.username='companion2'
AND NOT EXISTS (SELECT 1 FROM audit_record ar WHERE ar.business_type='COMPANION_PROFILE' AND ar.business_id=cp2.id);

INSERT INTO audit_record(business_type, business_id, auditor_id, audit_status, remark, created_at)
SELECT 'COMPANION_PROFILE', cp3.id, (SELECT id FROM sys_user WHERE username='admin2'), 1, '护理专业背景，审核通过。', '2025-06-02 14:00:00'
FROM companion_profile cp3 JOIN sys_user su3 ON cp3.user_id=su3.id WHERE su3.username='companion3'
AND NOT EXISTS (SELECT 1 FROM audit_record ar WHERE ar.business_type='COMPANION_PROFILE' AND ar.business_id=cp3.id);

INSERT INTO audit_record(business_type, business_id, auditor_id, audit_status, remark, created_at)
SELECT 'COMPANION_PROFILE', cp4.id, (SELECT id FROM sys_user WHERE username='admin2'), 1, '资质优秀，经验丰富，审核通过。', '2025-06-11 10:00:00'
FROM companion_profile cp4 JOIN sys_user su4 ON cp4.user_id=su4.id WHERE su4.username='companion4'
AND NOT EXISTS (SELECT 1 FROM audit_record ar WHERE ar.business_type='COMPANION_PROFILE' AND ar.business_id=cp4.id);


-- ============================================================================
-- 第九部分：投诉记录（3条，覆盖不同状态）
-- ============================================================================

-- 投诉1: PENDING 待处理（对应订单6）
INSERT INTO report_record(order_id, reporter_id, reason, content, status, created_at)
SELECT
    (SELECT id FROM service_order WHERE customer_id=(SELECT id FROM sys_user WHERE username='customer3') AND status='COMPLAINT' LIMIT 1),
    (SELECT id FROM sys_user WHERE username='customer3'),
    '服务态度差',
    '陪诊师刘建国在2026年7月10日的陪诊服务中，迟到30分钟，服务过程中一直玩手机，对老人的问题爱答不理。虽然最终完成了就医流程，但服务态度极差，让人非常不满意。要求退还部分服务费并道歉。',
    'PENDING', '2026-07-10 16:00:00'
ON DUPLICATE KEY UPDATE reason=VALUES(reason), content=VALUES(content);

-- 投诉2: PROCESSING 处理中
INSERT INTO report_record(order_id, reporter_id, reason, content, status, handled_by, created_at)
SELECT
    (SELECT id FROM service_order WHERE customer_id=(SELECT id FROM sys_user WHERE username='family01') AND status='COMPLETED' LIMIT 1),
    (SELECT id FROM sys_user WHERE username='family01'),
    '费用争议',
    '订单费用350元，实际服务只有3小时，按小时算偏贵。陪诊师服务本身没问题，但对费用标准有疑问，希望平台能给出合理解释。',
    'PROCESSING', (SELECT id FROM sys_user WHERE username='admin2'), '2026-07-09 10:00:00'
ON DUPLICATE KEY UPDATE reason=VALUES(reason), content=VALUES(content);

-- 投诉3: RESOLVED 已处理
INSERT INTO report_record(order_id, reporter_id, reason, content, status, handled_by, handled_at, handle_remark, created_at)
SELECT
    (SELECT id FROM service_order WHERE customer_id=(SELECT id FROM sys_user WHERE username='elder02') AND status='COMPLETED' LIMIT 1),
    (SELECT id FROM sys_user WHERE username='companion1'),
    '客户爽约',
    '约定7月1日上午9点服务，客户家属临时更改时间到10点，导致我当天的其他安排被打乱。虽然最终完成了服务，但这种临时变更希望能提前沟通。',
    'RESOLVED', (SELECT id FROM sys_user WHERE username='admin1'),
    '2026-07-02 10:00:00', '已联系客户家属沟通，家属表示歉意并承诺以后提前沟通。陪诊师表示理解。',
    '2026-07-02 09:00:00'
ON DUPLICATE KEY UPDATE reason=VALUES(reason), content=VALUES(content);
