# 家庭数字孪生系统 (Home Digital Twin)

一个用于管理家中所有房间物品的数字孪生系统，支持物品录入、照片识别、搜索查找、清理管理等功能，适配 PC 端、移动端及微信小程序。

## 项目结构

```
home-twin/
├── backend/          # 后端服务 (Spring Boot)
├── frontend/         # 前端应用 (UniApp)
└── README.md
```

## 技术栈

### 后端
- **Spring Boot 3.2.5** - 核心框架
- **Spring Data JPA** - 数据持久化
- **MySQL 8.0+** - 关系型数据库
- **MinIO** - 对象存储（图片存储）
- **Lombok** - 代码简化
- **Fastjson** - JSON 处理

### 前端
- **UniApp** - 跨平台开发框架
- **Vue 3** - 前端框架
- **TypeScript** - 类型安全
- **Pinia** - 状态管理
- **SCSS** - 样式预处理

## 功能特性

- 房间管理：主卧、次卧、客厅、餐厅、厨房、卫生间、阳台
- 人物分类：4岁小女孩、小女孩父亲、小女孩母亲、小女孩外婆
- 物品管理：支持拍照添加（自动识别）和手动添加
- 搜索功能：按关键词、房间筛选查找物品
- 清理管理：标记物品为已清理状态
- 统计分析：物品数量、房间分布、人物分布、使用状态占比
- 多端适配：PC端、移动端、微信小程序

## 快速开始

### 环境要求
- JDK 21+
- Maven 3.8+
- Node.js 18+
- MySQL 8.0+
- MinIO (可选，用于图片存储)

### 1. 数据库初始化

创建数据库：
```sql
CREATE DATABASE hometwin CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. 后端启动

```bash
cd backend
mvn spring-boot:run
```

后端服务默认运行在 `http://localhost:8080`

### 3. 前端开发

```bash
cd frontend
npm install

# H5 开发模式
npm run dev:h5

# 微信小程序开发模式
npm run dev:mp-weixin
```

### 4. 构建部署

```bash
# H5 构建
npm run build:h5

# 微信小程序构建
npm run build:mp-weixin
```

微信小程序构建完成后，使用微信开发者工具导入 `dist/build/mp-weixin` 目录。

## 配置说明

### 后端配置 (application.yml)

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/hometwin?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: admin
    password: password
    driver-class-name: com.mysql.cj.jdbc.Driver

minio:
  url: http://localhost:9000
  access-key: minioadmin
  secret-key: minioadmin
  bucket-name: hometwin
```

### 前端配置

修改 `frontend/src/utils/request.ts` 中的 `BASE_URL` 以匹配后端地址：

```typescript
const BASE_URL = 'http://localhost:8080/api'
```

## API 接口

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/rooms` | GET | 获取房间列表 |
| `/api/persons` | GET | 获取人物列表 |
| `/api/items` | GET | 获取物品列表（支持分页和筛选） |
| `/api/items` | POST | 创建物品 |
| `/api/items/{id}` | GET | 获取物品详情 |
| `/api/items/{id}` | PUT | 更新物品 |
| `/api/items/{id}` | DELETE | 删除物品 |
| `/api/items/{id}/clean` | PUT | 标记物品为已清理 |
| `/api/items/search` | GET | 搜索物品 |
| `/api/items/statistics` | GET | 获取统计数据 |

## 初始数据

系统启动时会自动初始化以下数据：

**房间：**
- 主卧、次卧、客厅、餐厅、厨房、卫生间、阳台

**人物：**
- 小女孩（4岁）
- 父亲
- 母亲
- 外婆

## 开发计划

- [x] 基础项目搭建
- [x] 数据库设计与实体创建
- [x] RESTful API 开发
- [x] 前端页面实现
- [x] 多端构建支持
- [ ] 图片自动识别功能（需接入 AI 服务）
- [ ] 用户认证与权限管理
- [ ] 数据导入导出

## 贡献指南

欢迎提交 Issue 和 Pull Request。

## 许可证

MIT License
