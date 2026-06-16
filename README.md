[Uploading README.md…]()
# 个人博客系统

一个基于 Spring Boot 3 的前后端分离博客系统，支持文章管理、评论互动、点赞、搜索和数据统计。

## 技术栈

| 分类 | 技术 |
|------|------|
| 后端 | Spring Boot 3, MyBatis-Plus |
| 缓存 | Redis（列表缓存、Set 点赞） |
| 数据库 | MySQL |
| 认证 | JWT + BCrypt |
| 文档 | Knife4j / Swagger |

## 功能模块

- 文章的增删改查、分类管理、模糊搜索
- 评论互动、点赞/取消（Redis Set 实现）
- 首页文章列表缓存（600s 过期），分类列表缓存（1800s 过期）
- JWT 无状态登录认证 + 拦截器鉴权
- 站点数据统计
- 统一响应体封装、全局异常处理

## 项目结构

```
├── controller      # 控制器层
├── service         # 业务逻辑层
├── mapper          # 数据访问层
├── entity          # 数据库实体
├── dto / vo        # 请求/响应对象
├── config          # 配置类（拦截器、分页、跨域等）
├── utils           # 工具类（JWT 等）
└── exception       # 全局异常处理
```

## 快速启动

1. 创建 MySQL 数据库，导入建表 SQL
2. 配置 `application.yml` 中的数据库和 Redis 连接信息
3. 启动项目，访问 Knife4j 文档：`http://localhost:8081/doc.html`

## 接口文档

> 基础地址：`http://localhost:8081`
> 认证方式：登录后在请求头传入 `Authorization: Bearer <token>`
> 公开接口无需 token，需登录的接口会标注 🔒

### 用户模块

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | `/api/user/register` | 用户注册 |  |
| POST | `/api/user/login` | 用户登录，返回 JWT token |  |
| GET | `/api/user/info` | 获取当前用户信息 | 🔒 |

**注册请求体：**
```json
{
  "username": "newuser",
  "password": "123456",
  "nickname": "小明"
}
```

**登录请求体：**
```json
{
  "username": "admin",
  "password": "123456"
}
```

**用户信息响应（UserVO）：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "username": "admin",
    "nickname": "小明",
    "email": "admin@example.com",
    "avatar": "https://...",
    "createTime": "2025-11-01T10:00:00"
  }
}
```

---

### 文章模块

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | `/api/article/list?page=1&size=10&categoryId=1` | 分页查询文章列表（带缓存） |  |
| GET | `/api/article/{id}` | 查看文章详情 |  |
| POST | `/api/article` | 发布文章 | 🔒 |
| PUT | `/api/article/{id}` | 修改文章 | 🔒 |
| DELETE | `/api/article/{id}` | 删除文章 | 🔒 |
| GET | `/api/article/hot` | 热门文章 Top10（按点赞数） |  |
| GET | `/api/article/search?keyword=xxx&page=1&size=10` | 模糊搜索（标题+内容） |  |
| GET | `/api/article/stats` | 站点统计数据 |  |

**发布/修改文章请求体：**
```json
{
  "id": 1,
  "title": "Spring Boot入门",
  "content": "本文介绍...",
  "summary": "摘要",
  "categoryId": 1
}
```
> `id` 修改时必传，发布时不需要

**文章列表响应（分页）：**
```json
{
  "code": 200,
  "data": {
    "records": [
      {
        "id": 1,
        "title": "Spring Boot入门",
        "summary": "摘要",
        "categoryId": 1,
        "categoryName": "Java",
        "authorId": 1,
        "authorName": "小明",
        "viewCount": 128,
        "likeCount": 32,
        "createTime": "2025-11-01T10:00:00",
        "updateTime": "2025-11-02T12:00:00"
      }
    ],
    "total": 50,
    "size": 10,
    "current": 1
  }
}
```

**站点统计响应：**
```json
{
  "code": 200,
  "data": {
    "articles": 50,
    "categories": 8,
    "totalViews": 12000,
    "totalLikes": 800
  }
}
```

---

### 分类模块

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | `/api/category/list` | 获取所有分类（带缓存） |  |
| POST | `/api/category` | 新增分类 | 🔒 |
| PUT | `/api/category` | 修改分类 | 🔒 |
| DELETE | `/api/category/{id}` | 删除分类 | 🔒 |

**分类请求/响应：**
```json
{
  "id": 1,
  "name": "Java",
  "description": "Java技术文章",
  "createTime": "2025-11-01T10:00:00"
}
```

---

### 评论模块

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | `/api/comment/list/{articleId}` | 获取文章评论列表 |  |
| POST | `/api/comment` | 发表评论 | 🔒 |
| DELETE | `/api/comment/{id}` | 删除评论 | 🔒 |

**评论请求体：**
```json
{
  "articleId": 1,
  "content": "写得真好！"
}
```

**评论响应：**
```json
{
  "code": 200,
  "data": [
    {
      "id": 1,
      "articleId": 1,
      "userId": 2,
      "content": "写得真好！",
      "nickname": "小红",
      "createTime": "2025-11-03T09:00:00"
    }
  ]
}
```

---

### 点赞模块

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | `/api/like/{articleId}` | 点赞文章 | 🔒 |
| DELETE | `/api/like/{articleId}` | 取消点赞 | 🔒 |
| GET | `/api/like/status/{articleId}` | 查询点赞状态+点赞数 | 🔒 |

**点赞响应：**
```json
{
  "code": 200,
  "data": {
    "liked": true,
    "likeCount": 33
  }
}
```
