-- 添加反馈表的新字段
-- 运行此脚本更新数据库结构

ALTER TABLE `feedback`
ADD COLUMN `type` varchar(50) NULL DEFAULT NULL COMMENT '问题类型' AFTER `user_id`,
ADD COLUMN `contact` varchar(255) NULL DEFAULT NULL COMMENT '联系方式' AFTER `type`,
ADD COLUMN `images` text NULL DEFAULT NULL COMMENT '截图，多张用逗号分隔' AFTER `content`;

-- 验证字段是否添加成功
-- SELECT id, user_id, type, contact, content, images, reply, status, reply_time, create_time FROM feedback;
