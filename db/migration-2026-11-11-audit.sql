-- 迁移：操作审计日志
-- ---------------------------------------------------------------------------
-- 记录所有写操作（POST/PUT/DELETE）以及登录相关的访问，用于事后追溯。
-- 只记方法、路径、用户、IP、耗时、状态码，不记请求体（避免把密码写进日志）。
-- ---------------------------------------------------------------------------

USE `club`;

CREATE TABLE IF NOT EXISTS `audit_log` (
  `id`          bigint       NOT NULL AUTO_INCREMENT,
  `user_id`     bigint       DEFAULT NULL,
  `username`    varchar(50)  DEFAULT NULL,
  `method`      varchar(10)  NOT NULL,
  `path`        varchar(200) NOT NULL,
  `ip`          varchar(64)  DEFAULT NULL,
  `status`      int          DEFAULT NULL COMMENT 'HTTP 状态码',
  `duration_ms` int          DEFAULT NULL,
  `create_time` datetime     DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_audit_time` (`create_time`),
  KEY `idx_audit_user` (`user_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  COMMENT = '操作审计日志';
