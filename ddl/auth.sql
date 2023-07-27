create database `xxxx_new` default character set utf8mb4 collate utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `oauth_client_details`;
create table `oauth_client_details` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    `client_id` VARCHAR(255) UNIQUE NOT NULL COMMENT '客户端id，用于唯一标识每一个客户端(client)',
    `resource_ids` VARCHAR(255) COMMENT '客户端所能访问的资源id集合,多个资源时用逗号分隔',
    `client_secret` VARCHAR(255) COMMENT '用于指定客户端(client)的访问密钥',
    `scope` VARCHAR(255) COMMENT '指定客户端申请的权限范围,可选值包括read,write;若有多个权限范围用逗号分隔',
    `authorized_grant_types` VARCHAR(255) COMMENT '指定客户端支持的grant_type,可选值包括authorization_code,password,refresh_token,implicit,client_credentials, 若支持多个grant_type用逗号(,)分隔',
    `redirect_uri` VARCHAR(255) COMMENT '客户端的重定向URI,可为空',
    `role` VARCHAR(255) COMMENT '客户端拥有的角色,可用于第三方客户端访问API限制',
    `access_token_validity` INTEGER COMMENT '设定客户端的access_token的有效时间值(单位:秒),可选, 若不设定值则使用默认的有效时间值(60 * 60 * 12, 12小时).',
    `refresh_token_validity` INTEGER COMMENT '设定客户端的refresh_token的有效时间值(单位:秒),可选, 若不设定值则使用默认的有效时间值(60 * 60 * 24 * 30, 30天).',
    `additional_information` TEXT COMMENT '附加信息，预留的字段没有实际的使用,可选,但若设置值,必须是JSON格式的数据',
    `create_at` bigint(13) NOT NULL COMMENT '创建时间戳',
    `create_by` bigint(20) NOT NULL COMMENT '创建人',
    `modify_at` bigint(13) NULL COMMENT '修改时间戳',
    `modify_by` bigint(20) NULL COMMENT '修改人',
    `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标记',
    `version` bigint(13) NOT NULL COMMENT '版本号，使用时间戳表示'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'oauth客户端信息表';


DROP TABLE IF EXISTS `oauth_access_token`;
create table `oauth_access_token` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    `token_id` CHAR(32) UNIQUE NOT NULL COMMENT '该字段具有唯一性, 是是将access_token的值通过MD5加密生成的',
    `token` TEXT NOT NULL COMMENT 'access_token内容',
    `user_name` VARCHAR(255) COMMENT '登录用户名',
    `client_id` VARCHAR(255) NOT NULL COMMENT '客户端id',
    `refresh_token` CHAR(32) COMMENT '该字段的值是将refresh_token的值通过MD5加密后存储的',
    `expires_in` int NOT NULL COMMENT '有效时长（s）',
    `create_at` bigint(13) NOT NULL COMMENT '创建时间戳',
    `create_by` bigint(20) NOT NULL COMMENT '创建人',
    `modify_at` bigint(13) NULL COMMENT '修改时间戳',
    `modify_by` bigint(20) NULL COMMENT '修改人',
    `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标记',
    `version` bigint(13) NOT NULL COMMENT '版本号，使用时间戳表示'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'oauth客户access_token表';

DROP TABLE IF EXISTS `oauth_refresh_token`;
create table `oauth_refresh_token` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    `token_id` CHAR(32) UNIQUE NOT NULL COMMENT '该字段具有唯一性, 是是将refresh_token的值通过MD5加密生成的',
    `token` TEXT NOT NULL COMMENT 'refresh_token内容',
    `expires_in` int NOT NULL COMMENT '有效时长（s）',
    `create_at` bigint(13) NOT NULL COMMENT '创建时间戳',
    `create_by` bigint(20) NOT NULL COMMENT '创建人',
    `modify_at` bigint(13) NULL COMMENT '修改时间戳',
    `modify_by` bigint(20) NULL COMMENT '修改人',
    `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标记',
    `version` bigint(13) NOT NULL COMMENT '版本号，使用时间戳表示'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'oauth客户refresh_token表';

DROP TABLE IF EXISTS `oauth_code`;
create table `oauth_code` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    `code` CHAR(6) NOT NULL COMMENT '授权码，有效期为5分钟',
    `apply_client_id` VARCHAR(255) NOT NULL COMMENT '申请客户端id',
    `redirect_uri` VARCHAR(255) NOT NULL COMMENT '客户端的重定向URI',
    `client_id` VARCHAR(255) NOT NULL COMMENT '授权的客户端id',
    `username` VARCHAR(255) NOT NULL COMMENT '授权的用户名',
    `scope` VARCHAR(255) COMMENT '授权的权限',
    `expires_in` int NULL DEFAULT 600 COMMENT '有效时长（s），默认为 10分钟',
    `create_at` bigint(13) NOT NULL COMMENT '创建时间戳',
    `create_by` bigint(20) NOT NULL COMMENT '创建人',
    `modify_at` bigint(13) NULL COMMENT '修改时间戳',
    `modify_by` bigint(20) NULL COMMENT '修改人',
    `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标记',
    `version` bigint(13) NOT NULL COMMENT '版本号，使用时间戳表示'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'oauth授权码表';

DROP TABLE IF EXISTS `auth_user`;
CREATE TABLE `auth_user`(
    `id` bigint(20) NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    `name` VARCHAR(255) NOT NULL COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '用户密码',
    `nickname` VARCHAR(200) COMMENT '用户昵称',
    `mobile` VARCHAR(20) COMMENT '用户手机',
    `last_login_time` bigint(13) NULL COMMENT '上次登录时间戳',
    `enabled` tinyint(1) NOT NULL DEFAULT 1 COMMENT '启用标记',
    `locked` tinyint(1) NOT NULL DEFAULT 0 COMMENT '锁定标记',
    `create_at` bigint(13) NOT NULL COMMENT '创建时间戳',
    `create_by` bigint(20) NOT NULL COMMENT '创建人',
    `modify_at` bigint(13) NULL COMMENT '修改时间戳',
    `modify_by` bigint(20) NULL COMMENT '修改人',
    `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标记',
    `version` bigint(13) NOT NULL COMMENT '版本号，使用时间戳表示'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户表';



INSERT INTO `oauth_client_details` VALUES (1, 'test_client', 'xxxx-demo', '123456', 'read,write,delete', 'password', 'https://www.baidu.com', NULL, 7200, 2592000, NULL, 1632724162669, 1, NULL, NULL, 0, 1632724162669);

INSERT INTO `auth_user` VALUES (1, 'zhangjw', '123456', '张俊伟', '13728614799', 1632972339479, 1, 0, 1632724162669, 1, 1632972339479, 1, 0, 1632972339479);




DROP TABLE IF EXISTS `auth_web_resource`;
CREATE TABLE `auth_web_resource`(
    `id` bigint(20) NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    `name` VARCHAR(64) NOT NULL COMMENT '资源名称',
    `display_name` VARCHAR(50) NOT NULL COMMENT '资源显示名称',
    `code` VARCHAR(100) NOT NULL COMMENT '资源编码',
    `uri` VARCHAR(255) COMMENT '访问uri',
    `type` VARCHAR(16) NOT NULL COMMENT '资源类型，MODULE-模块，MENU-菜单，BUTTON-按钮，ELEMENT-元素',
    `parent_id` bigint(20) DEFAULT 0 COMMENT '父资源id',
    `path` VARCHAR(255) COMMENT '资源路径，记录上溯至根资源id的访问路径',
    `order_num` int DEFAULT 1 COMMENT '序号，用于同级资源排序',
    `enabled` tinyint(1) NOT NULL DEFAULT 1 COMMENT '启用标记',
    `create_at` bigint(13) NOT NULL COMMENT '创建时间戳',
    `create_by` bigint(20) NOT NULL COMMENT '创建人',
    `modify_at` bigint(13) NULL COMMENT '修改时间戳',
    `modify_by` bigint(20) NULL COMMENT '修改人',
    `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标记',
    `version` bigint(13) NOT NULL COMMENT '版本号，使用时间戳表示'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'web资源表';

DROP TABLE IF EXISTS `auth_role`;
CREATE TABLE `auth_role`(
    `id` bigint(20) NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    `name` VARCHAR(64) NOT NULL COMMENT '角色名称',
    `code` VARCHAR(100) NOT NULL COMMENT '角色编码',
    `description` VARCHAR(255) COMMENT '描述',
    `enabled` tinyint(1) NOT NULL DEFAULT 1 COMMENT '启用标记',
    `create_at` bigint(13) NOT NULL COMMENT '创建时间戳',
    `create_by` bigint(20) NOT NULL COMMENT '创建人',
    `modify_at` bigint(13) NULL COMMENT '修改时间戳',
    `modify_by` bigint(20) NULL COMMENT '修改人',
    `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标记',
    `version` bigint(13) NOT NULL COMMENT '版本号，使用时间戳表示'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色表';

DROP TABLE IF EXISTS `auth_role_web_resource_relation`;
CREATE TABLE `auth_role_web_resource_relation`(
    `id` bigint(20) NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    `role_id` bigint(20) NOT NULL COMMENT '角色id',
    `resource_id` bigint(20) NOT NULL COMMENT 'web资源id',
    `create_at` bigint(13) NOT NULL COMMENT '创建时间戳',
    `create_by` bigint(20) NOT NULL COMMENT '创建人',
    `modify_at` bigint(13) NULL COMMENT '修改时间戳',
    `modify_by` bigint(20) NULL COMMENT '修改人',
    `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标记',
    `version` bigint(13) NOT NULL COMMENT '版本号，使用时间戳表示',
    unique key unique_rela(role_id, resource_id)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色和web资源关系表';

DROP TABLE IF EXISTS `auth_user_role_relation`;
CREATE TABLE `auth_user_role_relation`(
    `id` bigint(20) NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    `user_id` bigint(20) NOT NULL COMMENT '用户id',
    `role_id` bigint(20) NOT NULL COMMENT '角色id',
    `create_at` bigint(13) NOT NULL COMMENT '创建时间戳',
    `create_by` bigint(20) NOT NULL COMMENT '创建人',
    `modify_at` bigint(13) NULL COMMENT '修改时间戳',
    `modify_by` bigint(20) NULL COMMENT '修改人',
    `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标记',
    `version` bigint(13) NOT NULL COMMENT '版本号，使用时间戳表示',
    unique key unique_rela(user_id, role_id)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户和角色关系表';

DROP TABLE IF EXISTS `auth_api_resource`;
CREATE TABLE `auth_api_resource`(
    `id` bigint(20) NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    `name` VARCHAR(64) NOT NULL COMMENT '资源名称',
    `service` VARCHAR(64) NOT NULL COMMENT '资源所属服务',
    `uri` VARCHAR(255) COMMENT '访问uri',
    `method` VARCHAR(10) NOT NULL COMMENT 'api请求方法，包括 GET,POST,DELETE等',
    `exclude` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否排除权限校验',
    `create_at` bigint(13) NOT NULL COMMENT '创建时间戳',
    `create_by` bigint(20) NOT NULL COMMENT '创建人',
    `modify_at` bigint(13) NULL COMMENT '修改时间戳',
    `modify_by` bigint(20) NULL COMMENT '修改人',
    `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标记',
    `version` bigint(13) NOT NULL COMMENT '版本号，使用时间戳表示'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'api资源表';

DROP TABLE IF EXISTS `auth_web_api_resource_relation`;
CREATE TABLE `auth_web_api_resource_relation`(
    `id` bigint(20) NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    `web_resource_id` bigint(20) NOT NULL COMMENT 'web资源id',
    `api_resource_id` bigint(20) NOT NULL COMMENT 'api资源id',
    `create_at` bigint(13) NOT NULL COMMENT '创建时间戳',
    `create_by` bigint(20) NOT NULL COMMENT '创建人',
    `modify_at` bigint(13) NULL COMMENT '修改时间戳',
    `modify_by` bigint(20) NULL COMMENT '修改人',
    `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标记',
    `version` bigint(13) NOT NULL COMMENT '版本号，使用时间戳表示',
    unique key unique_rela(web_resource_id, api_resource_id)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'web和api资源关系表';

DROP TABLE IF EXISTS `auth_role_api_resource_relation`;
CREATE TABLE `auth_role_api_resource_relation`(
    `id` bigint(20) NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    `role_id` bigint(20) NOT NULL COMMENT '角色id',
    `resource_id` bigint(20) NOT NULL COMMENT 'api资源id',
    `create_at` bigint(13) NOT NULL COMMENT '创建时间戳',
    `create_by` bigint(20) NOT NULL COMMENT '创建人',
    `modify_at` bigint(13) NULL COMMENT '修改时间戳',
    `modify_by` bigint(20) NULL COMMENT '修改人',
    `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标记',
    `version` bigint(13) NOT NULL COMMENT '版本号，使用时间戳表示',
    unique key unique_rela(role_id, resource_id)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色和api资源关系表';