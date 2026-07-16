USE yipei;

-- Password for all seed users: 123456
-- Format matches com.yipei.util.PasswordUtil.
INSERT INTO sys_user(username, password, nickname, phone, role, status, created_at, updated_at)
VALUES
    ('customer1', 'eWlwZWktY3VzdG9tZXItMQ==:eJtfcMKaWA9YU1earQo/dYCpEXeKwJRa2zONf8N8pa8=', 'Zhang San', '13800000001', 'CUSTOMER', 1, NOW(), NOW()),
    ('companion1', 'eWlwZWktY29tcGFuaW9u:GJrEqXXIMUMpvW6j0iT5UGZf86Q2DXpSsXONwJ1Hqyo=', 'Li Peizhen', '13800000002', 'COMPANION', 1, NOW(), NOW()),
    ('admin1', 'eWlwZWktYWRtaW4tMDAw:+TZrSOeqcqAYBARD1OBrheJbXWC4T2Mt9RWFndsMtyo=', 'Admin', '13800000003', 'ADMIN', 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE
    password = VALUES(password),
    nickname = VALUES(nickname),
    phone = VALUES(phone),
    role = VALUES(role),
    status = VALUES(status),
    updated_at = NOW();
