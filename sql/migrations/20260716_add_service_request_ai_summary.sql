USE yipei;

SET @ai_summary_column_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'service_request'
      AND column_name = 'ai_summary'
);
SET @ai_summary_migration_sql = IF(
    @ai_summary_column_exists = 0,
    'ALTER TABLE service_request ADD COLUMN ai_summary VARCHAR(1000) AFTER special_notes',
    'SELECT 1'
);
PREPARE ai_summary_migration_statement FROM @ai_summary_migration_sql;
EXECUTE ai_summary_migration_statement;
DEALLOCATE PREPARE ai_summary_migration_statement;
