# 研路无忧 (YanLuWuYou) - 考研服务平台

<p align="center">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.2.0-green" alt="Spring Boot">
  <img src="https://img.shields.io/badge/Vue.js-3.3-blue" alt="Vue.js">
  <img src="https://img.shields.io/badge/DeepSeek-AI-orange" alt="DeepSeek AI">
  <img src="https://img.shields.io/badge/Java-17-red" alt="Java 17">
  <img src="https://img.shields.io/badge/MySQL-8.0+-blue" alt="MySQL">
</p>

## 项目简介

"研路无忧"是一个基于 **Spring Boot 3** 和 **Vue 3** 开发的全栈考研服务平台，集成 **DeepSeek AI** 大模型，为考研学生提供一站式智能服务，包括考研资讯、AI智能助手、资料商城、交流论坛、在线支付等功能。

## 技术架构

### 后端技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.2.0 | 核心框架 |
| MyBatis Plus | 3.5.5 | 持久层框架 |
| MySQL | 8.0+ | 关系型数据库 |
| Hutool | 5.8.25 | Java工具类库 |
| Lombok | 1.18.36 | 代码简化 |
| Knife4j | 4.3.0 | API文档 |
| Jsoup | 1.17.2 | 数据爬取 |
| 支付宝SDK | 4.16.2 | 支付集成 |
| Fastjson | 1.2.61 | JSON处理 |

### 前端技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Vue.js | 3.3.11 | 核心框架 |
| Vite | 5.0.10 | 构建工具 |
| Element Plus | 2.4.4 | UI组件库 |
| Vue Router | 4.2.5 | 路由管理 |
| Pinia | 2.1.7 | 状态管理 |
| Axios | 1.6.2 | HTTP请求 |
| WangEditor | 5.1.23 | 富文本编辑 |
| TinyMCE | 7.9.1 | 富文本编辑 |

## 功能模块

### 用户系统
- 用户注册、登录（JWT Token认证）
- 个人信息维护、头像上传
- 收货地址管理
- 用户行为记录（浏览、收藏、购买）

### 资讯中心
- 考研动态与新闻列表
- 资讯详情浏览
- 基于 Jsoup 的数据爬取

### 资料商城
- 资料分类浏览与搜索（支持关键词、分类、标签、适用年份筛选）
- 资料详情页（含描述、价格、库存、销量）
- 购物车功能
- 支付宝沙箱支付
- 订单管理

### AI 智能助手（核心特色）

#### AI 考研助手
- 智能问答：解答考研政策、备考规划、院校选择等问题
- 对话历史记录
- 基于 DeepSeek AI 的自然语言处理

#### AI 资料推荐
- 智能资料推荐（根据用户行为和偏好）
- "猜你喜欢"个性化推荐
- 场景化推荐（基础阶段、强化阶段、冲刺阶段）
- 热门资料推荐

#### AI 学习助手
- 个性化学习计划生成
- 用户学习画像分析
- 每日学习建议

#### AI 资料智能服务
- AI 智能搜索（语义搜索）
- 资料标签自动生成
- 批量标签生成

### 交流论坛
- 帖子发布、编辑、删除
- 评论功能
- 点赞、收藏
- 帖子分类浏览

### 备考指南
- 备考经验分享
- 各科目复习建议
- 指南文章详情

### 管理后台
- 数据统计仪表盘（用户、订单、销售额、帖子、反馈等）
- 用户管理
- 资料管理（上架、下架、编辑）
- 订单管理
- 资讯管理
- 论坛管理
- 反馈管理
- AI助手管理

## 项目结构

```
worry-free-road-research/
├── sql/                          # 数据库初始化脚本
│   ├── init.sql                  # 数据库表结构
│   └── yanluwuyou.sql            # 完整数据（含示例数据）
├── src/                          # 后端源代码
│   └── main/
│       ├── java/com/yanluwuyou/
│       │   ├── controller/       # 控制器层（REST API）
│       │   ├── service/          # 业务逻辑层
│       │   ├── mapper/           # 数据访问层（MyBatis Plus）
│       │   ├── entity/           # 实体类
│       │   ├── dto/              # 数据传输对象
│       │   ├── auth/             # 认证授权（JWT）
│       │   ├── common/           # 通用工具类
│       │   └── config/           # 配置类
│       └── resources/
│           ├── application.yml   # 应用配置文件
│           └── mapper/           # XML映射文件
├── web/                          # 前端源代码 (Vue 3)
│   ├── src/
│   │   ├── views/                # 页面组件
│   │   ├── components/           # 公共组件
│   │   ├── api/                  # API接口封装
│   │   ├── stores/               # Pinia状态管理
│   │   ├── router/               # 路由配置
│   │   └── assets/               # 静态资源
│   └── package.json
└── files/                        # 文件上传存储目录
```

## API 接口

### 用户模块
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /user/register | 用户注册 |
| POST | /user/login | 用户登录 |
| GET | /user/info | 获取当前用户信息 |
| PUT | /user/info | 更新用户信息 |
| POST | /user/avatar | 上传头像 |

### 资料商城模块
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /material/list | 资料列表（支持分页、筛选、排序） |
| GET | /material/{id} | 资料详情 |
| GET | /material/categories | 获取分类列表 |
| GET | /material/tags | 获取标签列表 |

### 购物车模块
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /cart/list | 购物车列表 |
| POST | /cart/add | 添加商品 |
| PUT | /cart/update/{id} | 更新数量 |
| DELETE | /cart/remove/{id} | 删除商品 |

### 订单模块
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /order/create | 创建订单 |
| GET | /order/list | 订单列表 |
| GET | /order/{id} | 订单详情 |
| POST | /order/pay | 支付宝支付 |

### AI 助手模块
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /ai/chat | AI对话 |
| GET | /ai/history | 对话历史 |
| DELETE | /ai/history | 清空历史 |

### AI 推荐模块
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /ai-recommend/smart | AI智能推荐 |
| GET | /ai-recommend/guess-you-like | 猜你喜欢 |
| GET | /ai-recommend/scene | 场景化推荐 |

### AI 学习助手模块
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /ai-study/plan | 生成学习计划 |
| GET | /ai-study/profile | 学习画像分析 |
| GET | /ai-study/daily-advice | 每日学习建议 |

### AI 资料服务模块
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /ai-material/search | AI智能搜索 |
| POST | /ai-material/generate-tags/{id} | 自动生成标签 |

### 论坛模块
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /post/list | 帖子列表 |
| POST | /post/create | 发布帖子 |
| GET | /post/{id} | 帖子详情 |
| POST | /post/{id}/comment | 发表评论 |

### 管理后台模块
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /dashboard/statistics | 数据统计 |
| GET | /dashboard/trends | 数据趋势 |
| GET | /user/list | 用户列表（管理员） |
| GET | /order/list | 订单列表（管理员） |
| GET | /ai/admin/list | AI聊天记录（管理员） |
| GET | /ai/admin/stats | AI聊天统计（管理员） |

## 快速开始

### 环境准备
- JDK 17
- MySQL 8.0+
- Node.js 18+
- Maven 3.8+

### 数据库配置
1. 创建数据库 `yanluwuyou`
2. 运行 `sql/init.sql` 导入表结构
3. （可选）运行 `sql/yanluwuyou.sql` 导入示例数据
4. 修改 `src/main/resources/application.yml` 中的数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/yanluwuyou?useUnicode=true&characterEncoding=utf-8
    username: your_username
    password: your_password
```

### AI 配置（可选）
在 `application.yml` 中配置 DeepSeek API：

```yaml
ai:
  deepseek:
    enabled: true
    api-key: your_deepseek_api_key
    base-url: https://api.deepseek.com/v1
    model: deepseek-chat
```

### 支付宝配置（可选）
在 `application.yml` 中配置支付宝沙箱参数：

```yaml
alipay:
  app-id: your_app_id
  private-key: your_private_key
  alipay-public-key: alipay_public_key
  server-url: https://openapi.alipaydev.com/gateway.do
  notify-url: http://your_domain/api/alipay/notify
  return-url: http://your_domain/order/success
```

### 后端启动
```bash
# 编译安装
mvn clean install

# 运行应用
# 直接运行 YanLuWuYouApplication.java

# 或打包后运行
mvn clean package
java -jar target/yanluwuyou-0.0.1-SNAPSHOT.jar
```

后端 API 文档访问地址：`http://localhost:8080/doc.html`

### 前端启动
```bash
cd web
npm install
npm run dev
```

访问地址：`http://localhost:5173`

### 生产部署
```bash
# 后端打包
mvn clean package

# 前端打包
cd web && npm run build
```

将前端 `web/dist/` 目录部署到 Nginx 或集成到 Spring Boot 的 static 目录。

## 开发注意事项

- **AI 功能**：需要配置 DeepSeek API Key 才能使用 AI 助手功能。未配置时，AI 助手将使用预设的 fallback 回复
- **支付功能**：默认配置为支付宝沙箱环境，如需正式环境请修改配置
- **文件上传**：上传的文件默认存储在项目根目录下的 `files/` 文件夹，生产环境建议配置为独立文件服务器
- **跨域配置**：后端已通过 `CorsConfig.java` 配置跨域支持，开发环境无需额外配置
- **数据库**：项目使用 MySQL 8.0+，确保数据库字符集为 `utf8mb4` 以支持 emoji

## 更新日志

### v1.1.0 (2026-05-02)
- 新增 AI 智能助手功能（DeepSeek 集成）
- 新增 AI 资料推荐系统
- 新增 AI 学习助手（学习计划、画像分析）
- 新增 AI 智能搜索和标签生成
- 新增用户行为记录系统
- 优化资料商城搜索和推荐算法
- 修复 AI 回答中的链接跳转问题

### v1.0.0 (2026-04)
- 初始版本发布
- 用户系统、资讯中心、资料商城、论坛、管理后台等基础功能

---

*本项目为毕业设计作品，持续更新中。*
