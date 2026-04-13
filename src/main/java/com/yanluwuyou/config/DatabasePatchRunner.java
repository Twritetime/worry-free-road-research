package com.yanluwuyou.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * 数据库补丁运行器
 * 用于自动修复数据库表结构差异
 */
@Component
public class DatabasePatchRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabasePatchRunner.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        logger.info("Checking database schema patches...");
        checkAndAddSalesColumn();
        checkAndAddMaterialFlashColumns();
        bootstrapFlashCampaignData();
        checkAndAddGuideColumns();
        checkAndAddCommentColumns();
        checkAndAddCountColumns();
        checkAndAddSortOrderColumns();
        checkAndAddUserColumns();
        fixFavoriteTimestampData();
        logger.info("Database schema check completed.");
    }

    private void checkAndAddSortOrderColumns() {
        checkAndAddSortOrderColumn("yl_material");
        checkAndAddSortOrderColumn("yl_news");
        checkAndAddSortOrderColumn("yl_guide");
    }

    private void checkAndAddSortOrderColumn(String tableName) {
        try {
            String checkSql = "SELECT count(*) FROM information_schema.columns " +
                    "WHERE table_schema = (SELECT DATABASE()) " +
                    "AND table_name = ? AND column_name = 'sort_order'";
            Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, tableName);
            if (count == null || count == 0) {
                jdbcTemplate.execute("ALTER TABLE " + tableName + " ADD COLUMN sort_order INT DEFAULT 0 COMMENT '排序值，越大越靠前'");
            }
            jdbcTemplate.execute("UPDATE " + tableName + " SET sort_order = id WHERE sort_order IS NULL OR sort_order = 0");
            logger.info("Column 'sort_order' checked for '{}'.", tableName);
        } catch (Exception e) {
            logger.error("Failed to patch sort_order for '{}': {}", tableName, e.getMessage());
        }
    }

    private void checkAndAddCountColumns() {
        try {
            // yl_guide comment_count
            String checkSql = "SELECT count(*) FROM information_schema.columns " +
                    "WHERE table_schema = (SELECT DATABASE()) " +
                    "AND table_name = 'yl_guide' AND column_name = 'comment_count'";
            Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class);
            if (count == null || count == 0) {
                jdbcTemplate.execute("ALTER TABLE yl_guide ADD COLUMN comment_count INT DEFAULT 0 COMMENT '评论数'");
                logger.info("Column 'comment_count' added to 'yl_guide'.");
            }

            // yl_news comment_count
            checkSql = "SELECT count(*) FROM information_schema.columns " +
                    "WHERE table_schema = (SELECT DATABASE()) " +
                    "AND table_name = 'yl_news' AND column_name = 'comment_count'";
            count = jdbcTemplate.queryForObject(checkSql, Integer.class);
            if (count == null || count == 0) {
                jdbcTemplate.execute("ALTER TABLE yl_news ADD COLUMN comment_count INT DEFAULT 0 COMMENT '评论数'");
                logger.info("Column 'comment_count' added to 'yl_news'.");
            }
        } catch (Exception e) {
            logger.error("Failed to patch count columns: {}", e.getMessage());
        }
    }

    private void checkAndAddSalesColumn() {
        try {
            // Check if 'sales' column exists in 'yl_material' table
            String checkSql = "SELECT count(*) FROM information_schema.columns " +
                    "WHERE table_schema = (SELECT DATABASE()) " +
                    "AND table_name = 'yl_material' AND column_name = 'sales'";
            
            Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class);
            
            if (count == null || count == 0) {
                logger.info("Column 'sales' not found in table 'yl_material'. Adding it...");
                String alterSql = "ALTER TABLE yl_material ADD COLUMN sales INT DEFAULT 0 COMMENT '销量'";
                jdbcTemplate.execute(alterSql);
                logger.info("Column 'sales' added successfully.");
            } else {
                logger.debug("Column 'sales' already exists in table 'yl_material'.");
            }
        } catch (Exception e) {
            logger.error("Failed to check or add 'sales' column: {}", e.getMessage());
        }
    }

    private void checkAndAddMaterialFlashColumns() {
        try {
            String checkSql = "SELECT count(*) FROM information_schema.columns " +
                    "WHERE table_schema = (SELECT DATABASE()) " +
                    "AND table_name = 'yl_material' AND column_name = 'original_price'";
            Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class);
            if (count == null || count == 0) {
                jdbcTemplate.execute("ALTER TABLE yl_material ADD COLUMN original_price DECIMAL(10, 2) NULL COMMENT '划线价'");
                jdbcTemplate.execute("UPDATE yl_material SET original_price = price WHERE original_price IS NULL");
                logger.info("Column 'original_price' added to 'yl_material'.");
            }

            checkSql = "SELECT count(*) FROM information_schema.columns " +
                    "WHERE table_schema = (SELECT DATABASE()) " +
                    "AND table_name = 'yl_material' AND column_name = 'flash_start_time'";
            count = jdbcTemplate.queryForObject(checkSql, Integer.class);
            if (count == null || count == 0) {
                jdbcTemplate.execute("ALTER TABLE yl_material ADD COLUMN flash_start_time DATETIME NULL COMMENT '活动开始时间'");
                logger.info("Column 'flash_start_time' added to 'yl_material'.");
            }

            checkSql = "SELECT count(*) FROM information_schema.columns " +
                    "WHERE table_schema = (SELECT DATABASE()) " +
                    "AND table_name = 'yl_material' AND column_name = 'flash_end_time'";
            count = jdbcTemplate.queryForObject(checkSql, Integer.class);
            if (count == null || count == 0) {
                jdbcTemplate.execute("ALTER TABLE yl_material ADD COLUMN flash_end_time DATETIME NULL COMMENT '活动结束时间'");
                logger.info("Column 'flash_end_time' added to 'yl_material'.");
            }
        } catch (Exception e) {
            logger.error("Failed to patch yl_material flash columns: {}", e.getMessage());
        }
    }

    private void bootstrapFlashCampaignData() {
        try {
            String countSql = "SELECT count(*) FROM yl_material " +
                    "WHERE status = 1 AND stock > 0 " +
                    "AND original_price IS NOT NULL AND original_price > price " +
                    "AND flash_start_time IS NOT NULL AND flash_end_time IS NOT NULL " +
                    "AND flash_start_time <= NOW() AND flash_end_time >= NOW()";
            Integer activeCount = jdbcTemplate.queryForObject(countSql, Integer.class);
            if (activeCount != null && activeCount > 0) {
                return;
            }
            String bootstrapSql = "UPDATE yl_material " +
                    "SET original_price = ROUND(price * 1.25, 2), " +
                    "flash_start_time = DATE_SUB(NOW(), INTERVAL 1 DAY), " +
                    "flash_end_time = DATE_ADD(NOW(), INTERVAL 7 DAY) " +
                    "WHERE status = 1 AND stock > 0 " +
                    "ORDER BY id ASC LIMIT 3";
            int updated = jdbcTemplate.update(bootstrapSql);
            logger.info("Bootstrapped flash campaign data for {} materials.", updated);
        } catch (Exception e) {
            logger.error("Failed to bootstrap flash campaign data: {}", e.getMessage());
        }
    }

    private void checkAndAddGuideColumns() {
        try {
            // Check institution
            String checkSql = "SELECT count(*) FROM information_schema.columns " +
                    "WHERE table_schema = (SELECT DATABASE()) " +
                    "AND table_name = 'yl_guide' AND column_name = 'institution'";
            Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class);
            if (count == null || count == 0) {
                jdbcTemplate.execute("ALTER TABLE yl_guide ADD COLUMN institution VARCHAR(100) COMMENT '报考院校'");
                logger.info("Column 'institution' added to 'yl_guide'.");
            }

            // Check major
            checkSql = "SELECT count(*) FROM information_schema.columns " +
                    "WHERE table_schema = (SELECT DATABASE()) " +
                    "AND table_name = 'yl_guide' AND column_name = 'major'";
            count = jdbcTemplate.queryForObject(checkSql, Integer.class);
            if (count == null || count == 0) {
                jdbcTemplate.execute("ALTER TABLE yl_guide ADD COLUMN major VARCHAR(100) COMMENT '报考专业'");
                logger.info("Column 'major' added to 'yl_guide'.");
            }
        } catch (Exception e) {
            logger.error("Failed to patch yl_guide: {}", e.getMessage());
        }
    }

    private void checkAndAddCommentColumns() {
        try {
            // target_type
            String checkSql = "SELECT count(*) FROM information_schema.columns " +
                    "WHERE table_schema = (SELECT DATABASE()) " +
                    "AND table_name = 'yl_comment' AND column_name = 'target_type'";
            Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class);
            if (count == null || count == 0) {
                jdbcTemplate.execute("ALTER TABLE yl_comment ADD COLUMN target_type INT DEFAULT 1 COMMENT '目标类型: 1-帖子, 2-指南'");
                jdbcTemplate.execute("ALTER TABLE yl_comment ADD COLUMN target_id BIGINT COMMENT '目标ID'");
                // Migrate existing data
                jdbcTemplate.execute("UPDATE yl_comment SET target_type = 1, target_id = post_id WHERE target_id IS NULL");
                logger.info("Column 'target_type/id' added to 'yl_comment'.");
            }

            // parent_id (reply)
            checkSql = "SELECT count(*) FROM information_schema.columns " +
                    "WHERE table_schema = (SELECT DATABASE()) " +
                    "AND table_name = 'yl_comment' AND column_name = 'parent_id'";
            count = jdbcTemplate.queryForObject(checkSql, Integer.class);
            if (count == null || count == 0) {
                jdbcTemplate.execute("ALTER TABLE yl_comment ADD COLUMN parent_id BIGINT COMMENT '父评论ID'");
                jdbcTemplate.execute("ALTER TABLE yl_comment ADD COLUMN reply_to_user_id BIGINT COMMENT '被回复用户ID'");
                jdbcTemplate.execute("ALTER TABLE yl_comment ADD COLUMN reply_to_nickname VARCHAR(50) COMMENT '被回复用户昵称'");
                logger.info("Column 'parent_id' etc. added to 'yl_comment'.");
            }
        } catch (Exception e) {
            logger.error("Failed to patch yl_comment: {}", e.getMessage());
        }
    }

    private void checkAndAddUserColumns() {
        try {
            String checkSql = "SELECT count(*) FROM information_schema.columns " +
                    "WHERE table_schema = (SELECT DATABASE()) " +
                    "AND table_name = 'sys_user' AND column_name = 'status'";
            Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class);
            if (count == null || count == 0) {
                jdbcTemplate.execute("ALTER TABLE sys_user ADD COLUMN status TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态: 1-正常, 0-禁用'");
                jdbcTemplate.execute("UPDATE sys_user SET status = 1 WHERE status IS NULL");
                logger.info("Column 'status' added to 'sys_user'.");
            }

            checkSql = "SELECT count(*) FROM information_schema.columns " +
                    "WHERE table_schema = (SELECT DATABASE()) " +
                    "AND table_name = 'sys_user' AND column_name = 'last_login_time'";
            count = jdbcTemplate.queryForObject(checkSql, Integer.class);
            if (count == null || count == 0) {
                jdbcTemplate.execute("ALTER TABLE sys_user ADD COLUMN last_login_time DATETIME NULL COMMENT '最后登录时间'");
                logger.info("Column 'last_login_time' added to 'sys_user'.");
            }
        } catch (Exception e) {
            logger.error("Failed to patch sys_user: {}", e.getMessage());
        }
    }

    private void fixFavoriteTimestampData() {
        try {
            String sql = "UPDATE favorite " +
                    "SET create_time = COALESCE(create_time, update_time, NOW()), " +
                    "update_time = COALESCE(update_time, create_time, NOW()) " +
                    "WHERE create_time IS NULL OR update_time IS NULL";
            int updated = jdbcTemplate.update(sql);
            logger.info("Favorite timestamp data checked, updated {} rows.", updated);
        } catch (Exception e) {
            logger.error("Failed to patch favorite timestamps: {}", e.getMessage());
        }
    }
}
