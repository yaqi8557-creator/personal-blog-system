[README.md](https://github.com/user-attachments/files/28953702/README.md)
# 个人博客系统

一个基于 Spring Boot 3 的前后端分离博客系统，支持文章管理、评论互动、点赞、搜索和数据统计。

## 技术栈

| 分类 | 技术 |
|------|------|
| 后端 | Spring Boot 3, MyBatis-Plus, Spring Data JPA |
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
3. 启动项目，访问 Knife4j 文档：`http://localhost:8080/doc.html`
