# "医陪" - 银发人群陪诊与生活陪伴服务平台

> YiPei -- Elderly Medical Escort and Daily Companionship Service Platform

## 目录

- [克隆后必读：为什么运行会"无权限"](#克隆后必读为什么运行会无权限)
- [项目简介](#项目简介)
- [技术栈](#技术栈)
- [本地启动（新人从零开始）](#本地启动新人从零开始)
- [核心业务流程](#核心业务流程)
- [主要角色](#主要角色)
- [内网穿透部署](#内网穿透部署)

---

## 克隆后必读：运行为什么"点不动"或"无权限"

### 原因就一句话

**数据库是空的，没有用户。** Git 只管理 `.java` / `.vue` 代码文件，不管 MySQL 里的数据。别人克隆项目时数据库里 0 条记录。

### 具体表现

启动项目后，会依次遇到这三个问题：

| 操作 | 发生什么 | 为什么 |
|------|---------|--------|
| 点击登录页"快速体验"按钮 | 没反应 | `testAccounts.js` 调用 `login('customer1','123456')`，但数据库里没有这个用户 → 后端返回 403 → 代码 `catch {}` 空块吞掉了错误 |
| 手动注册后点击 `/admin/users` | 被弹回首页 | 注册时选的角色是 CUSTOMER，但 `/admin/*` 路由要求 `role: ADMIN` → `router/index.js:254` 把用户重定向回了客户首页 |
| 注册后调后端管理接口 | 提示"仅管理员可操作" | `AdminController` 每个方法都调了 `userService.requireAdmin(adminId)`，非 ADMIN 角色抛 `ForbiddenException` |

### 解决只需一步

```bash
mysql -u root -p < sql/init.sql
```

这条命令会建库、建表、插入 3 个预加密账号：

| 用户名 | 密码 | 角色 |
|--------|------|------|
| `customer1` | `123456` | 家属 |
| `companion1` | `123456` | 陪诊师 |
| `admin1` | `123456` | 管理员 |

然后用 `admin1` 登录就能进后台，用 `companion1` 就能接单，用 `customer1` 就能发需求。

> MySQL 密码不是 `123456` 的话，先改 `src/main/resources/application.yaml` 里的 `spring.datasource.password`。

### 想要完整演示数据（推荐）

`init.sql` 只建库建表 + 3 个基础账号。要体验完整流程（需求广场、需求审核、订单全生命周期、评价、投诉、聊天等），再执行一次演示数据脚本：

```bash
mysql -u root -p < sql/init.sql            # 先建库建表
mysql -u root -p < sql/demo_test_data.sql  # 再灌入演示数据
```

`demo_test_data.sql` 会灌入 24 个用户、8 个陪诊师、21 条需求、14 个订单以及聊天等数据，所有账号密码均为 `123456`（如 `customer1`~`customer10`、`companion1`~`companion8`、`admin1`~`admin3`）。

> ⚠️ 该脚本开头会 `TRUNCATE` 清空所有业务表以保证可重复执行、结果一致，**仅用于演示/测试环境，勿在生产库运行**。
>
> 已有旧库升级：若之前建过库、缺少撮合与聊天相关字段/表，先执行 `sql/migrations/20260721_add_matching_and_chat.sql` 补齐结构（可重复执行，不动数据），无需重跑 `init.sql`。

---

## 项目简介

"医陪"面向老年人及其子女，提供陪诊和生活陪伴服务的信息发布、人员展示、订单管理、服务记录和评价功能。

平台不提供医疗诊断和治疗服务，陪诊师只负责陪同、路线引导、手续协助、信息记录和生活陪伴。

## 技术栈

| 层级 | 技术 | 版本 |
|------|------|------|
| JDK | Java | 17 |
| 后端框架 | Spring Boot | 3.4.1 |
| ORM | MyBatis + MyBatis Spring Boot Starter | 3.0.4 |
| 数据库 | MySQL | 8.0 |
| 缓存 | Redis（Lettuce 客户端） | 可选，不可用时自动降级 |
| 密码 | SHA-256 + 16 字节随机盐 | `util/PasswordUtil.java` |
| 前端 | Vue 2 + Element UI + Axios | Vue CLI |
| AI 摘要 | DeepSeek API | OpenAI 兼容 `/v1/chat/completions` |
| 语音朗读 | 火山引擎 TTS | 可选，`/api/tts/companion/{id}` |

## 配置说明

所有配置在 `src/main/resources/application.yaml`，敏感信息通过环境变量注入。

### 数据库

| 变量 | 默认值 | 说明 |
|------|--------|------|
| `YIPEI_DB_URL` | `jdbc:mysql://localhost:3306/yipei?...` | 数据库连接 |
| `YIPEI_DB_USERNAME` | `root` | 用户名 |
| `YIPEI_DB_PASSWORD` | `123456` | 密码 |

### Redis（可选）

| 变量 | 默认值 | 说明 |
|------|--------|------|
| `YIPEI_REDIS_HOST` | `localhost` | Redis 地址 |
| `YIPEI_REDIS_PORT` | `6379` | Redis 端口 |

不装 Redis 也能正常使用，`SubmitLock` 检测到 Redis 不可用时会自动放行。

### AI 摘要（DeepSeek）

| 变量 | 默认值 | 说明 |
|------|--------|------|
| `YIPEI_AI_ENABLED` | `true` | 是否启用 |
| `YIPEI_AI_BASE_URL` | `https://api.deepseek.com/v1` | API 地址 |
| `YIPEI_AI_API_KEY` | — | DeepSeek 密钥 |
| `YIPEI_AI_MODEL` | `deepseek-chat` | 模型名 |

未配置 API Key 时调用 AI 接口会返回 `503` 并提示接入。

### 语音朗读（火山引擎，可选）

| 变量 | 默认值 | 说明 |
|------|--------|------|
| `YIPEI_TTS_ENABLED` | `false` | 是否启用 |
| `YIPEI_TTS_APP_ID` | — | 火山 AppId |
| `YIPEI_TTS_ACCESS_TOKEN` | — | 火山 Access Token |
| `YIPEI_TTS_VOICE_TYPE` | `zh_female_qingxin` | 音色 |

未配置时调用 `GET /api/tts/companion/{id}` 返回 `503`。

## 本地启动（新人从零开始）

### 1. 环境要求

- JDK 17+
- MySQL 8.0+
- Maven 3.6+
- Node.js 14+

### 2. 初始化数据库

```bash
mysql -u root -p < sql/init.sql
```

如果 MySQL 用户名/密码不是 `root/123456`，修改 `src/main/resources/application.yaml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/yipei?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    username: 你的用户名
    password: 你的密码
```

### 3. 启动后端

```bash
mvn spring-boot:run
```

后端运行在 `http://localhost:8080`。

### 4. 启动前端

```bash
cd frontend
npm install
npm run serve
```

前端运行在 `http://localhost:3001`。

### 5. 登录测试

用演示账号登录：

| 用户名 | 密码 | 角色 | 可见页面 |
|--------|------|------|---------|
| `customer1` | `123456` | 家属 | 陪诊师列表、发布需求、我的需求、我的订单 |
| `companion1` | `123456` | 陪诊师 | 入驻资料、可接订单、我的订单、服务记录 |
| `admin1` | `123456` | 管理员 | 后台首页、用户管理、陪诊师审核、订单管理、投诉处理、平台统计 |

### 6. 运行完整流程

Postman 或 curl 测试流程见 `docs/HANDOFF.md`。

---

## 核心业务流程

```
家属注册登录 → 浏览陪诊师 → 发布陪诊需求
    → 选择合适的陪诊师 → 创建订单（平台抽成10%）
    → 陪诊师接单 → 开始服务 → 完成服务
    → 家属确认完成 → 双方评价
```

订单完整状态流转：

```
PENDING_ACCEPT → ACCEPTED → IN_SERVICE → PENDING_CONFIRM → COMPLETED
       │              │
       ├→ REJECTED    └→ CANCELLED
       └→ CANCELLED
```

## 主要角色

| 角色 | 枚举值 | 说明 |
|------|--------|------|
| 老人/家属 | `CUSTOMER` | 发布需求、浏览陪诊师、创建订单、评价 |
| 陪诊师 | `COMPANION` | 提交入驻资料、查看可接订单、接单、服务、填写记录 |
| 管理员 | `ADMIN` | 审核陪诊师、管理用户、处理投诉、查看统计 |

身份通过 `X-User-Id` 请求头传递（第一版无 token）。管理员接口额外校验 `requireAdmin()`。

## 内网穿透部署

`vue.config.js` 已配置 `host: 0.0.0.0` + `allowedHosts: all` + `/api` 代理转发，可直接配合 cloudflared 使用：

```yaml
# cloudflared config.yml
ingress:
  - hostname: yipei.hhhuan.top
    service: http://localhost:3001
  - service: http_status:404
```

一个隧道同时穿透前端和后端（前端 dev server 自动代理 `/api` 到后端 8080）。

---

## 项目结构

```
yipei/
├── sql/init.sql              # 数据库一键初始化脚本
├── src/main/java/com/yipei/
│   ├── config/               # AppProperties, CorsConfig, AiProperties, TtsProperties
│   ├── constant/             # RoleConstants
│   ├── controller/           # 11个 Controller
│   ├── entity/               # 20+ 实体/VO/DTO
│   ├── exception/            # BusinessException, NotFoundException, ForbiddenException
│   ├── handler/              # GlobalExceptionHandler
│   ├── mapper/               # 11个 MyBatis Mapper
│   ├── service/              # 9个 Service
│   └── util/                 # PasswordUtil, SubmitLock
├── src/main/resources/
│   └── application.yaml      # 主配置
├── frontend/                 # Vue2 + ElementUI 前端
│   └── src/
│       ├── api/              # 9个 API 封装文件
│       ├── views/            # 20+ 页面组件
│       ├── router/           # 路由 + 守卫
│       ├── layout/           # MainLayout（顶栏+侧栏+内容区）
│       └── utils/            # auth.js 登录状态管理
└── docs/
    ├── 项目设计文档.md        # 产品与设计文档
    └── HANDOFF.md            # 开发交接文档
```
