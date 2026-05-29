# 09老师说的都队

### 唐浩 赵文熙 陈闽 彭浩 樊世奇

<p align="center">
  <img src="./src/main/resources/image/work_harder.jpg" alt="Work Harder">
</p>

# BBS论坛系统

一个基于Spring Boot + MyBatis的轻量级论坛系统，支持用户注册登录、发帖回复、收藏管理、积分系统等完整的论坛功能。

## 技术栈

### 后端技术

- **框架**: Spring Boot 2.7.18
- **持久层**: MyBatis 2.3.1
- **数据库**: MySQL 8.0.33
- **连接池**: Druid 1.2.20
- **模板引擎**: Thymeleaf
- **构建工具**: Maven
- **Java版本**: 1.8
- **云存储**: 阿里云OSS SDK 3.18.1
- **HTTP客户端**: OkHttp 4.12.0

### 前端技术

- **模板引擎**: Thymeleaf
- **样式框架**: Bootstrap
- **图标库**: Font Awesome

## 核心功能

✅ 用户注册登录和个人信息管理
✅ 多板块发帖（娱乐、技术、美食、旅游、问题）
✅ 帖子回复和置顶回复功能
✅ 帖子收藏和收藏管理
✅ 积分系统和奖励机制
✅ 管理员功能（用户管理、帖子管理）
✅ 帖子置顶和板块分类
✅ 封面图片上传（阿里云OSS）
✅ 用户账户注销功能

## 快速开始

### 环境要求

- Java 1.8 或更高版本
- MySQL 8.0 或更高版本
- Maven 3.6 或更高版本

### Windows系统启动

#### 方法1：一键启动（推荐）

1. 以管理员运行 `run.bat`
2. 脚本会自动完成数据库更新和项目启动

#### 方法2：手动启动

```bash
# 1. 启动MySQL服务
# 2. 创建数据库并导入数据
mysql -u root -p123456
CREATE DATABASE bbs CHARACTER SET utf8mb4;
USE bbs;
source bbs.sql;

# 3. 编译项目
mvn clean compile

# 4. 启动应用
mvn spring-boot:run
```

### Linux系统启动

#### 方法1：一键启动（推荐）

```bash
# 给脚本执行权限
chmod +x run.sh

# 运行启动脚本
./run.sh
```

#### 方法2：手动启动

```bash
# 1. 安装必要依赖
sudo apt update
sudo apt install mysql-server maven openjdk-8-jdk

# 2. 启动MySQL服务
sudo systemctl start mysql

# 3. 创建数据库并导入数据
mysql -u root -p123456
CREATE DATABASE bbs CHARACTER SET utf8mb4;
USE bbs;
source bbs.sql;

# 4. 编译项目
mvn clean compile

# 5. 启动应用
mvn spring-boot:run
```

### 通用启动

java -jar bbs.jar

## 访问应用

应用启动后，访问以下地址：

- 主页面: http://localhost:8080

## 默认账户

### 管理员账户

- 用户名: `admin`
- 密码: `123456`

### 普通用户账户

- 用户名: `u1` / `u2` / `u3`
- 密码: `123456`

## 项目结构

```
├── src/main/java/com/zzx/
│   ├── Application.java              # 应用启动类
│   ├── controller/                   # 控制器层
│   ├── service/                      # 服务层
│   ├── mapper/                       # 数据访问层
│   ├── model/                        # 实体类
│   └── config/                       # 配置类
├── src/main/resources/
│   ├── application.yml              # 主配置文件
│   ├── application-dev.yml          # 开发环境配置
│   └── templates/                   # Thymeleaf模板
├── static/                          # 静态资源
├── bbs.sql                          # 数据库脚本
├── run.bat                          # Windows启动脚本
├── run.sh                           # Linux启动脚本
└── pom.xml                          # Maven配置文件
```

## 数据库配置

默认数据库配置（application-dev.yml）：

- **数据库**: MySQL
- **地址**: localhost:3306
- **数据库名**: bbs
- **用户名**: root
- **密码**: 123456

## 阿里云OSS配置

系统使用阿里云OSS存储帖子封面图片，配置文件中的OSS参数：

- **endpoint**: 接入点
- **bucket-name**: 桶名
- **access-key-id**: 请替换为您的AccessKey ID
- **access-key-secret**: 请替换为您的AccessKey Secret

## 主要页面说明

### 首页

- 显示所有帖子列表，支持分页
- 按置顶状态和最后回复时间排序
- 显示帖子板块分类和收藏数量

### 发帖页面

- 选择板块（娱乐、技术、美食、旅游、问题）
- 输入标题和内容
- 可选上传封面图片
- 问答板块可设置奖励积分

### 帖子详情页

- 显示帖子完整内容和封面图片
- 显示所有回复，置顶回复优先显示
- 支持回复和收藏功能

### 个人中心

- 显示用户信息和当前积分
- 管理个人信息
- 查看我的帖子和收藏

### 管理员后台

- 用户管理（禁言、解禁、注销）
- 帖子管理（删除、置顶）
- 系统监控

## 开发说明

### 代码规范

- 遵循Java编码规范
- 使用MyBatis注解和XML混合开发
- 异常处理采用全局异常处理器

### 安全说明

- 密码采用明文存储（仅限教学演示环境）
- 生产环境请务必使用密码加密存储

## 常见问题

### Q: 应用启动失败怎么办？

A: 检查以下几点：

1. MySQL服务是否启动
2. 数据库连接配置是否正确
3. 端口8080是否被占用

### Q: 如何修改数据库密码？

A: 修改 `application-dev.yml` 中的数据库密码配置，并确保MySQL用户密码匹配。

### Q: 如何更换OSS配置？

A: 在 `application-dev.yml` 中修改阿里云OSS的相关配置参数。

## 许可证

本项目仅供学习和教学使用

## 作者

21078

## 版本

v1.0.0 (2026-05-19)
