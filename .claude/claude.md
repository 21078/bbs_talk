# BBS论坛系统项目文档

## 项目概述

这是一个基于Spring Boot + MyBatis的简单BBS（Bulletin Board System）论坛系统，实现了用户注册登录、发帖、回复等基本功能。

## 阅读提醒：由于前后端代码在一个项目（前端在static目录），每次后续更新要同时修改前后端，并且结束后请务必更新claude.md文件，以便下一次开发阅读。
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
- `category`: 帖子板块 (必填)

### 回复表 (reply)
- `rid`: 回复ID (主键)
- `pid`: 帖子ID (外键)
- `uid`: 回复用户ID (外键)
- `replymessage`: 回复内容
- `replytime`: 回复时间

### 收藏表 (favorite)
- `fid`: 收藏ID (主键)
- `uid`: 用户ID (外键)
- `pid`: 帖子ID (外键)
- `favtime`: 收藏时间

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
- 发帖功能（必须选择板块）
- 帖子分页浏览
- 帖子详情查看
- 帖子收藏数统计
- 我的帖子管理（查看、编辑内容、删除）

### 3. 回复管理
- 帖子回复功能
- 回复时间排序
- 回复数量统计

### 4. 收藏管理
- 添加收藏 (favorite/add/{pid})
- 取消收藏 (favorite/remove/{pid})
- 查看收藏列表 (favorite/list)
- 收藏状态实时显示

### 5. 管理员功能
- 用户管理（禁言/解禁）
- 帖子管理（删除）
- 置顶/取消置顶帖子
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
- `GET /toggleSticky/{pid}/{action}` - 置顶/取消置顶帖子（action: sticky/unsticky）

### 帖子相关
- `GET /` - 首页帖子列表
- 分页参数: `page` (页码)

### 帖子相关
- `GET /` - 首页帖子列表
- 分页参数: `page` (页码)
- `GET /myPosts.do` - 我的帖子管理页面
- `POST /updatePostContent.do` - 更新帖子内容

### 收藏相关
- `GET /favorite/add/{pid}` - 添加收藏
- `GET /favorite/remove/{pid}` - 取消收藏
- `GET /favorite/list` - 查看收藏列表

## 最新功能更新

### 置顶功能
- 新增帖子置顶功能，管理员可以将重要帖子置顶显示
- 置顶帖子在列表顶部优先显示，并带有红色"置顶"标签
- 只有管理员可以操作置顶/取消置顶功能
- 排序规则：置顶帖子 > 按最后回复时间排序
- 在首页、帖子详情页和我的帖子页面均显示置顶状态

### 板块功能
- 发帖时必须从预设板块中选择
- 预设板块：娱乐、技术、美食、旅游、问题
- 在帖子列表和详情页以不同颜色显示板块标签
- 每个板块有独特的颜色标识：
  - 娱乐：绿色
  - 技术：蓝色  
  - 美食：黄色
  - 旅游：浅蓝
  - 问题：红色
- 新增板块筛选功能，可在首页按板块筛选帖子

### 帖子管理模块
- 新增"我的帖子"页面，用户可以管理自己发布的帖子
- 支持修改帖子内容（不能修改标题）
- 支持删除自己的帖子
- 显示帖子的回复数、收藏数等统计信息

### 用户体验优化
- 分页显示调整为每页4个帖子
- 注册页面添加密码长度提示（6-16位）
- 管理员后台页面移除文件管理相关内容
- 导航栏布局优化，退出按钮位置调整
- 丰富的示例数据，包含多板块帖子和回复

### 数据初始化
- 新增4个示例用户（admin、u1、u2、u3）
- 添加11个示例帖子，覆盖所有板块
- 添加14条回复数据，展示用户互动
- 添加10条收藏数据，展示收藏功能

## 后续开发建议

1. 添加前端页面美化
2. 实现更完善的权限控制系统
3. 添加预设板块下拉选择
4. 实现站内消息通知
5. 添加文件上传功能
6. 实现搜索引擎优化
7. 添加数据缓存机制
8. 完善日志记录系统
9. 置顶功能增强：添加置顶时间、过期时间、置顶理由等功能