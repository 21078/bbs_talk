# BBS论坛系统项目文档

## 项目概述

这是一个基于Spring Boot + MyBatis的简单BBS（Bulletin Board System）论坛系统，实现了用户注册登录、发帖、回复等基本功能。

## 阅读提醒：每次后续更新后，请务必更新claude.md文件，以便下一次开发阅读。
## 每次更新后端代码后，务必编写注释参考以下
```java
    /**
     * 保存帖子
     * @param post 帖子对象
     * @return void
     */
    @Override
    public void save(Post post) {
        postMapper.save(post);
    }
```

## 技术架构

### 后端技术栈
- **框架**: Spring Boot 2.2.6
- **持久层**: MyBatis 1.3.2
- **数据库**: MySQL
- **连接池**: Druid 1.1.13
- **模板引擎**: Thymeleaf
- **构建工具**: Maven
- **Java版本**: 1.8

### 前端技术栈
- **模板引擎**: Thymeleaf
- **HTTP客户端**: OkHttp 3.14.7

## 项目结构

```
src/main/java/com/zzx/
├── Application.java              # 应用启动类
├── config/
│   └── WebMvcConfig.java         # Web配置类
├── controller/
│   ├── HostController.java        # 主持人/管理员控制器
│   ├── IndexController.java       # 首页控制器
│   ├── PostController.java        # 帖子控制器
│   ├── ReplyController.java       # 回复控制器
│   └── UserController.java        # 用户控制器
├── exception/
│   ├── GlobalExceptionHandler.java # 全局异常处理器
│   └── MessageException.java      # 自定义异常类
├── mapper/
│   ├── PostMapper.java           # 帖子数据访问接口
│   ├── ReplyMapper.java          # 回复数据访问接口
│   └── UserMapper.java           # 用户数据访问接口
├── model/
│   ├── Page.java                 # 分页模型
│   ├── Post.java                 # 帖子实体类
│   ├── Reply.java                # 回复实体类
│   └── User.java                 # 用户实体类
└── service/
    ├── Impl/
    │   ├── PostServiceImpl.java   # 帖子服务实现
    │   ├── ReplyServiceImpl.java  # 回复服务实现
    │   └── UserServiceImpl.java   # 用户服务实现
    ├── PostService.java          # 帖子服务接口
    ├── ReplyService.java         # 回复服务接口
    └── UserService.java          # 用户服务接口
```

## 数据库设计

### 用户表 (user)
- `uid`: 用户ID (主键)
- `uname`: 用户名 (唯一)
- `upwd`: 密码 (明文存储)
- `ustate`: 用户状态 (1正常, 0禁用)
- `ucreatetime`: 创建时间
- `level`: 用户级别 (0管理员, 1普通用户)
- `phone`: 联系电话 (可选)
- `career`: 职业 (可选)
- `address`: 工作地址 (可选)

### 帖子表 (post)
- `pid`: 帖子ID (主键)
- `ptitle`: 帖子标题
- `pbody`: 帖子内容
- `psendtime`: 发帖时间
- `lastreplytime`: 最后回复时间
- `uid`: 发帖用户ID (外键)

### 回复表 (reply)
- `rid`: 回复ID (主键)
- `pid`: 帖子ID (外键)
- `uid`: 回复用户ID (外键)
- `replymessage`: 回复内容
- `replytime`: 回复时间

## 主要功能模块

### 1. 用户管理
- 用户注册 (register.do)
- 用户登录 (login.do)
- 用户退出 (logout.do)
- 密码修改 (updatePassword.do)
- 个人信息更新 (updateProfile.do)
- 用户禁言/解禁 (ban/unban)
- 个人信息页面 (person.do)

### 2. 帖子管理
- 首页帖子列表展示
- 发帖功能
- 帖子分页浏览
- 帖子详情查看

### 3. 回复管理
- 帖子回复功能
- 回复时间排序
- 回复数量统计

### 4. 管理员功能
- 用户管理（禁言/解禁）
- 系统监控

## 安全特性

1. **密码存储**: 明文存储密码
2. **会话管理**: 基于HttpSession的用户会话
3. **权限控制**: 管理员和普通用户分级权限
4. **输入验证**: 用户名和密码长度限制

## 配置文件

### application.yml
主配置文件，指定激活的开发环境

### application-dev.yml
开发环境配置：
- 服务器端口: 8080
- 数据库连接: MySQL (localhost:3306/bbs)
- 数据库账号: root/1234
- Thymeleaf配置
- 文件上传限制: 1024MB

## 启动方式

1. 创建数据库并执行 `bbs.sql` 脚本
2. 配置数据库连接信息
3. 运行 `Application.java` 启动Spring Boot应用
4. 访问 `http://localhost:8080`

## 默认管理员账户

- 用户名: admin
- 密码: 123456

## 开发注意事项

1. 密码采用明文存储方式
2. 用户名长度限制: ≤16字符
3. 密码长度限制: 6-16字符
4. 使用MyBatis注解和XML混合开发模式
5. 异常处理采用全局异常处理器
6. 前端页面使用Thymeleaf模板引擎

## API接口

### 用户相关
- `POST /register.do` - 用户注册
- `POST /login.do` - 用户登录
- `GET /logout.do` - 用户退出
- `POST /updatePassword.do` - 修改密码
- `POST /updateProfile.do` - 更新个人信息
- `GET /person.do` - 个人信息页面

### 管理员相关
- `GET /ban/{uid}` - 禁言用户
- `GET /unban/{uid}` - 解禁用户

### 帖子相关
- `GET /` - 首页帖子列表
- 分页参数: `page` (页码)

## 后续开发建议

1. 添加前端页面美化
2. 实现更完善的权限控制系统
3. 添加帖子分类功能
4. 实现站内消息通知
5. 添加文件上传功能
6. 实现搜索引擎优化
7. 添加数据缓存机制
8. 完善日志记录系统