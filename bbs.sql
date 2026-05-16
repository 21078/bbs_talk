DROP DATABASE IF EXISTS bbs;
CREATE DATABASE bbs CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE bbs;
SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for user (先创建user表)
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `uid` int(11) NOT NULL AUTO_INCREMENT,
  `uname` varchar(32) NOT NULL,
  `upwd` varchar(32) NOT NULL,
  `ustate` int(11) NOT NULL,
  `ucreatetime` datetime NOT NULL,
  `level` int(11) NOT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `career` TEXT DEFAULT NULL,
  `address` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`uid`),
  UNIQUE KEY `unique` (`uname`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for post
-- ----------------------------
DROP TABLE IF EXISTS `post`;
CREATE TABLE `post` (
  `pid` bigint(20) NOT NULL AUTO_INCREMENT,
  `ptitle` varchar(100) NOT NULL,
  `pbody` text NOT NULL,
  `psendtime` datetime NOT NULL,
  `lastreplytime` datetime NOT NULL,
  `uid` int(11) NOT NULL,
  `category` varchar(50) NOT NULL,
  PRIMARY KEY (`pid`),
  KEY `uid` (`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for reply
-- ----------------------------
DROP TABLE IF EXISTS `reply`;
CREATE TABLE `reply` (
  `pid` bigint(20) NOT NULL,
  `uid` int(11) NOT NULL,
  `replymessage` text NOT NULL,
  `replytime` datetime NOT NULL,
  `rid` bigint(20) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`rid`),
  KEY `uid` (`uid`),
  KEY `pid` (`pid`)
) ENGINE=InnoDB AUTO_INCREMENT=41 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for favorite
-- ----------------------------
DROP TABLE IF EXISTS `favorite`;
CREATE TABLE `favorite` (
  `fid` bigint(20) NOT NULL AUTO_INCREMENT,
  `uid` int(11) NOT NULL,
  `pid` bigint(20) NOT NULL,
  `favtime` datetime NOT NULL,
  PRIMARY KEY (`fid`),
  UNIQUE KEY `unique_fav` (`uid`, `pid`),
  KEY `pid` (`pid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- 初始化数据 - user表
-- ----------------------------
INSERT INTO `user`(`uid`, `uname`, `upwd`, `ustate`, `ucreatetime`, `level`, `phone`, `career`, `address`) VALUES 
(1, 'admin', '123456', 1, '2020-08-12 10:57:45', 0, '13800138000', '系统管理员', '北京市海淀区'),
(2, 'u1', '123456', 1, '2020-08-12 10:57:45', 1, '13800138001', '软件工程师', '北京市朝阳区'),
(3, 'u2', '123456', 1, '2020-08-13 09:30:00', 1, '13800138002', '产品经理', '上海市浦东新区'),
(4, 'u3', '123456', 1, '2020-08-14 14:20:30', 1, '13800138003', '设计师', '广州市天河区');

-- ----------------------------
-- 初始化数据 - post表
-- ----------------------------
INSERT INTO `post`(`pid`, `ptitle`, `pbody`, `psendtime`, `lastreplytime`, `uid`, `category`) VALUES
(1, 'Spring Boot入门教程', 'Spring Boot是一个快速开发框架，可以大大简化Spring应用的搭建过程。本文将介绍Spring Boot的基本概念和快速入门方法。', '2020-08-12 10:57:45', '2020-08-15 16:30:00', 1, '技术'),
(2, 'MySQL性能优化技巧', '分享一些MySQL数据库性能优化的实用技巧，包括索引优化、查询优化、配置调优等方面。', '2020-08-13 11:20:15', '2020-08-14 09:45:30', 2, '技术'),
(3, '前端框架对比：React vs Vue', '详细对比React和Vue这两个热门前端框架的优缺点，帮助开发者选择合适的工具。', '2020-08-14 15:30:45', '2020-08-15 10:20:15', 3, '技术'),
(4, '最近看的好电影推荐', '最近看了几部不错的电影，想和大家分享一下。《流浪地球2》真的很震撼，特效和剧情都很棒！', '2020-08-13 20:15:30', '2020-08-14 22:10:45', 2, '娱乐'),
(5, '推荐几首好听的歌曲', '分享一些最近单曲循环的歌曲，有民谣、摇滚、流行等各种风格，希望大家喜欢。', '2020-08-14 18:45:20', '2020-08-15 19:30:10', 4, '娱乐'),
(6, '家常红烧肉的做法', '分享一道经典的家常菜红烧肉的制作方法，简单易学，味道鲜美，适合初学者尝试。', '2020-08-14 12:30:15', '2020-08-15 14:20:30', 3, '美食'),
(7, '推荐几家好吃的火锅店', '在北京工作生活了几年，踩过不少坑，也发现了不少宝藏店铺。今天推荐几家个人觉得不错的火锅店。', '2020-08-15 13:45:40', '2020-08-15 21:15:25', 2, '美食'),
(8, '云南大理三日游攻略', '刚从大理回来，景色真的很美！给大家分享一份详细的三日游攻略，包括路线规划、必去景点、美食推荐等。', '2020-08-15 09:20:30', '2020-08-15 17:45:15', 4, '旅游'),
(9, '西藏旅行注意事项', '计划去西藏旅行的朋友们注意了，这里有一些重要的注意事项和准备工作，确保旅途安全顺利。', '2020-08-15 16:30:45', '2020-08-15 20:10:20', 1, '旅游'),
(10, '新手求助：Java环境配置问题', '刚开始学习Java，在配置开发环境时遇到了一些问题，JDK安装后环境变量设置总是出错，求大神指导。', '2020-08-15 14:15:30', '2020-08-15 18:30:45', 3, '问题'),
(11, '关于数据库设计的疑问', '在设计用户表结构时，对于密码字段应该选择什么类型比较合适？需要考虑安全性问题。', '2020-08-15 11:40:20', '2020-08-15 16:55:10', 2, '问题');

-- ----------------------------
-- 初始化数据 - reply表
-- ----------------------------
INSERT INTO `reply`(`rid`, `pid`, `uid`, `replymessage`, `replytime`) VALUES
(1, 1, 2, '很不错的教程，对新手很友好，学到了很多！', '2020-08-13 14:30:20'),
(2, 1, 3, 'Spring Boot确实很方便，我们公司项目都在用这个框架。', '2020-08-13 16:45:15'),
(3, 2, 1, '索引优化确实很重要，我们数据库加了合适的索引后查询速度提升了很多。', '2020-08-14 10:20:30'),
(4, 3, 4, 'React和Vue各有优势，个人更喜欢Vue的语法，比较简单直观。', '2020-08-14 18:30:45'),
(5, 4, 1, '《流浪地球2》确实很棒，已经二刷了，每次看都有新的感受。', '2020-08-14 23:15:20'),
(6, 5, 3, '民谣推荐陈粒的歌，很有味道。', '2020-08-15 20:45:10'),
(7, 6, 2, '红烧肉的做法很详细，明天就试试，谢谢分享！', '2020-08-15 15:30:25'),
(8, 7, 4, '推荐的那家海底捞确实不错，服务很好，就是人有点多需要排队。', '2020-08-15 22:20:40'),
(9, 8, 1, '大理真的很美，洱海的水特别清澈，推荐住在古城里。', '2020-08-15 18:50:15'),
(10, 9, 2, '去西藏一定要做好高原反应的准备，建议提前一周开始吃红景天。', '2020-08-15 21:25:30'),
(11, 10, 1, '环境变量配置可以参考官方文档，PATH和JAVA_HOME都要设置正确。', '2020-08-15 15:45:20'),
(12, 10, 4, '建议使用IDEA，它会自动帮你配置好大部分环境，对新手很友好。', '2020-08-15 16:20:35'),
(13, 11, 2, '密码建议用varchar类型，存储时一定要加密，推荐使用BCrypt。', '2020-08-15 17:10:45'),
(14, 11, 3, '安全性很重要，除了加密还要考虑防止SQL注入等安全问题。', '2020-08-15 18:15:50');

-- ----------------------------
-- 初始化数据 - favorite表
-- ----------------------------
INSERT INTO `favorite`(`fid`, `uid`, `pid`, `favtime`) VALUES
(1, 2, 1, '2020-08-13 14:32:10'),
(2, 3, 1, '2020-08-13 17:20:25'),
(3, 1, 2, '2020-08-14 11:15:30'),
(4, 4, 2, '2020-08-14 12:30:45'),
(5, 2, 3, '2020-08-14 19:45:20'),
(6, 3, 4, '2020-08-15 08:30:15'),
(7, 1, 6, '2020-08-15 16:20:30'),
(8, 4, 7, '2020-08-15 23:10:40'),
(9, 2, 8, '2020-08-15 19:25:50'),
(10, 3, 9, '2020-08-15 22:15:35');