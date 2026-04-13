-- 将反馈表的 images 字段改名为 cover_img，与资讯表保持一致
ALTER TABLE `feedback` CHANGE COLUMN `images` `cover_img` text NULL DEFAULT NULL COMMENT '截图，多张用逗号分隔';

-- 验证字段是否修改成功
-- SELECT id, user_id, type, contact, content, cover_img, reply, status, reply_time, create_time FROM feedback;