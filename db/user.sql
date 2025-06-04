/*
 Navicat Premium Dump SQL

 Source Server         : SQL
 Source Server Type    : MySQL
 Source Server Version : 80041 (8.0.41)
 Source Host           : localhost:3306
 Source Schema         : wmstest

 Target Server Type    : MySQL
 Target Server Version : 80041 (8.0.41)
 File Encoding         : 65001

 Date: 04/06/2025 17:25:36
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'primary key',
  `no` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT 'user id',
  `name` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT 'name',
  `password` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT 'password',
  `age` int DEFAULT NULL COMMENT 'age',
  `sex` int DEFAULT NULL COMMENT 'sex, 0-female, 1-male',
  `phone` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT 'phone number',
  `role_id` int DEFAULT NULL COMMENT 'role, 0-owner, 1-administrator, 2-user',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin;

-- ----------------------------
-- Records of user
-- ----------------------------
BEGIN;
INSERT INTO `user` (`id`, `no`, `name`, `password`, `age`, `sex`, `phone`, `role_id`) VALUES (1, '0', 'admin', '0', 11, 1, '13388774433', 0);
INSERT INTO `user` (`id`, `no`, `name`, `password`, `age`, `sex`, `phone`, `role_id`) VALUES (2, '2', 'stocker', '2', 22, 1, '12266554484', 2);
INSERT INTO `user` (`id`, `no`, `name`, `password`, `age`, `sex`, `phone`, `role_id`) VALUES (3, '1', 'seller', '1', 20, 1, '13344553322', 1);
INSERT INTO `user` (`id`, `no`, `name`, `password`, `age`, `sex`, `phone`, `role_id`) VALUES (4, '3', 'purchaser', '3', 33, 1, '13477536754', 3);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
