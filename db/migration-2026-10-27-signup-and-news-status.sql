-- 迁移脚本：活动报名 + 内容发布状态/浏览量
-- ---------------------------------------------------------------------------
-- 对应功能：
--   1. 活动报名（activity_signup）
--   2. 内容草稿/发布状态与浏览量（news.status / news.view_count）
--
-- 只做加法，不动已有数据：
--   - 新列都有默认值，历史内容自动视为「已发布」、浏览量为 0
--   - 新表不影响现有查询
--
-- 执行：
--   mysql -u root -p club < db/migration-2026-10-27-signup-and-news-status.sql
-- ---------------------------------------------------------------------------

USE `club`;

-- 1. 内容状态 + 浏览量
ALTER TABLE `news`
  ADD COLUMN `status` tinyint NOT NULL DEFAULT 1 COMMENT '0-草稿 1-已发布',
  ADD COLUMN `view_count` int NOT NULL DEFAULT 0 COMMENT '浏览量';

-- 2. 活动报名
-- 同一个用户对同一个活动只保留一行：取消报名把 status 置 0，重新报名再置回 1
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
