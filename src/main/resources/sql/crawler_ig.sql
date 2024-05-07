CREATE DATABASE `crawler_ig`
CHARACTER SET utf8mb4
COLLATE utf8mb4_0900_ai_ci;

USE `crawler_ig`;


SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for config
-- ----------------------------
DROP TABLE IF EXISTS `config`;
CREATE TABLE `config`  (
  `param` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'key',
  `value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'value',
  PRIMARY KEY (`param`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of config
-- ----------------------------
INSERT INTO `config` VALUES ('BRIGHT_DATA_ACCOUNT', '');
INSERT INTO `config` VALUES ('BRIGHT_DATA_PASSWORD', '');
INSERT INTO `config` VALUES ('MAX_COMMENTS_PER_REQUEST', '100');
INSERT INTO `config` VALUES ('MAX_FOLLOWERS_PER_REQUEST', '200');
INSERT INTO `config` VALUES ('MAX_LIKERS_PER_REQUEST', '200');
INSERT INTO `config` VALUES ('MAX_POSTS_PER_REQUEST', '50');
INSERT INTO `config` VALUES ('MAX_PROMOTION_BY_POST_SHARE_PER_DAY', '50');
INSERT INTO `config` VALUES ('SELENIUM_IG_FOLLOWERS_DATA_STYLE', '//*[contains(@style, \'line-height: var(--base-line-clamp-line-height); --base-line-clamp-line-height: 18px;\')]');
INSERT INTO `config` VALUES ('SELENIUM_IG_INPUT_STYLE', '//input[@aria-label=\'搜尋輸入\']');
INSERT INTO `config` VALUES ('SELENIUM_IG_READY_FOR_SEND_MESSAGE', 'svg.x1lliihq.x1n2onr6.x5n08af');
INSERT INTO `config` VALUES ('SELENIUM_IG_SEND_MESSAGE_GET_TITLE_BY_JS', 'return arguments[0].getElementsByTagName(\'title\')[0].textContent;');
INSERT INTO `config` VALUES ('SELENIUM_IG_VIEW_FANS_SEARCH_STYLE', '//h1[contains(@style, \'width: calc(100% - 100px);\')]');

-- ----------------------------
-- Table structure for followers
-- ----------------------------
DROP TABLE IF EXISTS `followers`;
CREATE TABLE `followers`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主鍵',
  `ig_user_id` int NULL DEFAULT NULL COMMENT 'fk',
  `follower_pk` bigint NOT NULL COMMENT '追蹤者的pk in ig',
  `follower_user_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '追蹤者帳號',
  `follower_full_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '追蹤者自定義名稱',
  `is_private` tinyint(1) NULL DEFAULT NULL COMMENT '是否為公開帳號',
  `profile_pic_url` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '照片地址',
  `profile_pic_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '照片id',
  `is_verified` tinyint(1) NULL DEFAULT NULL COMMENT '是否以驗證 1=true=已meta驗證',
  `has_anonymous_profile_picture` tinyint(1) NULL DEFAULT NULL COMMENT '是否為匿名投向 1=true=空白頭像',
  `latest_reel_media` bigint NULL DEFAULT NULL COMMENT '最後發布的media Id(目前活躍限動)',
  `post_count` int NULL DEFAULT NULL COMMENT '發文數',
  `follower_count` int NULL DEFAULT NULL COMMENT '追隨者數量',
  `following_count` int NULL DEFAULT NULL COMMENT '追蹤數量',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_ig_user_name_follower_pk`(`follower_pk` ASC) USING BTREE,
  INDEX `fk_followers_ig_user_id`(`ig_user_id` ASC) USING BTREE,
  CONSTRAINT `fk_followers_ig_user_id` FOREIGN KEY (`ig_user_id`) REFERENCES `ig_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 0 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;


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
  PRIMARY KEY (`id`, `ig_pk`) USING BTREE,
  INDEX `id`(`id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 0 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for interaction_rate
-- ----------------------------
DROP TABLE IF EXISTS `interaction_rate`;
CREATE TABLE `interaction_rate`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'pk',
  `ig_user_id` int NOT NULL COMMENT 'fk',
  `like_count` int NULL DEFAULT NULL COMMENT '計算喜歡數量',
  `comment_count` int NULL DEFAULT NULL COMMENT '計算留言數量',
  `reshare_count` int NULL DEFAULT NULL COMMENT '計算分享數量',
  `followers` int NULL DEFAULT NULL COMMENT '當下追蹤者數量',
  `media_count` int NULL DEFAULT NULL COMMENT '影片總數',
  `insert_time` datetime NULL DEFAULT NULL COMMENT '寫入時間',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_interaction_rate_ig_user_id`(`ig_user_id` ASC) USING BTREE,
  CONSTRAINT `fk_interaction_rate_ig_user_id` FOREIGN KEY (`ig_user_id`) REFERENCES `ig_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 0 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for login_account
-- ----------------------------
DROP TABLE IF EXISTS `login_account`;
CREATE TABLE `login_account`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `account` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登入帳號',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登入密碼',
  `status` enum('NORMAL','EXHAUSTED','DEVIANT','BLOCKED') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'NORMAL' COMMENT '帳號狀態',
  `status_remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '帳號狀態備註',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '對應信箱',
  `email_password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '對應信箱密碼',
  `backup_email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '對應備援信箱密碼',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '變更時間',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 0 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for media
-- ----------------------------
DROP TABLE IF EXISTS `media`;
CREATE TABLE `media`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'pk',
  `ig_user_id` int NULL DEFAULT NULL COMMENT 'fk',
  `media_pk` bigint NULL DEFAULT NULL COMMENT 'media pk from ig',
  `media_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'media id from ig',
  `play_count` int NULL DEFAULT NULL,
  `fb_play_count` int NULL DEFAULT NULL,
  `like_count` int NULL DEFAULT NULL,
  `fb_like_count` int NULL DEFAULT NULL,
  `reshare_count` int NULL DEFAULT NULL,
  `comment_count` int NULL DEFAULT NULL,
  `number_of_qualities` int NULL DEFAULT NULL,
  `taken_at` datetime NULL DEFAULT NULL,
  `text` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '內文',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `media_id`(`media_id` ASC) USING BTREE,
  INDEX `fk_media_ig_user_id`(`ig_user_id` ASC) USING BTREE,
  CONSTRAINT `fk_media_ig_user_id` FOREIGN KEY (`ig_user_id`) REFERENCES `ig_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 0 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for media_comment
-- ----------------------------
DROP TABLE IF EXISTS `media_comment`;
CREATE TABLE `media_comment`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'pk',
  `media_id` int NULL DEFAULT NULL COMMENT 'fk',
  `text` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '留言內容',
  `commenter_full_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '留言者全名',
  `commenter_user_id` bigint NULL DEFAULT NULL,
  `commenter_user_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `comment_pk` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `commenter_is_private` tinyint(1) NULL DEFAULT NULL COMMENT '是否為公開帳號',
  `commenter_is_verified` tinyint(1) NULL DEFAULT NULL COMMENT '是否為meta驗證帳號',
  `commenter_profile_pic_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `commenter_profile_pic_url` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `commenter_latest_reel_media` bigint NULL DEFAULT NULL,
  `content_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `comment_like_count` int NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FKc4g13ic2kajrl02anuikf5bao`(`media_id` ASC) USING BTREE,
  CONSTRAINT `FKc4g13ic2kajrl02anuikf5bao` FOREIGN KEY (`media_id`) REFERENCES `media` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 0 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for media_liker
-- ----------------------------
DROP TABLE IF EXISTS `media_liker`;
CREATE TABLE `media_liker`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'pk',
  `media_id` int NULL DEFAULT NULL COMMENT 'fk, media表的id',
  `liker_pk` bigint NULL DEFAULT NULL,
  `liker_user_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `liker_full_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `liker_is_private` tinyint(1) NULL DEFAULT NULL,
  `liker_is_verified` tinyint(1) NULL DEFAULT NULL,
  `liker_profile_pic_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `liker_profile_pic_url` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `liker_latest_reel_media` bigint NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_media_liker_unique`(`media_id` ASC, `liker_pk` ASC) USING BTREE,
  CONSTRAINT `FKrrb2t5yyng90b7lym3ou22jfp` FOREIGN KEY (`media_id`) REFERENCES `media` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 0 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for task_config
-- ----------------------------
DROP TABLE IF EXISTS `task_config`;
CREATE TABLE `task_config`  (
  `id` bigint NOT NULL COMMENT 'PK',
  `task_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'task類型',
  `need_login_ig` tinyint(1) NOT NULL DEFAULT 1 COMMENT '0:false 1:true 任務是否需要登入',
  `init_status` enum('DAILY_PENDING','PENDING') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of task_config
-- ----------------------------
INSERT INTO `task_config` VALUES (1, 'GET_FOLLOWERS', 1, 'PENDING');
INSERT INTO `task_config` VALUES (2, 'GET_MEDIA', 1, 'PENDING');
INSERT INTO `task_config` VALUES (3, 'NOTHING', 1, 'PENDING');
INSERT INTO `task_config` VALUES (4, 'GET_MEDIA_COMMENT', 1, 'PENDING');
INSERT INTO `task_config` VALUES (5, 'GET_MEDIA_LIKER', 1, 'PENDING');
INSERT INTO `task_config` VALUES (6, 'GET_FOLLOWERS_DETAIL', 0, 'PENDING');
INSERT INTO `task_config` VALUES (7, 'SEND_PROMOTE_MESSAGE', 0, 'PENDING');
INSERT INTO `task_config` VALUES (8, 'SEND_PROMOTE_MESSAGE_BY_POST_SHARE', 0, 'DAILY_PENDING');

-- ----------------------------
-- Table structure for task_queue
-- ----------------------------
DROP TABLE IF EXISTS `task_queue`;
CREATE TABLE `task_queue`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `ig_user_id` int NOT NULL COMMENT 'ig_user表的id',
  `task_config_id` bigint NOT NULL,
  `status` enum('PENDING','IN_PROGRESS','PAUSED','COMPLETED','FAILED','DAILY_PENDING','DAILY_PAUSED','DAILY_COMPLETED') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'PENDING' COMMENT '任務的當前狀態',
  `submit_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '任務提交時間',
  `start_time` datetime NULL DEFAULT NULL COMMENT '任務開始執行時間',
  `end_time` datetime NULL DEFAULT NULL COMMENT '任務結束時間',
  `result` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '任務的結果',
  `error_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '任務失敗時的錯誤訊息',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '任務修改時間',
  `next_id_for_search` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '爬蟲任務需要的next_id',
  `task_queue_media_id` int NULL DEFAULT NULL COMMENT '指針',
  `version` bigint NOT NULL DEFAULT 0 COMMENT '樂觀鎖用版本號',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `task_id`(`id` ASC) USING BTREE,
  UNIQUE INDEX `id`(`id` ASC) USING BTREE,
  UNIQUE INDEX `id_2`(`id` ASC) USING BTREE,
  UNIQUE INDEX `id_3`(`id` ASC) USING BTREE,
  UNIQUE INDEX `id_4`(`id` ASC) USING BTREE,
  UNIQUE INDEX `id_5`(`id` ASC) USING BTREE,
  UNIQUE INDEX `id_6`(`id` ASC) USING BTREE,
  UNIQUE INDEX `id_7`(`id` ASC) USING BTREE,
  UNIQUE INDEX `id_8`(`id` ASC) USING BTREE,
  UNIQUE INDEX `id_9`(`id` ASC) USING BTREE,
  UNIQUE INDEX `id_10`(`id` ASC) USING BTREE,
  UNIQUE INDEX `id_11`(`id` ASC) USING BTREE,
  UNIQUE INDEX `id_12`(`id` ASC) USING BTREE,
  UNIQUE INDEX `id_13`(`id` ASC) USING BTREE,
  UNIQUE INDEX `id_14`(`id` ASC) USING BTREE,
  INDEX `FK50qhqvwec3jbpuw8mvfhpjh5q`(`ig_user_id` ASC) USING BTREE,
  INDEX `FK5ie87hsevsebwkwlucs1bpqsa`(`task_config_id` ASC) USING BTREE,
  INDEX `FK310lon0pr93v3i9eoopolc1ak`(`task_queue_media_id` ASC) USING BTREE,
  CONSTRAINT `FK310lon0pr93v3i9eoopolc1ak` FOREIGN KEY (`task_queue_media_id`) REFERENCES `task_queue_media` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FK50qhqvwec3jbpuw8mvfhpjh5q` FOREIGN KEY (`ig_user_id`) REFERENCES `ig_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FK5ie87hsevsebwkwlucs1bpqsa` FOREIGN KEY (`task_config_id`) REFERENCES `task_config` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 0 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for task_queue_followers_detail
-- ----------------------------
DROP TABLE IF EXISTS `task_queue_followers_detail`;
CREATE TABLE `task_queue_followers_detail`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `task_queue_id` bigint NOT NULL,
  `follower_id` int NOT NULL,
  `status` enum('PENDING','IN_PROGRESS','PAUSED','COMPLETED','FAILED','DAILY_PENDING','DAILY_PAUSED','DAILY_COMPLETED') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FKa1rck2m0mxnnifd03h7ny8t6s`(`follower_id` ASC) USING BTREE,
  CONSTRAINT `FKa1rck2m0mxnnifd03h7ny8t6s` FOREIGN KEY (`follower_id`) REFERENCES `followers` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 0 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for task_queue_media
-- ----------------------------
DROP TABLE IF EXISTS `task_queue_media`;
CREATE TABLE `task_queue_media`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'pk',
  `task_queue_id` bigint UNSIGNED NOT NULL COMMENT 'fk, task_queue表的id',
  `media_id` int NOT NULL COMMENT 'fk, media表的id',
  `next_media_id` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '下個media_comment/liker的id',
  `status` enum('PENDING','PAUSED','IN_PROGRESS','FAILED','COMPLETED') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'PENDING' COMMENT '狀態',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FK3b9svyjvqr4yruh7e4oxi6aje`(`media_id` ASC) USING BTREE,
  INDEX `FK88qxy2ekwhqdtrpytfasr6akd`(`task_queue_id` ASC) USING BTREE,
  CONSTRAINT `FK3b9svyjvqr4yruh7e4oxi6aje` FOREIGN KEY (`media_id`) REFERENCES `media` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FK88qxy2ekwhqdtrpytfasr6akd` FOREIGN KEY (`task_queue_id`) REFERENCES `task_queue` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 0 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for task_send_promote_message
-- ----------------------------
DROP TABLE IF EXISTS `task_send_promote_message`;
CREATE TABLE `task_send_promote_message`  (
  `task_queue_id` decimal(38, 0) NOT NULL,
  `account` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `account_full_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `text_zh_tw` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `text_en` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `text_ja` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `text_ru` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `post_url` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `status` enum('PENDING','IN_PROGRESS','PAUSED','COMPLETED','FAILED','DAILY_PENDING','DAILY_PAUSED','DAILY_COMPLETED') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  `modify_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`task_queue_id`, `account`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;
