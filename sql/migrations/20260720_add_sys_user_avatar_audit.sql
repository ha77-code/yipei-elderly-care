USE yipei;

-- 为 sys_user 增加头像审核相关字段（幂等，重复执行不会报错）
-- pending_avatar：待审核的新头像；avatar_audit_status：NULL 无待审 / 0 待审核 / 1 通过 / 2 拒绝
SET @pending_exists = (
    SELECT COUNT(*) FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = 'sys_user' AND column_name = 'pending_avatar'
);
SET @sql1 = IF(@pending_exists = 0,
    'ALTER TABLE sys_user ADD COLUMN pending_avatar VARCHAR(500) AFTER avatar',
    'SELECT 1');
PREPARE s1 FROM @sql1; EXECUTE s1; DEALLOCATE PREPARE s1;

SET @status_exists = (
    SELECT COUNT(*) FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = 'sys_user' AND column_name = 'avatar_audit_status'
);
SET @sql2 = IF(@status_exists = 0,
    'ALTER TABLE sys_user ADD COLUMN avatar_audit_status TINYINT AFTER pending_avatar',
    'SELECT 1');
PREPARE s2 FROM @sql2; EXECUTE s2; DEALLOCATE PREPARE s2;
