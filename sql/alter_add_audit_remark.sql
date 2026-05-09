-- =============================================
-- 脚本说明：为帖子表添加审核备注字段
-- 适用场景：已存在 yl_post 表的数据库环境
-- 执行方式：在 MySQL 客户端或数据库管理工具中执行
-- =============================================

-- 为帖子表添加审核备注字段
ALTER TABLE `yl_post` 
ADD COLUMN `audit_remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审核备注（拒绝时填写原因）' AFTER `is_top`;

-- 更新现有待审核帖子的默认状态（可选）
-- UPDATE `yl_post` SET `status` = 0 WHERE `status` IS NULL;

-- 查看表结构确认修改
DESCRIBE `yl_post`;