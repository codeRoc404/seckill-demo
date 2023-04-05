/*
 Navicat Premium Data Transfer

 Source Server         : M1
 Source Server Type    : MySQL
 Source Server Version : 80031
 Source Host           : localhost:3306
 Source Schema         : seckill

 Target Server Type    : MySQL
 Target Server Version : 80031
 File Encoding         : 65001

 Date: 05/04/2023 20:50:34
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for permission
-- ----------------------------
DROP TABLE IF EXISTS `permission`;
CREATE TABLE `permission`  (
  `id` int NOT NULL DEFAULT 0 COMMENT '自定id,主要供前端展示权限列表分类排序使用.',
  `menu_code` varchar(255) NULL DEFAULT '' COMMENT '归属菜单,前端判断并展示菜单使用,',
  `menu_name` varchar(255) NULL DEFAULT '' COMMENT '菜单的中文释义',
  `permission_code` varchar(255) NULL DEFAULT '' COMMENT '权限的代码/通配符,对应代码中@RequiresPermissions 的value',
  `permission_name` varchar(255) NULL DEFAULT '' COMMENT '本权限的中文释义',
  `required_permission` tinyint(1) NULL DEFAULT 2 COMMENT '是否本菜单必选权限, 1.必选 2非必选 通常是\"列表\"权限是必选',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '后台权限表';

-- ----------------------------
-- Records of permission
-- ----------------------------
INSERT INTO `permission` VALUES (101, 'user', '用户', 'user:list', '列表', 2);
INSERT INTO `permission` VALUES (102, 'user', '用户', 'user:add', '新增', 2);
INSERT INTO `permission` VALUES (201, 'role', '角色管理', 'role:list', '列表', 2);
INSERT INTO `permission` VALUES (202, 'role', '角色管理', 'role:add', '新增', 2);
INSERT INTO `permission` VALUES (301, 'commodity', '商品管理', 'com:purchase', '购买', 2);
INSERT INTO `permission` VALUES (302, 'commodity', '商品管理', 'com:list', '列表', 2);

-- ----------------------------
-- Table structure for role
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `role_name` varchar(20) NULL DEFAULT NULL COMMENT '角色名',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `delete_stauts` varchar(1) NULL DEFAULT '1' COMMENT '是否有效 1有效 2无效',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '后台角色表';

-- ----------------------------
-- Records of role
-- ----------------------------
INSERT INTO `role` VALUES (1, 'admin', '2023-03-27 19:43:48', '2023-03-28 18:57:54', '1');
INSERT INTO `role` VALUES (2, 'nomuser', '2023-03-27 19:43:59', '2023-03-28 18:58:14', '1');

-- ----------------------------
-- Table structure for role_permission
-- ----------------------------
DROP TABLE IF EXISTS `role_permission`;
CREATE TABLE `role_permission`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `role_id` int DEFAULT NULL COMMENT '角色id',
  `permission_id` int DEFAULT NULL COMMENT '权限id',
  `create_time` timestamp DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `delete_status` varchar(1) NULL DEFAULT '1' COMMENT '是否有效 1有效     2无效',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色-权限关联表';

-- ----------------------------
-- Records of role_permission
-- ----------------------------
INSERT INTO `role_permission` VALUES (1, 1, 101, '2023-03-27 19:49:07', '2023-03-27 19:49:07', '1');
INSERT INTO `role_permission` VALUES (2, 1, 102, '2023-03-27 19:49:13', '2023-03-27 19:49:13', '1');
INSERT INTO `role_permission` VALUES (3, 1, 201, '2023-03-27 19:49:19', '2023-03-27 19:49:19', '1');
INSERT INTO `role_permission` VALUES (4, 1, 202, '2023-03-27 19:49:24', '2023-03-27 19:49:24', '1');
INSERT INTO `role_permission` VALUES (5, 2, 101, '2023-03-27 19:49:35', '2023-03-27 19:49:35', '1');
INSERT INTO `role_permission` VALUES (6, 2, 102, '2023-03-27 19:49:40', '2023-03-27 19:49:40', '1');
INSERT INTO `role_permission` VALUES (7, 1, 301, '2023-03-28 19:09:18', '2023-03-28 19:09:18', '1');
INSERT INTO `role_permission` VALUES (8, 1, 302, '2023-03-28 19:09:26', '2023-03-28 19:09:26', '1');
INSERT INTO `role_permission` VALUES (9, 2, 301, '2023-03-28 19:09:38', '2023-03-28 19:09:38', '1');

-- ----------------------------
-- Table structure for t_goods
-- ----------------------------
DROP TABLE IF EXISTS `t_goods`;
CREATE TABLE `t_goods`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '商品Id',
  `goods_name` varchar(16) NULL DEFAULT NULL COMMENT '商品名称',
  `goods_title` varchar(64) NULL DEFAULT NULL COMMENT '商品标题',
  `goods_img` varchar(128) NULL DEFAULT NULL COMMENT '商品图片',
  `goods_detail` longtext NULL COMMENT '商品详情',
  `goods_price` decimal(10, 2) DEFAULT 0.00 COMMENT '商品价格',
  `goods_stock` int DEFAULT 0 COMMENT '商品库存，-1表示没有限制',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci;

-- ----------------------------
-- Records of t_goods
-- ----------------------------
INSERT INTO `t_goods` VALUES (1, 'iphone 14 pro', 'Apple手机', 'http://localhost:8001/img/iphone14.jpg', '64g', 5000.00, 100);
INSERT INTO `t_goods` VALUES (2, '小米13', 'XiaoMi手机', 'http://localhost:8001/img/xiaomi13.jpg', '64g', 4599.00, 100);

-- ----------------------------
-- Table structure for t_order
-- ----------------------------
DROP TABLE IF EXISTS `t_order`;
CREATE TABLE `t_order`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单Id',
  `user_id` bigint DEFAULT NULL COMMENT '用户Id',
  `goods_id` bigint DEFAULT NULL COMMENT '商品Id',
  `delivery_addr_id` bigint DEFAULT NULL COMMENT '收货地址Id',
  `goods_name` varchar(16) DEFAULT NULL COMMENT '商品名称',
  `goods_count` int DEFAULT 0 COMMENT '商品数量',
  `goods_price` decimal(10, 2) DEFAULT 0.00 COMMENT '商品单价',
  `order_channel` tinyint DEFAULT 0 COMMENT '1:pc端，2：手机端',
  `status` tinyint DEFAULT 0 COMMENT '订单状态，0：新建未支付，1：已支付，2：已发货，3：已收货，4：已退款，5：已完成',
  `create_date` datetime DEFAULT NULL COMMENT '订单创建时间',
  `pay_date` datetime DEFAULT NULL COMMENT '订单支付时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 116143 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci;

-- ----------------------------
-- Records of t_order
-- ----------------------------

-- ----------------------------
-- Table structure for t_seckill_goods
-- ----------------------------
DROP TABLE IF EXISTS `t_seckill_goods`;
CREATE TABLE `t_seckill_goods`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '秒杀商品Id',
  `goods_id` bigint DEFAULT NULL COMMENT '商品Id',
  `seckill_price` decimal(10, 2) DEFAULT 0.00 COMMENT '秒杀价',
  `stock_count` int DEFAULT NULL COMMENT '库存数量',
  `start_date` datetime DEFAULT NULL COMMENT '秒杀开始时间',
  `end_date` datetime DEFAULT NULL COMMENT '秒杀结束时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci;

-- ----------------------------
-- Records of t_seckill_goods
-- ----------------------------
INSERT INTO `t_seckill_goods` VALUES (1, 1, 4500.00, 9995, '2023-04-01 17:02:00', '2023-04-06 17:05:00');
INSERT INTO `t_seckill_goods` VALUES (2, 2, 4099.00, 4997, '2023-04-05 20:53:15', '2023-04-22 20:53:18');

-- ----------------------------
-- Table structure for t_seckill_order
-- ----------------------------
DROP TABLE IF EXISTS `t_seckill_order`;
CREATE TABLE `t_seckill_order`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '秒杀订单Id',
  `user_id` bigint DEFAULT NULL COMMENT '用户Id',
  `order_id` bigint DEFAULT NULL COMMENT '订单Id',
  `goods_id` bigint DEFAULT NULL COMMENT '商品Id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 187496 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci;

-- ----------------------------
-- Records of t_seckill_order
-- ----------------------------

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user`  (
  `id` bigint NOT NULL COMMENT '用户id，手机号码',
  `nickname` varchar(255) NOT NULL,
  `password` varchar(32) NULL DEFAULT NULL COMMENT 'MD5(MD5(password明文+固定salt)+salt)',
  `salt` varchar(10) NULL DEFAULT NULL,
  `head` varchar(128) NULL DEFAULT NULL COMMENT '头像',
  `register_date` datetime DEFAULT NULL COMMENT '注册时间',
  `last_login_date` datetime DEFAULT NULL COMMENT '最后一次登录时间',
  `login_count` int DEFAULT 0 COMMENT '登录次数',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci;

-- ----------------------------
-- Records of t_user
-- ----------------------------

INSERT INTO `t_user` VALUES (15139769049, '莫嘉伦', '8e8bd4a115b9896cc27e8847ab611724', 'zxcvbn', NULL, NULL, NULL, 0);

-- ----------------------------
-- Table structure for user_role
-- ----------------------------
DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户id',
  `role_id` int DEFAULT NULL COMMENT '角色id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户-角色关联表';

-- ----------------------------
-- Records of user_role
-- ----------------------------
INSERT INTO `user_role` VALUES (1, 15139769049, 1);
INSERT INTO `user_role` VALUES (2, 18012099705, 2);
INSERT INTO `user_role` VALUES (3, 15139769049, 2);

SET FOREIGN_KEY_CHECKS = 1;
