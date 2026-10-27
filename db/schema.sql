-- 高校社团管理系统 数据库结构
-- ---------------------------------------------------------------------------
-- 导出时间：2026-10-27
-- 来源：作者开发库 `club`（MySQL 8.0）的 mysqldump --no-data
-- 说明：原仓库没有任何 SQL 脚本，新环境无法复现表结构，本文件用于补齐。
--
-- 使用方式：
--   mysql -u root -p < db/schema.sql
--
-- 表关系（逻辑外键，库里没有建外键约束）：
--   team.category_id      -> team_category.id
--   news.create_id        -> sys_user.user_id
--   member.team_id        -> team.id
--   member.user_id        -> sys_user.user_id
-- ---------------------------------------------------------------------------

CREATE DATABASE IF NOT EXISTS `club`
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_0900_ai_ci;

USE `club`;

-- 后台用户 / 前台用户，同一张表，is_admin = 1 表示可登录管理后台
-- 注意：当前版本密码是明文存储，登录时直接字符串比较（见 UserController#login）
CREATE TABLE IF NOT EXISTS `sys_user` (
  `user_id`   bigint       NOT NULL AUTO_INCREMENT,
  `username`  varchar(50)  NOT NULL COMMENT '登录账号',
  `password`  varchar(100) NOT NULL COMMENT 'BCrypt 哈希；历史明文密码会在登录成功时自动升级',
  `nick_name` varchar(50)  DEFAULT NULL COMMENT '姓名',
  `sex`       varchar(10)  DEFAULT NULL COMMENT '0-男 1-女',
  `phone`     varchar(20)  DEFAULT NULL,
  `status`    int          DEFAULT '1' COMMENT '0-禁用 1-启用',
  `is_admin`  int          DEFAULT '0' COMMENT '0-普通用户 1-管理员',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `uk_sys_user_username` (`username`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  COMMENT = '用户';

-- 社团分类，order_num 控制下拉框/列表顺序
CREATE TABLE IF NOT EXISTS `team_category` (
  `id`        bigint      NOT NULL AUTO_INCREMENT,
  `name`      varchar(100) DEFAULT NULL,
  `order_num` int          DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  COMMENT = '社团分类';

-- 社团，has_top = '1' 时出现在门户首页"推荐社团"
CREATE TABLE IF NOT EXISTS `team` (
  `id`          bigint       NOT NULL AUTO_INCREMENT,
  `category_id` bigint       DEFAULT NULL,
  `name`        varchar(100) DEFAULT NULL,
  `image`       varchar(500) DEFAULT NULL COMMENT '封面图，存 /images/xxx.jpg',
  `details`     text,
  `create_time` datetime     DEFAULT NULL,
  `has_top`     varchar(10)  DEFAULT '0' COMMENT '0-否 1-是',
  PRIMARY KEY (`id`),
  KEY `idx_team_category` (`category_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  COMMENT = '社团';

-- 活动 / 新闻 / 公告共用一张表，用 type 区分：
--   '0' 活动   '1' 新闻   '2' 公告
CREATE TABLE IF NOT EXISTS `news` (
  `id`          bigint       NOT NULL AUTO_INCREMENT,
  `type`        varchar(10)  DEFAULT NULL COMMENT '0-活动 1-新闻 2-公告',
  `create_id`   bigint       DEFAULT NULL COMMENT '发布人 sys_user.user_id',
  `image`       varchar(500) DEFAULT NULL,
  `title`       varchar(200) DEFAULT NULL,
  `details`     text,
  `has_top`     varchar(10)  DEFAULT '0' COMMENT '0-否 1-是',
  `create_time` datetime     DEFAULT NULL,
  `status`      tinyint      NOT NULL DEFAULT 1 COMMENT '0-草稿 1-已发布',
  `view_count`  int          NOT NULL DEFAULT 0 COMMENT '浏览量',
  PRIMARY KEY (`id`),
  KEY `idx_news_type_top` (`type`, `has_top`),
  KEY `idx_news_create_time` (`create_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  COMMENT = '活动/新闻/公告';

-- 社团成员。退出社团是逻辑删除：status 置 0，记录保留；
-- 因此同一个 (team_id, user_id) 允许多行（退出再加入会新增一行），不要建唯一索引。
CREATE TABLE IF NOT EXISTS `member` (
  `id`        bigint   NOT NULL AUTO_INCREMENT,
  `team_id`   bigint   DEFAULT NULL,
  `user_id`   bigint   DEFAULT NULL,
  `role`      int      DEFAULT '0' COMMENT '0-普通成员 1-管理员 2-社长',
  `status`    int      DEFAULT '1' COMMENT '0-已退出 1-正常',
  `join_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_member_team` (`team_id`, `status`),
  KEY `idx_member_user` (`user_id`, `status`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  COMMENT = '社团成员';

-- 活动报名：同一用户对同一活动只留一行，取消报名把 status 置 0
CREATE TABLE IF NOT EXISTS `activity_signup` (
  `id`          bigint  NOT NULL AUTO_INCREMENT,
  `news_id`     bigint  NOT NULL COMMENT '活动 id（news.type=0 的记录）',
  `user_id`     bigint  NOT NULL COMMENT '报名用户 sys_user.user_id',
  `status`      tinyint NOT NULL DEFAULT 1 COMMENT '1-已报名 0-已取消',
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_activity_signup` (`news_id`, `user_id`),
  KEY `idx_activity_signup_user` (`user_id`, `status`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  COMMENT = '活动报名';

-- 初始分类（可删）
INSERT INTO `team_category` (`name`, `order_num`)
SELECT * FROM (SELECT '体育竞技' AS n, 1 AS o
               UNION ALL SELECT '文艺爱好', 2
               UNION ALL SELECT '学术科技', 3
               UNION ALL SELECT '公益实践', 4
               UNION ALL SELECT '语言文化', 5) AS seed
WHERE NOT EXISTS (SELECT 1 FROM `team_category`);

-- 初始管理员：请自行执行下面这条，并把密码换成你自己的。
-- 这里写明文也能用：后端首次登录成功后会把它自动改写为 BCrypt 哈希。
-- INSERT INTO `sys_user` (`username`, `password`, `nick_name`, `sex`, `phone`, `status`, `is_admin`)
-- VALUES ('admin', '123456', '系统管理员', '0', '', 1, 1);
