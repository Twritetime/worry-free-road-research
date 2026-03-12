-- Initialize Database and Tables
CREATE DATABASE IF NOT EXISTS `yanluwuyou` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `yanluwuyou`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(64) NOT NULL COMMENT 'Username',
  `password` varchar(128) NOT NULL COMMENT 'Password',
  `nickname` varchar(64) DEFAULT NULL COMMENT 'Nickname',
  `email` varchar(64) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `avatar` varchar(255) DEFAULT NULL,
  `role` varchar(20) NOT NULL DEFAULT 'STUDENT' COMMENT 'Role: STUDENT, OPERATOR, ADMIN',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint(1) DEFAULT 0 COMMENT 'Logical Delete',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='User Table';

-- ----------------------------
-- Table structure for yl_news
-- ----------------------------
DROP TABLE IF EXISTS `yl_news`;
CREATE TABLE `yl_news` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL,
  `content` longtext,
  `type` varchar(50) DEFAULT NULL COMMENT 'News Type',
  `cover_img` varchar(255) DEFAULT NULL,
  `view_count` int(11) DEFAULT 0,
  `status` tinyint(4) DEFAULT 1 COMMENT '1: Published, 0: Draft',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint(1) DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='News/Info';

-- ----------------------------
-- Table structure for yl_guide
-- ----------------------------
DROP TABLE IF EXISTS `yl_guide`;
CREATE TABLE `yl_guide` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL,
  `content` longtext,
  `category` varchar(50) DEFAULT NULL COMMENT '分类',
  `cover_img` varchar(255) DEFAULT NULL,
  `view_count` int(11) DEFAULT 0,
  `status` tinyint(4) DEFAULT 1,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint(1) DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Admission Guides';

-- ----------------------------
-- Table structure for yl_material
-- ----------------------------
DROP TABLE IF EXISTS `yl_material`;
CREATE TABLE `yl_material` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `description` text,
  `price` decimal(10,2) NOT NULL,
  `stock` int(11) DEFAULT 0,
  `sales` int(11) DEFAULT 0 COMMENT 'Sales',
  `category` varchar(50) DEFAULT NULL,
  `cover_img` varchar(255) DEFAULT NULL,
  `file_url` varchar(255) DEFAULT NULL COMMENT '文件路径',
  `specs` varchar(255) DEFAULT NULL COMMENT 'Specifications',
  `status` tinyint(4) DEFAULT 1 COMMENT '1: On Shelf, 0: Off Shelf',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint(1) DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Study Materials';

-- ----------------------------
-- Table structure for address
-- ----------------------------
DROP TABLE IF EXISTS `address`;
CREATE TABLE `address` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `receiver_name` varchar(64) NOT NULL,
  `receiver_phone` varchar(20) NOT NULL,
  `province` varchar(50) DEFAULT NULL,
  `city` varchar(50) DEFAULT NULL,
  `district` varchar(50) DEFAULT NULL,
  `detail_address` varchar(255) NOT NULL,
  `is_default` tinyint(1) DEFAULT 0,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint(1) DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Shipping Address';

-- ----------------------------
-- Table structure for yl_order
-- ----------------------------
DROP TABLE IF EXISTS `yl_order`;
CREATE TABLE `yl_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_no` varchar(64) DEFAULT NULL COMMENT '订单编号',
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户ID',
  `total_amount` decimal(10,2) DEFAULT NULL COMMENT '总金额',
  `status` int(11) DEFAULT 0 COMMENT '状态: 0:待付款, 1:已付款, 2:已取消',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint(1) DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单';

-- ----------------------------
-- Table structure for yl_order_item
-- ----------------------------
DROP TABLE IF EXISTS `yl_order_item`;
CREATE TABLE `yl_order_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_id` bigint(20) DEFAULT NULL COMMENT '订单ID',
  `material_id` bigint(20) DEFAULT NULL COMMENT '资料ID',
  `material_name` varchar(255) DEFAULT NULL COMMENT '资料名称',
  `price` decimal(10,2) DEFAULT NULL COMMENT '价格',
  `quantity` int(11) DEFAULT NULL COMMENT '数量',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint(1) DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单项';

-- ----------------------------
-- Table structure for yl_cart_item
-- ----------------------------
DROP TABLE IF EXISTS `yl_cart_item`;
CREATE TABLE `yl_cart_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户ID',
  `material_id` bigint(20) DEFAULT NULL COMMENT '资料ID',
  `quantity` int(11) DEFAULT NULL COMMENT '数量',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint(1) DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车项';

-- ----------------------------
-- Table structure for yl_post
-- ----------------------------
DROP TABLE IF EXISTS `yl_post`;
CREATE TABLE `yl_post` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) DEFAULT NULL COMMENT '标题',
  `content` longtext COMMENT '内容',
  `user_id` bigint(20) DEFAULT NULL COMMENT '作者ID',
  `nickname` varchar(64) DEFAULT NULL COMMENT '作者昵称',
  `avatar` varchar(255) DEFAULT NULL COMMENT '作者头像',
  `view_count` int(11) DEFAULT 0 COMMENT '浏览量',
  `like_count` int(11) DEFAULT 0 COMMENT '点赞数',
  `comment_count` int(11) DEFAULT 0 COMMENT '评论数',
  `category` int(11) DEFAULT NULL COMMENT '板块分类: 1:公共课, 2:专业课, 3:复试经验, 4:其他',
  `status` int(11) DEFAULT 1 COMMENT '状态: 0:待审核, 1:正常(已审核), 2:拒绝',
  `is_top` int(11) DEFAULT 0 COMMENT '是否置顶: 0:否, 1:是',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint(1) DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='论坛帖子';

-- ----------------------------
-- Table structure for yl_comment
-- ----------------------------
DROP TABLE IF EXISTS `yl_comment`;
CREATE TABLE `yl_comment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `post_id` bigint(20) DEFAULT NULL COMMENT '帖子ID',
  `content` text COMMENT '内容',
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户ID',
  `nickname` varchar(64) DEFAULT NULL COMMENT '用户昵称',
  `avatar` varchar(255) DEFAULT NULL COMMENT '用户头像',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint(1) DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论';

-- ----------------------------
-- Table structure for favorite
-- ----------------------------
DROP TABLE IF EXISTS `favorite`;
CREATE TABLE `favorite` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `target_type` int(11) NOT NULL COMMENT '1: NEWS, 2: GUIDE, 3: MATERIAL',
  `target_id` bigint(20) NOT NULL,
  `target_title` varchar(255) DEFAULT NULL,
  `target_cover` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint(1) DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_target` (`user_id`, `target_type`, `target_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Favorites';

-- ----------------------------
-- Table structure for feedback
-- ----------------------------
DROP TABLE IF EXISTS `feedback`;
CREATE TABLE `feedback` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `content` text NOT NULL,
  `reply` text DEFAULT NULL,
  `status` int(11) DEFAULT 0 COMMENT '0: Pending, 1: Replied',
  `reply_time` datetime DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint(1) DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='User Feedback';

-- ----------------------------
-- Table structure for yl_home_config
-- ----------------------------
DROP TABLE IF EXISTS `yl_home_config`;
CREATE TABLE `yl_home_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `type` varchar(20) NOT NULL COMMENT 'CAROUSEL, NOTICE',
  `title` varchar(255) DEFAULT NULL,
  `img_url` varchar(255) DEFAULT NULL,
  `link_url` varchar(255) DEFAULT NULL,
  `sort_order` int(11) DEFAULT 0,
  `status` tinyint(4) DEFAULT 1,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint(1) DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Home Config';


-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` (`id`, `username`, `password`, `nickname`, `email`, `phone`, `avatar`, `role`, `create_time`, `update_time`, `deleted`) VALUES
(1, 'admin', '123456', '系统管理员', 'admin@yanlu.com', '13800138000', 'https://placeholder.co/100', 'ADMIN', NOW(), NOW(), 0),
(2, 'operator01', '123456', '运营人员', 'operator01@yanlu.com', '13900139001', 'https://placeholder.co/100', 'OPERATOR', NOW(), NOW(), 0),
(3, 'user01', '123456', '考研小白', 'user01@yanlu.com', '13900139002', 'https://placeholder.co/100', 'STUDENT', NOW(), NOW(), 0),
(4, 'user02', '123456', '学霸张三', 'user02@yanlu.com', '13900139003', 'https://placeholder.co/100', 'STUDENT', NOW(), NOW(), 0);

-- ----------------------------
-- Records of yl_news
-- ----------------------------
INSERT INTO `yl_news` (`id`, `title`, `content`, `type`, `cover_img`, `view_count`, `status`, `create_time`, `update_time`, `deleted`) VALUES
(1, '2025年全国硕士研究生招生考试公告', '教育部发布2025年全国硕士研究生招生工作管理规定...', '考研动态', 'https://placeholder.co/300x200', 1024, 1, NOW(), NOW(), 0),
(2, '考研英语大纲变动解析', '今年考研英语大纲词汇量有所增加...', '考试大纲', 'https://placeholder.co/300x200', 512, 1, NOW(), NOW(), 0),
(3, 'XX大学2025年硕士研究生招生简章', '欢迎报考XX大学...', '招生简章', 'https://placeholder.co/300x200', 256, 1, NOW(), NOW(), 0),
(4, '复试流程及注意事项', '复试一般包括笔试和面试...', '考研动态', 'https://placeholder.co/300x200', 128, 1, NOW(), NOW(), 0),
(5, '考研数学冲刺阶段复习策略', '最后两个月数学应该这样复习...', '考研动态', 'https://placeholder.co/300x200', 64, 1, NOW(), NOW(), 0);

-- ----------------------------
-- Records of yl_guide
-- ----------------------------
INSERT INTO `yl_guide` (`id`, `title`, `content`, `category`, `cover_img`, `view_count`, `status`, `create_time`, `update_time`, `deleted`) VALUES
(1, '北京大学计算机学院2025年招生专业目录', '北京大学计算机学院2025年拟招收...', 'zhuanyemulu', 'https://placeholder.co/300x200', 200, 1, NOW(), NOW(), 0),
(2, '清华大学软件学院复试细则', '清华大学软件学院复试包括机试...', 'fushixize', 'https://placeholder.co/300x200', 150, 1, NOW(), NOW(), 0),
(3, '复旦大学金融专硕考试大纲', '考试科目包括金融学综合...', 'kaoshidagang', 'https://placeholder.co/300x200', 120, 1, NOW(), NOW(), 0),
(4, '上海交通大学电子信息招生简章', '上海交通大学电子信息学院...', 'zhaoshengjianzhang', 'https://placeholder.co/300x200', 180, 1, NOW(), NOW(), 0);

-- ----------------------------
-- Records of yl_material
-- ----------------------------
INSERT INTO `yl_material` (`id`, `name`, `description`, `price`, `stock`, `category`, `cover_img`, `file_url`, `specs`, `status`, `create_time`, `update_time`, `deleted`) VALUES
(1, '2024考研英语一真题解析', '包含近20年真题详细解析', 45.00, 100, '公共课', 'https://placeholder.co/200', 'http://example.com/file1.pdf', '电子版', 1, NOW(), NOW(), 0),
(2, '计算机网络考研复习笔记', '重点难点一网打尽', 30.00, 50, '专业课', 'https://placeholder.co/200', 'http://example.com/file2.pdf', '电子版', 1, NOW(), NOW(), 0),
(3, '考研政治核心考点', '肖秀荣核心考点精编', 25.50, 200, '公共课', 'https://placeholder.co/200', 'http://example.com/file3.pdf', '实体书', 1, NOW(), NOW(), 0),
(4, '数据结构1800题', '经典习题集，刷题必备', 50.00, 80, '专业课', 'https://placeholder.co/200', 'http://example.com/file4.pdf', 'PDF', 1, NOW(), NOW(), 0),
(5, '复试英语口语模板', '包含自我介绍、常见问答', 15.00, 1000, '复试资料', 'https://placeholder.co/200', 'http://example.com/file5.pdf', '电子版', 1, NOW(), NOW(), 0);

-- ----------------------------
-- Records of address
-- ----------------------------
INSERT INTO `address` (`id`, `user_id`, `receiver_name`, `receiver_phone`, `province`, `city`, `district`, `detail_address`, `is_default`, `create_time`, `update_time`, `deleted`) VALUES
(1, 2, '考研小白', '13900139001', '北京市', '北京市', '海淀区', '学院路30号', 1, NOW(), NOW(), 0),
(2, 3, '学霸张三', '13900139002', '上海市', '上海市', '杨浦区', '邯郸路220号', 1, NOW(), NOW(), 0);

-- ----------------------------
-- Records of yl_post
-- ----------------------------
INSERT INTO `yl_post` (`id`, `title`, `content`, `user_id`, `nickname`, `avatar`, `view_count`, `like_count`, `comment_count`, `category`, `status`, `create_time`, `update_time`, `deleted`) VALUES
(1, '求问英语一和英语二的区别', '我是考专硕的，不知道该准备英语一还是英语二...', 2, '考研小白', 'https://placeholder.co/100', 100, 10, 2, 1, 1, NOW(), NOW(), 0),
(2, '计算机专硕复试经验分享', '刚刚上岸，分享一下我的复试准备过程...', 3, '学霸张三', 'https://placeholder.co/100', 500, 50, 5, 3, 1, NOW(), NOW(), 0),
(3, '有没有一起考北大的研友？', '本人二战北大，想找个研友一起监督...', 2, '考研小白', 'https://placeholder.co/100', 80, 5, 1, 4, 1, NOW(), NOW(), 0),
(4, '专业课数据结构太难了怎么办', '看不懂KMP算法，求大神指点...', 2, '考研小白', 'https://placeholder.co/100', 60, 2, 0, 2, 1, NOW(), NOW(), 0);

-- ----------------------------
-- Records of yl_comment
-- ----------------------------
INSERT INTO `yl_comment` (`id`, `post_id`, `content`, `user_id`, `nickname`, `avatar`, `create_time`, `update_time`, `deleted`) VALUES
(1, 1, '专硕一般考英语二，学硕考英语一，具体看学校招生简章。', 3, '学霸张三', 'https://placeholder.co/100', NOW(), NOW(), 0),
(2, 1, '英语二相对简单一些。', 3, '学霸张三', 'https://placeholder.co/100', NOW(), NOW(), 0),
(3, 2, '恭喜楼主上岸！沾沾喜气！', 2, '考研小白', 'https://placeholder.co/100', NOW(), NOW(), 0),
(4, 2, '感谢分享，很有用！', 1, '系统管理员', 'https://placeholder.co/100', NOW(), NOW(), 0),
(5, 3, '我是考清华的，加油！', 3, '学霸张三', 'https://placeholder.co/100', NOW(), NOW(), 0);

-- ----------------------------
-- Records of feedback
-- ----------------------------
INSERT INTO `feedback` (`id`, `user_id`, `content`, `reply`, `status`, `reply_time`, `create_time`, `update_time`, `deleted`) VALUES
(1, 2, '建议增加夜间模式，晚上看太亮了。', NULL, 0, NULL, NOW(), NOW(), 0),
(2, 3, '支付页面有时候会卡顿。', '收到，我们会尽快优化。', 1, NOW(), NOW(), NOW(), 0);

-- ----------------------------
-- Records of favorite
-- ----------------------------
INSERT INTO `favorite` (`id`, `user_id`, `target_id`, `target_type`, `target_title`, `target_cover`, `create_time`, `update_time`, `deleted`) VALUES
(1, 2, 1, 1, '2025年全国硕士研究生招生考试公告', 'https://placeholder.co/300x200', NOW(), NOW(), 0),
(2, 2, 2, 3, '计算机网络考研复习笔记', 'https://placeholder.co/200', NOW(), NOW(), 0);

-- ----------------------------
-- Records of yl_order
-- ----------------------------
INSERT INTO `yl_order` (`id`, `order_no`, `user_id`, `total_amount`, `status`, `create_time`, `update_time`, `deleted`) VALUES
(1, '202310010001', 2, 45.00, 1, NOW(), NOW(), 0),
(2, '202310020002', 3, 120.00, 0, NOW(), NOW(), 0);

-- ----------------------------
-- Records of yl_order_item
-- ----------------------------
INSERT INTO `yl_order_item` (`id`, `order_id`, `material_id`, `material_name`, `price`, `quantity`) VALUES
(1, 1, 1, '2024考研英语一真题解析', 45.00, 1),
(2, 2, 2, '计算机网络考研复习笔记', 30.00, 1),
(3, 2, 4, '数据结构1800题', 90.00, 1);

SET FOREIGN_KEY_CHECKS = 1;
