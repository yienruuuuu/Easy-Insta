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

 Date: 14/02/2024 23:35:57
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
INSERT INTO `ig_user` VALUES (1, 1103143815, 'marianlinlin', 'Marian馬莉恩', 1595, 1807, 1238);
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

-- ----------------------------
-- Table structure for task_queue
-- ----------------------------
DROP TABLE IF EXISTS `task_queue`;
CREATE TABLE `task_queue`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '唯一标识一个任务的ID',
  `user_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '提交任务的用户ID，用于任务权限控制',
  `task_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '任务类型，根据不同的任务类型，处理逻辑可能不同',
  `status` enum('PENDING','IN_PROGRESS','COMPLETED','FAILED') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'PENDING' COMMENT '任务的当前状态（等待中、进行中、完成、失败）',
  `submit_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '任务提交时间',
  `start_time` timestamp NULL DEFAULT NULL COMMENT '任务开始执行时间',
  `end_time` timestamp NULL DEFAULT NULL COMMENT '任务结束时间',
  `result` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '任务结果，根据需要存储结果的链接或者其他标识',
  `error_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '如果任务失败，存储错误信息',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `task_id`(`id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of task_queue
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
