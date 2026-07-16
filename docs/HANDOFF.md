# HANDOFF.md

## 项目名称

"医陪"——银发人群陪诊与生活陪伴服务平台

英文名称：YiPei — Elderly Medical Escort and Daily Companionship Service Platform

## 当前状态

项目已完成用户模块、服务需求模块、订单模块（含完整状态流转）的后端接口，以及前端的请求封装、登录注册和个人信息页面对接。陪诊师、评价、投诉、服务记录模块尚未实现。

当前项目的有效资料包括：

- `docs/项目设计文档.md`：产品、业务、技术和验收说明；
- `README.md`：项目简介、功能范围和技术栈；
- `docs/HANDOFF.md`：当前开发状态和后续交接说明。

## 一、已完成内容

### 1. 基础设施

| 内容 | 说明 |
|------|------|
| Spring Boot 主启动类 | `YipeiApplication` |
| 项目依赖 | MyBatis、MySQL 8、Redis、Lombok、Validation |
| CORS 跨域配置 | `config/CorsConfig.java`，允许前端直连 `localhost:8080` |
| AppProperties 配置 | `config/AppProperties.java`，前缀 `yipei`，含 uploadDir / platformFeeRate |
| 数据库 | 9 张表已创建，测试数据需手动导入 |
| 前端项目 | Vue2 + ElementUI，`frontend/` 目录 |

### 2. 统一响应与异常处理

| 文件 | 说明 |
|------|------|
| `entity/ApiResponse.java` | 统一响应格式 `{ code, message, data }` |
| `exception/BusinessException.java` | 业务异常基类，携带 `code` + `message` |
| `exception/NotFoundException.java` | 继承 BusinessException，code=404 |
| `exception/ForbiddenException.java` | 继承 BusinessException，code=403 |
| `handler/GlobalExceptionHandler.java` | `@RestControllerAdvice`，统一处理 BusinessException / Validation / 未知异常 |

### 3. 用户模块

**接口：**

```
POST /api/user/register       注册（校验角色=CUSTOMER/COMPANION/ADMIN，校验用户名不重复）
POST /api/user/login          登录（校验账号存在、密码正确、状态正常，不返回密码和 token）
GET  /api/user/info?id=       获取用户信息（不返回密码）
GET  /api/user/list           用户列表
GET  /api/user/{id}           用户详情
PUT  /api/user/info           修改个人信息（需 X-User-Id header）
```

- 第一版不做 token，身份通过 `X-User-Id` 请求头传递
- 登录和注册返回均不包含 password 字段

### 4. 服务需求模块

**接口：**

```
POST /api/service-request/create             发布需求（校验角色=CUSTOMER，默认 status=PENDING）
GET  /api/service-request/list?customerId=    我的需求列表（按创建时间倒序）
GET  /api/service-request/{id}                需求详情
PUT  /api/service-request/{id}/cancel         取消需求（校验归属、无订单、状态可取消）
```

**管理员需求管理：**

```
GET  /api/admin/service-request/list?status=&serviceType=   全部需求（支持按状态/类型筛选）
GET  /api/admin/statistics                                  平台统计数据
```

### 5. 订单模块 ⬅ 本次新增

**后端文件：**

| 文件 | 说明 |
|------|------|
| `entity/ServiceOrder.java` | 对应 `service_order` 表 |
| `entity/OrderDetailVO.java` | 订单详情 VO，含客户名、陪诊师名、需求摘要、状态变更记录 |
| `entity/OrderCreateRequest.java` | 创建订单 DTO |
| `entity/OrderStatusLog.java` | 状态变更记录实体 |
| `mapper/ServiceOrderMapper.java` | insert / selectById / selectByRole / selectAvailable / selectDetailById / accept / start / complete / updateStatus |
| `mapper/OrderStatusLogMapper.java` | selectByOrderId / insert |
| `service/OrderService.java` | create / listByRole / listAvailable / getDetail / accept / reject / start / complete |
| `controller/OrderController.java` | 见下方接口列表 |

**订单接口：**

```
POST /api/order/create                  创建订单（校验需求归属、陪诊师存在、不重复创建；计算平台费=10%）
GET  /api/order/list?userId=&role=      订单列表（CUSTOMER查自己/COMPANION查自己/ADMIN全部）
GET  /api/order/available?serviceType=&keyword=  可接订单（PENDING_ACCEPT状态，支持按类型/区域筛选）
GET  /api/order/{id}                    订单详情（含需求摘要+客户/陪诊师名+状态变更记录）
PUT  /api/order/{id}/accept             接单（校验陪诊师身份 PENDING_ACCEPT→ACCEPTED）
PUT  /api/order/{id}/reject             拒绝（PENDING_ACCEPT→REJECTED，记录拒绝原因）
PUT  /api/order/{id}/start              开始服务（ACCEPTED→IN_SERVICE）
PUT  /api/order/{id}/complete           提交完成（IN_SERVICE→PENDING_CONFIRM）
```

**订单状态流转：**

```
PENDING_ACCEPT ──accept──▶ ACCEPTED ──start──▶ IN_SERVICE ──complete──▶ PENDING_CONFIRM
       │                       │
       └──reject──▶ REJECTED   └──cancel──▶ CANCELLED（待实现）
```

每个状态变更均写入 `order_status_log`，记录 `from_status` / `to_status` / `operator_id` / `remark`。

**费用计算规则：**

```
platformFee    = servicePrice × 10%（四舍五入到分）
companionIncome = servicePrice − platformFee
```

### 6. 前端已对接

| 文件 | 说明 |
|------|------|
| `api/request.js` | Axios 封装，baseURL=`http://localhost:8080`，自动注入 `X-User-Id`，统一处理 code===200 |
| `api/user.js` | `/api` 前缀，封装 register / login / getUserInfo / updateUserInfo / getUserList |
| `api/serviceRequest.js` | `/api` 前缀，封装 create / list / detail / cancel / admin list |
| `api/order.js` | `/api` 前缀，封装 create / list / available / detail / accept / start / complete / confirm / cancel |
| `api/admin.js` | `/api` 前缀，封装 getUserList / updateUserStatus / getPendingCompanions / getStatistics |
| `utils/auth.js` | localStorage 管理用户信息 |
| `utils/testAccounts.js` | 开发环境测试账号快速登录 |
| `views/Login.vue` | 液态玻璃+光感交互设计，已对接 login |
| `views/Register.vue` | 已对接 register |
| `views/Profile.vue` | 已对接 getUserInfo / updateUserInfo |
| `views/customer/MyRequests.vue` | 已对接 getMyRequests，传入 customerId |
| `views/customer/RequestCreate.vue` | 已对接 createRequest |
| `views/CompanionDetail.vue` | 已创建，等待后端陪诊师接口 |
| `views/ServiceRecord.vue` | 已创建，等待后端服务记录接口 |

## 二、数据库表

| 表名 | 说明 | 后端 Entity |
|------|------|-------------|
| `sys_user` | 用户 | `SysUser.java` |
| `companion_profile` | 陪诊师资料 | 未创建 |
| `service_request` | 服务需求 | `ServiceRequest.java` |
| `service_order` | 服务订单 | `ServiceOrder.java` |
| `order_status_log` | 状态变更记录 | `OrderStatusLog.java` |
| `service_record` | 服务记录 | 未创建 |
| `evaluation` | 评价 | 未创建 |
| `audit_record` | 审核记录 | 未创建 |
| `report_record` | 投诉举报 | 未创建 |

### 测试数据

```sql
INSERT INTO sys_user(username, password, nickname, phone, role, status, created_at, updated_at)
VALUES
('customer1', '123456', '张三', '13800000001', 'CUSTOMER', 1, NOW(), NOW()),
('companion1', '123456', '李陪诊', '13800000002', 'COMPANION', 1, NOW(), NOW()),
('admin1', '123456', '管理员', '13800000003', 'ADMIN', 1, NOW(), NOW());
```

## 三、待完成

### 后端

| 模块 | 状态 | 涉及接口 |
|------|------|---------|
| 用户模块 | ✅ 完成 | 6 个接口 |
| 服务需求模块 | ✅ 完成 | 4 个接口 + 管理员筛选 |
| 订单模块 | ⚠️ 部分 | 创建/列表/详情/可接/接单/拒绝/开始/完成已实现；取消/确认完成待实现 |
| 陪诊师模块 | ❌ 未开始 | apply / list / detail / profile / audit |
| 评价模块 | ❌ 未开始 | submit / list / received |
| 投诉模块 | ❌ 未开始 | submit / list / detail / handle |
| 服务记录模块 | ❌ 未开始 | create / detail / list |

### 前端

| 问题 | 说明 |
|------|------|
| API 路径 `/api` 前缀 | `companion.js` / `evaluation.js` / `report.js` / `serviceRecord.js` 尚未加 `/api` 前缀 |
| 页面传参 | 各页面调用 GET /list 类接口时需传入 customerId 或 userId |

## 四、技术基线

- JDK 17
- Spring Boot 4.0.7
- MyBatis 4.0.1
- MySQL 8
- Redis（已配置，当前未使用）
- Vue2 + ElementUI + Axios

## 五、开发约定

- 统一使用 `ApiResponse<T>` 作为返回格式；
- 业务异常继承 `BusinessException`，由 `GlobalExceptionHandler` 统一处理；
- 第一版不做 token，身份通过 `X-User-Id` 请求头传递；
- 订单状态变更同步写入 `order_status_log`，记录操作人和时间；
- 不在代码中写入真实 API Key、手机号或身份证信息；
- 不把平台描述成医疗诊断或医疗治疗系统。

## 六、验证记录

- 后端编译：`mvn compile -q` 通过，无错误
- 数据库：9 张表已创建
- 接口验证：可用 curl 命令行逐接口测试
