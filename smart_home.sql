/*
 Navicat Premium Data Transfer

 Source Server         : mysql
 Source Server Type    : MySQL
 Source Server Version : 80027
 Source Host           : localhost:3306
 Source Schema         : smart_home

 Target Server Type    : MySQL
 Target Server Version : 80027
 File Encoding         : 65001

 Date: 25/02/2026 18:23:56
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for alerts
-- ----------------------------
DROP TABLE IF EXISTS `alerts`;
CREATE TABLE `alerts`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `device_id` bigint(0) NOT NULL,
  `user_id` bigint(0) NOT NULL,
  `threshold_value` double NULL DEFAULT NULL,
  `actual_value` double NULL DEFAULT NULL,
  `message` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `alert_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `is_read` tinyint(1) NULL DEFAULT 0,
  `created_at` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `device_id`(`device_id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  CONSTRAINT `alerts_ibfk_1` FOREIGN KEY (`device_id`) REFERENCES `devices` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `alerts_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of alerts
-- ----------------------------
INSERT INTO `alerts` VALUES (1, 4, 2, 35, 36, '客厅温度传感器 数值(36)超过最大阈值(35)', 'THRESHOLD_EXCEEDED', 1, '2026-02-16 17:44:04');
INSERT INTO `alerts` VALUES (2, 5, 2, 70, 75, '客厅湿度传感器 数值(75)超过最大阈值(70)', 'THRESHOLD_EXCEEDED', 1, '2026-02-18 17:44:04');
INSERT INTO `alerts` VALUES (3, 8, 2, 50, 55, '厨房烟雾报警器 数值(55)超过最大阈值(50)', 'THRESHOLD_EXCEEDED', 0, '2026-02-20 17:44:04');
INSERT INTO `alerts` VALUES (4, 4, 2, 15, 14, '客厅温度传感器 数值(14)低于最小阈值(15)', 'THRESHOLD_EXCEEDED', 0, '2026-02-21 17:44:04');
INSERT INTO `alerts` VALUES (5, 12, 3, 35, 37, '书房温度传感器 数值(37)超过最大阈值(35)', 'THRESHOLD_EXCEEDED', 0, '2026-02-19 17:44:04');
INSERT INTO `alerts` VALUES (6, 5, 2, 30, 19, '客厅湿度传感器 数值(19.0)低于最小阈值(30.0)', 'THRESHOLD_EXCEEDED', 0, '2026-02-22 01:32:49');
INSERT INTO `alerts` VALUES (7, 5, 2, 30, 24, '客厅湿度传感器 数值(24.0)低于最小阈值(30.0)', 'THRESHOLD_EXCEEDED', 0, '2026-02-22 01:32:50');

-- ----------------------------
-- Table structure for commands
-- ----------------------------
DROP TABLE IF EXISTS `commands`;
CREATE TABLE `commands`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `device_id` bigint(0) NOT NULL,
  `user_id` bigint(0) NOT NULL,
  `command_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `command_value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `executed_at` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'SUCCESS',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `device_id`(`device_id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  CONSTRAINT `commands_ibfk_1` FOREIGN KEY (`device_id`) REFERENCES `devices` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `commands_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 44 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of commands
-- ----------------------------
INSERT INTO `commands` VALUES (1, 1, 2, 'POWER_ON', NULL, '2026-02-19 17:44:04', 'SUCCESS');
INSERT INTO `commands` VALUES (2, 1, 2, 'SET_VALUE', '80', '2026-02-19 17:44:04', 'SUCCESS');
INSERT INTO `commands` VALUES (3, 3, 2, 'POWER_ON', NULL, '2026-02-19 17:44:04', 'SUCCESS');
INSERT INTO `commands` VALUES (4, 3, 2, 'SET_VALUE', '26', '2026-02-19 17:44:04', 'SUCCESS');
INSERT INTO `commands` VALUES (5, 1, 2, 'SET_VALUE', '50', '2026-02-20 17:44:04', 'SUCCESS');
INSERT INTO `commands` VALUES (6, 1, 2, 'SET_VALUE', '80', '2026-02-20 17:44:04', 'SUCCESS');
INSERT INTO `commands` VALUES (7, 2, 2, 'POWER_ON', NULL, '2026-02-20 17:44:04', 'SUCCESS');
INSERT INTO `commands` VALUES (8, 4, 2, 'SET_VALUE', '25', '2026-02-21 17:44:04', 'SUCCESS');
INSERT INTO `commands` VALUES (9, 5, 2, 'SET_VALUE', '55', '2026-02-21 17:44:04', 'SUCCESS');
INSERT INTO `commands` VALUES (10, 10, 3, 'POWER_ON', NULL, '2026-02-18 17:44:04', 'SUCCESS');
INSERT INTO `commands` VALUES (11, 10, 3, 'SET_VALUE', '70', '2026-02-18 17:44:04', 'SUCCESS');
INSERT INTO `commands` VALUES (12, 9, 2, 'POWER_OFF', NULL, '2026-02-22 01:32:47', 'SUCCESS');
INSERT INTO `commands` VALUES (13, 9, 2, 'POWER_ON', NULL, '2026-02-22 01:32:47', 'SUCCESS');
INSERT INTO `commands` VALUES (14, 5, 2, 'SET_VALUE', '19', '2026-02-22 01:32:49', 'SUCCESS');
INSERT INTO `commands` VALUES (15, 5, 2, 'SET_VALUE', '24', '2026-02-22 01:32:50', 'SUCCESS');
INSERT INTO `commands` VALUES (16, 1, 2, 'POWER_ON', NULL, '2026-02-22 01:33:33', 'SUCCESS');
INSERT INTO `commands` VALUES (17, 1, 2, 'SET_VALUE', '80', '2026-02-22 01:33:33', 'SUCCESS');
INSERT INTO `commands` VALUES (18, 3, 2, 'POWER_ON', NULL, '2026-02-22 01:33:33', 'SUCCESS');
INSERT INTO `commands` VALUES (19, 3, 2, 'SET_VALUE', '26', '2026-02-22 01:33:33', 'SUCCESS');
INSERT INTO `commands` VALUES (20, 1, 2, 'POWER_OFF', NULL, '2026-02-22 01:33:35', 'SUCCESS');
INSERT INTO `commands` VALUES (21, 2, 2, 'POWER_OFF', NULL, '2026-02-22 01:33:35', 'SUCCESS');
INSERT INTO `commands` VALUES (22, 3, 2, 'POWER_OFF', NULL, '2026-02-22 01:33:35', 'SUCCESS');
INSERT INTO `commands` VALUES (23, 6, 2, 'POWER_OFF', NULL, '2026-02-22 01:33:35', 'SUCCESS');
INSERT INTO `commands` VALUES (24, 4, 2, 'POWER_OFF', NULL, '2026-02-22 01:33:36', 'SUCCESS');
INSERT INTO `commands` VALUES (25, 7, 2, 'POWER_OFF', NULL, '2026-02-22 01:33:36', 'SUCCESS');
INSERT INTO `commands` VALUES (26, 5, 2, 'POWER_OFF', NULL, '2026-02-22 01:33:36', 'SUCCESS');
INSERT INTO `commands` VALUES (27, 8, 2, 'POWER_OFF', NULL, '2026-02-22 01:33:36', 'SUCCESS');
INSERT INTO `commands` VALUES (28, 9, 2, 'POWER_OFF', NULL, '2026-02-22 01:33:36', 'SUCCESS');
INSERT INTO `commands` VALUES (29, 1, 2, 'POWER_ON', NULL, '2026-02-24 19:51:00', 'SUCCESS');
INSERT INTO `commands` VALUES (30, 1, 2, 'SET_VALUE', '80', '2026-02-24 19:51:00', 'SUCCESS');
INSERT INTO `commands` VALUES (31, 3, 2, 'POWER_ON', NULL, '2026-02-24 19:51:00', 'SUCCESS');
INSERT INTO `commands` VALUES (32, 3, 2, 'SET_VALUE', '26', '2026-02-24 19:51:00', 'SUCCESS');
INSERT INTO `commands` VALUES (34, 8, 2, 'POWER_OFF', NULL, '2026-02-24 19:51:01', 'SUCCESS');
INSERT INTO `commands` VALUES (35, 9, 2, 'POWER_OFF', NULL, '2026-02-24 19:51:01', 'SUCCESS');
INSERT INTO `commands` VALUES (37, 1, 2, 'POWER_ON', NULL, '2026-02-24 19:51:02', 'SUCCESS');
INSERT INTO `commands` VALUES (38, 5, 2, 'POWER_ON', NULL, '2026-02-24 19:51:03', 'SUCCESS');
INSERT INTO `commands` VALUES (39, 2, 2, 'POWER_ON', NULL, '2026-02-24 19:51:03', 'SUCCESS');
INSERT INTO `commands` VALUES (40, 6, 2, 'POWER_ON', NULL, '2026-02-24 19:51:03', 'SUCCESS');
INSERT INTO `commands` VALUES (41, 7, 2, 'POWER_ON', NULL, '2026-02-24 19:51:03', 'SUCCESS');
INSERT INTO `commands` VALUES (42, 4, 2, 'POWER_ON', NULL, '2026-02-24 19:51:03', 'SUCCESS');
INSERT INTO `commands` VALUES (43, 8, 2, 'POWER_ON', NULL, '2026-02-24 19:51:03', 'SUCCESS');
INSERT INTO `commands` VALUES (44, 9, 2, 'POWER_ON', NULL, '2026-02-24 19:51:03', 'SUCCESS');

-- ----------------------------
-- Table structure for device_types
-- ----------------------------
DROP TABLE IF EXISTS `device_types`;
CREATE TABLE `device_types`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `param_template` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `icon` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `name`(`name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of device_types
-- ----------------------------
INSERT INTO `device_types` VALUES (1, '智能灯', '{\"brightness\": {\"min\": 0, \"max\": 100}}', '可调节亮度的智能灯', 'mdi-lightbulb');
INSERT INTO `device_types` VALUES (2, '空调', '{\"temperature\": {\"min\": 16, \"max\": 30}}', '智能空调', 'mdi-air-conditioner');
INSERT INTO `device_types` VALUES (3, '温度传感器', '{\"unit\": \"celsius\"}', '温度监测传感器', 'mdi-thermometer');
INSERT INTO `device_types` VALUES (4, '湿度传感器', '{\"unit\": \"percent\"}', '湿度监测传感器', 'mdi-water-percent');
INSERT INTO `device_types` VALUES (5, '智能插座', '{}', '可远程控制的智能插座', 'mdi-power-socket');
INSERT INTO `device_types` VALUES (6, '门窗传感器', '{}', '门窗开关状态传感器', 'mdi-door');
INSERT INTO `device_types` VALUES (7, '烟雾报警器', '{\"threshold\": {\"min\": 0, \"max\": 100}}', '烟雾浓度检测报警器', 'mdi-smoke-detector');
INSERT INTO `device_types` VALUES (8, '智能窗帘', '{\"position\": {\"min\": 0, \"max\": 100}}', '电动智能窗帘', 'mdi-blinds');

-- ----------------------------
-- Table structure for devices
-- ----------------------------
DROP TABLE IF EXISTS `devices`;
CREATE TABLE `devices`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `type_id` bigint(0) NOT NULL,
  `user_id` bigint(0) NOT NULL,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'offline',
  `power_state` tinyint(1) NULL DEFAULT 0,
  `current_value` double NULL DEFAULT NULL,
  `threshold_min` double NULL DEFAULT NULL,
  `threshold_max` double NULL DEFAULT NULL,
  `params` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `created_at` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  `updated_at` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `type_id`(`type_id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  CONSTRAINT `devices_ibfk_1` FOREIGN KEY (`type_id`) REFERENCES `device_types` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `devices_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of devices
-- ----------------------------
INSERT INTO `devices` VALUES (1, '客厅灯', 1, 2, 'online', 1, 80, 0, 100, NULL, '2026-02-21 17:44:04', '2026-02-24 19:51:02');
INSERT INTO `devices` VALUES (2, '卧室灯', 1, 2, 'online', 1, 50, 0, 100, NULL, '2026-02-21 17:44:04', '2026-02-24 19:51:03');
INSERT INTO `devices` VALUES (3, '客厅空调', 2, 2, 'online', 1, 26, 18, 28, NULL, '2026-02-21 17:44:04', '2026-02-24 19:51:00');
INSERT INTO `devices` VALUES (4, '客厅温度传感器', 3, 2, 'online', 1, 25, 15, 35, NULL, '2026-02-21 17:44:04', '2026-02-24 19:51:03');
INSERT INTO `devices` VALUES (5, '客厅湿度传感器', 4, 2, 'online', 1, 24, 30, 70, NULL, '2026-02-21 17:44:04', '2026-02-24 19:51:03');
INSERT INTO `devices` VALUES (6, '电视插座', 5, 2, 'online', 1, 0, NULL, NULL, NULL, '2026-02-21 17:44:04', '2026-02-24 19:51:03');
INSERT INTO `devices` VALUES (7, '大门传感器', 6, 2, 'online', 1, 0, NULL, NULL, NULL, '2026-02-21 17:44:04', '2026-02-24 19:51:03');
INSERT INTO `devices` VALUES (8, '厨房烟雾报警器', 7, 2, 'online', 1, 5, 0, 50, NULL, '2026-02-21 17:44:04', '2026-02-24 19:51:03');
INSERT INTO `devices` VALUES (9, '卧室窗帘', 8, 2, 'online', 1, 100, 0, 100, NULL, '2026-02-21 17:44:04', '2026-02-24 19:51:03');
INSERT INTO `devices` VALUES (10, '书房灯', 1, 3, 'online', 1, 70, 0, 100, NULL, '2026-02-21 17:44:04', '2026-02-21 17:44:04');
INSERT INTO `devices` VALUES (11, '书房空调', 2, 3, 'offline', 0, 24, 18, 28, NULL, '2026-02-21 17:44:04', '2026-02-21 17:44:04');
INSERT INTO `devices` VALUES (12, '书房温度传感器', 3, 3, 'online', 1, 23, 15, 35, NULL, '2026-02-21 17:44:04', '2026-02-21 17:44:04');
INSERT INTO `devices` VALUES (13, '书房灯', 1, 3, 'online', 1, 70, 0, 100, NULL, '2026-02-22 01:22:46', '2026-02-22 01:22:46');
INSERT INTO `devices` VALUES (14, '书房空调', 2, 3, 'offline', 0, 24, 18, 28, NULL, '2026-02-22 01:22:46', '2026-02-22 01:22:46');
INSERT INTO `devices` VALUES (15, '书房温度传感器', 3, 3, 'online', 1, 23, 15, 35, NULL, '2026-02-22 01:22:46', '2026-02-22 01:22:46');

-- ----------------------------
-- Table structure for feedbacks
-- ----------------------------
DROP TABLE IF EXISTS `feedbacks`;
CREATE TABLE `feedbacks`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(0) NOT NULL,
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'PENDING',
  `admin_reply` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `created_at` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  `updated_at` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  CONSTRAINT `feedbacks_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of feedbacks
-- ----------------------------
INSERT INTO `feedbacks` VALUES (1, 2, '希望增加定时功能', '希望能够设置设备定时开关的功能，比如早上7点自动开灯', 'PROCESSED', '感谢您的建议，我们会在下个版本中考虑添加定时功能。', '2026-02-14 17:44:04', '2026-02-16 17:44:04');
INSERT INTO `feedbacks` VALUES (2, 2, '空调控制有延迟', '控制空调时感觉有1-2秒的延迟，希望能优化', 'PENDING', NULL, '2026-02-19 17:44:04', '2026-02-19 17:44:04');
INSERT INTO `feedbacks` VALUES (3, 3, '界面很好用', '系统界面设计很简洁，使用起来很方便，点赞！', 'PROCESSED', '感谢您的认可，我们会继续努力！', '2026-02-18 17:44:04', '2026-02-20 17:44:04');

-- ----------------------------
-- Table structure for logs
-- ----------------------------
DROP TABLE IF EXISTS `logs`;
CREATE TABLE `logs`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(0) NULL DEFAULT NULL,
  `operation` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `details` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `ip_address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `created_at` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  CONSTRAINT `logs_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 65 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of logs
-- ----------------------------
INSERT INTO `logs` VALUES (1, 1, 'USER_LOGIN', '管理员登录', '127.0.0.1', '2026-02-16 17:44:04');
INSERT INTO `logs` VALUES (2, 2, 'USER_LOGIN', '用户登录', '192.168.1.100', '2026-02-18 17:44:04');
INSERT INTO `logs` VALUES (3, 2, 'DEVICE_COMMAND', '客厅灯 - 开启设备', NULL, '2026-02-18 17:44:04');
INSERT INTO `logs` VALUES (4, 2, 'DEVICE_COMMAND', '客厅灯 - 设置数值: 80', NULL, '2026-02-18 17:44:04');
INSERT INTO `logs` VALUES (5, 2, 'SCENE_CREATE', '创建场景: 回家模式', NULL, '2026-02-19 17:44:04');
INSERT INTO `logs` VALUES (6, 2, 'SCENE_ACTIVATE', '激活场景: 回家模式', NULL, '2026-02-19 17:44:04');
INSERT INTO `logs` VALUES (7, 2, 'ALERT_TRIGGERED', '客厅温度传感器 数值(36)超过最大阈值(35)', NULL, '2026-02-16 17:44:04');
INSERT INTO `logs` VALUES (8, 3, 'USER_LOGIN', '用户登录', '192.168.1.101', '2026-02-17 17:44:04');
INSERT INTO `logs` VALUES (9, 3, 'DEVICE_BINDING', '绑定设备: 书房灯', NULL, '2026-02-17 17:44:04');
INSERT INTO `logs` VALUES (10, 1, 'USER_LOGIN', '管理员登录', '127.0.0.1', '2026-02-21 17:44:04');
INSERT INTO `logs` VALUES (11, 1, 'USER_LOGIN', '用户登录', NULL, '2026-02-22 00:01:56');
INSERT INTO `logs` VALUES (12, 1, 'USER_LOGIN', '用户登录', NULL, '2026-02-22 00:47:59');
INSERT INTO `logs` VALUES (13, 2, 'USER_LOGIN', '用户登录', NULL, '2026-02-22 01:24:59');
INSERT INTO `logs` VALUES (14, 1, 'USER_LOGIN', '用户登录', NULL, '2026-02-22 01:30:24');
INSERT INTO `logs` VALUES (15, 2, 'USER_LOGIN', '用户登录', NULL, '2026-02-22 01:32:09');
INSERT INTO `logs` VALUES (16, 2, 'DEVICE_COMMAND', '卧室窗帘 - 关闭设备', NULL, '2026-02-22 01:32:47');
INSERT INTO `logs` VALUES (17, 2, 'DEVICE_COMMAND', '卧室窗帘 - 开启设备', NULL, '2026-02-22 01:32:47');
INSERT INTO `logs` VALUES (18, 2, 'ALERT_TRIGGERED', '客厅湿度传感器 数值(19.0)低于最小阈值(30.0)', NULL, '2026-02-22 01:32:49');
INSERT INTO `logs` VALUES (19, 2, 'DEVICE_COMMAND', '客厅湿度传感器 - 设置数值: 19.0', NULL, '2026-02-22 01:32:49');
INSERT INTO `logs` VALUES (20, 2, 'ALERT_TRIGGERED', '客厅湿度传感器 数值(24.0)低于最小阈值(30.0)', NULL, '2026-02-22 01:32:50');
INSERT INTO `logs` VALUES (21, 2, 'DEVICE_COMMAND', '客厅湿度传感器 - 设置数值: 24.0', NULL, '2026-02-22 01:32:50');
INSERT INTO `logs` VALUES (22, 2, 'SCENE_CREATE', '创建场景: 1', NULL, '2026-02-22 01:33:09');
INSERT INTO `logs` VALUES (23, 2, 'SCENE_DELETE', '删除场景: 1', NULL, '2026-02-22 01:33:13');
INSERT INTO `logs` VALUES (24, 2, 'DEVICE_COMMAND', '客厅灯 - 开启设备', NULL, '2026-02-22 01:33:33');
INSERT INTO `logs` VALUES (25, 2, 'DEVICE_COMMAND', '客厅灯 - 设置数值: 80.0', NULL, '2026-02-22 01:33:33');
INSERT INTO `logs` VALUES (26, 2, 'DEVICE_COMMAND', '客厅空调 - 开启设备', NULL, '2026-02-22 01:33:33');
INSERT INTO `logs` VALUES (27, 2, 'DEVICE_COMMAND', '客厅空调 - 设置数值: 26.0', NULL, '2026-02-22 01:33:33');
INSERT INTO `logs` VALUES (28, 2, 'SCENE_ACTIVATE', '激活场景: 回家模式', NULL, '2026-02-22 01:33:33');
INSERT INTO `logs` VALUES (29, 2, 'DEVICE_COMMAND', '客厅灯 - 关闭设备', NULL, '2026-02-22 01:33:35');
INSERT INTO `logs` VALUES (30, 2, 'DEVICE_COMMAND', '卧室灯 - 关闭设备', NULL, '2026-02-22 01:33:35');
INSERT INTO `logs` VALUES (31, 2, 'DEVICE_COMMAND', '客厅空调 - 关闭设备', NULL, '2026-02-22 01:33:35');
INSERT INTO `logs` VALUES (32, 2, 'DEVICE_COMMAND', '电视插座 - 关闭设备', NULL, '2026-02-22 01:33:35');
INSERT INTO `logs` VALUES (33, 2, 'SCENE_ACTIVATE', '激活场景: 离家模式', NULL, '2026-02-22 01:33:35');
INSERT INTO `logs` VALUES (34, 2, 'DEVICE_COMMAND', '客厅温度传感器 - 关闭设备', NULL, '2026-02-22 01:33:36');
INSERT INTO `logs` VALUES (35, 2, 'DEVICE_COMMAND', '厨房烟雾报警器 - 关闭设备', NULL, '2026-02-22 01:33:36');
INSERT INTO `logs` VALUES (36, 2, 'DEVICE_COMMAND', '卧室窗帘 - 关闭设备', NULL, '2026-02-22 01:33:36');
INSERT INTO `logs` VALUES (37, 2, 'DEVICE_COMMAND', '客厅湿度传感器 - 关闭设备', NULL, '2026-02-22 01:33:36');
INSERT INTO `logs` VALUES (38, 2, 'DEVICE_COMMAND', '大门传感器 - 关闭设备', NULL, '2026-02-22 01:33:36');
INSERT INTO `logs` VALUES (39, 1, 'USER_LOGIN', '用户登录', NULL, '2026-02-22 12:57:11');
INSERT INTO `logs` VALUES (40, 1, 'USER_LOGIN', '用户登录', NULL, '2026-02-24 16:21:17');
INSERT INTO `logs` VALUES (41, 2, 'USER_LOGIN', '用户登录', NULL, '2026-02-24 19:50:57');
INSERT INTO `logs` VALUES (42, 2, 'DEVICE_COMMAND', '客厅灯 - 开启设备', NULL, '2026-02-24 19:51:00');
INSERT INTO `logs` VALUES (43, 2, 'DEVICE_COMMAND', '客厅灯 - 设置数值: 80.0', NULL, '2026-02-24 19:51:00');
INSERT INTO `logs` VALUES (44, 2, 'DEVICE_COMMAND', '客厅空调 - 开启设备', NULL, '2026-02-24 19:51:00');
INSERT INTO `logs` VALUES (45, 2, 'DEVICE_COMMAND', '客厅空调 - 设置数值: 26.0', NULL, '2026-02-24 19:51:00');
INSERT INTO `logs` VALUES (46, 2, 'SCENE_ACTIVATE', '激活场景: 回家模式', NULL, '2026-02-24 19:51:00');
INSERT INTO `logs` VALUES (51, 2, 'DEVICE_COMMAND', '厨房烟雾报警器 - 关闭设备', NULL, '2026-02-24 19:51:01');
INSERT INTO `logs` VALUES (52, 2, 'DEVICE_COMMAND', '卧室窗帘 - 关闭设备', NULL, '2026-02-24 19:51:01');
INSERT INTO `logs` VALUES (53, 2, 'SCENE_ACTIVATE', '激活场景: 离家模式', NULL, '2026-02-24 19:51:01');
INSERT INTO `logs` VALUES (58, 2, 'DEVICE_COMMAND', '客厅灯 - 开启设备', NULL, '2026-02-24 19:51:02');
INSERT INTO `logs` VALUES (59, 2, 'DEVICE_COMMAND', '卧室灯 - 开启设备', NULL, '2026-02-24 19:51:03');
INSERT INTO `logs` VALUES (60, 2, 'DEVICE_COMMAND', '大门传感器 - 开启设备', NULL, '2026-02-24 19:51:03');
INSERT INTO `logs` VALUES (61, 2, 'DEVICE_COMMAND', '电视插座 - 开启设备', NULL, '2026-02-24 19:51:03');
INSERT INTO `logs` VALUES (62, 2, 'DEVICE_COMMAND', '客厅湿度传感器 - 开启设备', NULL, '2026-02-24 19:51:03');
INSERT INTO `logs` VALUES (63, 2, 'DEVICE_COMMAND', '客厅温度传感器 - 开启设备', NULL, '2026-02-24 19:51:03');
INSERT INTO `logs` VALUES (64, 2, 'DEVICE_COMMAND', '厨房烟雾报警器 - 开启设备', NULL, '2026-02-24 19:51:03');
INSERT INTO `logs` VALUES (65, 2, 'DEVICE_COMMAND', '卧室窗帘 - 开启设备', NULL, '2026-02-24 19:51:03');

-- ----------------------------
-- Table structure for notification_preferences
-- ----------------------------
DROP TABLE IF EXISTS `notification_preferences`;
CREATE TABLE `notification_preferences`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(0) NOT NULL,
  `notification_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `enabled` tinyint(1) NULL DEFAULT 1,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_type`(`user_id`, `notification_type`) USING BTREE,
  CONSTRAINT `notification_preferences_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of notification_preferences
-- ----------------------------
INSERT INTO `notification_preferences` VALUES (1, 1, 'POPUP', 1);
INSERT INTO `notification_preferences` VALUES (2, 1, 'LOG', 1);
INSERT INTO `notification_preferences` VALUES (3, 2, 'POPUP', 1);
INSERT INTO `notification_preferences` VALUES (4, 2, 'LOG', 1);
INSERT INTO `notification_preferences` VALUES (5, 3, 'POPUP', 1);
INSERT INTO `notification_preferences` VALUES (6, 3, 'LOG', 0);

-- ----------------------------
-- Table structure for scene_actions
-- ----------------------------
DROP TABLE IF EXISTS `scene_actions`;
CREATE TABLE `scene_actions`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `scene_id` bigint(0) NOT NULL,
  `device_id` bigint(0) NOT NULL,
  `action_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `action_value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `sort_order` int(0) NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `scene_id`(`scene_id`) USING BTREE,
  INDEX `device_id`(`device_id`) USING BTREE,
  CONSTRAINT `scene_actions_ibfk_1` FOREIGN KEY (`scene_id`) REFERENCES `scenes` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `scene_actions_ibfk_2` FOREIGN KEY (`device_id`) REFERENCES `devices` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of scene_actions
-- ----------------------------
INSERT INTO `scene_actions` VALUES (1, 1, 1, 'POWER_ON', NULL, 0);
INSERT INTO `scene_actions` VALUES (2, 1, 1, 'SET_VALUE', '80', 1);
INSERT INTO `scene_actions` VALUES (3, 1, 3, 'POWER_ON', NULL, 2);
INSERT INTO `scene_actions` VALUES (4, 1, 3, 'SET_VALUE', '26', 3);
INSERT INTO `scene_actions` VALUES (5, 2, 1, 'POWER_OFF', NULL, 0);
INSERT INTO `scene_actions` VALUES (6, 2, 2, 'POWER_OFF', NULL, 1);
INSERT INTO `scene_actions` VALUES (7, 2, 3, 'POWER_OFF', NULL, 2);
INSERT INTO `scene_actions` VALUES (8, 2, 6, 'POWER_OFF', NULL, 3);
INSERT INTO `scene_actions` VALUES (9, 3, 1, 'POWER_OFF', NULL, 0);
INSERT INTO `scene_actions` VALUES (10, 3, 2, 'SET_VALUE', '20', 1);
INSERT INTO `scene_actions` VALUES (11, 3, 9, 'SET_VALUE', '0', 2);
INSERT INTO `scene_actions` VALUES (12, 4, 10, 'POWER_ON', NULL, 0);
INSERT INTO `scene_actions` VALUES (13, 4, 10, 'SET_VALUE', '70', 1);
INSERT INTO `scene_actions` VALUES (14, 4, 11, 'POWER_ON', NULL, 2);
INSERT INTO `scene_actions` VALUES (15, 5, 8, 'POWER_OFF', NULL, 1);
INSERT INTO `scene_actions` VALUES (16, 5, 9, 'POWER_OFF', NULL, 2);
INSERT INTO `scene_actions` VALUES (17, 6, 7, 'POWER_OFF', NULL, 0);
INSERT INTO `scene_actions` VALUES (18, 6, 8, 'SET_VALUE', '20', 1);
INSERT INTO `scene_actions` VALUES (19, 6, 12, 'SET_VALUE', '0', 2);
INSERT INTO `scene_actions` VALUES (20, 7, 13, 'POWER_ON', NULL, 0);
INSERT INTO `scene_actions` VALUES (21, 7, 13, 'SET_VALUE', '70', 1);
INSERT INTO `scene_actions` VALUES (22, 7, 14, 'POWER_ON', NULL, 2);

-- ----------------------------
-- Table structure for scenes
-- ----------------------------
DROP TABLE IF EXISTS `scenes`;
CREATE TABLE `scenes`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(0) NOT NULL,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `is_active` tinyint(1) NULL DEFAULT 0,
  `created_at` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  CONSTRAINT `scenes_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of scenes
-- ----------------------------
INSERT INTO `scenes` VALUES (1, 2, '回家模式', '回家时自动开启灯光和空调', 0, '2026-02-21 17:44:04');
INSERT INTO `scenes` VALUES (2, 2, '离家模式', '离家时关闭所有设备', 0, '2026-02-21 17:44:04');
INSERT INTO `scenes` VALUES (3, 2, '睡眠模式', '睡觉时调暗灯光关闭窗帘', 0, '2026-02-21 17:44:04');
INSERT INTO `scenes` VALUES (4, 3, '工作模式', '工作时开启书房灯和空调', 0, '2026-02-21 17:44:04');
INSERT INTO `scenes` VALUES (5, 2, '离家模式', '离家时关闭所有设备', 1, '2026-02-22 01:22:46');
INSERT INTO `scenes` VALUES (6, 2, '睡眠模式', '睡觉时调暗灯光关闭窗帘', 0, '2026-02-22 01:22:46');
INSERT INTO `scenes` VALUES (7, 3, '学习模式', '工作时开启书房灯和空调', 0, '2026-02-22 01:22:46');

-- ----------------------------
-- Table structure for shared_snapshots
-- ----------------------------
DROP TABLE IF EXISTS `shared_snapshots`;
CREATE TABLE `shared_snapshots`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(0) NOT NULL,
  `device_id` bigint(0) NOT NULL,
  `status_data` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `link_uuid` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `expire_time` datetime(0) NULL DEFAULT NULL,
  `created_at` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `link_uuid`(`link_uuid`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  INDEX `device_id`(`device_id`) USING BTREE,
  CONSTRAINT `shared_snapshots_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `shared_snapshots_ibfk_2` FOREIGN KEY (`device_id`) REFERENCES `devices` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of shared_snapshots
-- ----------------------------
INSERT INTO `shared_snapshots` VALUES (1, 1, 1, '{\"deviceName\":\"办公室灯\",\"typeName\":\"智能灯\",\"status\":\"online\",\"powerState\":true,\"currentValue\":90,\"snapshotTime\":\"2024-01-15T10:30:00\"}', 'abc12345-1234-1234-1234-123456789abc', '2026-02-25 16:17:21', '2026-02-24 16:17:21');
INSERT INTO `shared_snapshots` VALUES (2, 2, 10, '{\"deviceName\":\"客厅温度传感器\",\"typeName\":\"温度传感器\",\"status\":\"online\",\"powerState\":true,\"currentValue\":25,\"snapshotTime\":\"2024-01-15T11:00:00\"}', 'def67890-5678-5678-5678-567890123def', '2026-02-25 04:17:21', '2026-02-24 16:17:21');

-- ----------------------------
-- Table structure for status_history
-- ----------------------------
DROP TABLE IF EXISTS `status_history`;
CREATE TABLE `status_history`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `device_id` bigint(0) NOT NULL,
  `status_value` double NULL DEFAULT NULL,
  `power_state` tinyint(1) NULL DEFAULT NULL,
  `recorded_at` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `device_id`(`device_id`) USING BTREE,
  CONSTRAINT `status_history_ibfk_1` FOREIGN KEY (`device_id`) REFERENCES `devices` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 46 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of status_history
-- ----------------------------
INSERT INTO `status_history` VALUES (1, 1, 0, 0, '2026-02-18 17:44:04');
INSERT INTO `status_history` VALUES (2, 1, 80, 1, '2026-02-19 17:44:04');
INSERT INTO `status_history` VALUES (3, 1, 50, 1, '2026-02-20 17:44:04');
INSERT INTO `status_history` VALUES (4, 1, 80, 1, '2026-02-21 17:44:04');
INSERT INTO `status_history` VALUES (5, 3, 0, 0, '2026-02-18 17:44:04');
INSERT INTO `status_history` VALUES (6, 3, 26, 1, '2026-02-19 17:44:04');
INSERT INTO `status_history` VALUES (7, 4, 22, 1, '2026-02-19 17:44:04');
INSERT INTO `status_history` VALUES (8, 4, 24, 1, '2026-02-20 17:44:04');
INSERT INTO `status_history` VALUES (9, 4, 25, 1, '2026-02-21 17:44:04');
INSERT INTO `status_history` VALUES (10, 5, 50, 1, '2026-02-20 17:44:04');
INSERT INTO `status_history` VALUES (11, 5, 55, 1, '2026-02-21 17:44:04');
INSERT INTO `status_history` VALUES (12, 10, 0, 0, '2026-02-18 17:44:04');
INSERT INTO `status_history` VALUES (13, 10, 70, 1, '2026-02-18 17:44:04');
INSERT INTO `status_history` VALUES (14, 9, 100, 0, '2026-02-22 01:32:47');
INSERT INTO `status_history` VALUES (15, 9, 100, 1, '2026-02-22 01:32:47');
INSERT INTO `status_history` VALUES (16, 5, 19, 1, '2026-02-22 01:32:49');
INSERT INTO `status_history` VALUES (17, 5, 24, 1, '2026-02-22 01:32:50');
INSERT INTO `status_history` VALUES (18, 1, 80, 1, '2026-02-22 01:33:33');
INSERT INTO `status_history` VALUES (19, 1, 80, 1, '2026-02-22 01:33:33');
INSERT INTO `status_history` VALUES (20, 3, 26, 1, '2026-02-22 01:33:33');
INSERT INTO `status_history` VALUES (21, 3, 26, 1, '2026-02-22 01:33:33');
INSERT INTO `status_history` VALUES (22, 1, 80, 0, '2026-02-22 01:33:35');
INSERT INTO `status_history` VALUES (23, 2, 50, 0, '2026-02-22 01:33:35');
INSERT INTO `status_history` VALUES (24, 3, 26, 0, '2026-02-22 01:33:35');
INSERT INTO `status_history` VALUES (25, 6, 0, 0, '2026-02-22 01:33:35');
INSERT INTO `status_history` VALUES (26, 4, 25, 0, '2026-02-22 01:33:36');
INSERT INTO `status_history` VALUES (27, 8, 5, 0, '2026-02-22 01:33:36');
INSERT INTO `status_history` VALUES (28, 9, 100, 0, '2026-02-22 01:33:36');
INSERT INTO `status_history` VALUES (29, 7, 0, 0, '2026-02-22 01:33:36');
INSERT INTO `status_history` VALUES (30, 5, 24, 0, '2026-02-22 01:33:36');
INSERT INTO `status_history` VALUES (31, 1, 80, 1, '2026-02-24 19:51:00');
INSERT INTO `status_history` VALUES (32, 1, 80, 1, '2026-02-24 19:51:00');
INSERT INTO `status_history` VALUES (33, 3, 26, 1, '2026-02-24 19:51:00');
INSERT INTO `status_history` VALUES (34, 3, 26, 1, '2026-02-24 19:51:00');
INSERT INTO `status_history` VALUES (36, 8, 5, 0, '2026-02-24 19:51:01');
INSERT INTO `status_history` VALUES (37, 9, 100, 0, '2026-02-24 19:51:01');
INSERT INTO `status_history` VALUES (39, 1, 80, 1, '2026-02-24 19:51:02');
INSERT INTO `status_history` VALUES (40, 5, 24, 1, '2026-02-24 19:51:03');
INSERT INTO `status_history` VALUES (41, 2, 50, 1, '2026-02-24 19:51:03');
INSERT INTO `status_history` VALUES (42, 7, 0, 1, '2026-02-24 19:51:03');
INSERT INTO `status_history` VALUES (43, 6, 0, 1, '2026-02-24 19:51:03');
INSERT INTO `status_history` VALUES (44, 4, 25, 1, '2026-02-24 19:51:03');
INSERT INTO `status_history` VALUES (45, 8, 5, 1, '2026-02-24 19:51:03');
INSERT INTO `status_history` VALUES (46, 9, 100, 1, '2026-02-24 19:51:03');

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'USER',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `enabled` tinyint(1) NULL DEFAULT 1,
  `created_at` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (1, 'admin', 'admin123', 'ADMIN', 'admin@smarthome.com', 1, '2026-02-21 17:44:04');
INSERT INTO `users` VALUES (2, 'user1', '123456', 'USER', 'user1@example.com', 1, '2026-02-21 17:44:04');
INSERT INTO `users` VALUES (3, 'user2', '123456', 'USER', 'user2@example.com', 1, '2026-02-21 17:44:04');

-- ----------------------------
-- View structure for v_device_command_frequency
-- ----------------------------
DROP VIEW IF EXISTS `v_device_command_frequency`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `v_device_command_frequency` AS select `d`.`id` AS `device_id`,`d`.`name` AS `device_name`,`d`.`user_id` AS `user_id`,count(`c`.`id`) AS `command_count`,max(`c`.`executed_at`) AS `last_command_time` from (`devices` `d` left join `commands` `c` on(((`d`.`id` = `c`.`device_id`) and (`c`.`executed_at` >= (now() - interval 30 day))))) group by `d`.`id`,`d`.`name`,`d`.`user_id`;

-- ----------------------------
-- View structure for v_monthly_alert_stats
-- ----------------------------
DROP VIEW IF EXISTS `v_monthly_alert_stats`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `v_monthly_alert_stats` AS select year(`alerts`.`created_at`) AS `year`,month(`alerts`.`created_at`) AS `month`,count(0) AS `alert_count`,`alerts`.`alert_type` AS `alert_type`,`alerts`.`user_id` AS `user_id` from `alerts` group by year(`alerts`.`created_at`),month(`alerts`.`created_at`),`alerts`.`alert_type`,`alerts`.`user_id`;

-- ----------------------------
-- View structure for v_user_device_stats
-- ----------------------------
DROP VIEW IF EXISTS `v_user_device_stats`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `v_user_device_stats` AS select `u`.`id` AS `user_id`,`u`.`username` AS `username`,count(`d`.`id`) AS `total_devices`,sum((case when (`d`.`status` = 'online') then 1 else 0 end)) AS `online_devices` from (`users` `u` left join `devices` `d` on((`u`.`id` = `d`.`user_id`))) group by `u`.`id`,`u`.`username`;

SET FOREIGN_KEY_CHECKS = 1;
