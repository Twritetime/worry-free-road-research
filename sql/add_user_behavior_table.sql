-- 用户行为记录表（用于智能推荐）
DROP TABLE IF EXISTS `yl_user_behavior`;
CREATE TABLE `yl_user_behavior` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `behavior_type` int NOT NULL COMMENT '行为类型: 1-浏览, 2-收藏, 3-购买, 4-搜索, 5-评论',
  `target_type` int DEFAULT NULL COMMENT '目标类型: 1-资料, 2-帖子, 3-指南, 4-资讯',
  `target_id` bigint NOT NULL COMMENT '目标ID',
  `target_title` varchar(255) DEFAULT NULL COMMENT '目标标题',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint(1) DEFAULT 0,
  PRIMARY KEY (`id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_behavior_type` (`behavior_type`),
  INDEX `idx_target_id` (`target_id`),
  INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户行为记录表';
