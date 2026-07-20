USE yipei;

-- 为 sys_user 增加头像字段（幂等，重复执行不会报错）
SET @avatar_column_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'sys_user'
      AND column_name = 'avatar'
);
SET @avatar_migration_sql = IF(
    @avatar_column_exists = 0,
    'ALTER TABLE sys_user ADD COLUMN avatar VARCHAR(500) AFTER phone',
    'SELECT 1'
);
PREPARE avatar_migration_statement FROM @avatar_migration_sql;
EXECUTE avatar_migration_statement;
DEALLOCATE PREPARE avatar_migration_statement;
