-- 迁移：站内通知
-- ---------------------------------------------------------------------------
-- 报名、社团申请审核结果、新成员加入等事件写一条通知，门户顶部铃铛能看到。
-- ---------------------------------------------------------------------------

USE `club`;

CREATE TABLE IF NOT EXISTS `notification` (
  `id`          bigint      NOT NULL AUTO_INCREMENT,
  `user_id`     bigint      NOT NULL COMMENT '接收人',
  `type`        varchar(20) NOT NULL DEFAULT 'system' COMMENT 'signup/apply/member/system',
  `title`       varchar(100) NOT NULL,
  `content`     varchar(500) DEFAULT NULL,
  `link`        varchar(200) DEFAULT NULL COMMENT '前端路由',
  `is_read`     tinyint     NOT NULL DEFAULT 0,
  `create_time` datetime    DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_notification_user` (`user_id`, `is_read`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  COMMENT = '站内通知';
