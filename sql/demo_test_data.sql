-- ============================================================================
-- 医陪平台 - 演示测试数据（纯数据插入，不修改表结构）
-- ============================================================================
-- 前置条件：已执行 sql/init.sql 创建数据库和表
-- 所有测试账号密码均为：123456
-- 安全运行：开头先清空所有业务表（TRUNCATE），因此可反复执行、每次结果一致。
--          注意：这会清除库中现有业务数据，仅用于演示/测试环境。
-- ============================================================================
-- 数据概览：
--   用户：13 CUSTOMER + 8 COMPANION + 3 ADMIN = 24 个用户
--   陪诊师资料：8 条（含1条待审核、1条审核拒绝）
--   服务需求：21 条（9 PENDING + 5 MATCHED + 1 CANCELLED + 6 CLOSED）
--     └ 审核：待匹配需求中3条 audit_status=0（供管理员审核演示），其余全部 audit_status=1
--   服务订单：14 个（5 COMPLETED + 2 IN_SERVICE + 3 ACCEPTED + 2 CANCELLED + 1 REJECTED + 1 COMPLAINT）
--   服务记录：6 条（含1条进行中记录）
--   评价记录：13 条（双向评价 + 投诉差评 + 拒绝场景评价）
--   状态记录：26 条（覆盖完整生命周期）
--   审核记录：9 条（7通过 + 1拒绝 + 1待处理）
--   投诉记录：6 条（2 PENDING + 2 PROCESSING + 1 RESOLVED + 1 REJECTED）
--   聊天消息：若干条（进行中/已完成订单的演示对话）
-- ============================================================================

USE yipei;

-- 设置客户端字符集为 utf8mb4，避免中文乱码和数据插入报错
SET NAMES utf8mb4;

-- ============================================================================
-- 第零部分：清空业务数据，保证脚本可重复执行、结果一致
-- （service_request 无业务唯一键，ON DUPLICATE KEY 无法去重，故统一先清空）
-- ============================================================================
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE chat_message;
TRUNCATE TABLE service_application;
TRUNCATE TABLE report_record;
TRUNCATE TABLE audit_record;
TRUNCATE TABLE order_status_log;
TRUNCATE TABLE evaluation;
TRUNCATE TABLE service_record;
TRUNCATE TABLE service_order;
TRUNCATE TABLE service_request;
TRUNCATE TABLE companion_profile;
TRUNCATE TABLE sys_user;
SET FOREIGN_KEY_CHECKS = 1;

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


-- ============================================================================
-- 第十部分：补充用户（更多CUSTOMER、COMPANION、ADMIN）
-- ============================================================================

-- 10.1 补充普通用户 CUSTOMER（老人/家属），新增5个
INSERT INTO sys_user(username, password, nickname, phone, role, status, created_at, updated_at) VALUES
('customer6',  @pwd, '吴奶奶',   '13900000004', 'CUSTOMER', 1, '2025-07-10 09:00:00', '2025-07-10 09:00:00'),
('customer7',  @pwd, '郑先生',   '13900000005', 'CUSTOMER', 1, '2025-08-01 14:00:00', '2025-08-01 14:00:00'),
('customer8',  @pwd, '冯奶奶',   '13900000006', 'CUSTOMER', 1, '2025-08-15 10:00:00', '2025-08-15 10:00:00'),
('customer9',  @pwd, '蒋女士',   '13900000007', 'CUSTOMER', 1, '2025-09-01 11:00:00', '2025-09-01 11:00:00'),
('customer10', @pwd, '韩爷爷',   '13900000008', 'CUSTOMER', 1, '2025-09-10 08:00:00', '2025-09-10 08:00:00')
ON DUPLICATE KEY UPDATE password=VALUES(password), nickname=VALUES(nickname), phone=VALUES(phone), role=VALUES(role), status=VALUES(status), updated_at=VALUES(updated_at);

-- 10.2 补充陪诊师 COMPANION，新增3个
INSERT INTO sys_user(username, password, nickname, phone, role, status, created_at, updated_at) VALUES
('companion6', @pwd, '赵护师', '13600000006', 'COMPANION', 1, '2025-08-01 09:00:00', '2025-08-01 09:00:00'),
('companion7', @pwd, '孙护士', '13600000007', 'COMPANION', 1, '2025-09-01 10:00:00', '2025-09-01 10:00:00'),
('companion8', @pwd, '吴医师', '13600000008', 'COMPANION', 1, '2025-10-01 08:00:00', '2025-10-01 08:00:00')
ON DUPLICATE KEY UPDATE password=VALUES(password), nickname=VALUES(nickname), phone=VALUES(phone), role=VALUES(role), status=VALUES(status), updated_at=VALUES(updated_at);

-- 10.3 补充管理员 ADMIN，新增1个
INSERT INTO sys_user(username, password, nickname, phone, role, status, created_at, updated_at) VALUES
('admin3', @pwd, '客服管理员', '13700000003', 'ADMIN', 1, '2025-06-01 09:00:00', '2025-06-01 09:00:00')
ON DUPLICATE KEY UPDATE password=VALUES(password), nickname=VALUES(nickname), phone=VALUES(phone), role=VALUES(role), status=VALUES(status), updated_at=VALUES(updated_at);


-- ============================================================================
-- 第十一部分：补充陪诊师资料（3个新陪诊师）
-- ============================================================================

INSERT INTO companion_profile(user_id, real_name, avatar, introduction, service_area, service_types, experience_years, rating, completed_count, audit_status, created_at, updated_at)
SELECT su.id, '赵丽华', '/uploads/avatars/companion6.jpg',
       '资深外科护士，10年三甲医院手术室工作经验。擅长术前术后陪护、手术流程指导、术后康复建议。对北京天坛医院、宣武医院流程非常熟悉。',
       '北京市西城区', '陪诊,手术陪同,住院办理,术后康复指导',
       10, 4.95, 280, 1, '2025-08-01 09:00:00', '2025-08-01 09:00:00'
FROM sys_user su WHERE su.username = 'companion6'
ON DUPLICATE KEY UPDATE real_name=VALUES(real_name), introduction=VALUES(introduction), service_area=VALUES(service_area), service_types=VALUES(service_types), experience_years=VALUES(experience_years), rating=VALUES(rating), completed_count=VALUES(completed_count), audit_status=VALUES(audit_status), updated_at=VALUES(updated_at);

INSERT INTO companion_profile(user_id, real_name, avatar, introduction, service_area, service_types, experience_years, rating, completed_count, audit_status, created_at, updated_at)
SELECT su.id, '孙晓萌', '/uploads/avatars/companion7.jpg',
       '护理学硕士，2年儿科陪诊经验。性格活泼开朗，善于与儿童沟通，擅长儿童就医陪护、疫苗接种陪同、儿科急诊陪护。',
       '北京市海淀区', '陪诊,儿科陪护,疫苗接种,检查引导',
       2, 4.60, 56, 1, '2025-09-01 10:00:00', '2025-09-01 10:00:00'
FROM sys_user su WHERE su.username = 'companion7'
ON DUPLICATE KEY UPDATE real_name=VALUES(real_name), introduction=VALUES(introduction), service_area=VALUES(service_area), service_types=VALUES(service_types), experience_years=VALUES(experience_years), rating=VALUES(rating), completed_count=VALUES(completed_count), audit_status=VALUES(audit_status), updated_at=VALUES(updated_at);

INSERT INTO companion_profile(user_id, real_name, avatar, introduction, service_area, service_types, experience_years, rating, completed_count, audit_status, created_at, updated_at)
SELECT su.id, '吴建国', '/uploads/avatars/companion8.jpg',
       '退休内科副主任医师，15年临床经验。精通各类疾病就诊流程，能为患者提供专业的就医建议和流程规划。对医保政策、慢性病管理有深入研究。',
       '北京市朝阳区', '陪诊,就医规划,慢性病管理,医保咨询,住院办理',
       15, 4.98, 420, 0, '2025-10-01 08:00:00', '2025-10-01 08:00:00'
FROM sys_user su WHERE su.username = 'companion8'
ON DUPLICATE KEY UPDATE real_name=VALUES(real_name), introduction=VALUES(introduction), service_area=VALUES(service_area), service_types=VALUES(service_types), experience_years=VALUES(experience_years), rating=VALUES(rating), completed_count=VALUES(completed_count), audit_status=VALUES(audit_status), updated_at=VALUES(updated_at);


-- ============================================================================
-- 第十二部分：补充服务需求（新增12条，覆盖更多场景）
-- ============================================================================

-- PENDING（待匹配）6条 --
INSERT INTO service_request(customer_id, service_type, service_date, hospital_name, department, requirement, special_notes, ai_summary, contact_name, contact_phone, budget, status, created_at, updated_at)
SELECT id, '陪诊', '2026-08-05 09:00:00', '北京天坛医院', '神经外科', '母亲头痛多日，需要做脑部核磁共振和脑血管造影检查。需要陪诊师陪同完成挂号、排队、检查全过程，并帮助记录医生建议。', '母亲有高血压病史，需要注意休息；检查前需要禁食6小时。', '客户母亲头痛多日需做脑部核磁共振和脑血管造影，有高血压病史需注意休息，检查前禁食6小时。', '吴奶奶', '13900000004', 500.00, 'PENDING', '2026-07-25 08:00:00', '2026-07-25 08:00:00' FROM sys_user WHERE username='customer6' ON DUPLICATE KEY UPDATE status=VALUES(status), updated_at=VALUES(updated_at);

INSERT INTO service_request(customer_id, service_type, service_date, hospital_name, department, requirement, special_notes, ai_summary, contact_name, contact_phone, budget, status, created_at, updated_at)
SELECT id, '陪诊', '2026-08-10 08:00:00', '北京儿童医院', '儿科', '孩子发烧咳嗽一周未愈，需要陪诊师帮忙挂号、陪同就诊、取药。父母工作忙无法请假，由奶奶带去看病，需要陪诊师协助沟通。', '孩子4岁，怕生，希望陪诊师有耐心有儿童陪伴经验。奶奶普通话不太好。', '4岁儿童发烧咳嗽一周未愈，需陪诊师协助挂号就诊取药，孩子怕生需耐心，奶奶普通话不好需协助沟通。', '郑先生', '13900000005', 350.00, 'PENDING', '2026-07-28 14:00:00', '2026-07-28 14:00:00' FROM sys_user WHERE username='customer7' ON DUPLICATE KEY UPDATE status=VALUES(status), updated_at=VALUES(updated_at);

INSERT INTO service_request(customer_id, service_type, service_date, hospital_name, department, requirement, special_notes, ai_summary, contact_name, contact_phone, budget, status, created_at, updated_at)
SELECT id, '住院办理', '2026-08-12 09:00:00', '北京宣武医院', '骨科', '老人膝盖置换手术住院，需要帮办住院手续、缴纳押金、领取住院用品、协助术前检查。', '手术安排在8月13日上午，需要术前一天办理好住院。', '老人膝盖置换手术需术前一天办理住院手续，包括缴纳押金、领取用品、协助术前检查。', '冯奶奶', '13900000006', 300.00, 'PENDING', '2026-08-01 10:00:00', '2026-08-01 10:00:00' FROM sys_user WHERE username='customer8' ON DUPLICATE KEY UPDATE status=VALUES(status), updated_at=VALUES(updated_at);

INSERT INTO service_request(customer_id, service_type, service_date, hospital_name, department, requirement, special_notes, ai_summary, contact_name, contact_phone, budget, status, created_at, updated_at)
SELECT id, '取药', '2026-08-08 14:00:00', '北京大学第一医院', '心血管内科', '帮父亲取高血压和冠心病的长期用药，共8种药。需要带医保卡和慢病管理手册，取药后送到家里。', '其中有2种药是冷藏药品，需要保温袋携带。药品清单已拍照发群。', '帮父亲取高血压和冠心病长期用药共8种，含2种冷藏药品需保温袋，需携带医保卡和慢病管理手册。', '蒋女士', '13900000007', 150.00, 'PENDING', '2026-08-02 09:00:00', '2026-08-02 09:00:00' FROM sys_user WHERE username='customer9' ON DUPLICATE KEY UPDATE status=VALUES(status), updated_at=VALUES(updated_at);

INSERT INTO service_request(customer_id, service_type, service_date, hospital_name, department, requirement, special_notes, ai_summary, contact_name, contact_phone, budget, status, created_at, updated_at)
SELECT id, '陪诊', '2026-08-15 08:30:00', '北京安贞医院', '心血管内科', '老人近期胸闷气短，需做心脏彩超、动态心电图和冠脉CTA检查。需要陪诊师全程陪同完成各项检查。', '老人腿脚不便需要轮椅，检查项目多预计需要一整天。', '老人胸闷气短需做心脏彩超、动态心电图和冠脉CTA，腿脚不便需轮椅，检查项目多预计全天。', '韩爷爷', '13900000008', 600.00, 'PENDING', '2026-08-05 08:00:00', '2026-08-05 08:00:00' FROM sys_user WHERE username='customer10' ON DUPLICATE KEY UPDATE status=VALUES(status), updated_at=VALUES(updated_at);

INSERT INTO service_request(customer_id, service_type, service_date, hospital_name, department, requirement, special_notes, ai_summary, contact_name, contact_phone, budget, status, created_at, updated_at)
SELECT id, '挂号', '2026-08-20 06:00:00', '中国医学科学院肿瘤医院', '肿瘤内科', '帮老人挂肿瘤内科专家号进行术后复查，需要提前排队挂专家号。', '需要挂张教授专家号（每周二出诊），号源紧张需要早起排队。', '老人术后需挂肿瘤内科专家号复查，指定张教授（每周二出诊），号源紧张需早起排队。', '张三', '13800000001', 150.00, 'PENDING', '2026-08-03 16:00:00', '2026-08-03 16:00:00' FROM sys_user WHERE username='customer1' ON DUPLICATE KEY UPDATE status=VALUES(status), updated_at=VALUES(updated_at);

-- MATCHED（已匹配）3条 --
INSERT INTO service_request(customer_id, service_type, service_date, hospital_name, department, requirement, special_notes, ai_summary, contact_name, contact_phone, budget, status, created_at, updated_at)
SELECT id, '陪诊', '2026-08-01 09:00:00', '北京协和医院', '内分泌科', '老人糖尿病复查，需要做糖化血红蛋白、尿微量白蛋白、眼底检查。需要陪诊师陪同完成各项检查并记录医嘱。', '老人需要空腹抽血，检查完后再吃饭。血糖仪已带上。', '老人糖尿病复查需做多项检查和空腹抽血，需陪诊师陪同并记录医嘱。', '吴奶奶', '13900000004', 350.00, 'MATCHED', '2026-07-20 08:00:00', '2026-07-22 10:00:00' FROM sys_user WHERE username='customer6' ON DUPLICATE KEY UPDATE status=VALUES(status), updated_at=VALUES(updated_at);

INSERT INTO service_request(customer_id, service_type, service_date, hospital_name, department, requirement, special_notes, ai_summary, contact_name, contact_phone, budget, status, created_at, updated_at)
SELECT id, '检查', '2026-08-03 08:00:00', '北京大学第三医院', '消化内科', '老人需要做无痛肠镜检查，需要陪诊师全程陪同：报到、换衣、检查陪护、检查后观察、取报告。', '肠镜检查需提前一天做肠道准备，检查当天需有人陪同签字。老人有些紧张需要安抚。', '老人需做无痛肠镜检查，需全流程陪同，检查后需观察，老人紧张需安抚。', '郑先生', '13900000005', 450.00, 'MATCHED', '2026-07-25 10:00:00', '2026-07-28 09:00:00' FROM sys_user WHERE username='customer7' ON DUPLICATE KEY UPDATE status=VALUES(status), updated_at=VALUES(updated_at);

INSERT INTO service_request(customer_id, service_type, service_date, hospital_name, department, requirement, special_notes, ai_summary, contact_name, contact_phone, budget, status, created_at, updated_at)
SELECT id, '陪诊', '2026-08-06 09:30:00', '北京友谊医院', '风湿免疫科', '老人关节疼痛加重，需要看风湿免疫科专家门诊，陪诊师协助挂号、陪同问诊、取药。', '老人手指关节变形，写字困难，需要陪诊师帮助填写表格和记录。', '老人关节疼痛加重需看风湿免疫科专家门诊，手指关节变形写字困难需协助填表。', '冯奶奶', '13900000006', 300.00, 'MATCHED', '2026-07-30 08:00:00', '2026-07-31 14:00:00' FROM sys_user WHERE username='customer8' ON DUPLICATE KEY UPDATE status=VALUES(status), updated_at=VALUES(updated_at);

-- CLOSED（已完成）3条 --
INSERT INTO service_request(customer_id, service_type, service_date, hospital_name, department, requirement, special_notes, ai_summary, contact_name, contact_phone, budget, status, created_at, updated_at)
SELECT id, '陪诊', '2026-07-20 08:30:00', '北京协和医院', '眼科', '老人白内障术后复查，需要陪诊师陪同完成视力检查、眼压测量、眼底检查。', '需要滴散瞳眼药水后检查，检查完约2小时视力模糊需有人陪同回家。', '老人白内障术后复查需陪同完成多项眼科检查，散瞳后视力模糊需护送回家。', '吴奶奶', '13900000004', 250.00, 'CLOSED', '2026-07-12 09:00:00', '2026-07-22 10:00:00' FROM sys_user WHERE username='customer6' ON DUPLICATE KEY UPDATE status=VALUES(status), updated_at=VALUES(updated_at);

INSERT INTO service_request(customer_id, service_type, service_date, hospital_name, department, requirement, special_notes, ai_summary, contact_name, contact_phone, budget, status, created_at, updated_at)
SELECT id, '取药', '2026-07-25 10:00:00', '北京同仁医院', '眼科', '帮老人取青光眼药物，每月定期取药。需要带医保卡和取药单，取药后送到老人家中。', '药品清单：拉坦前列素滴眼液、噻吗洛尔滴眼液，各2盒。', '帮老人定期取青光眼药物，每月一次，需携带医保卡和取药单后送药到家。', '韩爷爷', '13900000008', 100.00, 'CLOSED', '2026-07-22 08:00:00', '2026-07-26 11:00:00' FROM sys_user WHERE username='customer10' ON DUPLICATE KEY UPDATE status=VALUES(status), updated_at=VALUES(updated_at);

INSERT INTO service_request(customer_id, service_type, service_date, hospital_name, department, requirement, special_notes, ai_summary, contact_name, contact_phone, budget, status, created_at, updated_at)
SELECT id, '陪诊', '2026-07-18 08:00:00', '中国人民解放军总医院', '神经内科', '父亲近期头晕频繁，需要做脑部CT、颈动脉超声、经颅多普勒检查。陪诊师陪同完成全部检查流程。', '检查项目多，预计上午半天。父亲有轻微耳背，说话需要大声一些。', '父亲头晕频繁需做多项神经内科检查，预计半天，父亲轻微耳背需大声沟通。', '蒋女士', '13900000007', 400.00, 'CLOSED', '2026-07-10 09:00:00', '2026-07-19 14:00:00' FROM sys_user WHERE username='customer9' ON DUPLICATE KEY UPDATE status=VALUES(status), updated_at=VALUES(updated_at);


-- ============================================================================
-- 第十三部分：补充服务订单（新增8个，覆盖更多状态）
-- ============================================================================

-- 订单7: COMPLETED - 完整流程（customer6/吴奶奶 的 眼科陪诊 CLOSED 需求）
INSERT INTO service_order(request_id, customer_id, companion_id, service_price, platform_fee, companion_income, status, accepted_at, started_at, completed_at, confirmed_at, created_at, updated_at)
SELECT
    (SELECT id FROM service_request WHERE customer_id=(SELECT id FROM sys_user WHERE username='customer6') AND status='CLOSED' AND service_type='陪诊' AND hospital_name='北京协和医院' LIMIT 1),
    (SELECT id FROM sys_user WHERE username='customer6'),
    (SELECT cp.id FROM companion_profile cp JOIN sys_user su ON cp.user_id=su.id WHERE su.username='companion3'),
    250.00, 25.00, 225.00, 'COMPLETED',
    '2026-07-20 08:00:00', '2026-07-20 08:30:00', '2026-07-20 11:00:00', '2026-07-21 10:00:00',
    '2026-07-13 09:00:00', '2026-07-22 10:00:00'
ON DUPLICATE KEY UPDATE status=VALUES(status), updated_at=VALUES(updated_at);

-- 订单8: COMPLETED - 完整流程（customer10/韩爷爷 的 取药 CLOSED 需求）
INSERT INTO service_order(request_id, customer_id, companion_id, service_price, platform_fee, companion_income, status, accepted_at, started_at, completed_at, confirmed_at, created_at, updated_at)
SELECT
    (SELECT id FROM service_request WHERE customer_id=(SELECT id FROM sys_user WHERE username='customer10') AND status='CLOSED' AND service_type='取药' LIMIT 1),
    (SELECT id FROM sys_user WHERE username='customer10'),
    (SELECT cp.id FROM companion_profile cp JOIN sys_user su ON cp.user_id=su.id WHERE su.username='companion5'),
    100.00, 10.00, 90.00, 'COMPLETED',
    '2026-07-24 15:00:00', '2026-07-25 10:00:00', '2026-07-25 12:00:00', '2026-07-25 18:00:00',
    '2026-07-23 09:00:00', '2026-07-26 11:00:00'
ON DUPLICATE KEY UPDATE status=VALUES(status), updated_at=VALUES(updated_at);

-- 订单9: COMPLETED - 完整流程（customer9/蒋女士 的 陪诊 CLOSED 需求）
INSERT INTO service_order(request_id, customer_id, companion_id, service_price, platform_fee, companion_income, status, accepted_at, started_at, completed_at, confirmed_at, created_at, updated_at)
SELECT
    (SELECT id FROM service_request WHERE customer_id=(SELECT id FROM sys_user WHERE username='customer9') AND status='CLOSED' AND service_type='陪诊' AND hospital_name='中国人民解放军总医院' LIMIT 1),
    (SELECT id FROM sys_user WHERE username='customer9'),
    (SELECT cp.id FROM companion_profile cp JOIN sys_user su ON cp.user_id=su.id WHERE su.username='companion6'),
    400.00, 40.00, 360.00, 'COMPLETED',
    '2026-07-17 15:00:00', '2026-07-18 08:00:00', '2026-07-18 12:00:00', '2026-07-18 16:00:00',
    '2026-07-11 09:00:00', '2026-07-19 14:00:00'
ON DUPLICATE KEY UPDATE status=VALUES(status), updated_at=VALUES(updated_at);

-- 订单10: IN_SERVICE - 服务中（customer6/吴奶奶 的 陪诊 MATCHED 需求 - 糖尿病复查）
INSERT INTO service_order(request_id, customer_id, companion_id, service_price, platform_fee, companion_income, status, accepted_at, started_at, created_at, updated_at)
SELECT
    (SELECT id FROM service_request WHERE customer_id=(SELECT id FROM sys_user WHERE username='customer6') AND status='MATCHED' AND service_type='陪诊' AND department='内分泌科' LIMIT 1),
    (SELECT id FROM sys_user WHERE username='customer6'),
    (SELECT cp.id FROM companion_profile cp JOIN sys_user su ON cp.user_id=su.id WHERE su.username='companion1'),
    350.00, 35.00, 315.00, 'IN_SERVICE',
    '2026-07-30 16:00:00', '2026-08-01 09:00:00',
    '2026-07-22 10:00:00', '2026-08-01 09:00:00'
ON DUPLICATE KEY UPDATE status=VALUES(status), updated_at=VALUES(updated_at);

-- 订单11: ACCEPTED - 已接单待服务（customer7/郑先生 的 检查 MATCHED 需求 - 肠镜检查）
INSERT INTO service_order(request_id, customer_id, companion_id, service_price, platform_fee, companion_income, status, accepted_at, created_at, updated_at)
SELECT
    (SELECT id FROM service_request WHERE customer_id=(SELECT id FROM sys_user WHERE username='customer7') AND status='MATCHED' AND service_type='检查' LIMIT 1),
    (SELECT id FROM sys_user WHERE username='customer7'),
    (SELECT cp.id FROM companion_profile cp JOIN sys_user su ON cp.user_id=su.id WHERE su.username='companion4'),
    450.00, 45.00, 405.00, 'ACCEPTED',
    '2026-07-29 10:00:00',
    '2026-07-28 10:00:00', '2026-07-29 10:00:00'
ON DUPLICATE KEY UPDATE status=VALUES(status), updated_at=VALUES(updated_at);

-- 订单12: ACCEPTED - 已接单待服务（customer8/冯奶奶 的 陪诊 MATCHED 需求 - 风湿免疫科）
INSERT INTO service_order(request_id, customer_id, companion_id, service_price, platform_fee, companion_income, status, accepted_at, created_at, updated_at)
SELECT
    (SELECT id FROM service_request WHERE customer_id=(SELECT id FROM sys_user WHERE username='customer8') AND status='MATCHED' AND service_type='陪诊' AND department='风湿免疫科' LIMIT 1),
    (SELECT id FROM sys_user WHERE username='customer8'),
    (SELECT cp.id FROM companion_profile cp JOIN sys_user su ON cp.user_id=su.id WHERE su.username='companion7'),
    300.00, 30.00, 270.00, 'ACCEPTED',
    '2026-07-31 16:00:00',
    '2026-07-31 14:00:00', '2026-07-31 16:00:00'
ON DUPLICATE KEY UPDATE status=VALUES(status), updated_at=VALUES(updated_at);

-- 订单13: REJECTED - 被拒绝（customer4/赵六 的 PENDING 挂号需求被陪诊师拒绝）
INSERT INTO service_order(request_id, customer_id, companion_id, service_price, platform_fee, companion_income, status, accepted_at, cancelled_at, cancel_reason, created_at, updated_at)
SELECT
    (SELECT id FROM service_request WHERE customer_id=(SELECT id FROM sys_user WHERE username='customer4') AND status='PENDING' AND service_type='挂号' LIMIT 1),
    (SELECT id FROM sys_user WHERE username='customer4'),
    (SELECT cp.id FROM companion_profile cp JOIN sys_user su ON cp.user_id=su.id WHERE su.username='companion5'),
    200.00, 20.00, 180.00, 'REJECTED',
    '2026-07-16 10:00:00', '2026-07-16 12:00:00',
    '陪诊师查看后认为北京同仁医院挂号难度较大，无法保证挂到专家号，建议客户选择其他方式。',
    '2026-07-16 10:00:00', '2026-07-16 12:00:00'
ON DUPLICATE KEY UPDATE status=VALUES(status), updated_at=VALUES(updated_at);

-- 订单14: CANCELLED - 客户取消（假设匹配后又取消的场景，customer8的一条需求）
INSERT INTO service_order(request_id, customer_id, companion_id, service_price, platform_fee, companion_income, status, accepted_at, cancelled_at, cancel_reason, created_at, updated_at)
SELECT
    (SELECT id FROM service_request WHERE customer_id=(SELECT id FROM sys_user WHERE username='customer8') AND status='PENDING' AND service_type='住院办理' LIMIT 1),
    (SELECT id FROM sys_user WHERE username='customer8'),
    (SELECT cp.id FROM companion_profile cp JOIN sys_user su ON cp.user_id=su.id WHERE su.username='companion2'),
    300.00, 30.00, 270.00, 'CANCELLED',
    '2026-08-02 09:00:00', '2026-08-05 14:00:00',
    '医生调整手术时间为8月20日，住院日期延后，暂时取消本次服务。',
    '2026-08-02 09:00:00', '2026-08-05 14:00:00'
ON DUPLICATE KEY UPDATE status=VALUES(status), updated_at=VALUES(updated_at);


-- ============================================================================
-- 第十四部分：补充服务记录（4条，对应新完成的订单和进行中的订单）
-- ============================================================================

-- 订单7 服务记录（customer6 眼科陪诊）
INSERT INTO service_record(order_id, content, important_notes, completed_by, created_at)
SELECT so.id,
       '1. 8:30到北京协和医院眼科门诊等候\n2. 陪同老人挂号、候诊\n3. 协助老人完成视力检查、电脑验光\n4. 滴散瞳眼药水，等待30分钟后完成眼底检查和眼压测量\n5. 记录医生建议：恢复良好，视力从0.3提升到0.8\n6. 护送老人回家（散瞳后视力模糊约2小时）\n7. 帮助预约下次复查时间：10月20日',
       '术后恢复情况良好，下次复查间隔可延长至3个月；散瞳后老人有些头晕，休息后好转。',
       (SELECT id FROM sys_user WHERE username='companion3'), '2026-07-20 11:00:00'
FROM service_order so
WHERE so.customer_id=(SELECT id FROM sys_user WHERE username='customer6')
  AND so.status='COMPLETED'
  AND so.companion_id=(SELECT cp.id FROM companion_profile cp JOIN sys_user su ON cp.user_id=su.id WHERE su.username='companion3')
ON DUPLICATE KEY UPDATE content=VALUES(content), important_notes=VALUES(important_notes);

-- 订单8 服务记录（customer10 取药）
INSERT INTO service_record(order_id, content, important_notes, completed_by, created_at)
SELECT so.id,
       '1. 10:00到北京同仁医院门诊药房\n2. 使用医保卡和取药单排队取号\n3. 窗口取药：拉坦前列素滴眼液2盒、噻吗洛尔滴眼液2盒\n4. 核对药品名称、规格、数量、有效期\n5. 将药品送到老人家中，当面交给家属\n6. 告知用药方法和注意事项',
       '药品均在有效期内；已告知家属两种滴眼液需间隔5分钟使用；下次取药时间约8月25日。',
       (SELECT id FROM sys_user WHERE username='companion5'), '2026-07-25 12:00:00'
FROM service_order so
WHERE so.customer_id=(SELECT id FROM sys_user WHERE username='customer10')
  AND so.status='COMPLETED'
  AND so.companion_id=(SELECT cp.id FROM companion_profile cp JOIN sys_user su ON cp.user_id=su.id WHERE su.username='companion5')
ON DUPLICATE KEY UPDATE content=VALUES(content), important_notes=VALUES(important_notes);

-- 订单9 服务记录（customer9 神经内科陪诊）
INSERT INTO service_record(order_id, content, important_notes, completed_by, created_at)
SELECT so.id,
       '1. 8:00到中国人民解放军总医院门诊大厅\n2. 挂号神经内科专家门诊\n3. 陪同候诊，协助老人描述症状（头晕频率、持续时间、伴随症状）\n4. 陪同完成脑部CT平扫（排队约40分钟）\n5. 完成颈动脉超声检查\n6. 完成经颅多普勒（TCD）检查\n7. 取全部检查报告，记录医生诊断和用药调整\n8. 将检查报告拍照发给家属蒋女士',
       'CT显示轻微脑萎缩属老年性改变，无需过度担心；颈动脉有轻微斑块，医生开了阿托伐他汀；血压偏高需每天监测。下次复诊9月18日。',
       (SELECT id FROM sys_user WHERE username='companion6'), '2026-07-18 12:00:00'
FROM service_order so
WHERE so.customer_id=(SELECT id FROM sys_user WHERE username='customer9')
  AND so.status='COMPLETED'
  AND so.companion_id=(SELECT cp.id FROM companion_profile cp JOIN sys_user su ON cp.user_id=su.id WHERE su.username='companion6')
ON DUPLICATE KEY UPDATE content=VALUES(content), important_notes=VALUES(important_notes);

-- 订单10 进行中服务记录（customer6 糖尿病复查 - 部分完成）
INSERT INTO service_record(order_id, content, important_notes, completed_by, created_at)
SELECT so.id,
       '【进行中】1. 9:00到北京协和医院内分泌科\n2. 完成挂号，候诊中\n3. 已完成空腹抽血（糖化血红蛋白+空腹血糖）\n4. 正在等待尿微量白蛋白检测\n5. 预计还需完成眼底检查\n——记录更新中——',
       '老人今早空腹血糖6.2mmol/L；抽血后已让老人吃早餐（自带馒头和牛奶）；等待后续检查。',
       (SELECT id FROM sys_user WHERE username='companion1'), '2026-08-01 10:30:00'
FROM service_order so
WHERE so.customer_id=(SELECT id FROM sys_user WHERE username='customer6')
  AND so.status='IN_SERVICE'
  AND so.companion_id=(SELECT cp.id FROM companion_profile cp JOIN sys_user su ON cp.user_id=su.id WHERE su.username='companion1')
ON DUPLICATE KEY UPDATE content=VALUES(content), important_notes=VALUES(important_notes);


-- ============================================================================
-- 第十五部分：补充评价记录（8条）
-- ============================================================================

-- 订单7: 客户评价陪诊师 5星
INSERT INTO evaluation(order_id, from_user_id, to_user_id, score, content, created_at)
SELECT
    (SELECT id FROM service_order WHERE customer_id=(SELECT id FROM sys_user WHERE username='customer6') AND status='COMPLETED' AND service_price=250.00 LIMIT 1),
    (SELECT id FROM sys_user WHERE username='customer6'),
    (SELECT id FROM sys_user WHERE username='companion3'),
    5, '陈丽丽陪诊师非常细心！陪我妈看眼科，散瞳后视力模糊还专门打车送回家。整个过程都很顺利，强烈推荐！',
    '2026-07-21 10:00:00'
ON DUPLICATE KEY UPDATE score=VALUES(score), content=VALUES(content);

-- 订单7: 陪诊师评价客户 5星
INSERT INTO evaluation(order_id, from_user_id, to_user_id, score, content, created_at)
SELECT
    (SELECT id FROM service_order WHERE customer_id=(SELECT id FROM sys_user WHERE username='customer6') AND status='COMPLETED' AND service_price=250.00 LIMIT 1),
    (SELECT id FROM sys_user WHERE username='companion3'),
    (SELECT id FROM sys_user WHERE username='customer6'),
    5, '吴奶奶非常配合，就诊过程很顺利。祝愿奶奶身体健康，白内障恢复越来越好！',
    '2026-07-21 14:00:00'
ON DUPLICATE KEY UPDATE score=VALUES(score), content=VALUES(content);

-- 订单8: 客户评价陪诊师 4星
INSERT INTO evaluation(order_id, from_user_id, to_user_id, score, content, created_at)
SELECT
    (SELECT id FROM service_order WHERE customer_id=(SELECT id FROM sys_user WHERE username='customer10') AND status='COMPLETED' AND service_price=100.00 LIMIT 1),
    (SELECT id FROM sys_user WHERE username='customer10'),
    (SELECT id FROM sys_user WHERE username='companion5'),
    4, '取药很及时，药品核对无误。不过希望以后能附上用药时间表，老人记性不好容易忘。',
    '2026-07-26 09:00:00'
ON DUPLICATE KEY UPDATE score=VALUES(score), content=VALUES(content);

-- 订单8: 陪诊师评价客户 5星
INSERT INTO evaluation(order_id, from_user_id, to_user_id, score, content, created_at)
SELECT
    (SELECT id FROM service_order WHERE customer_id=(SELECT id FROM sys_user WHERE username='customer10') AND status='COMPLETED' AND service_price=100.00 LIMIT 1),
    (SELECT id FROM sys_user WHERE username='companion5'),
    (SELECT id FROM sys_user WHERE username='customer10'),
    5, '韩爷爷家属沟通清晰，药品清单准备得很齐全，取药过程顺利。',
    '2026-07-26 14:00:00'
ON DUPLICATE KEY UPDATE score=VALUES(score), content=VALUES(content);

-- 订单9: 客户评价陪诊师 5星
INSERT INTO evaluation(order_id, from_user_id, to_user_id, score, content, created_at)
SELECT
    (SELECT id FROM service_order WHERE customer_id=(SELECT id FROM sys_user WHERE username='customer9') AND status='COMPLETED' AND service_price=400.00 LIMIT 1),
    (SELECT id FROM sys_user WHERE username='customer9'),
    (SELECT id FROM sys_user WHERE username='companion6'),
    5, '赵护师太专业了！帮我爸做检查全程有条不紊，还发现了CT报告里我们没注意到的细节。不愧是资深护士出身，非常满意！',
    '2026-07-18 18:00:00'
ON DUPLICATE KEY UPDATE score=VALUES(score), content=VALUES(content);

-- 订单9: 陪诊师评价客户 5星
INSERT INTO evaluation(order_id, from_user_id, to_user_id, score, content, created_at)
SELECT
    (SELECT id FROM service_order WHERE customer_id=(SELECT id FROM sys_user WHERE username='customer9') AND status='COMPLETED' AND service_price=400.00 LIMIT 1),
    (SELECT id FROM sys_user WHERE username='companion6'),
    (SELECT id FROM sys_user WHERE username='customer9'),
    5, '蒋女士非常孝顺，虽然自己工作忙但一直通过微信关心父亲检查进展。老人也很配合。',
    '2026-07-18 20:00:00'
ON DUPLICATE KEY UPDATE score=VALUES(score), content=VALUES(content);

-- 订单3: 补充陪诊师评价客户（IN_SERVICE订单，陪诊师先评价）
INSERT INTO evaluation(order_id, from_user_id, to_user_id, score, content, created_at)
SELECT
    (SELECT id FROM service_order WHERE customer_id=(SELECT id FROM sys_user WHERE username='customer3') AND status='IN_SERVICE' LIMIT 1),
    (SELECT id FROM sys_user WHERE username='companion4'),
    (SELECT id FROM sys_user WHERE username='customer3'),
    4, '王五先生沟通配合度不错，胃镜检查过程顺利。希望下次预约能提前一天确认时间。',
    '2026-07-10 14:00:00'
ON DUPLICATE KEY UPDATE score=VALUES(score), content=VALUES(content);

-- 订单13: 陪诊师拒绝后客户评价（特殊场景 - 虽然REJECTED但允许客户给平台反馈）
INSERT INTO evaluation(order_id, from_user_id, to_user_id, score, content, created_at)
SELECT
    (SELECT id FROM service_order WHERE customer_id=(SELECT id FROM sys_user WHERE username='customer4') AND status='REJECTED' LIMIT 1),
    (SELECT id FROM sys_user WHERE username='customer4'),
    (SELECT id FROM sys_user WHERE username='companion5'),
    2, '虽然陪诊师解释了原因，但拒绝得太快了，也没有帮忙推荐其他能接单的陪诊师。希望平台能改进匹配机制。',
    '2026-07-17 10:00:00'
ON DUPLICATE KEY UPDATE score=VALUES(score), content=VALUES(content);


-- ============================================================================
-- 第十六部分：补充订单状态记录（新增订单的状态流转日志）
-- ============================================================================

-- 订单7 完整状态流转
INSERT INTO order_status_log(order_id, from_status, to_status, operator_id, remark, created_at)
SELECT so.id, NULL, 'PENDING_ACCEPT', (SELECT id FROM sys_user WHERE username='admin1'), '系统自动创建订单，等待陪诊师接单', '2026-07-13 09:00:00'
FROM service_order so WHERE so.customer_id=(SELECT id FROM sys_user WHERE username='customer6') AND so.status='COMPLETED' AND so.service_price=250.00
AND NOT EXISTS (SELECT 1 FROM order_status_log osl WHERE osl.order_id=so.id AND osl.to_status='PENDING_ACCEPT' AND osl.from_status IS NULL);

INSERT INTO order_status_log(order_id, from_status, to_status, operator_id, remark, created_at)
SELECT so.id, 'PENDING_ACCEPT', 'ACCEPTED', (SELECT id FROM sys_user WHERE username='companion3'), '陪诊师接单', '2026-07-20 08:00:00'
FROM service_order so WHERE so.customer_id=(SELECT id FROM sys_user WHERE username='customer6') AND so.status='COMPLETED' AND so.service_price=250.00
AND NOT EXISTS (SELECT 1 FROM order_status_log osl WHERE osl.order_id=so.id AND osl.to_status='ACCEPTED');

INSERT INTO order_status_log(order_id, from_status, to_status, operator_id, remark, created_at)
SELECT so.id, 'ACCEPTED', 'IN_SERVICE', (SELECT id FROM sys_user WHERE username='companion3'), '陪诊师到达协和医院，开始服务', '2026-07-20 08:30:00'
FROM service_order so WHERE so.customer_id=(SELECT id FROM sys_user WHERE username='customer6') AND so.status='COMPLETED' AND so.service_price=250.00
AND NOT EXISTS (SELECT 1 FROM order_status_log osl WHERE osl.order_id=so.id AND osl.to_status='IN_SERVICE');

INSERT INTO order_status_log(order_id, from_status, to_status, operator_id, remark, created_at)
SELECT so.id, 'IN_SERVICE', 'PENDING_CONFIRM', (SELECT id FROM sys_user WHERE username='companion3'), '服务完成，已提交服务记录和检查报告照片，等待客户确认', '2026-07-20 11:00:00'
FROM service_order so WHERE so.customer_id=(SELECT id FROM sys_user WHERE username='customer6') AND so.status='COMPLETED' AND so.service_price=250.00
AND NOT EXISTS (SELECT 1 FROM order_status_log osl WHERE osl.order_id=so.id AND osl.to_status='PENDING_CONFIRM');

INSERT INTO order_status_log(order_id, from_status, to_status, operator_id, remark, created_at)
SELECT so.id, 'PENDING_CONFIRM', 'COMPLETED', (SELECT id FROM sys_user WHERE username='customer6'), '客户确认服务完成，非常满意', '2026-07-21 10:00:00'
FROM service_order so WHERE so.customer_id=(SELECT id FROM sys_user WHERE username='customer6') AND so.status='COMPLETED' AND so.service_price=250.00
AND NOT EXISTS (SELECT 1 FROM order_status_log osl WHERE osl.order_id=so.id AND osl.to_status='COMPLETED');

-- 订单9 完整状态流转
INSERT INTO order_status_log(order_id, from_status, to_status, operator_id, remark, created_at)
SELECT so.id, NULL, 'PENDING_ACCEPT', (SELECT id FROM sys_user WHERE username='admin1'), '系统自动创建订单', '2026-07-11 09:00:00'
FROM service_order so WHERE so.customer_id=(SELECT id FROM sys_user WHERE username='customer9') AND so.status='COMPLETED' AND so.service_price=400.00
AND NOT EXISTS (SELECT 1 FROM order_status_log osl WHERE osl.order_id=so.id AND osl.to_status='PENDING_ACCEPT' AND osl.from_status IS NULL);

INSERT INTO order_status_log(order_id, from_status, to_status, operator_id, remark, created_at)
SELECT so.id, 'PENDING_ACCEPT', 'ACCEPTED', (SELECT id FROM sys_user WHERE username='companion6'), '陪诊师接单，提前沟通检查注意事项', '2026-07-17 15:00:00'
FROM service_order so WHERE so.customer_id=(SELECT id FROM sys_user WHERE username='customer9') AND so.status='COMPLETED' AND so.service_price=400.00
AND NOT EXISTS (SELECT 1 FROM order_status_log osl WHERE osl.order_id=so.id AND osl.to_status='ACCEPTED');

INSERT INTO order_status_log(order_id, from_status, to_status, operator_id, remark, created_at)
SELECT so.id, 'ACCEPTED', 'IN_SERVICE', (SELECT id FROM sys_user WHERE username='companion6'), '陪诊师到达301医院，核实检查项目后开始服务', '2026-07-18 08:00:00'
FROM service_order so WHERE so.customer_id=(SELECT id FROM sys_user WHERE username='customer9') AND so.status='COMPLETED' AND so.service_price=400.00
AND NOT EXISTS (SELECT 1 FROM order_status_log osl WHERE osl.order_id=so.id AND osl.to_status='IN_SERVICE');

INSERT INTO order_status_log(order_id, from_status, to_status, operator_id, remark, created_at)
SELECT so.id, 'IN_SERVICE', 'PENDING_CONFIRM', (SELECT id FROM sys_user WHERE username='companion6'), '全部检查完成，服务记录已提交，报告已发家属', '2026-07-18 12:00:00'
FROM service_order so WHERE so.customer_id=(SELECT id FROM sys_user WHERE username='customer9') AND so.status='COMPLETED' AND so.service_price=400.00
AND NOT EXISTS (SELECT 1 FROM order_status_log osl WHERE osl.order_id=so.id AND osl.to_status='PENDING_CONFIRM');

INSERT INTO order_status_log(order_id, from_status, to_status, operator_id, remark, created_at)
SELECT so.id, 'PENDING_CONFIRM', 'COMPLETED', (SELECT id FROM sys_user WHERE username='customer9'), '家属确认服务完成，对陪诊师非常满意', '2026-07-18 16:00:00'
FROM service_order so WHERE so.customer_id=(SELECT id FROM sys_user WHERE username='customer9') AND so.status='COMPLETED' AND so.service_price=400.00
AND NOT EXISTS (SELECT 1 FROM order_status_log osl WHERE osl.order_id=so.id AND osl.to_status='COMPLETED');

-- 订单10 进行中的状态记录
INSERT INTO order_status_log(order_id, from_status, to_status, operator_id, remark, created_at)
SELECT so.id, NULL, 'PENDING_ACCEPT', (SELECT id FROM sys_user WHERE username='admin1'), '系统自动创建订单', '2026-07-22 10:00:00'
FROM service_order so WHERE so.customer_id=(SELECT id FROM sys_user WHERE username='customer6') AND so.status='IN_SERVICE'
AND NOT EXISTS (SELECT 1 FROM order_status_log osl WHERE osl.order_id=so.id AND osl.to_status='PENDING_ACCEPT' AND osl.from_status IS NULL);

INSERT INTO order_status_log(order_id, from_status, to_status, operator_id, remark, created_at)
SELECT so.id, 'PENDING_ACCEPT', 'ACCEPTED', (SELECT id FROM sys_user WHERE username='companion1'), '陪诊师接单，已提前了解老人糖尿病史', '2026-07-30 16:00:00'
FROM service_order so WHERE so.customer_id=(SELECT id FROM sys_user WHERE username='customer6') AND so.status='IN_SERVICE'
AND NOT EXISTS (SELECT 1 FROM order_status_log osl WHERE osl.order_id=so.id AND osl.to_status='ACCEPTED');

INSERT INTO order_status_log(order_id, from_status, to_status, operator_id, remark, created_at)
SELECT so.id, 'ACCEPTED', 'IN_SERVICE', (SELECT id FROM sys_user WHERE username='companion1'), '陪诊师到达协和医院内分泌科，开始服务', '2026-08-01 09:00:00'
FROM service_order so WHERE so.customer_id=(SELECT id FROM sys_user WHERE username='customer6') AND so.status='IN_SERVICE'
AND NOT EXISTS (SELECT 1 FROM order_status_log osl WHERE osl.order_id=so.id AND osl.to_status='IN_SERVICE');

-- 订单13 拒绝状态流转
INSERT INTO order_status_log(order_id, from_status, to_status, operator_id, remark, created_at)
SELECT so.id, NULL, 'PENDING_ACCEPT', (SELECT id FROM sys_user WHERE username='admin1'), '系统自动创建订单，推送至陪诊师', '2026-07-16 10:00:00'
FROM service_order so WHERE so.customer_id=(SELECT id FROM sys_user WHERE username='customer4') AND so.status='REJECTED'
AND NOT EXISTS (SELECT 1 FROM order_status_log osl WHERE osl.order_id=so.id AND osl.to_status='PENDING_ACCEPT' AND osl.from_status IS NULL);

INSERT INTO order_status_log(order_id, from_status, to_status, operator_id, remark, created_at)
SELECT so.id, 'PENDING_ACCEPT', 'REJECTED', (SELECT id FROM sys_user WHERE username='companion5'), '陪诊师拒单：同仁医院专家号挂号难度大，无法保证成功率，建议客户通过官方渠道预约', '2026-07-16 12:00:00'
FROM service_order so WHERE so.customer_id=(SELECT id FROM sys_user WHERE username='customer4') AND so.status='REJECTED'
AND NOT EXISTS (SELECT 1 FROM order_status_log osl WHERE osl.order_id=so.id AND osl.to_status='REJECTED');

-- 订单14 取消状态流转
INSERT INTO order_status_log(order_id, from_status, to_status, operator_id, remark, created_at)
SELECT so.id, NULL, 'PENDING_ACCEPT', (SELECT id FROM sys_user WHERE username='admin1'), '系统自动创建订单', '2026-08-02 09:00:00'
FROM service_order so WHERE so.customer_id=(SELECT id FROM sys_user WHERE username='customer8') AND so.status='CANCELLED'
AND NOT EXISTS (SELECT 1 FROM order_status_log osl WHERE osl.order_id=so.id AND osl.to_status='PENDING_ACCEPT' AND osl.from_status IS NULL);

INSERT INTO order_status_log(order_id, from_status, to_status, operator_id, remark, created_at)
SELECT so.id, 'PENDING_ACCEPT', 'CANCELLED', (SELECT id FROM sys_user WHERE username='customer8'), '客户取消：医生调整手术时间至8月20日，住院日期延后', '2026-08-05 14:00:00'
FROM service_order so WHERE so.customer_id=(SELECT id FROM sys_user WHERE username='customer8') AND so.status='CANCELLED'
AND NOT EXISTS (SELECT 1 FROM order_status_log osl WHERE osl.order_id=so.id AND osl.to_status='CANCELLED');


-- ============================================================================
-- 第十七部分：补充审核记录（companion5 + 新陪诊师 + 拒绝案例）
-- ============================================================================

-- companion5 审核通过（之前缺失）
INSERT INTO audit_record(business_type, business_id, auditor_id, audit_status, remark, created_at)
SELECT 'COMPANION_PROFILE', cp5.id, (SELECT id FROM sys_user WHERE username='admin1'), 1, '护理学本科，资料完整，审核通过。', '2025-07-02 10:00:00'
FROM companion_profile cp5 JOIN sys_user su5 ON cp5.user_id=su5.id WHERE su5.username='companion5'
AND NOT EXISTS (SELECT 1 FROM audit_record ar WHERE ar.business_type='COMPANION_PROFILE' AND ar.business_id=cp5.id);

-- companion6 审核通过
INSERT INTO audit_record(business_type, business_id, auditor_id, audit_status, remark, created_at)
SELECT 'COMPANION_PROFILE', cp6.id, (SELECT id FROM sys_user WHERE username='admin2'), 1, '资深外科护士，10年三甲医院经验，资质优秀，审核通过。', '2025-08-03 10:00:00'
FROM companion_profile cp6 JOIN sys_user su6 ON cp6.user_id=su6.id WHERE su6.username='companion6'
AND NOT EXISTS (SELECT 1 FROM audit_record ar WHERE ar.business_type='COMPANION_PROFILE' AND ar.business_id=cp6.id);

-- companion7 审核通过
INSERT INTO audit_record(business_type, business_id, auditor_id, audit_status, remark, created_at)
SELECT 'COMPANION_PROFILE', cp7.id, (SELECT id FROM sys_user WHERE username='admin1'), 1, '护理学硕士，儿科陪诊专长，符合平台要求，审核通过。', '2025-09-03 10:00:00'
FROM companion_profile cp7 JOIN sys_user su7 ON cp7.user_id=su7.id WHERE su7.username='companion7'
AND NOT EXISTS (SELECT 1 FROM audit_record ar WHERE ar.business_type='COMPANION_PROFILE' AND ar.business_id=cp7.id);

-- companion8 审核拒绝（特殊案例：退休医师资质认证材料不全）
INSERT INTO audit_record(business_type, business_id, auditor_id, audit_status, remark, created_at)
SELECT 'COMPANION_PROFILE', cp8.id, (SELECT id FROM sys_user WHERE username='admin2'), 2, '退休医师资质证明不清晰，请提供退休证原件照片和执业医师资格证扫描件后重新提交。', '2025-10-05 14:00:00'
FROM companion_profile cp8 JOIN sys_user su8 ON cp8.user_id=su8.id WHERE su8.username='companion8'
AND NOT EXISTS (SELECT 1 FROM audit_record ar WHERE ar.business_type='COMPANION_PROFILE' AND ar.business_id=cp8.id);


-- ============================================================================
-- 第十八部分：补充投诉/举报记录（新增3条，覆盖REJECTED状态）
-- ============================================================================

-- 投诉4: REJECTED 已驳回 - 陪诊师投诉客户无理取闹
INSERT INTO report_record(order_id, reporter_id, reason, content, status, handled_by, handled_at, handle_remark, created_at)
SELECT
    (SELECT id FROM service_order WHERE customer_id=(SELECT id FROM sys_user WHERE username='customer3') AND status='COMPLAINT' LIMIT 1),
    (SELECT id FROM sys_user WHERE username='companion4'),
    '客户不实投诉',
    '客户投诉内容不属实。当天迟到是因为上一单服务客户临时需要延长服务时间，已通过APP提前告知。服务过程中使用手机是在记录服务内容和查询检查结果，并非玩手机。要求平台核实并撤销差评。',
    'REJECTED', (SELECT id FROM sys_user WHERE username='admin1'),
    '2026-07-12 10:00:00', '经核实，陪诊师迟到有APP通知记录，手机使用属于正常服务操作。客户投诉依据不足，予以驳回。已联系客户说明情况。',
    '2026-07-11 09:00:00'
ON DUPLICATE KEY UPDATE reason=VALUES(reason), content=VALUES(content);

-- 投诉5: PROCESSING 处理中 - 费用争议
INSERT INTO report_record(order_id, reporter_id, reason, content, status, handled_by, created_at)
SELECT
    (SELECT id FROM service_order WHERE customer_id=(SELECT id FROM sys_user WHERE username='customer9') AND status='COMPLETED' AND service_price=400.00 LIMIT 1),
    (SELECT id FROM sys_user WHERE username='customer9'),
    '平台费用过高',
    '订单金额400元，平台抽成40元感觉偏高。虽然对陪诊师本人很满意，但希望平台能适当降低服务费率，特别是对长期慢性病患者家庭。',
    'PROCESSING', (SELECT id FROM sys_user WHERE username='admin3'), '2026-07-19 10:00:00'
ON DUPLICATE KEY UPDATE reason=VALUES(reason), content=VALUES(content);

-- 投诉6: PENDING 待处理 - 服务质量投诉
INSERT INTO report_record(order_id, reporter_id, reason, content, status, created_at)
SELECT
    (SELECT id FROM service_order WHERE customer_id=(SELECT id FROM sys_user WHERE username='customer10') AND status='COMPLETED' AND service_price=100.00 LIMIT 1),
    (SELECT id FROM sys_user WHERE username='customer10'),
    '药品配送延迟',
    '约定取药后1小时内送到，实际用了2个半小时。虽然最终药品没问题，但老人等着用药非常着急。希望陪诊师以后能更准时，或者提前告知延迟原因。',
    'PENDING', '2026-07-26 16:00:00'
ON DUPLICATE KEY UPDATE reason=VALUES(reason), content=VALUES(content);


-- ============================================================================
-- 第十九部分：修正需求审核状态（关键！否则陪诊师需求广场为空）
-- 说明：需求广场只展示 audit_status=1（审核通过）且 status='PENDING' 的需求；
--       待审核需求(audit_status=0)只在管理员「需求审核」页出现。
-- ============================================================================

-- 已进入后续流程的需求（已匹配/已完成/已取消）视为早已审核通过
UPDATE service_request SET audit_status = 1 WHERE status IN ('MATCHED', 'CLOSED', 'CANCELLED');

-- 待匹配需求：默认全部通过审核，进入陪诊师需求广场
UPDATE service_request SET audit_status = 1 WHERE status = 'PENDING';

-- 保留最新的 3 条待匹配需求为「待审核」，用于演示管理员审核流程
UPDATE service_request SET audit_status = 0
WHERE status = 'PENDING'
ORDER BY id DESC
LIMIT 3;


-- ============================================================================
-- 第二十部分：聊天消息演示数据
-- 绑定到「进行中」和「已完成」订单，展示聊天中心的会话列表与聊天记录。
-- 用 SELECT ... WHERE @var IS NOT NULL 方式插入，订单不存在时自动跳过、不报错。
-- ============================================================================

-- 进行中订单（IN_SERVICE）：客户与陪诊师的实时沟通，末尾留一条未读
SET @o_in := (SELECT id FROM service_order WHERE status='IN_SERVICE' ORDER BY id LIMIT 1);
SET @cust_in := (SELECT customer_id FROM service_order WHERE id=@o_in);
SET @comp_in := (SELECT cp.user_id FROM companion_profile cp JOIN service_order o ON cp.id=o.companion_id WHERE o.id=@o_in);

INSERT INTO chat_message(order_id, from_user_id, to_user_id, content, is_read, created_at)
SELECT @o_in, @cust_in, @comp_in, '您好，我是今天的客户家属，请问您大概几点能到医院？', 1, '2026-08-01 07:30:00' WHERE @o_in IS NOT NULL
UNION ALL SELECT @o_in, @comp_in, @cust_in, '您好！我8:50左右到内分泌科门诊，请让老人先别吃早饭，今天要空腹抽血。', 1, '2026-08-01 07:35:00' WHERE @o_in IS NOT NULL
UNION ALL SELECT @o_in, @cust_in, @comp_in, '好的，老人已经准备好了，医保卡和之前的化验单都带着。', 1, '2026-08-01 07:40:00' WHERE @o_in IS NOT NULL
UNION ALL SELECT @o_in, @comp_in, @cust_in, '我已到门诊并取了号，前面还有几位患者，已完成空腹抽血，血糖结果约1小时后出。', 1, '2026-08-01 09:20:00' WHERE @o_in IS NOT NULL
UNION ALL SELECT @o_in, @cust_in, @comp_in, '辛苦您了！麻烦帮忙记一下医生对用药的调整。', 0, '2026-08-01 09:45:00' WHERE @o_in IS NOT NULL;

-- 已完成订单（COMPLETED）：服务结束后的历史记录（只读）
SET @o_done := (SELECT id FROM service_order WHERE status='COMPLETED' ORDER BY id LIMIT 1);
SET @cust_done := (SELECT customer_id FROM service_order WHERE id=@o_done);
SET @comp_done := (SELECT cp.user_id FROM companion_profile cp JOIN service_order o ON cp.id=o.companion_id WHERE o.id=@o_done);

INSERT INTO chat_message(order_id, from_user_id, to_user_id, content, is_read, created_at)
SELECT @o_done, @cust_done, @comp_done, '您好，麻烦今天多照顾一下老人，谢谢！', 1, '2026-07-01 09:00:00' WHERE @o_done IS NOT NULL
UNION ALL SELECT @o_done, @comp_done, @cust_done, '放心，住院手续我来办，有情况随时同步给您。', 1, '2026-07-01 09:05:00' WHERE @o_done IS NOT NULL
UNION ALL SELECT @o_done, @comp_done, @cust_done, '住院手续已办好，老人已入住病房，押金收据我拍照发您。', 1, '2026-07-01 14:00:00' WHERE @o_done IS NOT NULL
UNION ALL SELECT @o_done, @cust_done, @comp_done, '太感谢了，辛苦您！', 1, '2026-07-01 14:10:00' WHERE @o_done IS NOT NULL;
