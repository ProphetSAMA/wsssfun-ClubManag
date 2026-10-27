-- 迁移：社团成立申请
-- ---------------------------------------------------------------------------
-- 学生提交"我想成立一个社团"的申请，平台管理员审核；通过后自动创建社团，
-- 并把申请人加为该社团的社长（member.role = 2）。
-- ---------------------------------------------------------------------------

USE `club`;

CREATE TABLE IF NOT EXISTS `team_apply` (
  `id`            bigint       NOT NULL AUTO_INCREMENT,
  `name`          varchar(100) NOT NULL COMMENT '拟成立的社团名称',
  `category_id`   bigint       DEFAULT NULL COMMENT '所属分类',
  `reason`        varchar(500) DEFAULT NULL COMMENT '申请理由',
  `applicant_id`  bigint       NOT NULL COMMENT '申请人 sys_user.user_id',
  `status`        tinyint      NOT NULL DEFAULT 0 COMMENT '0-待审核 1-已通过 2-已驳回',
  `review_id`     bigint       DEFAULT NULL COMMENT '审核人',
  `review_time`   datetime     DEFAULT NULL,
  `reject_reason` varchar(200) DEFAULT NULL,
  `team_id`       bigint       DEFAULT NULL COMMENT '通过后创建的社团',
  `create_time`   datetime     DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_team_apply_status` (`status`),
  KEY `idx_team_apply_applicant` (`applicant_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  COMMENT = '社团成立申请';
