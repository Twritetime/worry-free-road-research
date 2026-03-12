# 研路无忧 - 考研服务平台

[![Gitee Repo](https://img.shields.io/badge/Gitee-研路无忧-red)](https://gitee.com/Twritetime/worry-free-road-research)

"研路无忧"是一个专注于考研服务的综合平台，旨在为考研学子提供一站式的备考资源和交流社区。

## 核心功能

### 👨‍🎓 用户端
- **资讯与指南**: 浏览考研动态、招生简章、考试大纲等。
- **资料商城**: 购买公共课、专业课、复试资料，支持在线预览和下载。
- **交流论坛**: 分类发帖（公共课/专业课/复试），互动评论，点赞收藏。
- **个人中心**: 订单管理、收藏夹、地址管理、在线客服反馈。

### 👨‍💼 管理端
- **数据看板**: 销售统计、资料分类占比等可视化图表。
- **内容管理**: 资讯、指南、资料、帖子的发布与审核（支持上下架、置顶）。
- **用户与订单**: 用户管理、订单状态变更（支付/发货/退款）。
- **反馈处理**: 回复用户咨询与建议。

## 技术架构

- **后端**: Spring Boot 3.2 + MyBatis-Plus + MySQL 8.0
- **前端**: Vue 3 + Element Plus + Pinia + ECharts
- **工具**: Maven, Hutool, Knife4j (Swagger API文档)

## 快速开始

### 1. 数据库初始化
本项目提供了一键初始化脚本，包含完整的表结构和演示数据。

1. 创建数据库 `yanluwuyou` (字符集 `utf8mb4`)。
2. 执行脚本 `sql/init.sql`。

### 2. 后端启动
1. 确保 JDK 17+ 和 Maven 3.6+ 已安装。
2. 修改 `src/main/resources/application.yml` 中的数据库账号密码。
3. 运行项目：
   ```bash
   mvn spring-boot:run
   ```
   服务地址: `http://localhost:8080`
   API文档: `http://localhost:8080/doc.html`
   
   > **注意**: 文件上传默认存储在项目根目录的 `files/` 文件夹下。

### 3. 前端启动
1. 确保 Node.js 16+ 已安装。
2. 进入 `web` 目录并安装依赖：
   ```bash
   cd web
   npm install
   ```
3. 启动开发服务器：
   ```bash
   npm run dev
   ```
   访问地址: `http://localhost:5173`

## 测试账号

- **管理员**: `admin` / `123456`
- **普通用户**: `user01` / `123456`

## 项目地址
[https://gitee.com/Twritetime/worry-free-road-research](https://gitee.com/Twritetime/worry-free-road-research)
