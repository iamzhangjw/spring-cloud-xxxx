SET NAMES utf8mb4;

create database `demo` default character set utf8mb4 collate utf8mb4_unicode_ci;
-- ----------------------------
-- Table structure for dict
-- ----------------------------
DROP TABLE IF EXISTS `dict`;
CREATE TABLE `dict`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'code',
  `value` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'value',
  `create_at` bigint(13) NOT NULL COMMENT '创建时间戳',
  `create_by` bigint(20) NOT NULL COMMENT '创建人',
  `modify_at` bigint(13) NULL DEFAULT NULL COMMENT '修改时间戳',
  `modify_by` bigint(20) NULL DEFAULT NULL COMMENT '修改人',
  `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标记',
  `version` bigint(13) NOT NULL COMMENT '版本号，使用时间戳表示',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字典表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of dict
-- ----------------------------
INSERT INTO `dict` VALUES (1, 'test1', '测试数据1', 1632290303571, NULL, 0, 1632290303571);
INSERT INTO `dict` VALUES (2, 'test2', '测试数据2', 1632290303571, NULL, 0, 1632290303571);


DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '姓名',
  `mobile` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '电话',
  `certification_no` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '证件号码',
  `email` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '邮箱地址',
  `create_at` bigint(13) NOT NULL COMMENT '创建时间戳',
  `create_by` bigint(20) NOT NULL COMMENT '创建人',
  `modify_at` bigint(13) NULL DEFAULT NULL COMMENT '修改时间戳',
  `modify_by` bigint(20) NULL DEFAULT NULL COMMENT '修改人',
  `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标记',
  `version` bigint(13) NOT NULL COMMENT '版本号，使用时间戳表示',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户表';