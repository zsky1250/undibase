/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50615
Source Host           : localhost:3306
Source Database       : undibase

Target Server Type    : MYSQL
Target Server Version : 50615
File Encoding         : 65001

Date: 2015-09-09 15:31:40
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for catalog
-- ----------------------------
DROP TABLE IF EXISTS `catalog`;
CREATE TABLE `catalog` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `lft` int(11) NOT NULL,
  `parent` bigint(20) DEFAULT NULL,
  `rgt` int(11) NOT NULL,
  `seq` int(11) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_cspy8bw8avso7fjfux1fg6s71` (`parent`),
  CONSTRAINT `FK_cspy8bw8avso7fjfux1fg6s71` FOREIGN KEY (`parent`) REFERENCES `catalog` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of catalog
-- ----------------------------
INSERT INTO `catalog` VALUES ('1', '1', null, '12', '1', 'A', null);
INSERT INTO `catalog` VALUES ('2', '2', '1', '9', '1', 'B', null);
INSERT INTO `catalog` VALUES ('3', '3', '2', '6', '1', 'C', null);
INSERT INTO `catalog` VALUES ('4', '4', '3', '5', '1', 'D', null);
INSERT INTO `catalog` VALUES ('5', '7', '2', '8', '1', 'E', null);
INSERT INTO `catalog` VALUES ('6', '10', '1', '11', '1', 'F', null);
