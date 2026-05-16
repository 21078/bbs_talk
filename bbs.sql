CREATE DATABASE bbs CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE bbs;
SET FOREIGN_KEY_CHECKS=0;


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
  PRIMARY KEY (`pid`),
  KEY `uid` (`uid`),
  CONSTRAINT `post_ibfk_1` FOREIGN KEY (`uid`) REFERENCES `user` (`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

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
  KEY `pid` (`pid`),
  CONSTRAINT `reply_ibfk_2` FOREIGN KEY (`uid`) REFERENCES `user` (`uid`),
  CONSTRAINT `reply_ibfk_3` FOREIGN KEY (`pid`) REFERENCES `post` (`pid`)
) ENGINE=InnoDB AUTO_INCREMENT=41 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for user
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
  `career` varchar(50) DEFAULT NULL,
  `address` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`uid`),
  UNIQUE KEY `unique` (`uname`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8;

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
  KEY `pid` (`pid`),
  CONSTRAINT `favorite_ibfk_1` FOREIGN KEY (`uid`) REFERENCES `user` (`uid`),
  CONSTRAINT `favorite_ibfk_2` FOREIGN KEY (`pid`) REFERENCES `post` (`pid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- 初始化数据
-- ----------------------------
INSERT INTO `user`(`uid`, `uname`, `upwd`, `ustate`, `ucreatetime`, `level`, `phone`, `career`, `address`) VALUES (1, 'admin', '123456', 1, '2020-08-12 10:57:45', 0, NULL, NULL, NULL);
INSERT INTO `user`(`uid`, `uname`, `upwd`, `ustate`, `ucreatetime`, `level`, `phone`, `career`, `address`) VALUES (2, '1', '123456', 1, '2020-08-12 10:57:45', 1, NULL, NULL, NULL);

INSERT INTO `post`(`pid`, `ptitle`, `pbody`, `psendtime`, `lastreplytime`, `uid`) VALUES (1, '测试帖子', '测试帖子内容', '2020-08-12 10:57:45', '2020-08-12 10:57:45', 1);