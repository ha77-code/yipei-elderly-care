# HANDOFF.md

## 项目名称

"医陪" YiPei -- 银发人群陪诊与生活陪伴服务平台

## 当前状态

后端 11 个模块全部完成，订单完整状态流转 PENDING_ACCEPT→ACCEPTED→IN_SERVICE→PENDING_CONFIRM→COMPLETED。前端 20+ 页面全部就绪，全局视觉已升级。可通过 Postman 完整跑通全部业务流程。

## 一、后端已完成

### 1. 基础设施

| 内容 | 说明 |
|------|------|
| `YipeiApplication.java` | Spring Boot 4.0.7 启动类 |
| `config/CorsConfig.java` | CORS 跨域 |
| `config/WebConfig.java` | 静态资源映射 /uploads |
| `config/AppProperties.java` | 应用配置（上传目录、抽成比例） |
| `constant/RoleConstants.java` | 角色常量 CUSTOMER/COMPANION/ADMIN |
| `exception/` | BusinessException / NotFoundException / ForbiddenException |
| `handler/GlobalExceptionHandler.java` | 统一异常处理 |
| `util/PasswordUtil.java` | SHA-256 + 随机盐密码加密 |
| `util/SubmitLock.java` | Redis 防重复提交锁 |
| `entity/` | 20+ 实体类和 DTO/VO |

### 2. 接口清单

#### 用户模块 UserController

```
POST /api/user/register        注册（加密密码、校验角色、用户名不重复）
POST /api/user/login           登录（加密比对、不返回密码和 token）
GET  /api/user/info?id=        获取用户信息
GET  /api/user/list            用户列表
GET  /api/user/{id}            用户详情
PUT  /api/user/info            修改个人信息（X-User-Id）
PUT  /api/user/{id}/status     管理员修改用户状态
PUT  /api/user/password        修改密码（校验旧密码，加密新密码）
```

#### 陪诊师模块 CompanionController + AdminController

```
POST /api/companion/apply              提交入驻（角色=COMPANION，audit_status=0）
PUT  /api/companion/profile            修改资料（回退待审核）
GET  /api/companion/profile/my         获取我的入驻资料
GET  /api/companion/list?serviceArea=&serviceType=  审核通过列表（按评分降序）
GET  /api/companion/{id}              详情（未审核通过 404）
GET  /api/admin/companion/pending      管理员-待审核列表
PUT  /api/admin/companion/{id}/audit   管理员-审核（1通过/2拒绝，写 audit_record）
```

#### 服务需求模块 ServiceRequestController + AdminController

```
POST /api/service-request/create             发布需求（角色=CUSTOMER）
GET  /api/service-request/list?customerId=    我的需求（按时间倒序）
GET  /api/service-request/{id}                需求详情
PUT  /api/service-request/{id}/cancel         取消需求（校验归属+无订单+状态）
GET  /api/admin/service-request/list?status=&serviceType=  管理员全部需求（筛选）
```

#### 订单模块 OrderController + AdminController

```
POST /api/order/create                     创建订单（校验需求/陪诊师/不重复；平台费=10%）
GET  /api/order/list?userId=&role=          订单列表（CUSTOMER/COMPANION/ADMIN）
GET  /api/order/available?serviceType=&keyword=  可接订单（PENDING_ACCEPT）
GET  /api/order/{id}                        订单详情（含需求+客户/陪诊师名+状态日志）
PUT  /api/order/{id}/accept                 接单（校验陪诊师身份）→ ACCEPTED
PUT  /api/order/{id}/reject                 拒单（记录原因）→ REJECTED
PUT  /api/order/{id}/start                  开始服务 → IN_SERVICE
PUT  /api/order/{id}/complete               完成服务 → PENDING_CONFIRM
PUT  /api/order/{id}/confirm                客户确认完成 → COMPLETED
PUT  /api/order/{id}/cancel                 取消（仅未开始订单）→ CANCELLED
GET  /api/order/{id}/status-log             状态变更记录
GET  /api/admin/orders?status=&customerId=&companionId=  管理员全部订单（筛选）
```

订单状态流转：
```
PENDING_ACCEPT ──accept──▶ ACCEPTED ──start──▶ IN_SERVICE ──complete──▶ PENDING_CONFIRM ──confirm──▶ COMPLETED
       │                      │
       ├──reject──▶ REJECTED  ├──cancel──▶ CANCELLED
       └──cancel──▶ CANCELLED
```

每次状态变更写入 `order_status_log`。

#### 评价模块 EvaluationController

```
POST /api/evaluation/create               创建评价（校验订单已完成、本人参与、不重复、1-5分）
GET  /api/evaluation/order/{orderId}      订单下的评价
GET  /api/evaluation/companion/{companionId}  陪诊师收到的评价
```
- 评价陪诊师后自动更新 `companion_profile.rating`

#### 投诉模块 ReportController + AdminController

```
POST /api/report/create                    发起投诉（校验订单参与方）
GET  /api/admin/report/list?status=        管理员投诉列表
PUT  /api/admin/report/{id}/handle         处理投诉（RESOLVED/REJECTED）
```

#### 服务记录模块 ServiceRecordController

```
POST /api/service-record/create            创建服务记录（仅订单陪诊师）
GET  /api/service-record/order/{orderId}   查看订单服务记录
```

#### 其他

```
POST /api/file/upload                      文件上传（保存至 uploads/）
POST /api/ai/service-summary               AI 需求摘要（Stub）
POST /api/ai/service-tags                  AI 服务标签（Stub）
GET  /api/export/users                     导出用户 CSV
GET  /api/export/orders                    导出订单 CSV
GET  /api/admin/statistics                 平台统计（用户/陪诊师/订单/投诉/收入）
```

## 二、前端已完成

### 全局设计系统 (App.vue)

- CSS 变量体系：品牌色/中性色/阴影/圆角/字体
- ElementUI 全局覆写：按钮/表格/表单/弹窗/标签/分页/菜单

### 布局 (MainLayout.vue)

- 64px 毛玻璃顶栏，导航下划线展开动画
- 侧边栏左侧竖线激活指示器
- 响应式设计

### 页面清单

| 路由 | 页面 | 状态 |
|------|------|------|
| `/login` | 登录（液态玻璃 + 光感交互） | ✅ |
| `/register` | 注册 | ✅ |
| `/` | 首页（快捷入口 + 统计） | ✅ |
| `/profile` | 个人信息（含修改密码） | ✅ |
| `/customer/companions` | 陪诊师列表（筛选） | ✅ |
| `/customer/request/create` | 发布需求 | ✅ |
| `/customer/requests` | 我的需求 | ✅ |
| `/customer/orders` | 客户订单列表 | ✅ |
| `/companion/:id` | 陪诊师详情 | ✅ |
| `/companion/profile` | 入驻资料 | ✅ |
| `/companion/available-orders` | 可接订单（卡片+渐变条） | ✅ |
| `/companion/orders` | 陪诊师订单 | ✅ |
| `/companion/service-records` | 服务记录列表 | ✅ |
| `/order/:id` | 订单详情（状态流+操作+评价+投诉+时间线） | ✅ |
| `/order/:id/service-record` | 服务记录 | ✅ |
| `/admin/dashboard` | 后台首页 | ✅ |
| `/admin/users` | 用户管理 | ✅ |
| `/admin/companion-review` | 陪诊师审核 | ✅ |
| `/admin/requests` | 需求管理 | ✅ |
| `/admin/orders` | 订单管理 | ✅ |
| `/admin/reports` | 投诉处理 | ✅ |
| `/admin/statistics` | 平台统计（12 卡片） | ✅ |

### API 封装 (frontend/src/api/)

全部 9 个 API 文件均已加 `/api` 前缀，baseURL=`http://localhost:8080`，自动注入 `X-User-Id`。

## 三、数据库表

| 表名 | Entity | 状态 |
|------|--------|------|
| `sys_user` | `SysUser.java` | ✅ |
| `companion_profile` | `CompanionProfile.java` | ✅ |
| `service_request` | `ServiceRequest.java` | ✅ |
| `service_order` | `ServiceOrder.java` | ✅ |
| `order_status_log` | `OrderStatusLog.java` | ✅ |
| `service_record` | `ServiceRecord.java` | ✅ |
| `evaluation` | `Evaluation.java` | ✅ |
| `audit_record` | `AuditRecord.java` | ✅ |
| `report_record` | `ReportRecord.java` | ✅ |

### 测试数据

```sql
INSERT INTO sys_user(username, password, nickname, phone, role, status, created_at, updated_at)
VALUES
('customer1', '123456', '张三', '13800000001', 'CUSTOMER', 1, NOW(), NOW()),
('companion1', '123456', '李陪诊', '13800000002', 'COMPANION', 1, NOW(), NOW()),
('admin1', '123456', '管理员', '13800000003', 'ADMIN', 1, NOW(), NOW());
```

注意：密码现在是 SHA-256 加密存储，需通过 API 注册才会自动加密。直接 SQL INSERT 的明文密码无法登录。

## 四、待完成

| 内容 | 说明 |
|------|------|
| 密码加密后旧数据 | 数据库中旧明文密码无法登录，运行 `sql/init.sql` 重新初始化 |
| AI 接口 | 目前是 Stub 占位，需接入真实大模型 API |
| companion 目录额外页面 | Apply.vue、Evaluate.vue、CompanionList.vue、Order.vue 等文件存在但未在 router 注册 |
| 测试数据 | 需通过 API 注册或运行 `sql/init.sql` 导入预加密测试账号 |

## 五、技术基线

- JDK 17 / Spring Boot 4.0.7 / MyBatis 4.0.1 / MySQL 8 / Redis
- Vue2 + ElementUI + Axios

## 六、开发约定

- `ApiResponse<T>` 统一返回 `{ code, message, data }`
- 业务异常继承 `BusinessException`，GlobalExceptionHandler 统一处理
- 身份通过 `X-User-Id` 请求头传递（第一版无 token）
- 订单状态变更同步写入 `order_status_log`
- 密码 SHA-256 + 随机盐加密，不存明文
- 不描述成医疗诊断或治疗系统

## 七、启动步骤

```bash
# 1. MySQL 一键初始化（建库+建表+测试数据）
mysql -u root -p < sql/init.sql

# 2. 启动后端（Redis 非必须，未启动时自动降级）
mvn spring-boot:run

# 3. 注册测试用户（Postman）
POST http://localhost:8080/api/user/register
Body: {"username":"cus","password":"123456","nickname":"张三","phone":"13800000001","role":"CUSTOMER"}

# 4. 启动前端
cd frontend && npm install && npm run serve
```

## 八、验证记录

- 后端编译：`mvn compile -q` 通过
- 全流程跑通：注册→登录→入驻→审核→需求→订单→接单→开始→完成→详情→状态日志，全部 200
