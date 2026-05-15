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
  PRIMARY KEY (`uid`),
  UNIQUE KEY `unique` (`uname`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8;

-- ----------------------------
-- user record
-- ----------------------------
INSERT INTO `user`(`uid`, `uname`, `upwd`, `ustate`, `ucreatetime`, `level`) VALUES (1, 'admin', '123456', 1, '2020-08-12 10:57:45', 0);

