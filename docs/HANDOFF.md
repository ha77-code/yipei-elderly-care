# HANDOFF.md

## 项目名称

"医陪"——银发人群陪诊与生活陪伴服务平台

英文名称：YiPei — Elderly Medical Escort and Daily Companionship Service Platform

## 当前状态

项目已完成统一响应格式、全局异常处理、用户模块（注册/登录/信息查询）、服务需求模块（发布/列表/详情/取消），以及前端请求工具封装和用户相关页面对接。陪诊师、订单、评价、投诉等模块尚未实现。

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
| 数据库 | 9 张表已创建（见下方表结构），测试数据需手动导入 |
| 前端项目 | Vue2 + ElementUI，`frontend/` 目录 |

### 2. 统一响应与异常处理

| 文件 | 说明 |
|------|------|
| `entity/ApiResponse.java` | 统一响应格式 `{ code, message, data }`，工厂方法 `success()` / `error()` |
| `exception/BusinessException.java` | 业务异常基类，携带 `code` + `message` |
| `exception/NotFoundException.java` | 继承 BusinessException，code=404 |
| `exception/ForbiddenException.java` | 继承 BusinessException，code=403 |
| `handler/GlobalExceptionHandler.java` | `@RestControllerAdvice`，统一处理 BusinessException / Validation / 未知异常 |

### 3. 用户模块

**后端文件：**

| 文件 | 说明 |
|------|------|
| `entity/SysUser.java` | 对应 `sys_user` 表 |
| `entity/UserVO.java` | 用户视图对象，不含 password |
| `entity/LoginVO.java` | 登录返回对象，不含 password 和 token |
| `entity/UserLoginRequest.java` | 登录请求 DTO（@NotBlank username, password） |
| `entity/UserRegisterRequest.java` | 注册请求 DTO（@NotBlank username/password/nickname/phone/role） |
| `entity/UpdateUserInfoRequest.java` | 修改个人信息 DTO |
| `mapper/SysUserMapper.java` | selectAll / selectById / selectByUsername / insert / updateStatus / updateUserInfo |
| `service/UserService.java` | login / register / getCurrentUser / getUserList / getUserById / updateUserStatus / updateUserInfo |
| `controller/UserController.java` | 见下方接口列表 |

**用户接口：**

```
POST /api/user/register       注册（校验角色只允许 CUSTOMER/COMPANION/ADMIN）
POST /api/user/login          登录（校验账号存在、密码正确、状态正常）
GET  /api/user/info?id=       获取用户信息（不返回密码）
GET  /api/user/list           用户列表
GET  /api/user/{id}           用户详情
PUT  /api/user/info           修改个人信息（需 X-User-Id header）
```

- 第一版不做 token，身份通过 `X-User-Id` 请求头传递
- 注册时校验角色必须是 CUSTOMER、COMPANION 或 ADMIN
- 登录和注册返回均不包含 password 字段

### 4. 服务需求模块

**后端文件：**

| 文件 | 说明 |
|------|------|
| `entity/ServiceRequest.java` | 对应 `service_request` 表 |
| `entity/ServiceRequestCreateRequest.java` | 发布需求 DTO（校验 serviceType/date/hospital/department/requirement/contactName/phone） |
| `mapper/ServiceRequestMapper.java` | selectById / selectByCustomerId / insert / updateStatus / countOrdersByRequestId |
| `service/ServiceRequestService.java` | create（校验 CUSTOMER 角色）/ listByCustomerId / getById / cancel |
| `controller/ServiceRequestController.java` | 见下方接口列表 |

**服务需求接口：**

```
POST /api/service-request/create         发布需求（校验角色=CUSTOMER，默认 status=PENDING）
GET  /api/service-request/list?customerId=  我的需求列表（按创建时间倒序）
GET  /api/service-request/{id}           需求详情
PUT  /api/service-request/{id}/cancel    取消需求（校验归属、无订单、状态可取消）
```

- cancel 校验：①需求归属 ②未生成订单 ③当前状态不是 CANCELLED/CLOSED

### 5. 前端已对接的模块

| 文件 | 说明 |
|------|------|
| `api/request.js` | Axios 封装，baseURL=`http://localhost:8080`，自动注入 `X-User-Id`，统一处理 code===200 和 HTTP 错误 |
| `api/user.js` | 封装 register / login / getUserInfo / updateUserInfo / getUserList |
| `api/serviceRequest.js` | 封装 createRequest / getMyRequests / getRequestDetail / cancelRequest / getAllRequests |
| `utils/auth.js` | localStorage 管理用户信息和 token |
| `utils/testAccounts.js` | 开发环境测试账号快速登录 |
| `views/Login.vue` | 已对接 login 接口，适配无 token 响应格式 |
| `views/Register.vue` | 已对接 register 接口 |
| `views/Profile.vue` | 已对接 getUserInfo / updateUserInfo，传入 userId |
| `views/customer/MyRequests.vue` | 已对接 getMyRequests，传入 customerId |
| `views/customer/RequestCreate.vue` | 已对接 createRequest，X-User-Id 由拦截器自动注入 |
| `views/CompanionDetail.vue` | 已创建页面，等待后端陪诊师接口 |
| `views/ServiceRecord.vue` | 已创建页面，等待后端服务记录接口 |

## 二、数据库表

已创建 9 张表（均为设计文档 §10 定义）：

| 表名 | 说明 | 后端 Entity |
|------|------|-------------|
| `sys_user` | 用户 | `SysUser.java` |
| `companion_profile` | 陪诊师资料 | 未创建 |
| `service_request` | 服务需求 | `ServiceRequest.java` |
| `service_order` | 服务订单 | 未创建 |
| `order_status_log` | 状态变更记录 | 未创建 |
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
| 用户模块 | ✅ 完成 | register / login / info / list / detail / updateInfo |
| 服务需求模块 | ✅ 完成 | create / list / detail / cancel |
| 陪诊师模块 | ❌ 未开始 | apply / list / detail / profile / audit |
| 订单模块 | ❌ 未开始 | create / list / detail / accept / start / complete / confirm / cancel |
| 评价模块 | ❌ 未开始 | submit / list / received |
| 投诉模块 | ❌ 未开始 | submit / list / detail / handle |
| 服务记录模块 | ❌ 未开始 | create / detail / list |
| 管理员统计 | ❌ 未开始 | statistics |

### 前端

| 问题 | 说明 |
|------|------|
| API 路径 `/api` 前缀 | `companion.js` / `order.js` / `evaluation.js` / `report.js` / `serviceRecord.js` / `admin.js` 尚未加 `/api` 前缀，需逐个更新 |
| 页面传参 | 各 Vue 页面调用 API 时需传入 `customerId` 或确保 `X-User-Id` 由拦截器自动注入 |

## 四、技术基线

- JDK 17（pom.xml 中 java.version=17）
- Spring Boot 4.0.7
- MyBatis 4.0.1
- MySQL 8
- Redis（已配置，当前未使用）
- Vue2 + ElementUI + Axios

## 五、开发约定

- 修改前先阅读 `docs/项目设计文档.md`；
- 统一使用 `ApiResponse<T>` 作为返回格式；
- 业务异常继承 `BusinessException`，由 `GlobalExceptionHandler` 统一处理；
- 第一版不做 token，身份通过 `X-User-Id` 请求头传递；
- 不在代码中写入真实 API Key、手机号或身份证信息；
- 不把平台描述成医疗诊断或医疗治疗系统。

## 六、验证记录

- 后端编译：`mvn compile -q` 通过，无错误
- 数据库：9 张表已创建
- 接口验证：待启动后通过 Postman 或前端页面验证
