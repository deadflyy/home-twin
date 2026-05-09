# 家庭数字孪生系统 - 项目规划

## 一、需求分析

### 1.1 业务需求概述

| 需求类别 | 需求描述 | 来源 |
| :--- | :--- | :--- |
| 房间管理 | 支持7个房间：主卧、次卧、客厅、餐厅、厨房、卫生间、阳台 | 用户需求 |
| 人物分类 | 支持4类人物：4岁小女孩、小女孩父亲、小女孩母亲、小女孩外婆 | 用户需求 |
| 物品添加 | 支持从照片添加（自动识别）+ 手动添加 | 用户需求 |
| 物品查找 | 支持搜索查找物品 | 用户需求 |
| 物品清理 | 支持标记物品已清理/移除 | 用户需求 |
| 多端适配 | PC端 + 移动端 + 微信小程序 | 用户需求 |

### 1.2 功能需求矩阵

| 功能模块 | 功能点 | 描述 | 优先级 |
| :--- | :--- | :--- | :--- |
| 房间管理 | 房间列表展示 | 展示7个预设房间 | 高 |
| | 房间切换 | 切换查看不同房间的物品 | 高 |
| 物品管理 | 物品列表展示 | 按房间/人物分类展示物品 | 高 |
| | 照片添加物品 | 上传照片自动识别物品 | 高 |
| | 手动添加物品 | 手动输入物品信息 | 高 |
| | 物品搜索 | 按名称/位置/人物搜索 | 高 |
| | 物品清理 | 标记物品已清理/移除 | 高 |
| | 物品详情 | 查看物品详细信息 | 中 |
| | 物品统计 | 统计各房间/人物的物品数量 | 中 |
| 用户体验 | 响应式设计 | 适配PC和移动端 | 高 |
| | 微信小程序 | 支持小程序部署 | 高 |

### 1.3 数据实体设计

#### 1.3.1 房间 (Room)
| 字段 | 类型 | 说明 |
| :--- | :--- | :--- |
| id | String | 房间唯一标识 |
| name | String | 房间名称 |
| icon | String | 房间图标 |
| color | String | 房间主题色 |

#### 1.3.2 人物 (Person)
| 字段 | 类型 | 说明 |
| :--- | :--- | :--- |
| id | String | 人物唯一标识 |
| name | String | 人物名称 |
| relation | String | 与小女孩的关系 |
| avatar | String | 头像URL |

#### 1.3.3 物品 (Item)
| 字段 | 类型 | 说明 |
| :--- | :--- | :--- |
| id | String | 物品唯一标识 |
| name | String | 物品名称 |
| photo | String | 物品照片URL |
| roomId | String | 所属房间ID |
| personId | String | 所属人物ID |
| description | String | 物品描述 |
| location | String | 具体位置 |
| addedAt | Date | 添加时间 |
| cleanedAt | Date | 清理时间（null表示未清理） |
| status | String | 状态：active/cleaned |

---

## 二、技术架构

### 2.1 技术选型

| 分类 | 技术 | 版本 | 选型理由 |
| :--- | :--- | :--- | :--- |
| 前端框架 | UniApp | 最新 | 一套代码支持多端（H5、微信小程序） |
| UI组件 | UniUI | 最新 | UniApp官方组件库，适配多端 |
| 后端框架 | Spring Boot | 3.2.x | 成熟稳定，社区活跃 |
| 数据库 | MySQL | 8.0+ | 关系型数据库，适合结构化数据存储 |
| 图片服务 | MinIO | 最新 | 轻量级对象存储，支持本地部署 |
| AI识别 | 百度AI/腾讯AI | - | 可选接入图片识别服务 |

### 2.2 架构设计

#### 2.2.1 架构图

```
┌─────────────────────────────────────────────────────────────┐
│                      客户端层                              │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐     │
│  │   PC H5      │  │   移动端H5   │  │  微信小程序   │     │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘     │
└─────────┼─────────────────┼─────────────────┼─────────────┘
          │                 │                 │
┌─────────▼─────────────────▼─────────────────▼─────────────┐
│                      API网关层                             │
│                    (Nginx / Spring Cloud Gateway)         │
└───────────────────────────┬───────────────────────────────┘
                            │
┌───────────────────────────▼───────────────────────────────┐
│                      服务层                               │
│  ┌──────────────────┐  ┌──────────────────┐              │
│  │   ItemService    │  │   RoomService    │              │
│  │   (物品管理)     │  │   (房间管理)     │              │
│  └────────┬─────────┘  └────────┬─────────┘              │
│           │                     │                         │
│  ┌────────▼─────────┐  ┌────────▼─────────┐              │
│  │  PersonService   │  │   AiService      │              │
│  │   (人物管理)     │  │   (AI识别)       │              │
│  └──────────────────┘  └──────────────────┘              │
└───────────────────────────┬───────────────────────────────┘
                            │
┌───────────────────────────▼───────────────────────────────┐
│                      数据层                               │
│  ┌──────────────────┐  ┌──────────────────┐              │
│  │    MySQL         │  │    MinIO         │              │
│  │  (结构化数据)    │  │  (图片存储)      │              │
│  └──────────────────┘  └──────────────────┘              │
└───────────────────────────────────────────────────────────┘
```

#### 2.2.2 模块划分

| 模块 | 职责 | 说明 |
| :--- | :--- | :--- |
| controller | REST API控制层 | 处理HTTP请求，参数校验 |
| service | 业务逻辑层 | 核心业务处理 |
| repository | 数据访问层 | 数据库CRUD操作 |
| entity | 数据库实体 | JPA实体定义 |
| dto | 数据传输对象 | 请求/响应数据结构 |
| config | 配置类 | 数据源、文件存储等配置 |
| util | 工具类 | 通用工具方法 |

---

## 三、项目结构

### 3.1 后端项目结构

```
backend/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/example/hometwin/
│       │       ├── HomeTwinApplication.java
│       │       ├── controller/
│       │       │   ├── ItemController.java
│       │       │   ├── RoomController.java
│       │       │   └── PersonController.java
│       │       ├── service/
│       │       │   ├── ItemService.java
│       │       │   ├── RoomService.java
│       │       │   ├── PersonService.java
│       │       │   └── AiService.java
│       │       ├── repository/
│       │       │   ├── ItemRepository.java
│       │       │   ├── RoomRepository.java
│       │       │   └── PersonRepository.java
│       │       ├── entity/
│       │       │   ├── Item.java
│       │       │   ├── Room.java
│       │       │   └── Person.java
│       │       ├── dto/
│       │       │   ├── request/
│       │       │   │   ├── ItemCreateRequest.java
│       │       │   │   ├── ItemUpdateRequest.java
│       │       │   │   └── ItemSearchRequest.java
│       │       │   └── response/
│       │       │       ├── ItemResponse.java
│       │       │       ├── RoomResponse.java
│       │       │       ├── PersonResponse.java
│       │       │       └── ApiResponse.java
│       │       ├── config/
│       │       │   ├── MinioConfig.java
│       │       │   └── WebConfig.java
│       │       └── util/
│       │           └── FileUtil.java
│       └── resources/
│           ├── application.yml
│           └── schema.sql
└── pom.xml
```

### 3.2 前端项目结构

```
frontend/
├── src/
│   ├── pages/
│   │   ├── index/
│   │   │   └── index.vue          # 首页 - 房间列表
│   │   ├── room/
│   │   │   └── room.vue           # 房间详情 - 物品列表
│   │   ├── item/
│   │   │   ├── add.vue            # 添加物品
│   │   │   ├── detail.vue         # 物品详情
│   │   │   └── search.vue         # 搜索物品
│   │   └── statistics/
│   │       └── statistics.vue     # 统计页面
│   ├── components/
│   │   ├── RoomCard.vue           # 房间卡片
│   │   ├── ItemCard.vue           # 物品卡片
│   │   └── SearchBar.vue          # 搜索栏
│   ├── utils/
│   │   └── request.js             # 请求封装
│   ├── data/
│   │   └── mock.js                # Mock数据
│   ├── App.vue
│   ├── main.js
│   └── manifest.json
├── package.json
└── vue.config.js
```

---

## 四、API接口设计

### 4.1 物品接口 (ItemController)

| API路径 | HTTP方法 | Controller文件 | 功能描述 |
| :--- | :--- | :--- | :--- |
| /api/items | POST | ItemController.java | 添加物品 |
| /api/items | GET | ItemController.java | 分页查询物品列表 |
| /api/items/{id} | GET | ItemController.java | 查询物品详情 |
| /api/items/{id} | PUT | ItemController.java | 更新物品信息 |
| /api/items/{id} | DELETE | ItemController.java | 删除物品 |
| /api/items/{id}/clean | PUT | ItemController.java | 标记物品已清理 |
| /api/items/search | GET | ItemController.java | 搜索物品 |

#### 4.1.1 POST /api/items - 添加物品

请求体：
```json
{
  "name": "string",
  "description": "string",
  "roomId": "string",
  "personId": "string",
  "location": "string",
  "photo": "base64 string or null"
}
```

响应体：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": "string",
    "name": "string",
    "description": "string",
    "roomId": "string",
    "roomName": "string",
    "personId": "string",
    "personName": "string",
    "location": "string",
    "photo": "url",
    "addedAt": "timestamp",
    "status": "active"
  }
}
```

#### 4.1.2 GET /api/items - 查询物品列表

请求参数：

| 参数 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| roomId | String | 否 | 按房间筛选 |
| personId | String | 否 | 按人物筛选 |
| page | Integer | 是 | 页码（从0开始） |
| size | Integer | 是 | 每页数量 |

响应体：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "content": [...],
    "totalElements": 100,
    "totalPages": 10,
    "currentPage": 0
  }
}
```

#### 4.1.3 GET /api/items/search - 搜索物品

请求参数：

| 参数 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| keyword | String | 是 | 搜索关键词 |
| roomId | String | 否 | 房间筛选 |

响应体：同上

### 4.2 房间接口 (RoomController)

| API路径 | HTTP方法 | Controller文件 | 功能描述 |
| :--- | :--- | :--- | :--- |
| /api/rooms | GET | RoomController.java | 获取所有房间列表 |
| /api/rooms/{id} | GET | RoomController.java | 查询房间详情（含物品统计） |

### 4.3 人物接口 (PersonController)

| API路径 | HTTP方法 | Controller文件 | 功能描述 |
| :--- | :--- | :--- | :--- |
| /api/persons | GET | PersonController.java | 获取所有人物列表 |
| /api/persons/{id} | GET | PersonController.java | 查询人物详情（含物品统计） |

### 4.4 文件上传接口

| API路径 | HTTP方法 | Controller文件 | 功能描述 |
| :--- | :--- | :--- | :--- |
| /api/upload/image | POST | ItemController.java | 上传图片 |

---

## 五、数据库设计

### 5.1 数据库表结构

#### 5.1.1 rooms 表

| 字段名 | 类型 | 约束 | 说明 |
| :--- | :--- | :--- | :--- |
| id | VARCHAR(36) | PRIMARY KEY | 房间ID |
| name | VARCHAR(50) | NOT NULL | 房间名称 |
| icon | VARCHAR(100) | NULL | 图标URL |
| color | VARCHAR(20) | NULL | 主题色 |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

#### 5.1.2 persons 表

| 字段名 | 类型 | 约束 | 说明 |
| :--- | :--- | :--- | :--- |
| id | VARCHAR(36) | PRIMARY KEY | 人物ID |
| name | VARCHAR(50) | NOT NULL | 人物名称 |
| relation | VARCHAR(50) | NULL | 与小女孩的关系 |
| avatar | VARCHAR(200) | NULL | 头像URL |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

#### 5.1.3 items 表

| 字段名 | 类型 | 约束 | 说明 |
| :--- | :--- | :--- | :--- |
| id | VARCHAR(36) | PRIMARY KEY | 物品ID |
| name | VARCHAR(100) | NOT NULL | 物品名称 |
| description | VARCHAR(500) | NULL | 物品描述 |
| photo | VARCHAR(200) | NULL | 照片URL |
| room_id | VARCHAR(36) | FOREIGN KEY | 所属房间ID |
| person_id | VARCHAR(36) | FOREIGN KEY | 所属人物ID |
| location | VARCHAR(200) | NULL | 具体位置 |
| status | VARCHAR(20) | DEFAULT 'active' | 状态：active/cleaned |
| added_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 添加时间 |
| cleaned_at | DATETIME | NULL | 清理时间 |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

### 5.2 初始数据

#### 5.2.1 房间初始数据

| id | name | icon | color |
| :--- | :--- | :--- | :--- |
| room-001 | 主卧 | 🏠 | #4A90D9 |
| room-002 | 次卧 | 🛏️ | #67C23A |
| room-003 | 客厅 | 🛋️ | #E6A23C |
| room-004 | 餐厅 | 🥘 | #F56C6C |
| room-005 | 厨房 | 🍳 | #909399 |
| room-006 | 卫生间 | 🚽 | #B37FEB |
| room-007 | 阳台 | 🌳 | #30C5FF |

#### 5.2.2 人物初始数据

| id | name | relation | avatar |
| :--- | :--- | :--- | :--- |
| person-001 | 小女孩 | 本人 | 👧 |
| person-002 | 服务物品 | 服务小女孩 | 🎁 |
| person-003 | 外婆 | 小女孩外婆 | 👵 |

---

## 六、部署与集成

### 6.1 依赖与环境

| 依赖名称 | GroupId | ArtifactId | 版本 |
| :--- | :--- | :--- | :--- |
| Spring Boot Starter Web | org.springframework.boot | spring-boot-starter-web | 3.2.x |
| Spring Boot Starter Data JPA | org.springframework.boot | spring-boot-starter-data-jpa | 3.2.x |
| MySQL Connector | com.mysql | mysql-connector-j | 8.0.x |
| MinIO SDK | io.minio | minio | 8.5.x |
| Lombok | org.projectlombok | lombok | 1.18.x |
| Spring Boot Starter Validation | org.springframework.boot | spring-boot-starter-validation | 3.2.x |

### 6.2 配置说明

#### 6.2.1 application.yml 关键配置

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/hometwin?useSSL=false&serverTimezone=Asia/Shanghai
    username: admin
    password: password
    driver-class-name: com.mysql.cj.jdbc.Driver
  
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

minio:
  url: http://localhost:9000
  access-key: minioadmin
  secret-key: minioadmin
  bucket-name: hometwin

ai:
  baidu:
    api-key: your-api-key
    secret-key: your-secret-key
```

### 6.3 启动方式

#### 6.3.1 开发环境

```bash
# 启动MySQL
docker run -d -p 3306:3306 -e MYSQL_ROOT_PASSWORD=password -e MYSQL_DATABASE=hometwin mysql:8.0

# 启动MinIO
docker run -d -p 9000:9000 -p 9001:9001 -e MINIO_ROOT_USER=minioadmin -e MINIO_ROOT_PASSWORD=minioadmin minio/minio server /data --console-address ":9001"

# 启动后端服务
cd backend
mvn spring-boot:run

# 启动前端服务
cd frontend
npm install
npm run dev:h5
```

#### 6.3.2 微信小程序开发

```bash
# 构建小程序
cd frontend
npm run build:mp-weixin

# 打开微信开发者工具，导入 dist/build/mp-weixin 目录
```

---

## 七、安全性考虑

| 风险点 | 解决方案 |
| :--- | :--- |
| 文件上传安全 | 限制文件类型（仅允许图片）、限制文件大小（最大5MB）、文件重命名防止路径遍历 |
| SQL注入 | 使用JPA/Hibernate预编译语句，禁止拼接SQL |
| 未授权访问 | 后续可集成JWT认证，当前阶段可通过IP白名单限制 |
| 数据泄露 | 敏感数据加密存储，API响应脱敏处理 |
| 图片存储安全 | MinIO配置访问策略，设置合理的过期时间 |

---

## 八、开发计划

| 阶段 | 任务 | 负责人 | 预估时长 |
| :--- | :--- | :--- | :--- |
| 第一阶段 | 后端项目初始化 + 数据库设计 | 开发 | 2天 |
| | 房间、人物、物品实体与Repository | 开发 | 1天 |
| | 房间、人物、物品Service实现 | 开发 | 2天 |
| | Controller层API实现 | 开发 | 2天 |
| | 初始数据导入 | 开发 | 0.5天 |
| 第二阶段 | 前端项目初始化 (UniApp) | 开发 | 1天 |
| | 首页 - 房间列表页面 | 开发 | 1天 |
| | 房间详情 - 物品列表页面 | 开发 | 1天 |
| | 添加物品页面 | 开发 | 1天 |
| | 物品搜索页面 | 开发 | 1天 |
| | 统计页面 | 开发 | 0.5天 |
| 第三阶段 | 微信小程序构建与调试 | 开发 | 1天 |
| | 响应式适配优化 | 开发 | 1天 |
| | 测试与Bug修复 | 开发 | 2天 |

---

## 九、预期成果

| 成果物 | 说明 |
| :--- | :--- |
| 后端API服务 | 完整的RESTful API，支持物品CRUD和搜索 |
| 前端H5应用 | 适配PC和移动端的Web应用 |
| 微信小程序 | 可部署到微信的小程序包 |
| 数据库脚本 | 包含初始数据的SQL脚本 |
| API文档 | Swagger/OpenAPI格式的接口文档 |
