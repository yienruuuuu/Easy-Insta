/*
 Navicat Premium Data Transfer

 Source Server         : 本地
 Source Server Type    : MySQL
 Source Server Version : 80027 (8.0.27)
 Source Host           : localhost:3306
 Source Schema         : crawler_ig

 Target Server Type    : MySQL
 Target Server Version : 80027 (8.0.27)
 File Encoding         : 65001

 Date: 06/02/2024 00:27:32
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for ig_user
-- ----------------------------
DROP TABLE IF EXISTS `ig_user`;
CREATE TABLE `ig_user`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主鍵',
  `ig_pk` bigint NOT NULL,
  `user_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '帳號',
  `full_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '自定義名稱',
  `media_count` int NULL DEFAULT NULL COMMENT '貼文數量',
  `follower_count` int NULL DEFAULT NULL COMMENT '追蹤者數量',
  `following_count` int NULL DEFAULT NULL COMMENT '追蹤他人數量',
  PRIMARY KEY (`id`, `ig_pk`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ig_user
-- ----------------------------
INSERT INTO `ig_user` VALUES (1, 1103143815, 'marianlinlin', 'Marian馬莉恩', 1594, 1806, 1236);
INSERT INTO `ig_user` VALUES (2, 24829723610, 'mitaka__0702', '米塔', 12, 34, 8);

-- ----------------------------
-- Table structure for login_account
-- ----------------------------
DROP TABLE IF EXISTS `login_account`;
CREATE TABLE `login_account`  (
  `id` int NOT NULL,
  `account` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登入帳號',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登入密碼',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of login_account
-- ----------------------------
INSERT INTO `login_account` VALUES (0, 'ericlee09578', 's8903132');

SET FOREIGN_KEY_CHECKS = 1;
