# “医陪”——银发人群陪诊与生活陪伴服务平台

> YiPei — Elderly Medical Escort and Daily Companionship Service Platform

## 项目简介

“医陪”面向老年人及其子女，提供陪诊和生活陪伴服务的信息发布、人员展示、订单管理、服务记录和评价功能。

家属可以代替老人发布陪诊需求，浏览经过平台审核的陪诊师资料，创建服务订单并查看服务状态。陪诊师可以提交入驻申请、管理订单和填写服务记录。管理员负责审核陪诊师、管理订单和处理投诉。

平台不提供医疗诊断和治疗服务，陪诊师只负责陪同、路线引导、手续协助、信息记录和生活陪伴。

## 核心业务流程

```text
家属/老人发布需求
    ↓
浏览陪诊师
    ↓
创建服务订单
    ↓
陪诊师接单
    ↓
服务开始
    ↓
服务完成确认
    ↓
双方评价
```

## 核心功能

- 用户注册、登录和角色权限；
- 老人/家属发布陪诊需求；
- 陪诊师入驻申请；
- 管理员审核陪诊师资料；
- 陪诊师列表和服务标签；
- 服务订单创建和状态流转；
- 服务记录和完成确认；
- 双方评价；
- 管理员订单管理和投诉处理；
- AI 生成服务需求摘要和标签；
- 模拟订单金额、平台抽成和陪诊师收入统计。

## 商业价值

目标用户包括老年人、异地子女、陪诊师、社区养老机构和养老服务中心。

平台可以通过以下方式商业化：

- 服务订单撮合抽成；
- 家属会员服务；
- 陪诊师入驻和认证服务；
- 养老机构和社区机构版 SaaS；
- 定制部署和数据统计服务。


## 技术栈

- JDK 21
- Spring Boot 3.x
- MyBatis
- MySQL 8
- Redis
- Vue2
- ElementUI
- Axios
- 文本 AI API
- Spring Boot MultipartFile 本地文件存储

## 本地启动

克隆项目后都需要初始化自己的本地 MySQL，不能复用数据库或手工插入明文密码。

```powershell
# 1. 创建 yipei 数据库、表和可登录的演示账号
powershell.exe -ExecutionPolicy Bypass -File .\scripts\bootstrap-local.ps1

# MySQL 密码不是 123456 时，显式传入自己的密码
powershell.exe -ExecutionPolicy Bypass -File .\scripts\bootstrap-local.ps1 -DbPassword 'your_mysql_password'

# 2. 安装前端依赖（首次即可）
cd frontend
npm.cmd install
cd ..

# 3. 启动前后端（会打开两个可查看日志的 PowerShell 窗口）
powershell.exe -ExecutionPolicy Bypass -File .\scripts\start-dev.ps1
```

浏览器访问 `http://localhost:3000`。演示账号为 `customer1`、`companion1`、`admin1`，密码均为 `123456`。

后端默认使用 `root / 123456` 连接本机 MySQL。可通过 `YIPEI_DB_URL`、`YIPEI_DB_USERNAME`、`YIPEI_DB_PASSWORD` 环境变量覆盖，配置见 `src/main/resources/application.yaml`。

## AI 需求摘要配置

发布陪诊需求后，后端会调用一个 OpenAI 兼容的 `chat/completions` 接口生成摘要，并将摘要保存到 `service_request.ai_summary`。陪诊师可在可接订单、我的订单和订单详情中查看摘要。模型调用失败不会阻塞需求发布。

默认关闭 AI 功能。启动后端前，在同一个 PowerShell 窗口设置以下环境变量：

```powershell
$env:YIPEI_AI_ENABLED = 'true'
$env:YIPEI_AI_BASE_URL = 'https://api.openai.com/v1'
$env:YIPEI_AI_API_KEY = 'your_api_key'
$env:YIPEI_AI_MODEL = 'gpt-4o-mini'
```

DeepSeek 等兼容 OpenAI Chat Completions 的服务同样可用，只需替换 `YIPEI_AI_BASE_URL` 和 `YIPEI_AI_MODEL`。不要将 API Key 写入仓库；完整配置项见 `src/main/resources/application-example.yaml`。

## 主要角色

- 老人/家属用户；
- 陪诊师/陪伴师；
- 平台管理员。
