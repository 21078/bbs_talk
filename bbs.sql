DROP DATABASE IF EXISTS bbs;
CREATE DATABASE bbs CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE bbs;
SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for user (先创建user表)
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `uid` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `uname` varchar(32) NOT NULL COMMENT '用户名，唯一',
  `upwd` varchar(32) NOT NULL COMMENT '密码，明文存储',
  `ustate` int(11) NOT NULL COMMENT '用户状态：1正常，0禁用',
  `ucreatetime` datetime NOT NULL COMMENT '创建时间',
  `level` int(11) NOT NULL COMMENT '用户级别：0管理员，1普通用户',
  `phone` varchar(20) DEFAULT NULL COMMENT '联系电话，可选',
  `career` TEXT DEFAULT NULL COMMENT '职业，可选',
  `address` varchar(500) DEFAULT NULL COMMENT '工作地址，可选',
  `score` int(11) DEFAULT 0 COMMENT '用户积分',
  `path` varchar(500) DEFAULT NULL COMMENT '用户头像路径',
  `verified` int(11) DEFAULT 0 COMMENT '认证状态：0未认证，1已认证',
  PRIMARY KEY (`uid`),
  UNIQUE KEY `unique` (`uname`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ----------------------------
-- Table structure for post
-- ----------------------------
DROP TABLE IF EXISTS `post`;
CREATE TABLE `post` (
  `pid` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '帖子ID',
  `ptitle` varchar(100) NOT NULL COMMENT '帖子标题',
  `pbody` text NOT NULL COMMENT '帖子内容',
  `psendtime` datetime NOT NULL COMMENT '发帖时间',
  `lastreplytime` datetime NOT NULL COMMENT '最后回复时间',
  `uid` int(11) NOT NULL COMMENT '发帖用户ID',
  `category` varchar(50) NOT NULL COMMENT '帖子板块',
  `is_sticky` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否置顶：0否，1是',
  `prize` int(11) DEFAULT 0 COMMENT '奖励积分，问答板块使用',
  `path` varchar(500) DEFAULT NULL COMMENT '封面图片路径',
  PRIMARY KEY (`pid`),
  KEY `uid` (`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='帖子表';

-- ----------------------------
-- Table structure for reply
-- ----------------------------
DROP TABLE IF EXISTS `reply`;
CREATE TABLE `reply` (
  `rid` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '回复ID',
  `pid` bigint(20) NOT NULL COMMENT '帖子ID',
  `uid` int(11) NOT NULL COMMENT '回复用户ID',
  `replymessage` text NOT NULL COMMENT '回复内容',
  `replytime` datetime NOT NULL COMMENT '回复时间',
  `is_sticky` tinyint(1) DEFAULT 0 COMMENT '是否置顶，0否1是',
  PRIMARY KEY (`rid`),
  KEY `uid` (`uid`),
  KEY `pid` (`pid`)
) ENGINE=InnoDB AUTO_INCREMENT=41 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='回复表';

-- ----------------------------
-- Table structure for favorite
-- ----------------------------
DROP TABLE IF EXISTS `favorite`;
CREATE TABLE `favorite` (
  `fid` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
  `uid` int(11) NOT NULL COMMENT '用户ID',
  `pid` bigint(20) NOT NULL COMMENT '帖子ID',
  `favtime` datetime NOT NULL COMMENT '收藏时间',
  PRIMARY KEY (`fid`),
  UNIQUE KEY `unique_fav` (`uid`, `pid`) COMMENT '用户帖子收藏唯一约束',
  KEY `pid` (`pid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收藏表';

-- ----------------------------
-- Table structure for follow
-- ----------------------------
DROP TABLE IF EXISTS `follow`;
CREATE TABLE `follow` (
  `fid` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '关注ID',
  `uid` int(11) NOT NULL COMMENT '关注者用户ID',
  `follow_uid` int(11) NOT NULL COMMENT '被关注用户ID',
  `followtime` datetime NOT NULL COMMENT '关注时间',
  PRIMARY KEY (`fid`),
  UNIQUE KEY `unique_follow` (`uid`, `follow_uid`) COMMENT '用户关注唯一约束',
  KEY `follow_uid` (`follow_uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='关注表';

-- ----------------------------
-- Table structure for notification
-- ----------------------------
DROP TABLE IF EXISTS `notification`;
CREATE TABLE `notification` (
  `nid` int(11) NOT NULL AUTO_INCREMENT COMMENT '通知ID',
  `uid` int(11) NOT NULL COMMENT '接收通知的用户ID',
  `type` varchar(20) NOT NULL COMMENT '通知类型：reply/follow/favorite/system',
  `content` varchar(255) NOT NULL COMMENT '通知内容',
  `from_uid` int(11) DEFAULT NULL COMMENT '触发者用户ID',
  `from_uname` varchar(32) DEFAULT NULL COMMENT '触发者用户名',
  `pid` int(11) DEFAULT NULL COMMENT '关联帖子ID',
  `ptitle` varchar(100) DEFAULT NULL COMMENT '关联帖子标题',
  `is_read` int(11) DEFAULT 0 COMMENT '是否已读：0未读，1已读',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`nid`),
  KEY `idx_uid_read` (`uid`, `is_read`) COMMENT '用户未读索引',
  KEY `idx_uid_time` (`uid`, `create_time`) COMMENT '用户时间排序索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知表';

-- ----------------------------
-- 初始化数据 - user表
-- ----------------------------
INSERT INTO `user`(`uid`, `uname`, `upwd`, `ustate`, `ucreatetime`, `level`, `phone`, `career`, `address`, `score`, `path`) VALUES
(1, 'admin', '111222333', 1, '2026-06-06 10:57:45', 0, '13800138000', '系统管理员', '北京市海淀区', 100000, 'https://bbspicturebed.oss-cn-huhehaote.aliyuncs.com/post_covers/admin_face.png'),
(2, 'u1', '123456', 1, '2026-06-06 10:57:45', 1, '13800138001', '软件工程师', '北京市朝阳区', 100, 'https://bbspicturebed.oss-cn-huhehaote.aliyuncs.com/post_covers/default_face.jpg'),
(3, 'u2', '123456', 1, '2026-06-06 09:30:00', 1, '13800138002', '产品经理', '上海市浦东新区', 100, 'https://bbspicturebed.oss-cn-huhehaote.aliyuncs.com/post_covers/default_face.jpg'),
(4, 'u3', '123456', 1, '2026-06-06 14:20:30', 1, '13800138003', '设计师', '广州市天河区', 100, 'https://bbspicturebed.oss-cn-huhehaote.aliyuncs.com/post_covers/default_face.jpg');

-- ----------------------------
-- 初始化数据 - post表
-- ----------------------------
INSERT INTO `post`(`pid`, `ptitle`, `pbody`, `psendtime`, `lastreplytime`, `uid`, `category`, `is_sticky`, `prize`) VALUES
(1, 'Spring Boot入门教程', 'Spring Boot是一个快速开发框架，可以大大简化Spring应用的搭建过程。本文将介绍Spring Boot的基本概念和快速入门方法。', '2026-06-06 10:57:45', '2026-06-06 16:30:00', 1, '技术', 1, 0),
(2, 'MySQL性能优化技巧', '分享一些MySQL数据库性能优化的实用技巧，包括索引优化、查询优化、配置调优等方面。', '2026-06-06 11:20:15', '2026-06-06 09:45:30', 2, '技术', 1, 0),
(3, '这个项目是谁做的？', '09-老师说的都队-唐浩、赵文熙、陈闽、彭浩、樊世奇。但是一个人做=AI做，一堆人做=AI做，所以', '2026-06-06 15:30:45', '2026-06-06 10:20:15', 3, '问题', 0, 5),
(4, '最近看的好电影推荐', '最近看了几部不错的电影，想和大家分享一下。《流浪地球2》真的很震撼，特效和剧情都很棒！', '2026-06-06 20:15:30', '2026-06-06 22:10:45', 2, '娱乐', 0, 0),
(6, '家常红烧肉的做法', '分享一道经典的家常菜红烧肉的制作方法，简单易学，味道鲜美，适合初学者尝试。', '2026-06-06 12:30:15', '2026-06-06 14:20:30', 3, '美食', 1, 0),
(8, '云南大理三日游攻略', '刚从大理回来，景色真的很美！给大家分享一份详细的三日游攻略，包括路线规划、必去景点、美食推荐等。', '2026-06-06 09:20:30', '2026-06-06 17:45:15', 4, '旅游', 0, 0),
(11, '计算机是新时代天坑吗', '蚌埠住了，头一次见一个专业想找实习居然要自学这么多东西，根本学不完。', '2026-06-06 11:40:20', '2026-06-06 16:55:10', 2, '问题', 0, 5),
(12, '能玩一辈子音游吗', '感觉大学玩了这么久废了，都怪染上了这种透支身体的游戏，往后怎么办啊。', '2026-06-04 11:40:20', '2026-06-04 16:55:10', 1, '娱乐', 0, 0);
-- 为一些现有帖子添加测试图片路径
UPDATE post SET path = 'https://bbspicturebed.oss-cn-huhehaote.aliyuncs.com/post_covers/test-image-1.png' WHERE pid = 1;
UPDATE post SET path = 'https://bbspicturebed.oss-cn-huhehaote.aliyuncs.com/post_covers/test-image-3.png' WHERE pid = 2;
UPDATE post SET path = 'https://bbspicturebed.oss-cn-huhehaote.aliyuncs.com/post_covers/test-image-5.png' WHERE pid = 3;
UPDATE post SET path = 'https://bbspicturebed.oss-cn-huhehaote.aliyuncs.com/post_covers/test-image-2.png' WHERE pid = 6;
UPDATE post SET path = 'https://bbspicturebed.oss-cn-huhehaote.aliyuncs.com/post_covers/test-image-4.jpg' WHERE pid = 12;

-- ----------------------------
-- 初始化数据 - reply表
-- ----------------------------
INSERT INTO `reply`(`rid`, `pid`, `uid`, `replymessage`, `replytime`, `is_sticky`) VALUES
(1, 1, 2, '很不错的教程，对新手很友好，学到了很多！', '2026-06-06 14:30:20', 1),
(2, 1, 3, 'Spring Boot确实很方便，我们公司项目都在用这个框架。', '2026-06-06 16:45:15', 0),
(3, 2, 1, '索引优化确实很重要，我们数据库加了合适的索引后查询速度提升了很多。', '2026-06-06 10:20:30', 0),
(4, 3, 1, '很难不认同', '2026-06-06 18:30:45', 1),
(5, 4, 1, '《流浪地球2》确实很棒，已经二刷了，每次看都有新的感受。', '2026-06-06 23:15:20', 0),
(7, 6, 2, '红烧肉的做法很详细，明天就试试，谢谢分享！', '2026-06-06 15:30:25', 0),
(9, 8, 1, '大理真的很美，洱海的水特别清澈，推荐住在古城里。', '2026-06-06 18:50:15', 1),
(13, 11, 3, '那还说啥了，考公考编考研三件套走起', '2026-06-06 17:10:45', 1),
(14, 11, 2, '寝室大牛去大厂实习了，和他相比感觉自己就是个只会开关机电脑的猴子，之前应该听劝早点学的，难受💀💀💀', '2026-06-06 18:15:50', 0),
(15, 12, 2, '埋了吧，没救了，你个三无大学生就算了，还沾上这东西，废了废了。', '2026-06-06 18:15:50', 1);
-- ----------------------------
-- 初始化数据 - favorite表
-- ----------------------------
INSERT INTO `favorite`(`fid`, `uid`, `pid`, `favtime`) VALUES
(1, 2, 1, '2026-06-06 14:32:10'),
(2, 3, 1, '2026-06-06 17:20:25'),
(3, 1, 2, '2026-06-06 11:15:30'),
(4, 4, 2, '2026-06-06 12:30:45'),
(5, 2, 3, '2026-06-06 19:45:20'),
(6, 3, 4, '2026-06-06 08:30:15'),
(7, 1, 6, '2026-06-06 16:20:30'),
(9, 2, 8, '2026-06-06 19:25:50');

-- ----------------------------
-- 初始化数据 - follow表
-- ----------------------------
INSERT INTO `follow`(`uid`, `follow_uid`, `followtime`) VALUES
(2, 1, '2026-06-06 11:00:00'),
(3, 1, '2026-06-06 11:30:00'),
(1, 2, '2026-06-06 14:00:00'),
(3, 2, '2026-06-06 15:00:00'),
(4, 1, '2026-06-06 16:00:00');

-- ----------------------------
-- 初始化数据 - notification表
-- ----------------------------
INSERT INTO `notification`(`nid`, `uid`, `type`, `content`, `from_uid`, `from_uname`, `pid`, `ptitle`, `is_read`, `create_time`) VALUES
-- 回复通知
(1, 1, 'reply', 'u1 回复了你的帖子《Spring Boot入门教程》', 2, 'u1', 1, 'Spring Boot入门教程', 1, '2026-06-06 14:30:20'),
(2, 1, 'reply', 'u2 回复了你的帖子《Spring Boot入门教程》', 3, 'u2', 1, 'Spring Boot入门教程', 1, '2026-06-06 16:45:15'),
(3, 2, 'reply', 'admin 回复了你的帖子《MySQL性能优化技巧》', 1, 'admin', 2, 'MySQL性能优化技巧', 1, '2026-06-06 10:20:30'),
(4, 3, 'reply', 'admin 回复了你的帖子《这个项目是谁做的？》', 1, 'admin', 3, '这个项目是谁做的？', 1, '2026-06-06 18:30:45'),
(5, 2, 'reply', 'admin 回复了你的帖子《最近看的好电影推荐》', 1, 'admin', 4, '最近看的好电影推荐', 1, '2026-06-06 23:15:20'),
(6, 3, 'reply', 'u1 回复了你的帖子《家常红烧肉的做法》', 2, 'u1', 6, '家常红烧肉的做法', 1, '2026-06-06 15:30:25'),
(7, 4, 'reply', 'admin 回复了你的帖子《云南大理三日游攻略》', 1, 'admin', 8, '云南大理三日游攻略', 1, '2026-06-06 18:50:15'),
(8, 2, 'reply', 'u2 回复了你的帖子《计算机是新时代天坑吗》', 3, 'u2', 11, '计算机是新时代天坑吗', 1, '2026-06-06 17:10:45'),
(9, 1, 'reply', 'u1 回复了你的帖子《能玩一辈子音游吗》', 2, 'u1', 12, '能玩一辈子音游吗', 1, '2026-06-06 18:15:50'),
-- 收藏通知
(10, 1, 'favorite', 'u1 收藏了你的帖子《Spring Boot入门教程》', 2, 'u1', 1, 'Spring Boot入门教程', 1, '2026-06-06 14:32:10'),
(11, 1, 'favorite', 'u2 收藏了你的帖子《Spring Boot入门教程》', 3, 'u2', 1, 'Spring Boot入门教程', 1, '2026-06-06 17:20:25'),
(12, 2, 'favorite', 'admin 收藏了你的帖子《MySQL性能优化技巧》', 1, 'admin', 2, 'MySQL性能优化技巧', 1, '2026-06-06 11:15:30'),
(13, 2, 'favorite', 'u3 收藏了你的帖子《MySQL性能优化技巧》', 4, 'u3', 2, 'MySQL性能优化技巧', 1, '2026-06-06 12:30:45'),
(14, 3, 'favorite', 'u1 收藏了你的帖子《这个项目是谁做的？》', 2, 'u1', 3, '这个项目是谁做的？', 1, '2026-06-06 19:45:20'),
(15, 2, 'favorite', 'u2 收藏了你的帖子《最近看的好电影推荐》', 3, 'u2', 4, '最近看的好电影推荐', 1, '2026-06-06 08:30:15'),
(16, 3, 'favorite', 'admin 收藏了你的帖子《家常红烧肉的做法》', 1, 'admin', 6, '家常红烧肉的做法', 1, '2026-06-06 16:20:30'),
(17, 4, 'favorite', 'u1 收藏了你的帖子《云南大理三日游攻略》', 2, 'u1', 8, '云南大理三日游攻略', 1, '2026-06-06 19:25:50'),
-- 关注通知
(18, 1, 'follow', 'u1 关注了你', 2, 'u1', NULL, NULL, 1, '2026-06-06 11:00:00'),
(19, 1, 'follow', 'u2 关注了你', 3, 'u2', NULL, NULL, 1, '2026-06-06 11:30:00'),
(20, 2, 'follow', 'admin 关注了你', 1, 'admin', NULL, NULL, 1, '2026-06-06 14:00:00'),
(21, 2, 'follow', 'u2 关注了你', 3, 'u2', NULL, NULL, 1, '2026-06-06 15:00:00'),
(22, 1, 'follow', 'u3 关注了你', 4, 'u3', NULL, NULL, 1, '2026-06-06 16:00:00');