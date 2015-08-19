/*
Navicat MySQL Data Transfer

Source Server         : lh
Source Server Version : 50615
Source Host           : localhost:3306
Source Database       : undibase

Target Server Type    : MYSQL
Target Server Version : 50615
File Encoding         : 65001

Date: 2015-08-19 17:40:15
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for catalog
-- ----------------------------
DROP TABLE IF EXISTS `catalog`;
CREATE TABLE `catalog` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `lft` int(11) NOT NULL,
  `parent` int(11) DEFAULT NULL,
  `rgt` int(11) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_cspy8bw8avso7fjfux1fg6s71` (`parent`),
  CONSTRAINT `FK_cspy8bw8avso7fjfux1fg6s71` FOREIGN KEY (`parent`) REFERENCES `catalog` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of catalog
-- ----------------------------
INSERT INTO `catalog` VALUES ('1', '1', null, '12', 'A', null);
INSERT INTO `catalog` VALUES ('2', '2', '1', '9', 'B', null);
INSERT INTO `catalog` VALUES ('3', '3', '2', '6', 'C', null);
INSERT INTO `catalog` VALUES ('4', '4', '3', '5', 'D', null);
INSERT INTO `catalog` VALUES ('5', '7', '2', '8', 'E', null);
INSERT INTO `catalog` VALUES ('6', '10', '1', '11', 'F', null);
