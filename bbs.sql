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
-- 初始化数据 - user表
-- ----------------------------
INSERT INTO `user`(`uid`, `uname`, `upwd`, `ustate`, `ucreatetime`, `level`, `phone`, `career`, `address`, `score`) VALUES
(1, 'admin', 'your_password', 1, '2026-06-06 10:57:45', 0, '13800138000', '系统管理员', '北京市海淀区', 100000),
(2, 'u1', 'your_password', 1, '2026-06-06 10:57:45', 1, '13800138001', '软件工程师', '北京市朝阳区', 100),
(3, 'u2', 'your_password', 1, '2026-06-06 09:30:00', 1, '13800138002', '产品经理', '上海市浦东新区', 100),
(4, 'u3', 'your_password', 1, '2026-06-06 14:20:30', 1, '13800138003', '设计师', '广州市天河区', 100);

-- ----------------------------
-- 初始化数据 - post表
-- ----------------------------
INSERT INTO `post`(`pid`, `ptitle`, `pbody`, `psendtime`, `lastreplytime`, `uid`, `category`, `is_sticky`, `prize`) VALUES
(1, 'Spring Boot入门教程', 'Spring Boot是一个快速开发框架，可以大大简化Spring应用的搭建过程。本文将介绍Spring Boot的基本概念和快速入门方法。', '2026-06-06 10:57:45', '2026-06-06 16:30:00', 1, '技术', 1, 0),
(2, 'MySQL性能优化技巧', '分享一些MySQL数据库性能优化的实用技巧，包括索引优化、查询优化、配置调优等方面。', '2026-06-06 11:20:15', '2026-06-06 09:45:30', 2, '技术', 1, 0),
(3, '前端框架对比：React vs Vue', '详细对比React和Vue这两个热门前端框架的优缺点，帮助开发者选择合适的工具。', '2026-06-06 15:30:45', '2026-06-06 10:20:15', 3, '技术', 0, 0),
(4, '最近看的好电影推荐', '最近看了几部不错的电影，想和大家分享一下。《流浪地球2》真的很震撼，特效和剧情都很棒！', '2026-06-06 20:15:30', '2026-06-06 22:10:45', 2, '娱乐', 0, 0),
(5, '推荐几首好听的歌曲', '分享一些最近单曲循环的歌曲，有民谣、摇滚、流行等各种风格，希望大家喜欢。', '2026-06-06 18:45:20', '2026-06-06 19:30:10', 4, '娱乐', 0, 0),
(6, '家常红烧肉的做法', '分享一道经典的家常菜红烧肉的制作方法，简单易学，味道鲜美，适合初学者尝试。', '2026-06-06 12:30:15', '2026-06-06 14:20:30', 3, '美食', 1, 0),
(7, '推荐几家好吃的火锅店', '在北京工作生活了几年，踩过不少坑，也发现了不少宝藏店铺。今天推荐几家个人觉得不错的火锅店。', '2026-06-06 13:45:40', '2026-06-06 21:15:25', 2, '美食', 0, 0),
(8, '云南大理三日游攻略', '刚从大理回来，景色真的很美！给大家分享一份详细的三日游攻略，包括路线规划、必去景点、美食推荐等。', '2026-06-06 09:20:30', '2026-06-06 17:45:15', 4, '旅游', 0, 0),
(9, '西藏旅行注意事项', '计划去西藏旅行的朋友们注意了，这里有一些重要的注意事项和准备工作，确保旅途安全顺利。', '2026-06-06 16:30:45', '2026-06-06 20:10:20', 1, '旅游', 0, 0),
(10, '新手求助：Java环境配置问题', '刚开始学习Java，在配置开发环境时遇到了一些问题，JDK安装后环境变量设置总是出错，求大神指导。', '2026-06-06 14:15:30', '2026-06-06 18:30:45', 3, '问题', 0, 8),
(11, '关于数据库设计的疑问', '在设计用户表结构时，对于密码字段应该选择什么类型比较合适？需要考虑安全性问题。', '2026-06-06 11:40:20', '2026-06-06 16:55:10', 2, '问题', 0, 5);

-- 为一些现有帖子添加测试图片路径
UPDATE post SET path = 'https://bbspicturebed.oss-cn-huhehaote.aliyuncs.com/post_covers/test-image-1.png' WHERE pid = 1;
UPDATE post SET path = 'https://bbspicturebed.oss-cn-huhehaote.aliyuncs.com/post_covers/test-image-3.png' WHERE pid = 2;
UPDATE post SET path = 'https://bbspicturebed.oss-cn-huhehaote.aliyuncs.com/post_covers/test-image-2.png' WHERE pid = 6;


-- ----------------------------
-- 初始化数据 - reply表
-- ----------------------------
INSERT INTO `reply`(`rid`, `pid`, `uid`, `replymessage`, `replytime`, `is_sticky`) VALUES
(1, 1, 2, '很不错的教程，对新手很友好，学到了很多！', '2026-06-06 14:30:20', 1),
(2, 1, 3, 'Spring Boot确实很方便，我们公司项目都在用这个框架。', '2026-06-06 16:45:15', 0),
(3, 2, 1, '索引优化确实很重要，我们数据库加了合适的索引后查询速度提升了很多。', '2026-06-06 10:20:30', 0),
(4, 3, 4, 'React和Vue各有优势，个人更喜欢Vue的语法，比较简单直观。', '2026-06-06 18:30:45', 1),
(5, 4, 1, '《流浪地球2》确实很棒，已经二刷了，每次看都有新的感受。', '2026-06-06 23:15:20', 0),
(6, 5, 3, '民谣推荐陈粒的歌，很有味道。', '2026-06-06 20:45:10', 0),
(7, 6, 2, '红烧肉的做法很详细，明天就试试，谢谢分享！', '2026-06-06 15:30:25', 0),
(8, 7, 4, '推荐的那家海底捞确实不错，服务很好，就是人有点多需要排队。', '2026-06-06 22:20:40', 0),
(9, 8, 1, '大理真的很美，洱海的水特别清澈，推荐住在古城里。', '2026-06-06 18:50:15', 1),
(10, 9, 2, '去西藏一定要做好高原反应的准备，建议提前一周开始吃红景天。', '2026-06-06 21:25:30', 0),
(11, 10, 1, '环境变量配置可以参考官方文档，PATH和JAVA_HOME都要设置正确。', '2026-06-06 15:45:20', 0),
(12, 10, 4, '建议使用IDEA，它会自动帮你配置好大部分环境，对新手很友好。', '2026-06-06 16:20:35', 1),
(13, 11, 2, '密码建议用varchar类型，存储时一定要加密，推荐使用BCrypt。', '2026-06-06 17:10:45', 1),
(14, 11, 3, '安全性很重要，除了加密还要考虑防止SQL注入等安全问题。', '2026-06-06 18:15:50', 0);

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
(8, 4, 7, '2026-06-06 23:10:40'),
(9, 2, 8, '2026-06-06 19:25:50'),
(10, 3, 9, '2026-06-06 22:15:35');