/*
 Navicat Premium Data Transfer

 Source Server         : Eric
 Source Server Type    : MySQL
 Source Server Version : 80036 (8.0.36)
 Source Host           : localhost:3306
 Source Schema         : crawler_ig

 Target Server Type    : MySQL
 Target Server Version : 80036 (8.0.36)
 File Encoding         : 65001

 Date: 02/02/2024 18:00:11
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
  `is_private` tinyint NULL DEFAULT NULL COMMENT '是否為公開帳號',
  `is_business` tinyint NULL DEFAULT NULL COMMENT '是否為商業帳號',
  `media_count` int NULL DEFAULT NULL COMMENT '貼文數量',
  `follower_count` int NULL DEFAULT NULL COMMENT '追蹤者數量',
  `following_count` int NULL DEFAULT NULL COMMENT '追蹤他人數量',
  `ig_account_type` tinyint NULL DEFAULT NULL COMMENT '未知屬性',
  PRIMARY KEY (`id`, `ig_pk`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ig_user
-- ----------------------------
INSERT INTO `ig_user` VALUES (1, 1103143815, 'marianlinlin', 'Marian馬莉恩', 0, 0, 1594, 1806, 1236, 1);

-- ----------------------------
-- Table structure for login_account
-- ----------------------------
DROP TABLE IF EXISTS `login_account`;
CREATE TABLE `login_account`  (
  `id` int NOT NULL,
  `account` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登入帳號',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登入密碼',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of login_account
-- ----------------------------
INSERT INTO `login_account` VALUES (0, 'ericlee09578', 's8903132');

SET FOREIGN_KEY_CHECKS = 1;
