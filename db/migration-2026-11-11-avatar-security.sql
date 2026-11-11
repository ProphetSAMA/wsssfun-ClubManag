-- 迁移：用户头像与密保问题
-- ---------------------------------------------------------------------------
-- avatar：头像路径（复用上传目录）
-- security_question / security_answer_hash：忘记密码时用于自助找回，答案用 BCrypt 存
-- ---------------------------------------------------------------------------

USE `club`;

ALTER TABLE `sys_user`
  ADD COLUMN `avatar` varchar(255) DEFAULT NULL COMMENT '头像相对路径',
  ADD COLUMN `security_question` varchar(100) DEFAULT NULL COMMENT '密保问题',
  ADD COLUMN `security_answer_hash` varchar(100) DEFAULT NULL COMMENT '密保答案（BCrypt）';
