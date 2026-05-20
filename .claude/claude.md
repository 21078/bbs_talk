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
- **框架**: Spring Boot 2.7.18
- **持久层**: MyBatis 2.3.1
- **数据库**: MySQL 8.0.33
- **连接池**: Druid 1.2.20
- **模板引擎**: Thymeleaf
- **构建工具**: Maven
- **Java版本**: 1.8
- **云存储**: 阿里云OSS SDK 3.18.1

### 前端技术栈
- **模板引擎**: Thymeleaf
- **HTTP客户端**: OkHttp 4.12.0

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
- `score`: 用户积分 (默认0)

### 帖子表 (post)
- `pid`: 帖子ID (主键)
- `ptitle`: 帖子标题
- `pbody`: 帖子内容
- `psendtime`: 发帖时间
- `lastreplytime`: 最后回复时间
- `uid`: 发帖用户ID (外键)
- `category`: 帖子板块 (必填)
- `is_sticky`: 是否置顶 (0否, 1是)
- `prize`: 奖励积分 (问答板块使用, 默认0)
- `path`: 封面图片路径 (阿里云OSS存储地址)

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

### 6. 积分系统
- 用户积分展示（个人信息页面）
- 问答板块奖励积分设置（发帖时）
- 回复置顶自动积分奖励
- 积分获取记录和统计

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
- 数据库账号: root/your_db_password
- Thymeleaf配置
- 文件上传限制: 1024MB
- 阿里云OSS配置：
  - endpoint: oss-cn-huhehaote.aliyuncs.com
  - access-key-id: your_access_key_id
  - access-key-secret: your_access_key_secret
  - bucket-name: bbspicturebed

## 启动方式

1. 创建数据库并执行 `bbs.sql` 脚本
2. 配置数据库连接信息
3. 运行 `Application.java` 启动Spring Boot应用
4. 访问 `http://localhost:8080`

## 默认管理员账户

- 用户名: admin
- 密码: your_admin_password

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

### 回复置顶功能
- 新增回复置顶功能，只有帖子创建者可以置顶该帖子的回复
- 每个帖子只能有一个置顶回复，置顶回复会优先显示在回复列表顶部
- 置顶回复有特殊标识（红色置顶标签和黄色背景）
- 帖子创建者可以置顶或取消置顶回复
- 回复按置顶状态和时间排序：置顶回复 > 按时间正序排列
- 问答板块的帖子置顶回复时，回复者可获得相应积分奖励

### 积分和奖励系统
- 用户表新增积分字段，用于记录用户积分总数
- 帖子表新增奖励字段，问答板块可设置奖励积分
- 个人信息页面显示用户当前积分
- 发帖时选择问答板块需设置奖励积分（1-10分）
- 当帖子创建者置顶回复时，系统自动给回复者发放奖励积分
- 积分只能由系统自动发放，用户和管理员无法手动修改

### 数据初始化
- 新增4个示例用户（admin、u1、u2、u3）并设置初始积分
- 添加11个示例帖子，覆盖所有板块，其中问答板块设置奖励积分
- 添加14条回复数据，展示用户互动
- 添加10条收藏数据，展示收藏功能
- 为部分回复设置置顶状态，展示置顶功能

### 封面图片功能
- 新增帖子封面图片功能，用户发帖时可选择上传封面图片
- 使用阿里云OSS存储图片，支持JPG、PNG、GIF格式，最大5MB
- 首页帖子列表显示小尺寸封面图片（如果有）
- 帖子详情页显示完整尺寸封面图片（如果有）
- 支持在"我的帖子"页面编辑帖子时更新封面图片
- 删除帖子时自动同步删除OSS中的封面图片
- 图片路径存储在帖子表的path字段中

### 依赖版本升级
- 升级Spring Boot从2.2.6到2.7.18
- 升级MyBatis从1.3.2到2.3.1
- 升级Druid从1.1.13到1.2.20
- 升级MySQL驱动到8.0.33
- 升级OkHttp从3.14.7到4.12.0
- 升级阿里云OSS SDK从3.17.4到3.18.1
- 修复OSS V1签名问题，使用新的CredentialProvider方式创建客户端
- 配置OSS客户端使用V4签名(SignVersion.V4)
- 指定OSS region为cn-huhehaote
- 使用OSSClientBuilder.create()流式API创建客户端
- 更新MySQL驱动类名为com.mysql.cj.jdbc.Driver
- 移除endpoint中的https://前缀以兼容新版本OSS SDK

### 用户注销功能
- 新增用户账户注销功能，允许用户永久删除账户
- 普通用户可在个人管理页面找到注销按钮
- 管理员可在后台管理页面注销普通用户账户
- 注销操作会删除用户的所有帖子、回复及相关数据
- 管理员账户不能注销（安全保护）
- 注销操作前需要用户确认，防止误操作
- 支持管理员注销普通用户，但不能注销自己

### 用户体验优化
- 错误页面添加5秒自动跳转到首页功能
- 错误页面美化，使用Bootstrap样式
- 添加倒计时显示，提升用户体验
- 提供立即跳转按钮，用户可手动跳转
- 所有非法操作都会跳转到错误页面并自动返回首页

## 后续开发建议

1. 添加前端页面美化
2. 实现更完善的权限控制系统
3. 添加预设板块下拉选择
4. 实现站内消息通知
5. 实现搜索引擎优化
6. 添加数据缓存机制
7. 完善日志记录系统
8. 回复置顶功能增强：添加置顶时间、过期时间、置顶理由等功能