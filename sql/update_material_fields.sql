-- 为资料表添加新字段
ALTER TABLE `yl_material` 
ADD COLUMN `file_size` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文件大小 (如 15.2MB, 2.3GB)' AFTER `sales`,
ADD COLUMN `download_count` int NULL DEFAULT 0 COMMENT '下载次数' AFTER `file_size`,
ADD COLUMN `tags` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标签 (逗号分隔，如 政治,英语,数学)' AFTER `download_count`,
ADD COLUMN `apply_year` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '适用年份 (如 2026, 2027)' AFTER `tags`,
ADD COLUMN `author` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '作者/上传者' AFTER `apply_year`,
ADD COLUMN `rating` double NULL DEFAULT 0 COMMENT '评分 (1-5分)' AFTER `author`,
ADD COLUMN `preview_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '预览内容 (目录或部分预览)' AFTER `rating`,
ADD COLUMN `file_format` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '资料格式 (PDF, 视频, 音频, 压缩包等)' AFTER `preview_content`;
