package com.yanluwuyou.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

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
        checkAndAddGuideColumns();
        checkAndAddCommentColumns();
        checkAndAddCountColumns();
        logger.info("Database schema check completed.");
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
}
