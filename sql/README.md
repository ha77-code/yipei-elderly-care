# sql 使用说明

本目录存放益陪养老平台的数据库脚本。所有种子账号密码均为 `123456`。

## 目录结构

| 路径 | 作用 |
|------|------|
| `init.sql` | 全新初始化：建库 + 建全部表 + 3 个基础账号。**新装必跑。** |
| `demo_test_data.sql` | 完整演示数据（24 用户 / 21 需求 / 14 订单 / 聊天等），可选。 |
| `migrations/` | 增量升级脚本，仅用于给已有旧库补结构。 |
| `legacy/` | 早期按单表拆分的历史脚本，已被 `init.sql` 覆盖，无需执行。 |

## 一、全新安装（推荐）

```bash
mysql -u root -p < sql/init.sql            # 建库建表 + 基础账号
mysql -u root -p < sql/demo_test_data.sql  # 灌入完整演示数据（可选）
```

- 只跑 `init.sql`：得到空库 + 3 个账号 `customer1` / `companion1` / `admin1`，可登录但没有业务数据。
- 再跑 `demo_test_data.sql`：得到完整可演示的数据，覆盖需求广场、需求审核、订单全生命周期、评价、投诉、聊天等所有功能。

> MySQL 的 root 密码不是 `123456` 时，先改 `src/main/resources/application.yaml` 里的 `spring.datasource.password`。

## 二、演示数据说明（demo_test_data.sql）

- **可重复执行**：脚本开头会 `SET FOREIGN_KEY_CHECKS=0` 后 `TRUNCATE` 清空所有业务表，再重新灌入，因此每次运行结果都一致、不会产生重复数据。
- ⚠️ **具破坏性**：会清空库中现有业务数据，**仅用于演示 / 测试环境，切勿在生产库运行**。
- 关键点：需求的 `audit_status` 会被正确设置——多数待匹配需求设为「已通过」进入陪诊师需求广场，另保留 3 条「待审核」用于演示管理员审核流程。若插入需求时漏设 `audit_status`（默认 0=待审核），需求广场会为空。

### 演示账号（密码均 123456）

| 角色 | 账号 |
|------|------|
| 家属 / 老人 | `customer1` ~ `customer10`、`elder01`、`elder02`、`family01` |
| 陪诊师 | `companion1` ~ `companion8` |
| 管理员 | `admin1` ~ `admin3` |

## 三、已有旧库升级

如果之前已经建过库，只是缺少新功能的字段或表（如撮合、聊天），**不要重跑 `init.sql`**（避免影响已有数据），改为按需执行 `migrations/` 下的增量脚本。所有增量脚本都是幂等的，可重复执行、不会破坏已有数据：

| 脚本 | 作用 |
|------|------|
| `20260716_add_service_request_ai_summary.sql` | service_request 增加 AI 摘要字段 |
| `20260720_add_sys_user_avatar.sql` | sys_user 增加头像字段 |
| `20260720_add_sys_user_avatar_audit.sql` | sys_user 增加头像审核字段 |
| `20260720_add_user_notification.sql` | 用户、陪诊师、管理员的系统通知表 |
| `20260721_add_matching_and_chat.sql` | 需求审核字段 + 陪诊师申请表 + 私信聊天表 |

执行示例：

```bash
mysql -u root -p < sql/migrations/20260720_add_user_notification.sql
mysql -u root -p < sql/migrations/20260721_add_matching_and_chat.sql
```

> 全新安装无需关心 `migrations/`——`init.sql` 已包含所有最新结构。
