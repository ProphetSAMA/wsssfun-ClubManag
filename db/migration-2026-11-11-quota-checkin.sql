-- 迁移：活动名额与签到
-- ---------------------------------------------------------------------------
-- news.max_participants：活动名额，0 表示不限
-- activity_signup.checked_in / checkin_time：社团管理员核销签到
-- ---------------------------------------------------------------------------

USE `club`;

ALTER TABLE `news`
  ADD COLUMN `max_participants` int NOT NULL DEFAULT 0 COMMENT '活动名额，0 表示不限' AFTER `view_count`;

ALTER TABLE `activity_signup`
  ADD COLUMN `checked_in` tinyint NOT NULL DEFAULT 0 COMMENT '0-未签到 1-已签到',
  ADD COLUMN `checkin_time` datetime DEFAULT NULL;
