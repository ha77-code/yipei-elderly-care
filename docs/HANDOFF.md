# HANDOFF.md

## 项目名称

“医陪”——银发人群陪诊与生活陪伴服务平台

英文名称：YiPei — Elderly Medical Escort and Daily Companionship Service Platform

## 当前状态

项目已完成基础 Spring Boot 项目骨架、数据库脚本整理和用户列表查询接口，尚未完成完整业务闭环。

当前项目的有效资料包括：

- `docs/项目设计文档.md`：产品、业务、技术和验收说明；
- `README.md`：项目简介、功能范围和技术栈；
- `docs/HANDOFF.md`：当前开发状态和后续交接说明。

## 已完成内容

### 1. 项目基础结构

- 已创建 Spring Boot 主启动类 `YipeiApplication`；
- 已建立 `controller`、`entity`、`mapper`、`service` 基础代码目录；
- 已配置 MyBatis 和 MySQL 相关项目依赖；
- 已保留 Vue 前端目录，但目前尚未实现前端页面。

### 2. 数据库和 SQL 脚本

- 已删除并重建 `yipei` 数据库；
- 数据库使用 `utf8mb4` 字符集和 `utf8mb4_0900_ai_ci` 排序规则；
- 已根据设计文档和 SQL 文件创建以下 9 张表：
  - `sys_user`：系统用户表；
  - `companion_profile`：陪诊师资料表；
  - `audit_record`：审核记录表；
  - `service_request`：陪诊服务需求表；
  - `service_order`：服务订单表；
  - `order_status_log`：订单状态变更记录表；
  - `service_record`：陪诊服务记录表；
  - `evaluation`：订单评价表；
  - `report_record`：订单投诉记录表。
- 已在 SQL 表定义前补充简要表用途注释；
- 当前数据库表已创建，但业务数据和测试数据需要后续按需导入；
- `sql/test.sql` 可用于插入测试用户，尚未作为正式初始化脚本执行。

### 3. 用户列表查询功能

- 已创建 `SysUser` 用户实体，对应 `sys_user` 表；
- 已创建 `SysUserMapper`，支持按用户编号倒序查询全部用户；
- 已创建 `UserService`，封装用户列表查询业务；
- 已创建 `UserController`，提供以下接口：

```text
GET /api/user/list
```

该接口返回系统用户列表。

### 4. 当前已有但未实现的代码

- `ServiceRequestController` 已建立空控制器类，目前没有接口逻辑；
- `frontend` 目录目前没有实际页面；
- 注册、登录、角色权限和密码加密尚未完成；
- 陪诊师资料、需求发布、订单、服务记录、评价和投诉功能尚未完成。

## 数据库表之间的主要关系

```text
sys_user
  ├── companion_profile
  ├── service_request
  ├── service_order
  ├── evaluation
  ├── audit_record
  └── report_record

service_request
  └── service_order

service_order
  ├── order_status_log
  ├── service_record
  ├── evaluation
  └── report_record
```

## 技术基线

- JDK 21；
- Spring Boot 3.x；
- MyBatis；
- MySQL 8；
- Redis；
- Vue2；
- ElementUI；
- Axios。

项目采用单体 Spring Boot 应用，不提前拆分微服务。

## 角色和业务范围

- `CUSTOMER`：老人或家属用户；
- `COMPANION`：陪诊师或陪伴师；
- `ADMIN`：系统管理员。

平台只提供陪同、路线引导、手续协助、信息记录和生活陪伴，不提供医疗诊断、治疗、用药或医疗操作。

## 明确不做

- 医疗诊断、治疗、用药和医疗操作；
- 真实支付、提现和资金托管；
- 实时 GPS 和轨迹；
- WebSocket 实时聊天；
- 视频通话；
- 短信验证码；
- 医院系统对接；
- 医疗保险；
- 真实个人健康数据。

## 后续开发顺序

1. 完成用户注册、登录和角色权限；
2. 完成陪诊师资料、新增和入驻审核；
3. 完成服务需求发布和需求列表；
4. 完成陪诊师列表和详情；
5. 完成订单创建、接单和状态流转；
6. 完成服务记录、完成确认和评价；
7. 完成管理员用户、订单、陪诊师和投诉管理；
8. 接入文本 AI 需求摘要或服务标签；
9. 使用虚拟数据完成完整演示。

## 开发约定

- 修改前先阅读 `docs/项目设计文档.md`；
- 不把平台描述成医疗诊断或医疗治疗系统；
- 不在代码中写入真实 API Key、手机号或身份证信息；
- AI 只做文本整理和标签提取，失败时不能阻塞业务流程；
- 真实支付、地图和实时聊天不作为当前任务；
- 修改后执行编译、接口或静态检查，并记录验证结果。

## 验证记录

- 数据库已验证包含 9 张业务表和 16 个外键约束；
- 曾尝试执行 `mvnw.cmd -q -DskipTests compile`，但当前 PowerShell 环境下 Maven Wrapper 启动失败，报错为 `Cannot index into a null array`，后续需要修复本地 Maven Wrapper 启动环境后重新编译验证。
