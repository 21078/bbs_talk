# BBS论坛系统项目文档

## 项目概述

这是一个基于Spring Boot + MyBatis的简单BBS（Bulletin Board System）论坛系统，实现了用户注册登录、发帖、回复等基本功能。

## 阅读提醒：全局使用中文与我交流，由于前后端代码在一个项目（前端在static目录），每次后续更新要同时修改前后端，并且结束后请务必更新opencode.md文件，以便下一次开发阅读。

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

### WebSocket

- **服务端**: spring-boot-starter-websocket (TextWebSocketHandler)
- **客户端**: 原生 WebSocket API (自动重连)

## 项目结构

```
src/main/java/com/zzx/
├── Application.java              # 应用启动类
├── config/
│   ├── WebMvcConfig.java         # Web配置类
│   └── WebSocketConfig.java      # WebSocket配置类
├── controller/
│   ├── FavoriteController.java    # 收藏控制器
│   ├── FollowController.java      # 关注控制器
│   ├── HostController.java        # 主持人/管理员控制器
│   ├── IndexController.java       # 首页控制器
│   ├── NotificationController.java # 通知控制器
│   ├── PostController.java        # 帖子控制器
│   ├── ReplyController.java       # 回复控制器
│   └── UserController.java        # 用户控制器
├── exception/
│   ├── GlobalExceptionHandler.java # 全局异常处理器
│   └── MessageException.java      # 自定义异常类
├── mapper/
│   ├── FavoriteMapper.java        # 收藏数据访问接口
│   ├── FollowMapper.java          # 关注数据访问接口
│   ├── NotificationMapper.java    # 通知数据访问接口
│   ├── PostMapper.java            # 帖子数据访问接口
│   ├── ReplyMapper.java           # 回复数据访问接口
│   └── UserMapper.java            # 用户数据访问接口
├── model/
│   ├── Follow.java                # 关注实体类
│   ├── Notification.java          # 通知实体类
│   ├── Page.java                  # 分页模型
│   ├── Post.java                  # 帖子实体类
│   ├── Reply.java                 # 回复实体类
│   └── User.java                  # 用户实体类
├── service/
│   ├── Impl/
│   │   ├── FollowServiceImpl.java # 关注服务实现
│   │   ├── NotificationServiceImpl.java # 通知服务实现
│   │   ├── PostServiceImpl.java   # 帖子服务实现
│   │   ├── ReplyServiceImpl.java  # 回复服务实现
│   │   └── UserServiceImpl.java   # 用户服务实现
│   ├── FollowService.java         # 关注服务接口
│   ├── NotificationService.java   # 通知服务接口
│   ├── PostService.java           # 帖子服务接口
│   ├── ReplyService.java          # 回复服务接口
│   └── UserService.java           # 用户服务接口
└── websocket/
    └── NotificationWebSocketHandler.java # WebSocket通知处理器
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
- `path`: 用户头像路径 (阿里云OSS存储地址)
- `verified`: 认证状态 (0未认证, 1已认证，管理员可切换)

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
- `path`: 封面图片路径 (阿里云OSS存储地址，可选)

### 回复表 (reply)

- `rid`: 回复ID (主键)
- `pid`: 帖子ID (外键)
- `uid`: 回复用户ID (外键)
- `replymessage`: 回复内容
- `replytime`: 回复时间
- `is_sticky`: 是否置顶 (0否, 1是)

### 收藏表 (favorite)

- `fid`: 收藏ID (主键)
- `uid`: 用户ID (外键)
- `pid`: 帖子ID (外键)
- `favtime`: 收藏时间

### 关注表 (follow)

- `fid`: 关注ID (主键)
- `uid`: 关注者用户ID (外键)
- `follow_uid`: 被关注用户ID (外键)
- `followtime`: 关注时间
- 唯一约束: (uid, follow_uid)

### 通知表 (notification)

- `nid`: 通知ID (主键，自增)
- `uid`: 接收通知的用户ID (外键，索引)
- `type`: 通知类型 (reply/follow/favorite/system)
- `content`: 通知内容
- `from_uid`: 触发者用户ID
- `from_uname`: 触发者用户名
- `pid`: 关联帖子ID (可选)
- `ptitle`: 关联帖子标题 (可选)
- `is_read`: 是否已读 (0未读, 1已读，复合索引 uid+is_read)
- `create_time`: 创建时间 (默认当前时间，复合索引 uid+create_time)

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

### 5. 关注/粉丝管理

- 关注用户 (follow/add/{followUid})
- 取消关注 (follow/remove/{followUid})
- 检查关注状态 (follow/check/{followUid})
- 我的关注/粉丝页面 (follow/list?type=following|fans)
- 查看关注用户的帖子 (user/{uid}/posts)
- 用户信息弹窗内实时显示关注/取消关注按钮

### 6. 通知系统

- 基于 WebSocket 实时推送，支持自动重连（5秒间隔）
- 三种触发通知：回复通知、关注通知、收藏通知
- 导航栏铃铛图标 + 红色未读角标
- 通知中心页面 (/notification/page)，支持分页查看
- 通知操作：标为已读、全部已读、删除
- 通知类型过滤标签：全部/回复/收藏/关注/系统
- 系统通知不显示删除按钮

### 7. 管理员功能

- 用户管理（禁言/解禁、注销、认证切换）
- 帖子管理（删除、置顶/取消置顶）
- 全局通知发送（后台面板 → 所有用户实时推送）
- 全局设置开关：注册开关、互动开关、视频跳转开关

### 8. 积分系统

- 用户积分展示（个人信息页面）
- 问答板块奖励积分设置（发帖时）
- 回复置顶自动积分奖励
- 积分获取记录和统计

### 9. 用户信息查看

- `GET /user/profile/{uid}` JSON 接口
- 首页、帖子详情、收藏列表等处点击头像/昵称弹出用户信息浮窗
- 浮窗内可关注/取消关注

### 10. 视频播放 & 积分支付

- 视频播放页面 (`/video/{pid}`)，自定义播放控制栏
- 首次播放需支付50积分，二次确认防误触
- 播放完毕后暂停，不自动跳转

### 11. Emoji表情选择器

- 发帖/回帖/编辑帖子时 textarea 上方插入表情按钮
- 纯前端 Unicode Emoji，后端零改动

### 12. 彩蛋功能（pid=12）

- 音乐播放器（两首曲目 + 封面 + 进度条）
- 帖子锁定（拦截所有修改操作）
- "狠狠批评"整蛊小游戏（10道题 + 怒气值）
- 全部答对跳转视频页

## 安全特性

1. **密码存储**: 明文存储密码
2. **会话管理**: 基于HttpSession的用户会话
3. **权限控制**: 管理员和普通用户分级权限，认证用户标识（verified字段）
4. **输入验证**: 用户名和密码长度限制
5. **WebSocket鉴权**: 连接时通过URL参数传递uid，服务端维护uid→session映射

## 配置文件

### application.yml

主配置文件，指定激活的开发环境

### application-dev.yml

开发环境配置：

- 服务器端口: 8080
- 数据库连接: MySQL (localhost:3306/bbs)
- 数据库账号: root/123456
- Thymeleaf配置
- 文件上传限制: 1024MB
- 阿里云OSS配置：
  - endpoint: 
  - access-key-id: 
  - access-key-secret: 
  - bucket-name: 

## 启动方式

1. 创建数据库并执行 `bbs.sql` 脚本
2. 配置数据库连接信息
3. 运行 `Application.java` 启动Spring Boot应用
4. 访问 `http://localhost:8080`

## 默认管理员账户

- 用户名: admin
- 密码: 111222333

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
- `POST /admin/deleteUser/{uid}` - 管理员注销用户
- `POST /admin/toggleVerified/{uid}` - 切换用户认证状态
- `POST /admin/toggleVideoRedirect` - 切换视频跳转模式
- `POST /admin/toggleRegistration` - 切换注册开关
- `POST /admin/toggleInteraction` - 切换互动开关
- `POST /admin/sendGlobalNotification` - 发送全局通知

### 帖子相关

- `GET /` - 首页帖子列表（参数: `page`, `category`）
- `GET /post/{pid}.html` - 帖子详情页
- `GET /myPosts.do` - 我的帖子管理页面
- `POST /updatePostContent.do` - 更新帖子内容
- `POST /deletePost/{pid}` - 删除帖子
- `GET /video/{pid}` - 视频播放页面
- `POST /video/pay` - 视频播放积分支付

### 收藏相关

- `GET /favorite/add/{pid}` - 添加收藏
- `GET /favorite/remove/{pid}` - 取消收藏
- `GET /favorite/list` - 查看收藏列表

### 关注相关

- `GET /follow/add/{followUid}` - 关注用户
- `GET /follow/remove/{followUid}` - 取消关注
- `GET /follow/check/{followUid}` - 检查是否已关注（返回JSON）
- `GET /follow/list?type=following|fans` - 我的关注/粉丝页面（分页，type参数默认following）

### 用户信息相关

- `GET /user/profile/{uid}` - 获取用户公开信息（JSON）
- `GET /user/{uid}/posts` - 查看指定用户的帖子列表

### 通知相关

- `GET /notification/page` - 通知中心页面（参数: page, type）
- `GET /notification/list` - 通知列表JSON接口
- `GET /notification/unread-count` - 未读通知数量
- `POST /notification/read/{nid}` - 标为已读
- `POST /notification/read-all` - 全部已读
- `POST /notification/delete/{nid}` - 删除通知

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

### 用户头像功能

- 新增用户头像功能，所有用户都有头像显示
- 用户表添加path字段存储头像路径
- 默认头像地址：https://bbspicturebed.oss-cn-huhehaote.aliyuncs.com/post_covers/default_face.jpg
- 头像显示位置：
  - 导航栏用户名旁边（圆形24x24px）
  - 首页帖子列表发帖人旁边（圆形24x24px）
  - 个人信息页面（圆形80x80px）
  - 我的帖子页面
  - 我的收藏页面
  - 帖子详情页发帖人和回复人旁边（圆形24x24px）
- 头像样式统一使用圆形裁剪显示
- 新注册用户自动分配默认头像
- 所有头像加载失败时自动显示默认头像

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

### 回复删除功能完善

- 新增完善的回复删除权限控制系统
- 支持三种删除权限：
  - 管理员可删除任意回复
  - 帖子作者可删除该帖子的所有回复
  - 回复作者可删除自己的回复
- 前端权限控制：只有具备删除权限的用户才能看到删除按钮
- 后端权限验证：在控制器层进行严格的权限检查
- 删除确认机制：点击删除按钮时显示确认对话框
- 删除成功提示：操作完成后显示结果并刷新页面
- 修复重复确认问题：确保删除时只显示一次确认对话框

### 隐藏彩蛋 - 帖子ID=12音乐播放器

- 帖子ID为12时，在帖子详情页显示一个隐藏音乐播放器面板（panel-info风格）
- 左侧显示音乐封面（60x60px），右侧为播放/暂停按钮（▶/⏸）和可拖拽进度条
- 点击播放先弹出"曲风比较炸裂，建议佩戴耳机并调低音量"确认，再弹出"佩戴耳机！调低音量！"二次确认
- 使用阿里云OSS的MP3音频进行流式播放（Range请求）
- 播放状态实时更新进度条和时间显示

### 帖子锁定功能（pid=12）

- 帖子ID为12的帖子被锁定，所有操作（回复、收藏、删除、置顶）点击时弹出提示"该帖子已被锁定，无法修改"
- 按钮保持可见，不隐藏，仅拦截操作

### 狠狠批评整蛊功能（pid=12）

- 在帖子底部新增"狠狠批评"按钮（红色，所有用户可见，仅pid=12）
- 点击后弹出自定义模态框，包含10道整蛊选择题和"怒气值"进度条
- 前5题："狠狠批评"(红) vs "同情一下"(绿)，正确答案为狠狠批评
- 第6-7题："同情一下"(红) vs "狠狠批评"(绿)，正确答案为狠狠批评
- 第8题："毫不留情"(红) vs "放放放放"(绿) vs "再想想"(黄)，正确答案为毫不留情
- 第9题："必须狠批"(红) vs "算了"(绿)，正确答案为必须狠批
- 第10题："狠狠批！"(红) vs "原谅他"(绿) vs "不知道"(黄)，正确答案为狠狠批！
- 每题选错直接退出，全部答对后跳转到视频播放页面

### 用户信息查看功能

- 新增 `GET /user/profile/{uid}` JSON 接口，返回用户公开信息（头像、用户名、电话、职业、工作地址）
- 首页帖子列表、帖子详情页（发帖人及回复人）、收藏列表中，所有用户名和头像变为可点击
- 点击后弹出 Bootstrap 模态框，展示该用户的头像、用户名、联系电话、职业、工作地址
- 弹窗底部设有关闭按钮
- 涉及文件：`UserController.java`、`index.html`、`post.html`、`favorite_list.html`

### 视频播放页面（/video/{pid}）

- 新增视频播放页面（`video.html`），美术风格与帖子详情页一致
- 使用HTML5 `<video>` 标签播放OSS视频，移除原生controls，全部由外部界面控制
- 外部控制栏：播放/暂停按钮、拖拽进度条+时间显示、音量控制（喇叭图标点击弹出竖向滑轨）、全屏按钮
- 全屏模式：进入全屏时给`<video>`动态添加controls显示原生控件，退出全屏时移除
- 播放完毕后回到暂停状态（不跳转）
- 路由：`GET /video/{pid}`（`PostController.videoPage()`）

### 视频播放积分支付功能

- 新增视频付费播放机制，使用全局变量 `videoHasBeenPlayed` 控制
- 首次点击播放弹出支付模态框：「你需要支付播放此视频的费用（50积分）」
  - 支付50积分 → 弹出二次确认「没骗你，认真的」
    - 继续支付 → `POST /video/pay` 检查登录状态和积分余额
      - 未登录 → alert提示并弹出登录框
      - 积分不足 → alert「积分余额不够」
      - 成功 → 设置 `videoHasBeenPlayed = true`，开始播放
    - 取消 → 关闭弹窗
  - 取消 → 关闭弹窗
- 已支付后正常切换播放/暂停
- 后端：`PostController.payForVideo()` — `POST /video/pay`

### Emoji表情选择器

- 纯前端Unicode Emoji选择器，在发帖、回帖、编辑帖子的textarea上方插入
- 点击「😊 表情」按钮展开表情面板，选择表情插入光标位置
- 表情作为普通Unicode文本存储，数据库utf8mb4原生支持，后端零改动
- 涉及文件：
  - `static/css/tx.css` — 新增emoji选择器样式
  - `static/js/private.js` — 新增EMOJI_LIST、initEmojiPicker()、insertEmoji()
  - `templates/my_posts.html` — 编辑帖子textarea初始化

### 资源路径格式统一修复 — 所有静态资源改绝对路径

**⚠️ 路径格式规范：所有 `th:src`/`th:href` 和 `src`/`href` 属性中，资源路径必须使用以 `/` 开头的绝对路径（如 `@{/js/...}`、`src="/js/..."`），禁止使用相对路径（如 `@{js/...}`、`@{../js/...}`、`src="js/..."`），否则部分页面二级路由下会导致资源加载 404。**

- 修复了 9 个模板文件中 CSS、JS、背景动画脚本的资源路径，统一从相对路径改为绝对路径
- 涉及文件：
  - `src/main/resources/templates/error.html` — jquery、bootstrap 路径修正
  - `src/main/resources/templates/favorite_list.html` — jquery、bootstrap、private.js、triangle-bg.js、tx.css 路径修正
  - `src/main/resources/templates/host.html` — jquery、bootstrap、private.js 路径修正
  - `src/main/resources/templates/index.html` — jquery、bootstrap、private.js、triangle-bg.js、tx.css 路径修正
  - `src/main/resources/templates/my_posts.html` — jquery、bootstrap、private.js、triangle-bg.js、tx.css 路径修正
  - `src/main/resources/templates/person.html` — jquery、bootstrap、private.js 路径修正
  - `src/main/resources/templates/post.html` — jquery、bootstrap、private.js、triangle-bg.js、tx.css 路径修正
  - `src/main/resources/templates/quick_start.html` — jquery、bootstrap、tx.css、triangle-bg.js 路径修正
  - `src/main/resources/templates/video.html` — jquery、bootstrap、tx.css、triangle-bg.js 路径修正

### 关注功能

- 新增用户关注系统，支持用户之间相互关注
- 新增数据库 `follow` 表，记录关注关系（关注者、被关注者、关注时间），唯一约束防止重复关注
- 新增完整 MVC 分层：`Follow.java`（模型）、`FollowMapper.java/.xml`（数据访问）、`FollowService.java/.xml`（服务）、`FollowController.java`（控制器）
- 导航栏「个人管理」右侧新增「我的关注」按钮，点击进入关注管理页面
- 我的关注页面（`/follow/list`）分页展示已关注用户列表，每个用户显示头像、用户名、职业/地址信息
- 每个关注用户提供「查看TA的帖子」和「取消关注」按钮
- 查看TA的帖子跳转到 `user_posts.html`，以表格形式展示该用户所有帖子（标题、板块、时间、回复数、收藏数），点击「查看」进入帖子详情
- 所有用户信息弹窗（`showUserProfile`）底部新增关注/取消关注按钮，打开弹窗时自动检查关注状态
- 用户不能关注自己，未登录时隐藏关注按钮
- 涉及文件：
  - 新增：`Follow.java`、`FollowMapper.java`、`FollowMapper.xml`、`FollowService.java`、`FollowServiceImpl.java`、`FollowController.java`、`follow_list.html`、`user_posts.html`
  - 修改：`head.html`（导航栏）、`PostController.java`（`/user/{uid}/posts` 端点）、`index.html`、`post.html`、`favorite_list.html`（弹窗关注按钮）、`bbs.sql`（follow建表）

### 粉丝功能

- 导航栏「我的关注」改名为「关注/粉丝」
- `/follow/list` 新增 `type` 参数（following/fans），支持在关注和粉丝列表间切换
- 页面顶部新增 nav-tabs 切换标签「我的关注」/「我的粉丝」
- 粉丝列表（type=fans）分页展示关注当前用户的用户，布局与关注列表一致
- 粉丝卡片显示「关注TA」按钮（未互关时）或「已关注」按钮（已互关时），互关状态由服务端 `followStatusMap` 预查
- 新增后端粉丝查询：`FollowMapper.xml` 中 `findFansUsersByPage`（SQL JOIN user 表 WHERE follow_uid=当前用户）+ `countFansUsers`
- 分页链接保留 type 参数，切换视图不丢失分页状态
- 涉及文件：
  - 修改：`FollowMapper.xml`（新增粉丝分页 SQL）、`FollowMapper.java`（新增粉丝查询方法）、`FollowService.java`/`Impl`（新增粉丝服务方法）、`FollowController.java`（新增 type 参数处理）、`follow_list.html`（切换标签+粉丝卡片+新 JS）、`head.html`（菜单改名）

### 实时通知系统

- 新增基于 WebSocket 的实时消息通知系统
- 使用原生 Spring WebSocket（TextWebSocketHandler），无 STOMP 依赖
- 后端通过 ConcurrentHashMap 维护 uid→WebSocketSession 映射
- 前端使用原生 WebSocket API，自动重连（5秒间隔）
- 三种通知类型：
  - 回复通知：用户回复帖子时通知楼主
  - 关注通知：用户关注他人时通知被关注者
  - 收藏通知：用户收藏帖子时通知楼主
- 导航栏新增铃铛图标 + 红色未读角标
- 点击铃铛弹出下拉面板显示最近通知
- 新增通知收件箱页面（/notification/page），支持分页查看、标为已读、全部已读、删除
- 涉及文件：
  - 新增：Notification.java、NotificationMapper.java/.xml、NotificationService.java/.xml、NotificationController.java
  - 新增：WebSocketConfig.java、NotificationWebSocketHandler.java
  - 新增：notification_list.html
  - 修改：head.html（通知铃铛）、private.js（WebSocket客户端+通知JS）、tx.css（通知样式）
  - 修改：PostController.java（回复通知触发）、FollowController.java（关注通知触发）、FavoriteController.java（收藏通知触发）
  - 修改：pom.xml（添加 spring-boot-starter-websocket 依赖）

### 全局通知功能（管理员）

- 新增管理员全局通知功能，管理员可在后台向所有用户发送系统级通知
- 管理员在 `/host.do` 页面底部新增"全局通知"面板，输入内容后点击"发送全局通知"
- 后端 `POST /admin/sendGlobalNotification` 接口遍历所有用户，为每个用户插入一条 `type='system'` 的通知记录
- 已登录用户通过 WebSocket 实时推送，未登录用户登录后可在通知中心查看
- 通知中心新增"系统"分类标签，系统通知带有灰色"系统"徽标
- 系统通知不显示删除按钮，点击系统通知弹出管理员用户信息浮窗
- 后端 `NotificationService.notifyAllUsers()` + `UserMapper.findAllUids()` 全量插入
- 涉及文件：
  - 新增：HostController.java（sendGlobalNotification 端点）、UserMapper.xml（findAllUids SQL）
  - 修改：NotificationServiceImpl.java（notifyAllUsers）、NotificationWebSocketHandler.java（broadcastToAll）
  - 修改：host.html（全局通知面板+JS）、notification_list.html（系统标签+隐藏删除+点击弹窗）

## 后续开发建议

1. 添加前端页面美化
2. 实现更完善的权限控制系统
3. 实现搜索引擎优化
4. 添加数据缓存机制
5. 完善日志记录系统
6. 回复置顶功能增强：添加置顶时间、过期时间、置顶理由等功能
