# 研路无忧 (YanLuWuYou) - 考研服务平台

## 1. 项目简介
“研路无忧”是一个基于 Spring Boot 和 Vue 3 开发的全栈考研服务平台。该平台旨在为考研学生提供一站式的服务，包括考研资讯、资料分享、交流论坛、在线商城（购买学习资料）以及反馈建议等功能。

## 2. 技术架构

### 后端 (Backend)
- **核心框架**: Spring Boot 3.2.0
- **持久层框架**: MyBatis Plus 3.5.5
- **数据库**: MySQL 8.0+
- **工具类库**: Hutool (常用工具类), Lombok (简化代码), Fastjson (JSON处理)
- **数据爬取**: Jsoup (用于获取最新考研资讯)
- **API 文档**: Knife4j (OpenAPI 3)
- **支付集成**: 支付宝 SDK (Alipay SDK)
- **Java版本**: Java 17

### 前端 (Frontend)
- **构建工具**: Vite 5.0
- **核心框架**: Vue.js 3.3
- **状态管理**: Pinia 2.1
- **路由管理**: Vue Router 4.2
- **UI 组件库**: Element Plus 2.4
- **网络请求**: Axios 1.6

## 3. 主要功能
- **用户系统**: 注册、登录、个人信息维护、收货地址管理。
- **资讯中心**: 实时更新的考研动态与新闻。
- **资料商城**: 浏览、搜索学习资料，支持购物车及支付宝模拟支付。
- **交流论坛**: 发布帖子、发表评论、点赞收藏。
- **备考指南**: 提供各科目的备考建议与经验分享。
- **管理后台**: 对用户、帖子、资料、订单、反馈等进行统一管理。

## 4. 目录结构
```text
worry-free-road-research/
├── sql/                # 数据库初始化脚本
├── src/                # 后端源代码
│   ├── main/java/      # Java 业务代码
│   └── main/resources/ # 配置文件及资源
├── web/                # 前端源代码 (Vue 3 项目)
│   ├── src/            # 前端业务逻辑
│   └── public/         # 静态资源
└── 材料/                # 项目相关文档（论文、任务书等）
```

## 5. 快速开始

### 5.1 环境准备
- JDK 17
- MySQL 8.0+
- Node.js 18+
- Maven 3.8+

### 5.2 数据库配置
1. 创建数据库 `yanluwuyou`。
2. 运行 `sql/init.sql` 或 `sql/yanluwuyou.sql` 导入表结构和基础数据。
3. 修改 `src/main/resources/application.yml` 中的数据库连接信息（用户名、密码）。

### 5.3 后端启动
1. 在根目录下执行 `mvn clean install`（或使用 IDE 导入）。
2. 运行 `YanLuWuYouApplication.java`。
3. 后端 API 文档访问地址：`http://localhost:8080/doc.html`。

### 5.4 前端启动
1. 进入 `web` 目录：`cd web`。
2. 安装依赖：`npm install`。
3. 启动项目：`npm run dev`。
4. 访问地址：`http://localhost:5173`。

## 6. 开发注意事项
- **支付功能**: 目前配置为支付宝沙箱环境，如需测试请配置自己的沙箱参数。
- **文件上传**: 上传的文件默认存储在项目根目录下的 `files/` 文件夹。
- **跨域配置**: 后端已通过 `CorsConfig.java` 配置跨域支持。

---
*本项目为毕业设计作品。*
