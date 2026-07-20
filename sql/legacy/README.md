# sql/legacy

这些是早期按单表拆分的建表脚本和零散脚本，**已全部被 `../init.sql` 覆盖并统一**，仅作历史归档保留，不再维护，也无需执行。

- 各 `*.sql` 单表建表文件：结构已合并进 `init.sql`（且 `init.sql` 含最新字段，这里的可能过时）。
- `create_database.sql`：建库语句已包含在 `init.sql` 顶部。
- `test.sql`：种子账号，已并入 `init.sql`。
- `select.sql`：临时调试查询。

## 正确用法

- 全新初始化：`mysql -uroot -p < sql/init.sql`
- 演示数据（可选）：`mysql -uroot -p < sql/demo_test_data.sql`
- 旧库升级：见 `sql/migrations/`
