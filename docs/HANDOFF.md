# HANDOFF.md

## 项目名称

"医陪" YiPei -- 银发人群陪诊与生活陪伴服务平台

## 当前状态

后端已完成普通需求申请撮合、指定陪诊师订单、订单完整状态流转、私信聊天、用户通知及管理员审核。当前正式前端不是普通 Vue 侧边栏工作台，而是 `frontend/public/*-concept-light.html` 三个原版轮盘页面；所有新增功能必须继续在原轮盘滑动面板内部扩展。

## 2026-07-21 本次会话更新（最高优先级）

### A. 页面切换叠加 Bug 修复

`.panel-page` 原为 `height:100vh` 且默认 `overflow:visible`，某页内容超过一屏时会越界画到相邻页视口，出现"上一页残留 + 当前页只覆盖一部分"的叠加。已给三个轮盘页的 `.panel-page` 统一补 `overflow:hidden`，每页裁剪在自己一屏内，不影响内部 `.yp-scroll` 滚动，也不新建 stacking context（不破坏白瓷磨砂 `backdrop-filter`）。

### B. 轮盘聊天红点

- 客户/陪诊师轮盘的"订单"节点右上角新增红点，有未读私信时显示（`arc-chat-dot`，脉冲动画）。红点与"聊天记录"标签内既有的未读数字徽标并存，可在任意轮盘页看到未读提醒。
- 实现：`frontend/public/concept-chat-dot.js` 把红点 circle 追加到 `#nodesGroup` 的订单节点组，用 `requestAnimationFrame` 同步节点当前 `cx/cy`（客户订单节点 index 3、陪诊师 index 2）。**未改动内联轮盘动画代码。**
- 未读来源：`renderOrders` 计算会话未读总数后调用 `YP.setChatDot(n)`；`concept-live.js` 每 20 秒刷新时自动同步。
- 聊天权限沿用后端 `ChatService.OPEN_STATUSES`（`ACCEPTED/IN_SERVICE/PENDING_CONFIRM`）：仅双方达成后到服务结束前可收发；`COMPLETED/CANCELLED` 后 `/chat/{id}/open` 返回 false，前端聊天窗自动转为只读，仅可查看历史。

### C. 语音朗读（老年人 / 视障友好）

- 新增 `frontend/public/concept-tts.js`，暴露 `YP.speak({path,method,body,button})` 与 `YP.stopSpeak()`；直接 `fetch` 音频流（成功返回 `audio/mpeg`，出错返回 JSON），再次点击同一按钮即停止，按钮朗读中显示"◼ 停止朗读"并有脉冲态。
- 客户"陪诊师"页每张陪诊师卡片有"🔊 朗读信息"按钮 → `GET /api/tts/companion/{id}`，朗读姓名、区域、经验、擅长、介绍。
- 客户"发布需求"表单有"🔊 朗读已填内容"按钮 → `POST /api/tts/speak`，把当前已填的服务类型/时间/医院/科室/联系人/预算/需求/特殊说明拼成文本朗读，方便核对后再提交。
- TTS 已在 `application.yaml` 配好火山引擎（`yipei.tts`，含 17 个音色）。后端未配置时接口返回 503，前端提示"语音功能未接入"。

### D. 约定

- **每次功能更新后都要同步更新本 HANDOFF。**

## 2026-07-20 本次会话更新（最高优先级）

### 1. 前端基线与不可违反的约束

- 项目直接打开时必须先进入 `frontend/public/landing.html`。`frontend/public/index.html` 会在没有 `?skip=1` 时跳转到 `landing.html`。
- 登录成功或从落地页进入个人中心时，按角色进入：
  - 客户：`frontend/public/customer-concept-light.html`
  - 陪诊师：`frontend/public/companion-concept-light.html`
  - 管理员：`frontend/public/admin-concept-light.html`
- 必须保留以下原版效果，不能再次改成普通侧边栏或另一套 Vue 工作台：
  - 左侧弧形轮盘节点。
  - 鼠标滚轮旋转轮盘。
  - 右侧 `.panel-viewport` / `.panel-page` 纵向整屏滑动及 scroll snap。
  - `goPage(index)` 与轮盘联动。
  - GSAP 动画、自定义光标、粒子、聚光灯、竹林背景和白瓷磨砂卡片。
- 三个页面的轮盘节点和滑动面板数量保持不变：客户 `4/4`、陪诊师 `5/5`、管理员 `7/7`。
- 三个原轮盘 HTML 的现有动画代码没有修改，只增加了公共样式和功能脚本引用。
- 原 Vue `MainLayout` 及旧路由页面仍保留在仓库中，但不是当前正式业务入口。后续不要把新增功能重新接回侧边栏布局。

### 2. 新增前端公共层

| 文件 | 作用 |
|------|------|
| `frontend/public/concept-functional.css` | 在原白瓷磨砂设计体系内补充标签、表单、按钮、弹窗、聊天气泡和通知样式 |
| `frontend/public/concept-runtime.js` | 角色校验、API 请求、状态格式化、Toast、Modal、通知列表、聊天窗口和只读历史 |
| `frontend/public/concept-chat-navigation.js` | 点击私信通知后旋转到订单轮盘页、切换聊天标签并打开对应订单会话 |
| `frontend/public/concept-live.js` | 每 20 秒静默刷新通知、聊天未读、订单等数据 |
| `frontend/public/concept-reports.js` | 用户和陪诊师在订单卡片中提交投诉/反馈 |
| `frontend/public/concept-tts.js` | 语音朗读公共层：`YP.speak()` / `YP.stopSpeak()`，拉取 `/api/tts` 音频流播放 |
| `frontend/public/concept-chat-dot.js` | 轮盘订单节点未读私信红点，rAF 同步节点位置（不改动内联轮盘动画） |

静态轮盘页面通过 `localStorage/sessionStorage` 的 `yipei_user` 获取登录用户，并向后端自动添加 `X-User-Id`。

### 3. 客户轮盘已完成

客户轮盘仍为 4 页：

1. 首页
   - 真实订单、陪诊师和进行中统计。
   - 消息通知与全部已读。
   - 个人资料和密码修改。
2. 陪诊师
   - 读取审核通过的真实陪诊师。
   - 搜索、查看服务信息和指定陪诊师。
3. 发布需求
   - “发布需求 / 我的需求”标签。
   - 普通公开需求发布。
   - 指定陪诊师需求发布，指定时必须填写大于 0 的预算。
   - 查看需求审核状态、取消需求。
   - 查看陪诊师申请，客户可以同意或拒绝；同意后自动生成 `ACCEPTED` 订单。
4. 订单
   - “订单进度 / 聊天记录”标签。
   - 查看状态日志、确认完成、取消订单、评价陪诊师、投诉反馈。
   - 点击会话卡片或私信通知进入对应订单聊天。

相关脚本：

- `frontend/public/customer-base.js`
- `frontend/public/customer-profile.js`
- `frontend/public/customer-requests.js`
- `frontend/public/customer-orders.js`

### 4. 陪诊师轮盘已完成

陪诊师轮盘仍为 5 页：

1. 工作台
   - 真实统计、入驻认证资料、审核状态和消息通知。
2. 可接订单
   - “需求广场 / 指定给我的 / 我的申请”标签。
   - 公开需求申请和撤回申请。
   - 客户指定订单接受或拒绝。
3. 我的订单
   - “订单进度 / 聊天记录”标签。
   - 开始服务、提交服务完成、状态日志、私信和历史记录。
4. 服务记录
   - 查看和填写订单服务记录。
5. 客户评价
   - 查看真实评分和评价内容。

相关脚本：

- `frontend/public/companion-base.js`
- `frontend/public/companion-work.js`
- `frontend/public/companion-records.js`

### 5. 管理员轮盘已完成

管理员轮盘仍为 7 页：

1. 工作台：真实统计、管理员待办通知和快捷入口。
2. 用户：搜索、角色/状态筛选、启用和禁用账号。
3. 审核：“陪诊师 / 头像 / 服务需求”三类审核标签。
4. 需求：全部需求、审核和撮合状态、指定陪诊师信息。
5. 订单：全部订单、状态筛选和订单状态日志。
6. 投诉：开始处理、解决或驳回，并通知投诉人。
7. 统计：用户、订单、投诉、平台收入和陪诊师收入。

相关脚本：

- `frontend/public/admin-base.js`
- `frontend/public/admin-manage.js`
- `frontend/public/admin-operations.js`

### 6. 订单撮合流程

#### 普通公开需求

```text
客户发布需求
  → 管理员审核通过
  → 需求进入陪诊师需求广场
  → 陪诊师提交申请
  → 客户同意某个申请
  → 自动生成 ACCEPTED 订单并拒绝其他申请
  → 双方私信开启
```

#### 客户指定陪诊师

```text
客户选择指定陪诊师并发布需求
  → 管理员审核通过
  → 自动生成 PENDING_ACCEPT 订单
  → 指定陪诊师接受：进入 ACCEPTED，私信开启
  → 指定陪诊师拒绝：订单 REJECTED，需求 CLOSED
```

关键后端文件：

- `ApplicationService.java`
- `ServiceRequestService.java`
- `OrderService.java`
- `ServiceApplicationMapper.java`
- `ServiceRequestMapper.java`

### 7. 私信聊天规则

- 私信只存在于客户和陪诊师的订单轮盘页中。
- 仅双方已经同意服务的订单可以发送消息，后端允许状态：
  - `ACCEPTED`
  - `IN_SERVICE`
  - `PENDING_CONFIRM`
- `PENDING_ACCEPT` 表示指定陪诊师尚未同意，不能聊天。
- `COMPLETED`、`CANCELLED` 等结束状态不能继续发送，只能查看已有聊天记录。
- 后端读取历史记录时会标记聊天消息已读。
- 每次发送私信都会写入一条 `CHAT_MESSAGE` 用户通知，消息和通知在同一事务内完成。
- 点击“收到新的私信”通知时：
  1. 自动旋转到角色的订单轮盘页。
  2. 自动切换到“聊天记录”标签。
  3. 自动打开通知关联订单的聊天界面。
  4. 对应聊天通知标记为已读。
- 服务结束后聊天弹窗显示“服务已结束，无法继续聊天，仅可查看聊天记录”。

关键文件：

- `src/main/java/com/yipei/service/ChatService.java`
- `src/main/java/com/yipei/mapper/ChatMessageMapper.java`
- `frontend/public/concept-runtime.js`
- `frontend/public/concept-chat-navigation.js`

### 8. 消息通知

已覆盖：

- 陪诊师资料、头像、服务需求审核待办和审核结果。
- 陪诊师提交/撤回申请，客户同意/拒绝申请。
- 指定订单、接单、拒单、开始服务、待确认、完成和取消。
- 投诉提交和投诉处理结果。
- 新私信 `CHAT_MESSAGE` 通知。
- 前端通知和聊天未读数每 20 秒静默刷新。

通知接口：

```text
GET /api/notifications
GET /api/notifications/unread
PUT /api/notifications/{id}/read
PUT /api/notifications/read-all
```

### 9. 数据库迁移状态

迁移文件：

- `sql/migrations/20260720_add_user_notification.sql`
- `sql/migrations/20260721_add_matching_and_chat.sql`

当前开发机 MySQL `3306` 未监听，因此本次会话没有实际执行迁移。启动数据库后必须先执行上述迁移；尤其是 `user_notification` 表不存在时，私信通知写入会失败，并因事务回滚导致该次消息发送失败。

### 10. 验证结果

- `./mvnw.cmd test`：9 个测试全部通过。
- 新增 `ChatServiceTest`：
  - 验证双方同意后可以发送消息并生成订单私信通知。
  - 验证服务完成后发送被后端拒绝。
- `npm run build`：生产构建成功。
- 前端构建只有原有资源体积 warning，无编译错误。
- 所有新增轮盘脚本通过 `node --check`。
- `git diff --check` 通过。

### 11. 后续接手时的首要步骤

1. 启动 MySQL 并执行两份迁移。
2. 启动后端和前端，从 `landing.html` 进入。
3. 使用客户、陪诊师、管理员三类账号实际走一遍：
   - 普通需求申请撮合。
   - 指定陪诊师接单。
   - 双方同意后聊天。
   - 点击私信通知进入对应聊天。
   - 完成订单后确认只能查看历史、不能继续发送。
4. 后续前端功能只能继续添加在原轮盘面板内部，不得修改轮盘形式、动画和整体视觉。

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

运行 `sql/test.sql` 导入或修复三个演示账号：`customer1`、`companion1`、`admin1`，密码均为 `123456`。

`test.sql` 使用与 `PasswordUtil` 一致的 `salt:hash` 格式。不要直接将明文 `123456` 写入 `sys_user.password`，否则登录校验会失败。

## 四、待完成

| 内容 | 说明 |
|------|------|
| 密码加密后旧数据 | 数据库中旧明文密码无法登录，运行 `sql/init.sql` 重新初始化 |
| AI 接口 | 目前是 Stub 占位，需接入真实大模型 API |
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

# 2. 在项目根目录启动后端（Redis 非必须，未启动时自动降级）
mvn -q spring-boot:run

# 3. 新开 PowerShell，启动前端（Windows PowerShell 使用 npm.cmd）
cd frontend
npm.cmd run serve

# 4. 浏览器访问 http://localhost:3000
# 前端通过 vue.config.js 将 /api 转发到 http://localhost:8080
```

## 八、验证记录

- 后端编译：`mvn compile -q` 通过
- 全流程跑通：注册→登录→入驻→审核→需求→订单→接单→开始→完成→详情→状态日志，全部 200
