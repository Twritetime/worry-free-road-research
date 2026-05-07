/*
 Navicat Premium Dump SQL

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80045 (8.0.45)
 Source Host           : localhost:3306
 Source Schema         : yanluwuyou

 Target Server Type    : MySQL
 Target Server Version : 80045 (8.0.45)
 File Encoding         : 65001

 Date: 07/05/2026 12:46:08
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for address
-- ----------------------------
DROP TABLE IF EXISTS `address`;
CREATE TABLE `address`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `receiver_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `receiver_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `province` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `city` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `district` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `detail_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `is_default` tinyint(1) NULL DEFAULT 0,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint(1) NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'Shipping Address' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of address
-- ----------------------------
INSERT INTO `address` VALUES (1, 3, '张三', '13811110001', '北京市', '北京市', '海淀区', '中关村大街1号清华园X号楼501室', 1, '2026-05-04 17:23:25', '2026-05-04 17:23:25', 0);
INSERT INTO `address` VALUES (2, 3, '张三', '13811110001', '上海市', '上海市', '浦东新区', '张江高科技园区碧波路690号', 0, '2026-05-04 17:23:25', '2026-05-04 17:23:25', 0);
INSERT INTO `address` VALUES (3, 4, '李四', '13811110002', '广东省', '广州市', '天河区', '天河路228号正佳广场写字楼A座1201', 1, '2026-05-04 17:23:25', '2026-05-04 17:23:25', 0);
INSERT INTO `address` VALUES (4, 5, '王五', '13811110003', '浙江省', '杭州市', '西湖区', '文三路269号华星科技大厦B座802', 1, '2026-05-04 17:23:25', '2026-05-04 17:23:25', 0);
INSERT INTO `address` VALUES (5, 6, '赵六', '13811110004', '江苏省', '南京市', '鼓楼区', '汉中路188号南京大学南园宿舍楼302', 1, '2026-05-04 17:23:25', '2026-05-04 17:23:25', 0);
INSERT INTO `address` VALUES (6, 7, '孙七', '13811110005', '湖北省', '武汉市', '洪山区', '珞喻路152号华中科技大学紫菘学生公寓', 1, '2026-05-04 17:23:25', '2026-05-04 17:23:25', 0);
INSERT INTO `address` VALUES (7, 8, '周八', '13811110006', '四川省', '成都市', '武侯区', '一环路南一段24号四川大学望江校区', 1, '2026-05-04 17:23:25', '2026-05-04 17:23:25', 0);
INSERT INTO `address` VALUES (8, 9, '吴九', '13811110007', '陕西省', '西安市', '雁塔区', '咸宁西路28号西安交通大学兴庆校区', 1, '2026-05-04 17:23:25', '2026-05-04 17:23:25', 0);
INSERT INTO `address` VALUES (9, 10, '郑十', '13811110008', '黑龙江省', '哈尔滨市', '南岗区', '西大直街92号哈尔滨工业大学', 1, '2026-05-04 17:23:25', '2026-05-04 17:23:25', 0);
INSERT INTO `address` VALUES (10, 11, '陈一', '13811110009', '安徽省', '合肥市', '蜀山区', '黄山路443号中国科学技术大学东校区', 1, '2026-05-04 17:23:25', '2026-05-04 17:23:25', 0);
INSERT INTO `address` VALUES (11, 12, '黄二', '13811110010', '福建省', '厦门市', '思明区', '思明南路422号厦门大学芙蓉宿舍', 1, '2026-05-04 17:23:25', '2026-05-04 17:23:25', 0);
INSERT INTO `address` VALUES (12, 13, '刘三', '13811110011', '山东省', '青岛市', '市南区', '香港东路23号中国海洋大学浮山校区', 1, '2026-05-04 17:23:25', '2026-05-04 17:23:25', 0);
INSERT INTO `address` VALUES (13, 14, '杨四', '13811110012', '湖南省', '长沙市', '岳麓区', '麓山南路932号中南大学', 1, '2026-05-04 17:23:25', '2026-05-04 17:23:25', 0);
INSERT INTO `address` VALUES (14, 15, '许五', '13811110013', '重庆市', '重庆市', '沙坪坝区', '沙正街174号重庆大学A区学生公寓', 1, '2026-05-04 17:23:25', '2026-05-04 17:23:25', 0);
INSERT INTO `address` VALUES (15, 16, '何六', '13811110014', '辽宁省', '大连市', '甘井子区', '凌工路2号大连理工大学西山校区', 1, '2026-05-04 17:23:25', '2026-05-04 17:23:25', 0);
INSERT INTO `address` VALUES (16, 17, '马七', '13811110015', '天津市', '天津市', '南开区', '卫津路94号南开大学八里台校区', 1, '2026-05-04 17:23:25', '2026-05-04 17:23:25', 0);
INSERT INTO `address` VALUES (17, 18, '林八', '13811110016', '河南省', '郑州市', '金水区', '科学大道100号郑州大学新校区', 1, '2026-05-04 17:23:25', '2026-05-04 17:23:25', 0);
INSERT INTO `address` VALUES (18, 19, '高九', '13811110017', '云南省', '昆明市', '五华区', '一二一大街298号云南大学呈贡校区', 1, '2026-05-04 17:23:25', '2026-05-04 17:23:25', 0);
INSERT INTO `address` VALUES (19, 20, '罗十', '13811110018', '甘肃省', '兰州市', '城关区', '天水南路222号兰州大学本部', 1, '2026-05-04 17:23:25', '2026-05-04 17:23:25', 0);
INSERT INTO `address` VALUES (20, 4, '李四', '13811110002', '广东省', '深圳市', '南山区', '科技园南区深南大道9988号', 0, '2026-05-04 17:23:25', '2026-05-04 17:23:25', 0);

-- ----------------------------
-- Table structure for favorite
-- ----------------------------
DROP TABLE IF EXISTS `favorite`;
CREATE TABLE `favorite`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `target_type` int NOT NULL COMMENT '1: NEWS, 2: GUIDE, 3: MATERIAL',
  `target_id` bigint NOT NULL,
  `target_title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `target_cover` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint(1) NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_target`(`user_id` ASC, `target_type` ASC, `target_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 24 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'Favorites' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of favorite
-- ----------------------------
INSERT INTO `favorite` VALUES (1, 3, 1, 1, '2026年清华大学硕士研究生招生简章', 'https://picsum.photos/seed/fav1/300/200', '2026-05-04 17:23:25', '2026-05-04 23:01:48', 0);
INSERT INTO `favorite` VALUES (2, 3, 2, 1, '2026考研政治全程班讲义', 'https://picsum.photos/seed/fav2/300/200', '2026-05-04 17:23:25', '2026-05-04 17:44:46', 0);
INSERT INTO `favorite` VALUES (3, 3, 3, 1, '【经验帖】双非一本逆袭985计算机', 'https://picsum.photos/seed/fav3/300/200', '2026-05-04 17:23:25', '2026-05-04 17:44:46', 0);
INSERT INTO `favorite` VALUES (4, 4, 1, 2, '2026年北京大学硕士研究生招生简章', 'https://picsum.photos/seed/fav4/300/200', '2026-05-04 17:23:25', '2026-05-04 17:44:46', 0);
INSERT INTO `favorite` VALUES (5, 4, 2, 3, '数学一复习全书', 'https://picsum.photos/seed/fav5/300/200', '2026-05-04 17:23:25', '2026-05-04 17:44:46', 0);
INSERT INTO `favorite` VALUES (6, 4, 3, 3, '【资料分享】2026考研英语一作文模板', 'https://picsum.photos/seed/fav6/300/200', '2026-05-04 17:23:25', '2026-05-04 17:44:46', 0);
INSERT INTO `favorite` VALUES (7, 5, 1, 6, '2026年考研思想政治理论大纲', 'https://picsum.photos/seed/fav7/300/200', '2026-05-04 17:23:25', '2026-05-04 17:44:46', 0);
INSERT INTO `favorite` VALUES (8, 5, 2, 4, '计算机408历年真题详解', 'https://picsum.photos/seed/fav8/300/200', '2026-05-04 17:23:25', '2026-05-04 17:44:46', 0);
INSERT INTO `favorite` VALUES (9, 5, 3, 4, '【日常】今天收到拟录取通知书了！！！', 'https://picsum.photos/seed/fav9/300/200', '2026-05-04 17:23:25', '2026-05-04 17:44:46', 0);
INSERT INTO `favorite` VALUES (10, 6, 1, 8, '清华大学计算机系复试细则', 'https://picsum.photos/seed/fav10/300/200', '2026-05-04 17:23:25', '2026-05-04 17:44:46', 0);
INSERT INTO `favorite` VALUES (11, 6, 2, 7, '北大光华MBA面试宝典', 'https://picsum.photos/seed/fav11/300/200', '2026-05-04 17:23:25', '2026-05-04 17:44:46', 0);
INSERT INTO `favorite` VALUES (12, 6, 3, 5, '【经验】在职考研上岸北大的血泪史', 'https://picsum.photos/seed/fav12/300/200', '2026-05-04 17:23:25', '2026-05-04 17:44:46', 0);
INSERT INTO `favorite` VALUES (13, 7, 1, 10, '浙江大学医学院复试录取办法', 'https://picsum.photos/seed/fav13/300/200', '2026-05-04 17:23:25', '2026-05-04 17:44:46', 0);
INSERT INTO `favorite` VALUES (14, 7, 2, 9, '复旦新传考研专题整理', 'https://picsum.photos/seed/fav14/300/200', '2026-05-04 17:23:25', '2026-05-04 17:44:46', 0);
INSERT INTO `favorite` VALUES (15, 7, 3, 7, '【资料】408四本书思维导图（超全）', 'https://picsum.photos/seed/fav15/300/200', '2026-05-04 17:23:25', '2026-05-04 17:44:46', 0);
INSERT INTO `favorite` VALUES (16, 8, 1, 12, '北京大学光华管理学院复试办法', 'https://picsum.photos/seed/fav16/300/200', '2026-05-04 17:23:25', '2026-05-04 17:44:46', 0);
INSERT INTO `favorite` VALUES (17, 8, 2, 11, '计算机考研择校指南', 'https://picsum.photos/seed/fav17/300/200', '2026-05-04 17:23:25', '2026-05-04 17:44:46', 0);
INSERT INTO `favorite` VALUES (18, 8, 3, 9, '【吐槽】室友每天打游戏到凌晨三点', 'https://picsum.photos/seed/fav18/300/200', '2026-05-04 17:23:25', '2026-05-04 17:44:46', 0);
INSERT INTO `favorite` VALUES (19, 9, 1, 14, '复旦大学新闻学院复试方案', 'https://picsum.photos/seed/fav19/300/200', '2026-05-04 17:23:25', '2026-05-04 17:44:46', 0);
INSERT INTO `favorite` VALUES (20, 9, 2, 16, '人工智能专业深度解读', 'https://picsum.photos/seed/fav20/300/200', '2026-05-04 17:23:25', '2026-05-04 17:44:46', 0);
INSERT INTO `favorite` VALUES (21, 1, 5, 3, NULL, NULL, '2026-05-04 19:32:46', '2026-05-04 19:32:46', 0);
INSERT INTO `favorite` VALUES (22, 1, 4, 3, '【资料分享】2026考研英语一作文模板（免费）', NULL, '2026-05-04 19:32:47', '2026-05-04 19:32:47', 0);
INSERT INTO `favorite` VALUES (23, 3, 3, 2, '2026考研英语一真题解析', 'https://picsum.photos/seed/material2/400/250', '2026-05-04 23:00:07', '2026-05-04 23:00:07', 0);

-- ----------------------------
-- Table structure for feedback
-- ----------------------------
DROP TABLE IF EXISTS `feedback`;
CREATE TABLE `feedback`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '问题类型',
  `contact` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系方式',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `cover_img` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '截图，多张用逗号分隔',
  `reply` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `status` int NULL DEFAULT 0 COMMENT '0: Pending, 1: Replied',
  `reply_time` datetime NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint(1) NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'User Feedback' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of feedback
-- ----------------------------
INSERT INTO `feedback` VALUES (1, 3, 'suggest', 'zhangsan@qq.com', '希望增加资料预览功能，购买前能看部分内容', 'https://picsum.photos/seed/feedback1/300/200', '感谢您的建议，我们正在开发此功能，预计下周上线。', 1, '2026-04-29 17:23:25', '2026-05-04 17:23:25', '2026-05-04 17:44:46', 0);
INSERT INTO `feedback` VALUES (2, 4, 'bug', '13811110002', '购物车数量修改后价格没有实时更新', 'https://picsum.photos/seed/feedback2/300/200', '已修复，请刷新页面重试。', 1, '2026-04-30 17:23:25', '2026-05-04 17:23:25', '2026-05-04 17:44:46', 0);
INSERT INTO `feedback` VALUES (3, 5, 'suggest', 'wangwu@qq.com', '希望能添加夜间模式，晚上看书不伤眼', 'https://picsum.photos/seed/feedback3/300/200', '好的，已加入开发计划。', 1, '2026-05-01 17:23:25', '2026-05-04 17:23:25', '2026-05-04 17:44:46', 0);
INSERT INTO `feedback` VALUES (4, 6, 'complaint', '13811110003', '购买的资料下载链接失效了', 'https://picsum.photos/seed/feedback4/300/200', '非常抱歉，链接已修复并发送到您的邮箱。', 1, '2026-05-02 17:23:25', '2026-05-04 17:23:25', '2026-05-04 17:44:46', 0);
INSERT INTO `feedback` VALUES (5, 7, 'other', 'sunqi@qq.com', '网站整体体验很好，加油！', 'https://picsum.photos/seed/feedback5/300/200', '谢谢您的支持！', 1, '2026-05-03 17:23:25', '2026-05-04 17:23:25', '2026-05-04 17:44:46', 0);
INSERT INTO `feedback` VALUES (6, 8, 'suggest', 'zhouba@qq.com', '建议增加社区帖子搜索功能', 'https://picsum.photos/seed/feedback6/300/200', '已采纳，正在开发中。', 0, NULL, '2026-05-04 17:23:25', '2026-05-04 17:44:46', 0);
INSERT INTO `feedback` VALUES (7, 9, 'bug', '13811110005', '移动端首页轮播图显示异常', 'https://picsum.photos/seed/feedback7/300/200', '已定位问题，正在修复。', 0, NULL, '2026-05-04 17:23:25', '2026-05-04 17:44:46', 0);
INSERT INTO `feedback` VALUES (8, 10, 'other', 'zhengshi@qq.com', '客服响应速度很快，点赞！', 'https://picsum.photos/seed/feedback8/300/200', '感谢认可，我们会继续努力！', 1, '2026-05-04 17:23:25', '2026-05-04 17:23:25', '2026-05-04 17:44:46', 0);
INSERT INTO `feedback` VALUES (9, 11, 'suggest', 'chenyi@qq.com', '希望能增加资料评分和评论功能', 'https://picsum.photos/seed/feedback9/300/200', '好主意，已列入产品路线图。', 0, NULL, '2026-05-04 17:23:25', '2026-05-04 17:44:46', 0);
INSERT INTO `feedback` VALUES (10, 12, 'bug', '13811110010', '订单列表分页加载有问题', 'https://picsum.photos/seed/feedback10/300/200', '已修复，请刷新重试。', 1, '2026-05-04 11:23:25', '2026-05-04 17:23:25', '2026-05-04 17:44:46', 0);
INSERT INTO `feedback` VALUES (11, 13, 'complaint', 'liusann@qq.com', '资料描述与实际内容不符', 'https://picsum.photos/seed/feedback11/300/200', '已核实并更新资料详情页，给您带来的不便敬请谅解。', 1, '2026-05-04 05:23:25', '2026-05-04 17:23:25', '2026-05-04 17:44:46', 0);
INSERT INTO `feedback` VALUES (12, 14, 'suggest', 'yangsi@qq.com', '希望增加考研倒计时功能', 'https://picsum.photos/seed/feedback12/300/200', '已添加至首页顶部，请查看。', 1, '2026-05-03 17:23:25', '2026-05-04 17:23:25', '2026-05-04 17:44:46', 0);
INSERT INTO `feedback` VALUES (13, 15, 'other', 'xuwu@qq.com', '社区氛围很好，认识了很多研友', 'https://picsum.photos/seed/feedback13/300/200', '很高兴能为大家提供交流平台！', 1, '2026-05-02 17:23:25', '2026-05-04 17:23:25', '2026-05-04 17:44:46', 0);
INSERT INTO `feedback` VALUES (14, 16, 'bug', '13811110014', '收藏夹删除后还在显示', 'https://picsum.photos/seed/feedback14/300/200', '缓存问题，清除浏览器缓存即可解决。', 1, '2026-05-01 17:23:25', '2026-05-04 17:23:25', '2026-05-04 17:44:46', 0);
INSERT INTO `feedback` VALUES (15, 17, 'suggest', 'maqi@qq.com', '希望能增加资料分类筛选功能', 'https://picsum.photos/seed/feedback15/300/200', '已在资料页实现多维度筛选。', 1, '2026-04-30 17:23:25', '2026-05-04 17:23:25', '2026-05-04 17:44:46', 0);
INSERT INTO `feedback` VALUES (16, 18, 'other', 'linba@qq.com', '界面设计很清爽，用户体验不错', 'https://picsum.photos/seed/feedback16/300/200', '谢谢反馈！', 1, '2026-04-29 17:23:25', '2026-05-04 17:23:25', '2026-05-04 17:44:46', 0);
INSERT INTO `feedback` VALUES (17, 19, 'bug', '13811110017', '支付完成后订单状态未及时更新', 'https://picsum.photos/seed/feedback17/300/200', '已优化支付回调逻辑。', 0, NULL, '2026-05-04 17:23:25', '2026-05-04 17:44:46', 0);
INSERT INTO `feedback` VALUES (18, 20, 'suggest', 'luoshi@qq.com', '建议增加学习打卡功能', 'https://picsum.photos/seed/feedback18/300/200', '有趣的想法，我们会考虑实现。', 0, NULL, '2026-05-04 17:23:25', '2026-05-04 17:44:46', 0);
INSERT INTO `feedback` VALUES (19, 3, 'other', 'zhangsan@qq.com', '第二次反馈，夜间模式什么时候上线？', 'https://picsum.photos/seed/feedback19/300/200', '预计本周五上线，敬请期待。', 1, '2026-05-04 17:23:25', '2026-05-04 17:23:25', '2026-05-04 17:44:46', 0);
INSERT INTO `feedback` VALUES (20, 5, 'complaint', 'wangwu@qq.com', '购买的资料有缺页', 'https://picsum.photos/seed/feedback20/300/200', '已补发完整版，请查收邮件。', 1, '2026-05-04 09:23:25', '2026-05-04 17:23:25', '2026-05-04 17:44:46', 0);

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Username',
  `password` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Password',
  `nickname` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Nickname',
  `email` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'STUDENT' COMMENT 'Role: STUDENT, OPERATOR, ADMIN',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT 'Logical Delete',
  `status` tinyint(1) NOT NULL DEFAULT 1 COMMENT '状态: 1-正常, 0-禁用',
  `last_login_time` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'User Table' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'admin', 'e10adc3949ba59abbe56e057f20f883e', '系统管理员', 'admin@yanluwuyou.com', '13800138000', 'https://picsum.photos/seed/user1/200/200', 'ADMIN', '2026-05-04 17:23:25', '2026-05-04 20:02:26', 0, 1, '2026-05-06 01:40:50');
INSERT INTO `sys_user` VALUES (2, 'operator01', 'e10adc3949ba59abbe56e057f20f883e', '运营小王', 'wang@yanluwuyou.com', '13800138001', 'https://picsum.photos/seed/user2/200/200', 'OPERATOR', '2026-05-04 17:23:25', '2026-05-06 01:39:57', 0, 1, '2026-05-06 01:40:29');
INSERT INTO `sys_user` VALUES (3, 'zhangsan', 'e10adc3949ba59abbe56e057f20f883e', '张三', 'zhangsan@qq.com', '13811110001', 'http://localhost:8080/files/bc59bef7846e4a40ab827bbedb4891aa.png', 'STUDENT', '2026-05-04 17:23:25', '2026-05-04 20:02:42', 0, 1, '2026-05-04 20:02:45');
INSERT INTO `sys_user` VALUES (4, 'lisi', 'e10adc3949ba59abbe56e057f20f883e', '李四', 'lisi@qq.com', '13811110002', 'https://picsum.photos/seed/user4/200/200', 'STUDENT', '2026-05-04 17:23:25', '2026-05-06 01:40:05', 0, 1, '2026-05-04 17:23:25');
INSERT INTO `sys_user` VALUES (5, 'wangwu', 'e10adc3949ba59abbe56e057f20f883e', '王五', 'wangwu@qq.com', '13811110003', 'https://picsum.photos/seed/user5/200/200', 'STUDENT', '2026-05-04 17:23:25', '2026-05-06 01:40:08', 0, 1, '2026-05-04 17:23:25');
INSERT INTO `sys_user` VALUES (6, 'zhaoliu', 'e10adc3949ba59abbe56e057f20f883e', '赵六', 'zhaoliu@qq.com', '13811110004', 'https://picsum.photos/seed/user6/200/200', 'STUDENT', '2026-05-04 17:23:25', '2026-05-06 01:40:24', 0, 1, '2026-05-04 17:23:25');
INSERT INTO `sys_user` VALUES (7, 'sunqi', 'e10adc3949ba59abbe56e057f20f883e', '孙七', 'sunqi@qq.com', '13811110005', 'https://picsum.photos/seed/user7/200/200', 'STUDENT', '2026-05-04 17:23:25', '2026-05-06 01:40:24', 0, 1, '2026-05-04 17:23:25');
INSERT INTO `sys_user` VALUES (8, 'zhouba', 'e10adc3949ba59abbe56e057f20f883e', '周八', 'zhouba@qq.com', '13811110006', 'https://picsum.photos/seed/user8/200/200', 'STUDENT', '2026-05-04 17:23:25', '2026-05-06 01:40:24', 0, 1, '2026-05-04 17:23:25');
INSERT INTO `sys_user` VALUES (9, 'wujiu', 'e10adc3949ba59abbe56e057f20f883e', '吴九', 'wujiu@qq.com', '13811110007', 'https://picsum.photos/seed/user9/200/200', 'STUDENT', '2026-05-04 17:23:25', '2026-05-06 01:40:24', 0, 1, '2026-05-04 17:23:25');
INSERT INTO `sys_user` VALUES (10, 'zhengshi', 'e10adc3949ba59abbe56e057f20f883e', '郑十', 'zhengshi@qq.com', '13811110008', 'https://picsum.photos/seed/user10/200/200', 'STUDENT', '2026-05-04 17:23:25', '2026-05-06 01:40:24', 0, 1, '2026-05-04 17:23:25');
INSERT INTO `sys_user` VALUES (11, 'chenyi', 'e10adc3949ba59abbe56e057f20f883e', '陈一', 'chenyi@qq.com', '13811110009', 'https://picsum.photos/seed/user11/200/200', 'STUDENT', '2026-05-04 17:23:25', '2026-05-06 01:40:24', 0, 1, NULL);
INSERT INTO `sys_user` VALUES (12, 'huanger', 'e10adc3949ba59abbe56e057f20f883e', '黄二', 'huanger@qq.com', '13811110010', 'https://picsum.photos/seed/user12/200/200', 'STUDENT', '2026-05-04 17:23:25', '2026-05-06 01:40:24', 0, 1, '2026-05-04 17:23:25');
INSERT INTO `sys_user` VALUES (13, 'liusann', 'e10adc3949ba59abbe56e057f20f883e', '刘三', 'liusann@qq.com', '13811110011', 'https://picsum.photos/seed/user13/200/200', 'STUDENT', '2026-05-04 17:23:25', '2026-05-06 01:40:24', 0, 1, '2026-05-04 17:23:25');
INSERT INTO `sys_user` VALUES (14, 'yangsi', 'e10adc3949ba59abbe56e057f20f883e', '杨四', 'yangsi@qq.com', '13811110012', 'https://picsum.photos/seed/user14/200/200', 'STUDENT', '2026-05-04 17:23:25', '2026-05-06 01:40:24', 0, 1, NULL);
INSERT INTO `sys_user` VALUES (15, 'xuwu', 'e10adc3949ba59abbe56e057f20f883e', '许五', 'xuwu@qq.com', '13811110013', 'https://picsum.photos/seed/user15/200/200', 'STUDENT', '2026-05-04 17:23:25', '2026-05-06 01:40:24', 0, 1, '2026-05-04 17:23:25');
INSERT INTO `sys_user` VALUES (16, 'heliu', 'e10adc3949ba59abbe56e057f20f883e', '何六', 'heliu@qq.com', '13811110014', 'https://picsum.photos/seed/user16/200/200', 'STUDENT', '2026-05-04 17:23:25', '2026-05-06 01:40:24', 0, 1, '2026-05-04 17:23:25');
INSERT INTO `sys_user` VALUES (17, 'maqi', 'e10adc3949ba59abbe56e057f20f883e', '马七', 'maqi@qq.com', '13811110015', 'https://picsum.photos/seed/user17/200/200', 'STUDENT', '2026-05-04 17:23:25', '2026-05-06 01:40:24', 0, 1, NULL);
INSERT INTO `sys_user` VALUES (18, 'linba', 'e10adc3949ba59abbe56e057f20f883e', '林八', 'linba@qq.com', '13811110016', 'https://picsum.photos/seed/user18/200/200', 'STUDENT', '2026-05-04 17:23:25', '2026-05-06 01:40:24', 0, 1, '2026-05-04 17:23:25');
INSERT INTO `sys_user` VALUES (19, 'gaojiu', 'e10adc3949ba59abbe56e057f20f883e', '高九', 'gaojiu@qq.com', '13811110017', 'https://picsum.photos/seed/user19/200/200', 'STUDENT', '2026-05-04 17:23:25', '2026-05-06 01:40:24', 0, 1, '2026-05-04 17:23:25');
INSERT INTO `sys_user` VALUES (20, 'luoshi', 'e10adc3949ba59abbe56e057f20f883e', '罗十', 'luoshi@qq.com', '13811110018', 'https://picsum.photos/seed/user20/200/200', 'STUDENT', '2026-05-04 17:23:25', '2026-05-06 01:40:24', 0, 1, '2026-05-04 17:23:25');

-- ----------------------------
-- Table structure for yl_cart_item
-- ----------------------------
DROP TABLE IF EXISTS `yl_cart_item`;
CREATE TABLE `yl_cart_item`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户ID',
  `material_id` bigint NULL DEFAULT NULL COMMENT '资料ID',
  `quantity` int NULL DEFAULT NULL COMMENT '数量',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint(1) NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '购物车项' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of yl_cart_item
-- ----------------------------
INSERT INTO `yl_cart_item` VALUES (1, 3, 1, 1, '2026-05-04 17:25:35', '2026-05-04 17:25:35', 0);
INSERT INTO `yl_cart_item` VALUES (2, 3, 5, 2, '2026-05-04 17:25:35', '2026-05-04 17:25:35', 0);
INSERT INTO `yl_cart_item` VALUES (3, 4, 2, 1, '2026-05-04 17:25:35', '2026-05-04 17:25:35', 0);
INSERT INTO `yl_cart_item` VALUES (4, 4, 8, 1, '2026-05-04 17:25:35', '2026-05-04 17:25:35', 0);
INSERT INTO `yl_cart_item` VALUES (5, 5, 3, 1, '2026-05-04 17:25:35', '2026-05-04 17:25:35', 0);
INSERT INTO `yl_cart_item` VALUES (6, 5, 6, 1, '2026-05-04 17:25:35', '2026-05-04 17:25:35', 0);
INSERT INTO `yl_cart_item` VALUES (7, 6, 4, 2, '2026-05-04 17:25:35', '2026-05-04 17:25:35', 0);
INSERT INTO `yl_cart_item` VALUES (8, 6, 9, 1, '2026-05-04 17:25:35', '2026-05-04 17:25:35', 0);
INSERT INTO `yl_cart_item` VALUES (9, 7, 7, 1, '2026-05-04 17:25:35', '2026-05-04 17:25:35', 0);
INSERT INTO `yl_cart_item` VALUES (10, 7, 10, 1, '2026-05-04 17:25:35', '2026-05-04 17:25:35', 0);
INSERT INTO `yl_cart_item` VALUES (11, 8, 11, 1, '2026-05-04 17:25:35', '2026-05-04 17:25:35', 0);
INSERT INTO `yl_cart_item` VALUES (12, 8, 12, 2, '2026-05-04 17:25:35', '2026-05-04 17:25:35', 0);
INSERT INTO `yl_cart_item` VALUES (13, 9, 13, 1, '2026-05-04 17:25:35', '2026-05-04 17:25:35', 0);
INSERT INTO `yl_cart_item` VALUES (14, 9, 14, 1, '2026-05-04 17:25:35', '2026-05-04 17:25:35', 0);
INSERT INTO `yl_cart_item` VALUES (15, 10, 15, 1, '2026-05-04 17:25:35', '2026-05-04 17:25:35', 0);
INSERT INTO `yl_cart_item` VALUES (16, 10, 16, 1, '2026-05-04 17:25:35', '2026-05-04 17:25:35', 0);
INSERT INTO `yl_cart_item` VALUES (17, 11, 17, 1, '2026-05-04 17:25:35', '2026-05-04 17:25:35', 0);
INSERT INTO `yl_cart_item` VALUES (18, 11, 18, 1, '2026-05-04 17:25:35', '2026-05-04 17:25:35', 0);
INSERT INTO `yl_cart_item` VALUES (19, 12, 19, 1, '2026-05-04 17:25:35', '2026-05-04 17:25:35', 0);
INSERT INTO `yl_cart_item` VALUES (20, 12, 20, 1, '2026-05-04 17:25:35', '2026-05-04 17:25:35', 0);

-- ----------------------------
-- Table structure for yl_chat_message
-- ----------------------------
DROP TABLE IF EXISTS `yl_chat_message`;
CREATE TABLE `yl_chat_message`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `session_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '会话ID，用于区分不同对话',
  `role` int NOT NULL DEFAULT 0 COMMENT '角色：0系统消息，1用户消息，2助手消息',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '消息内容',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_session`(`user_id` ASC, `session_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 43 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'AI聊天消息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of yl_chat_message
-- ----------------------------
INSERT INTO `yl_chat_message` VALUES (1, 3, 'session_001', 0, '你好，我想咨询一下考研择校的问题', '2026-05-04 17:25:35', '2026-05-06 01:14:24', 1);
INSERT INTO `yl_chat_message` VALUES (2, 3, 'session_001', 1, '您好！很高兴为您服务。请问您目前是什么专业背景，想考什么方向的研究生呢？', '2026-05-04 17:25:35', '2026-05-06 01:14:24', 1);
INSERT INTO `yl_chat_message` VALUES (3, 4, 'session_002', 0, '请问政治复习从什么时候开始比较好？', '2026-05-04 17:25:35', '2026-05-06 02:08:26', 1);
INSERT INTO `yl_chat_message` VALUES (4, 4, 'session_002', 1, '政治复习建议从7-8月开始，前期以理解为主，10月后开始背诵和刷题。', '2026-05-04 17:25:35', '2026-05-06 02:08:26', 1);
INSERT INTO `yl_chat_message` VALUES (5, 5, 'session_003', 0, '英语基础很差，四级都没过，能考研吗？', '2026-05-04 17:25:35', '2026-05-04 17:25:35', 0);
INSERT INTO `yl_chat_message` VALUES (6, 5, 'session_003', 1, '当然可以！很多考研成功的同学英语基础也不好。关键是坚持背单词和做阅读，建议从考研核心词汇开始。', '2026-05-04 17:25:35', '2026-05-04 17:25:35', 0);
INSERT INTO `yl_chat_message` VALUES (7, 6, 'session_004', 0, '数学一高数部分怎么学？', '2026-05-04 17:25:35', '2026-05-04 17:25:35', 0);
INSERT INTO `yl_chat_message` VALUES (8, 6, 'session_004', 1, '高数复习建议跟汤家凤或张宇老师。重点是极限、导数、积分、级数这几大块。一定要动手算题，不能只看不练。', '2026-05-04 17:25:35', '2026-05-04 17:25:35', 0);
INSERT INTO `yl_chat_message` VALUES (9, 7, 'session_005', 0, '择校方面有什么建议吗？', '2026-05-04 17:25:35', '2026-05-04 17:25:35', 0);
INSERT INTO `yl_chat_message` VALUES (10, 7, 'session_005', 1, '择校要考虑：自身实力评估、地域偏好、专业排名、报录比、就业导向。建议使用我们的择校测评工具进行匹配。', '2026-05-04 17:25:35', '2026-05-04 17:25:35', 0);
INSERT INTO `yl_chat_message` VALUES (11, 8, 'session_006', 0, '复试一般考什么内容？', '2026-05-04 17:25:35', '2026-05-04 17:25:35', 0);
INSERT INTO `yl_chat_message` VALUES (12, 8, 'session_006', 1, '复试通常包括：专业课笔试、英语口语听力、综合面试。部分专业还有实验/实操考核。具体要求请查看目标院校的复试细则。', '2026-05-04 17:25:35', '2026-05-04 17:25:35', 0);
INSERT INTO `yl_chat_message` VALUES (13, 9, 'session_007', 0, '调剂需要注意什么？', '2026-05-04 17:25:35', '2026-05-04 17:25:35', 0);
INSERT INTO `yl_chat_message` VALUES (14, 9, 'session_007', 1, '调剂要点：1.关注研招网调剂服务系统开放时间；2.提前了解目标院校调剂政策；3.准备好个人材料；4.主动联系导师；5.不要错过黄金48小时。', '2026-05-04 17:25:35', '2026-05-04 17:25:35', 0);
INSERT INTO `yl_chat_message` VALUES (15, 10, 'session_008', 0, '在职考研时间怎么安排？', '2026-05-04 17:25:35', '2026-05-04 17:25:35', 0);
INSERT INTO `yl_chat_message` VALUES (16, 10, 'session_008', 1, '在职考研建议：早起1小时+晚睡1小时+周末全天。通勤时间可以听网课或背单词。关键是提高效率，利用碎片化时间。', '2026-05-04 17:25:35', '2026-05-04 17:25:35', 0);
INSERT INTO `yl_chat_message` VALUES (17, 11, 'session_009', 0, '请问专业课参考书目在哪里查？', '2026-05-04 17:26:09', '2026-05-04 17:26:09', 0);
INSERT INTO `yl_chat_message` VALUES (18, 11, 'session_009', 1, '可以在目标院校研究生院官网查看招生简章和专业目录，里面会列出参考书目。', '2026-05-04 17:26:09', '2026-05-04 17:26:09', 0);
INSERT INTO `yl_chat_message` VALUES (19, 12, 'session_010', 0, '考研需要报班吗？', '2026-05-04 17:26:09', '2026-05-04 17:26:09', 0);
INSERT INTO `yl_chat_message` VALUES (20, 12, 'session_010', 1, '报班与否取决于个人情况。自学能力强、自制力好的同学可以自学；基础薄弱或需要系统指导的同学可以考虑报班。', '2026-05-04 17:26:09', '2026-05-04 17:26:09', 0);
INSERT INTO `yl_chat_message` VALUES (21, 1, '7cdb6342-8fb1-44e7-9422-41c937fd4f85', 1, '\"考研英语资料\"', '2026-05-04 19:00:09', '2026-05-04 19:00:09', 0);
INSERT INTO `yl_chat_message` VALUES (22, 1, '7cdb6342-8fb1-44e7-9422-41c937fd4f85', 2, '考研英语备考主要围绕词汇、语法、阅读、写作和真题练习展开。建议你从基础词汇和语法入手，逐步过渡到真题训练，重点提升阅读理解和写作能力。系统会自动为你推荐相关资料，请关注后续链接。\n\n📚 **平台相关资料推荐**：\n\n• [2026考研英语一真题解析](path=/materials/2) - 89.00元 [kaoshidagang]\n', '2026-05-04 19:00:12', '2026-05-04 19:00:12', 0);
INSERT INTO `yl_chat_message` VALUES (23, 3, 'session_001', 1, '\"生成个性化学习计划\"', '2026-05-04 22:02:38', '2026-05-06 01:14:24', 1);
INSERT INTO `yl_chat_message` VALUES (24, 3, 'session_001', 2, '好的，我来帮您生成一个考研择校的个性化分析框架。请先回答以下几个问题，以便我为您提供更精准的建议：\n\n1. **您的本科专业**是什么？\n2. **目标专业方向**（是否跨考？跨考的话想考哪个专业？）\n3. **您的英语/数学基础**如何？（比如：四六级分数、是否学过数学一/二/三）\n4. **目标地区**有偏好吗？（比如：北上广、江浙沪、还是想回家乡发展？）\n5. **对学校层次的要求**？（985/211/双一流，还是求稳上岸即可？）\n\n请提供以上信息，我将为您分析院校梯度、备考难度和复习规划建议。\n\n📚 **平台相关资料推荐**：\n\n• [2026考研政治全程班讲义](path=/materials/1) - 46.00元 [public]\n• [2026考研英语一真题解析](path=/materials/2) - 30.00元 [public]\n• [数学一复习全书](path=/materials/3) - 25.50元 [public]\n• [计算机408历年真题详解](path=/materials/4) - 50.00元 [major]\n• [西医综合306考点速记](path=/materials/5) - 68.00元 [kaoshidagang]\n', '2026-05-04 22:02:43', '2026-05-06 01:14:24', 1);
INSERT INTO `yl_chat_message` VALUES (25, 3, 'session_001', 1, '\"个性化学习计划根据目标院校专业和时间安排生成备考计划、用户学习画像分析识别学习风格和薄弱科目，以及每日学习建议和进度提醒。\"', '2026-05-04 22:08:05', '2026-05-06 01:14:24', 1);
INSERT INTO `yl_chat_message` VALUES (26, 3, 'session_001', 2, '好的，您需要的是一个**个性化备考计划生成系统**，它需要包含以下核心功能：\n\n1. **目标院校专业输入** → 自动匹配该专业的考试科目、参考范围、历年分数线\n2. **时间轴规划** → 根据当前日期（如距离考研还有X个月）生成基础→强化→冲刺阶段任务\n3. **学习画像分析** → 通过您提供的英语/数学基础、每日可用学习时长、薄弱科目（如政治背诵弱、数学计算慢）来调整计划侧重点\n4. **每日建议与提醒** → 生成每日任务清单（如“背单词50个+做2篇阅读+复习高数第三章错题”），并支持进度追踪\n\n**要实现这个计划，请您先提供以下信息：**\n\n- **目标院校及专业**（例如：北京大学 计算机科学与技术）\n- **当前备考阶段**（刚开始/已复习一轮/强化中）\n- **每日可投入学习时长**（如：4小时/6小时/8小时）\n- **薄弱科目**（如：英语阅读、数学证明题、政治分析题）\n- **学习风格偏好**（如：喜欢看视频课/刷题/背诵为主）\n\n提供后，我将为您生成一份**分阶段、可执行**的备考计划框架，并给出每日学习建议。\n\n📚 **平台相关资料推荐**：\n\n• [2026考研政治全程班讲义](path=/materials/1) - 46.00元 [public]\n• [2026考研英语一真题解析](path=/materials/2) - 30.00元 [public]\n• [数学一复习全书](path=/materials/3) - 25.50元 [public]\n• [计算机408历年真题详解](path=/materials/4) - 50.00元 [major]\n• [西医综合306考点速记](path=/materials/5) - 68.00元 [kaoshidagang]\n\n📚 **平台相关资料推荐**：\n\n• [人工智能专业深度解读](path=/materials/16) - 35.00元 [专业课]\n• [法硕非法学入门必读](path=/materials/18) - 42.00元 [专业课]\n', '2026-05-04 22:08:12', '2026-05-06 01:14:24', 1);
INSERT INTO `yl_chat_message` VALUES (27, 3, 'session_001', 1, '\"ai学习助手\"', '2026-05-04 22:08:48', '2026-05-06 01:14:24', 1);
INSERT INTO `yl_chat_message` VALUES (28, 3, 'session_001', 2, '您好！“AI学习助手”是考研备考中非常实用的工具概念。作为您的智能助手，我可以帮您实现以下功能：\n\n**1. 智能答疑**\n- 随时提问考研政策、数学公式、英语长难句、政治知识点等\n- 根据您的错题记录，自动推送同类练习题\n\n**2. 个性化学习路径**\n- 分析您的学习数据（如：哪类题型错误率高、复习进度快慢）\n- 动态调整每日任务：比如发现您“极限计算”薄弱，会安排专项训练\n\n**3. 进度提醒与激励**\n- 每日推送学习计划（如：“今天建议完成：英语阅读2篇+数学第三章复习”）\n- 阶段性目标提醒（如：“距离考研还有100天，请完成第一轮复习”）\n\n**4. 模拟测试与评估**\n- 生成模拟卷并批改，给出分数段分析和提分建议\n\n**您希望我现在为您做哪方面的帮助？**\n- 生成一份**每日学习计划**（需提供目标院校、每日可用时间、薄弱科目）\n- 解答**具体知识点**（如：英语阅读技巧、政治马原原理）\n- 分析**院校数据**（如：某专业近3年报录比、分数线趋势）\n\n请告诉我您的需求，我将立即为您服务！\n\n📚 **平台相关资料推荐**：\n\n• [2026考研政治全程班讲义](path=/materials/1) - 46.00元 [public]\n• [2026考研英语一真题解析](path=/materials/2) - 30.00元 [public]\n• [数学一复习全书](path=/materials/3) - 25.50元 [public]\n• [计算机408历年真题详解](path=/materials/4) - 50.00元 [major]\n• [西医综合306考点速记](path=/materials/5) - 68.00元 [kaoshidagang]\n\n📚 **平台相关资料推荐**：\n\n• [2026考研政治全程班讲义](path=/materials/1) - 46.00元 [public]\n• [2026考研英语一真题解析](path=/materials/2) - 30.00元 [public]\n• [数学一复习全书](path=/materials/3) - 25.50元 [public]\n• [计算机408历年真题详解](path=/materials/4) - 50.00元 [major]\n• [西医综合306考点速记](path=/materials/5) - 68.00元 [kaoshidagang]\n', '2026-05-04 22:08:55', '2026-05-06 01:14:24', 1);
INSERT INTO `yl_chat_message` VALUES (29, 3, 'session_001', 1, '\"设置进度提醒\"', '2026-05-04 22:09:02', '2026-05-06 01:14:24', 1);
INSERT INTO `yl_chat_message` VALUES (30, 3, 'session_001', 2, '好的，我来帮您设置考研备考进度提醒。请提供以下信息：\n\n**1. 目标节点提醒**\n- 您希望提醒什么内容？（例如：每天学习任务、每周复习总结、阶段性目标完成）\n- 提醒频率？（每天/每周/特定日期）\n\n**2. 具体提醒事项**\n- 例如：\n  - “每天早8点提醒背单词”\n  - “每周日晚提醒整理错题本”\n  - “距离考研100天时提醒进入冲刺阶段”\n\n**3. 您的当前备考阶段**\n- 刚开始复习 / 第一轮进行中 / 强化阶段 / 冲刺阶段\n\n**示例提醒设置：**\n> 每天 7:00 提醒：今日学习计划（英语单词50个+数学一章+政治1节）\n> 每周日 20:00 提醒：本周错题复盘\n> 2025年10月15日 提醒：考研报名截止\n\n请告诉我您需要的具体提醒内容，我将为您生成一份可执行的进度提醒方案！\n\n📚 **平台相关资料推荐**：\n\n• [2026考研政治全程班讲义](path=/materials/1) - 46.00元 [public]\n• [2026考研英语一真题解析](path=/materials/2) - 30.00元 [public]\n• [数学一复习全书](path=/materials/3) - 25.50元 [public]\n• [计算机408历年真题详解](path=/materials/4) - 50.00元 [major]\n• [西医综合306考点速记](path=/materials/5) - 68.00元 [kaoshidagang]\n\n📚 **平台相关资料推荐**：\n\n• [2026考研政治全程班讲义](path=/materials/1) - 46.00元 [public]\n• [2026考研英语一真题解析](path=/materials/2) - 30.00元 [public]\n• [数学一复习全书](path=/materials/3) - 25.50元 [public]\n• [计算机408历年真题详解](path=/materials/4) - 50.00元 [major]\n• [西医综合306考点速记](path=/materials/5) - 68.00元 [kaoshidagang]\n', '2026-05-04 22:09:08', '2026-05-06 01:14:24', 1);
INSERT INTO `yl_chat_message` VALUES (31, 3, 'session_001', 1, '\"我是软件工程专业的考研生，希望复习考研数学，请推荐资料\"', '2026-05-04 22:41:42', '2026-05-06 01:14:24', 1);
INSERT INTO `yl_chat_message` VALUES (32, 3, 'session_001', 2, '您好！作为软件工程专业的考研生，数学通常是重中之重。关于复习资料，我为您提供以下学习建议：\n\n**数学复习的核心方法：**\n\n1. **基础阶段**：建议先系统梳理教材中的核心概念、定理和公式，建立完整的知识框架。重点掌握高等数学、线性代数和概率论的基础内容。\n\n2. **强化阶段**：通过大量练习巩固知识点，建议按题型分类训练，如极限、导数、积分、线性方程组、概率分布等。注意总结解题方法和易错点。\n\n3. **冲刺阶段**：以真题和模拟题为主，限时训练，培养考场节奏。重点分析近5年真题的出题规律和难度分布。\n\n**针对软件工程专业的特点：**\n- 数学一通常包含高等数学、线性代数、概率论与数理统计，与计算机专业关联紧密\n- 建议重点关注：矩阵运算、特征值与特征向量、概率分布、数理统计等与算法、机器学习相关的知识点\n- 复习时注意将数学理论与编程思维结合，例如用代码验证数学结论\n\n**学习建议：**\n- 每天保持2-3小时数学学习时间\n- 建立错题本，定期回顾\n- 每周进行一次阶段性测试，检验学习效果\n\n如需更具体的复习规划，请告知您的目标院校和当前复习进度，我可以为您生成个性化方案。\n\n📚 **平台相关资料推荐**：\n\n• [2026考研政治全程班讲义](path=/materials/1) - 46.00元 [public]\n• [2026考研英语一真题解析](path=/materials/2) - 30.00元 [public]\n• [数学一复习全书](path=/materials/3) - 25.50元 [public]\n• [计算机408历年真题详解](path=/materials/4) - 50.00元 [major]\n• [西医综合306考点速记](path=/materials/5) - 68.00元 [kaoshidagang]\n\n📚 **平台相关资料推荐**：\n\n• [数学一复习全书](path=/materials/3) - 25.50元 [public]\n', '2026-05-04 22:41:50', '2026-05-06 01:14:24', 1);
INSERT INTO `yl_chat_message` VALUES (33, 3, 'cab6ab8f-8f32-4f3d-b217-3ec7ec922fdd', 1, '\"平台有哪些功能\"', '2026-05-06 01:15:49', '2026-05-06 01:15:49', 0);
INSERT INTO `yl_chat_message` VALUES (34, 3, 'cab6ab8f-8f32-4f3d-b217-3ec7ec922fdd', 2, '您好！我们平台主要提供考研相关的智能服务，包括：考研政策解读、院校专业选择建议、各科目备考方法指导、复习规划制定、学习进度管理、模拟测试与错题分析等功能。您可以根据自己的需求，在平台内探索使用。\n\n📚 **平台相关资料推荐**：\n\n• [2026考研政治全程班讲义](path=/materials/1) - 46.00元 [public]\n• [2026考研英语一真题解析](path=/materials/2) - 30.00元 [public]\n• [数学一复习全书](path=/materials/3) - 25.50元 [public]\n• [计算机408历年真题详解](path=/materials/4) - 50.00元 [major]\n• [西医综合306考点速记](path=/materials/5) - 68.00元 [kaoshidagang]\n', '2026-05-06 01:15:59', '2026-05-06 01:15:59', 0);
INSERT INTO `yl_chat_message` VALUES (35, 3, 'cab6ab8f-8f32-4f3d-b217-3ec7ec922fdd', 1, '\"平台有哪些功能\"', '2026-05-06 01:17:53', '2026-05-06 01:17:53', 0);
INSERT INTO `yl_chat_message` VALUES (36, 3, 'cab6ab8f-8f32-4f3d-b217-3ec7ec922fdd', 2, '您好！我们平台主要提供考研相关的智能服务，包括：考研政策解读、院校专业选择建议、各科目备考方法指导、复习规划制定、学习进度管理、模拟测试与错题分析等功能。您可以根据自己的需求，在平台内探索使用。\n\n📚 **平台相关资料推荐**：\n\n• [2026考研政治全程班讲义](path=/materials/1) - 46.00元\n• [2026考研英语一真题解析](path=/materials/2) - 30.00元\n• [数学一复习全书](path=/materials/3) - 25.50元\n• [计算机408历年真题详解](path=/materials/4) - 50.00元\n• [西医综合306考点速记](path=/materials/5) - 68.00元\n', '2026-05-06 01:17:57', '2026-05-06 01:17:57', 0);
INSERT INTO `yl_chat_message` VALUES (37, 3, 'cab6ab8f-8f32-4f3d-b217-3ec7ec922fdd', 1, '\"平台有哪些功能\"', '2026-05-06 01:20:44', '2026-05-06 01:20:44', 0);
INSERT INTO `yl_chat_message` VALUES (38, 3, 'cab6ab8f-8f32-4f3d-b217-3ec7ec922fdd', 2, '您好！我们平台主要提供考研相关的智能服务，包括：考研政策解读、院校专业选择建议、各科目备考方法指导、复习规划制定、学习进度管理、模拟测试与错题分析等功能。您可以根据自己的需求，在平台内探索使用。\n\n📚 **平台相关资料推荐**：\n\n• [2026考研政治全程班讲义](path=/materials/1) - 46.00元\n• [2026考研英语一真题解析](path=/materials/2) - 30.00元\n• [数学一复习全书](path=/materials/3) - 25.50元\n• [计算机408历年真题详解](path=/materials/4) - 50.00元\n• [西医综合306考点速记](path=/materials/5) - 68.00元\n', '2026-05-06 01:20:46', '2026-05-06 01:20:46', 0);
INSERT INTO `yl_chat_message` VALUES (39, 3, 'cab6ab8f-8f32-4f3d-b217-3ec7ec922fdd', 1, '\"怎么联系客服\"', '2026-05-06 01:22:03', '2026-05-06 01:22:03', 0);
INSERT INTO `yl_chat_message` VALUES (40, 3, 'cab6ab8f-8f32-4f3d-b217-3ec7ec922fdd', 2, '您好，如需联系客服或反馈问题，请前往个人中心-帮助与反馈提交问题，或联系平台管理员处理。\n\n📚 **平台相关资料推荐**：\n\n• [2026考研政治全程班讲义](path=/materials/1) - 46.00元\n• [2026考研英语一真题解析](path=/materials/2) - 30.00元\n• [数学一复习全书](path=/materials/3) - 25.50元\n• [计算机408历年真题详解](path=/materials/4) - 50.00元\n• [西医综合306考点速记](path=/materials/5) - 68.00元\n', '2026-05-06 01:22:04', '2026-05-06 01:22:04', 0);
INSERT INTO `yl_chat_message` VALUES (41, 3, 'cab6ab8f-8f32-4f3d-b217-3ec7ec922fdd', 1, '\"如何下载资料\"', '2026-05-06 01:25:07', '2026-05-06 01:25:07', 0);
INSERT INTO `yl_chat_message` VALUES (42, 3, 'cab6ab8f-8f32-4f3d-b217-3ec7ec922fdd', 2, '您好，关于资料下载，建议您先了解自己的学习需求，选择适合的科目和资料类别进行学习。平台上的资料通常支持在线阅读或下载，具体操作方式请参考平台内的使用说明或帮助文档。\n\n如需进一步帮助，请前往个人中心-帮助与反馈提交问题。\n\n📚 **平台相关资料推荐**：\n\n• [2026考研政治全程班讲义](path=/materials/1) - 46.00元\n• [2026考研英语一真题解析](path=/materials/2) - 30.00元\n• [数学一复习全书](path=/materials/3) - 25.50元\n• [计算机408历年真题详解](path=/materials/4) - 50.00元\n• [西医综合306考点速记](path=/materials/5) - 68.00元\n\n📚 **平台相关资料推荐**：\n\n• [保研夏令营申请模板](path=/materials/13) - 19.00元\n• [计算机考研择校指南](path=/materials/11) - 1.00元\n• [数学一复习全书](path=/materials/3) - 25.50元\n• [考研院校信息大全](path=/materials/12) - 49.00元\n• [2026考研英语一真题解析](path=/materials/2) - 30.00元\n', '2026-05-06 01:25:10', '2026-05-06 01:25:10', 0);

-- ----------------------------
-- Table structure for yl_comment
-- ----------------------------
DROP TABLE IF EXISTS `yl_comment`;
CREATE TABLE `yl_comment`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `post_id` bigint NULL DEFAULT NULL COMMENT '帖子ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '内容',
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户ID',
  `nickname` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户昵称',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户头像',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint(1) NULL DEFAULT 0,
  `target_type` int NULL DEFAULT 1 COMMENT '目标类型: 1-帖子, 2-指南',
  `target_id` bigint NULL DEFAULT NULL COMMENT '目标ID',
  `parent_id` bigint NULL DEFAULT NULL COMMENT '父评论ID',
  `reply_to_user_id` bigint NULL DEFAULT NULL COMMENT '被回复用户ID',
  `reply_to_nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '被回复用户昵称',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '评论' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of yl_comment
-- ----------------------------
INSERT INTO `yl_comment` VALUES (1, 1, '楼主太厉害了！请问每天学习多久？', 4, '李四', 'https://picsum.photos/seed/comment1/100/100', '2026-05-04 17:25:35', '2026-05-04 17:44:46', 0, 3, 1, NULL, NULL, NULL);
INSERT INTO `yl_comment` VALUES (2, 1, '回复@李四：大概每天10-12小时，前期少点后期多点', 3, '张三', 'https://picsum.photos/seed/comment2/100/100', '2026-05-04 17:25:35', '2026-05-04 17:44:46', 0, 3, 1, 1, 4, '李四');
INSERT INTO `yl_comment` VALUES (3, 1, '请问专业课是怎么准备的？用的什么教材？', 5, '王五', 'https://picsum.photos/seed/comment3/100/100', '2026-05-04 17:25:35', '2026-05-04 17:44:46', 0, 3, 1, NULL, NULL, NULL);
INSERT INTO `yl_comment` VALUES (4, 1, '回复@王五：主要用王道的四本书，配合清华的历年真题', 3, '张三', 'https://picsum.photos/seed/comment4/100/100', '2026-05-04 17:25:35', '2026-05-04 17:44:46', 0, 3, 1, 3, 5, '王五');
INSERT INTO `yl_comment` VALUES (5, 2, '来得及！我也是二本，现在开始完全没问题', 3, '张三', 'https://picsum.photos/seed/comment5/100/100', '2026-05-04 17:25:35', '2026-05-04 17:44:46', 0, 3, 2, NULL, NULL, NULL);
INSERT INTO `yl_comment` VALUES (6, 2, '推荐汤家凤的高数课，适合基础差的同学', 7, '孙七', 'https://picsum.photos/seed/comment6/100/100', '2026-05-04 17:25:35', '2026-05-04 17:44:46', 0, 3, 2, NULL, NULL, NULL);
INSERT INTO `yl_comment` VALUES (7, 2, '关键是要坚持，每天至少保证4-5小时有效学习', 6, '赵六', 'https://picsum.photos/seed/comment7/100/100', '2026-05-04 17:25:35', '2026-05-04 17:44:46', 0, 3, 2, NULL, NULL, NULL);
INSERT INTO `yl_comment` VALUES (8, 3, '求分享！楼主好人一生平安', 6, '赵六', 'https://picsum.photos/seed/comment8/100/100', '2026-05-04 17:25:35', '2026-05-04 17:44:46', 0, 3, 3, NULL, NULL, NULL);
INSERT INTO `yl_comment` VALUES (9, 3, '已私信你了~', 5, '王五', 'https://picsum.photos/seed/comment9/100/100', '2026-05-04 17:25:35', '2026-05-04 17:44:46', 0, 3, 3, 1, 6, '赵六');
INSERT INTO `yl_comment` VALUES (10, 3, '我也想要一份，谢谢楼主', 8, '周八', 'https://picsum.photos/seed/comment10/100/100', '2026-05-04 17:25:35', '2026-05-04 17:44:46', 0, 3, 3, NULL, NULL, NULL);
INSERT INTO `yl_comment` VALUES (11, 4, '恭喜恭喜！是哪个学校呀？', 3, '张三', 'https://picsum.photos/seed/comment11/100/100', '2026-05-04 17:25:35', '2026-05-04 17:44:46', 0, 3, 4, NULL, NULL, NULL);
INSERT INTO `yl_comment` VALUES (12, 4, '回复@张三：是北京大学软件与微电子学院，方向是集成电路', 5, '王五', 'https://picsum.photos/seed/comment12/100/100', '2026-05-04 17:25:35', '2026-05-04 17:44:46', 0, 3, 4, 12, 3, '张三');
INSERT INTO `yl_comment` VALUES (13, 5, '同焦虑，一起加油！', 4, '李四', 'https://picsum.photos/seed/comment13/100/100', '2026-05-04 17:25:35', '2026-05-04 17:44:46', 0, 3, 5, NULL, NULL, NULL);
INSERT INTO `yl_comment` VALUES (14, 5, '建议制定详细计划，按部就班来就不慌', 6, '赵六', 'https://picsum.photos/seed/comment14/100/100', '2026-05-04 17:25:35', '2026-05-04 17:44:46', 0, 3, 5, NULL, NULL, NULL);
INSERT INTO `yl_comment` VALUES (15, 6, '请问英语一和英语二难度差多少？', 5, '王五', 'https://picsum.photos/seed/comment15/100/100', '2026-05-04 17:25:35', '2026-05-04 17:44:46', 0, 3, 6, NULL, NULL, NULL);
INSERT INTO `yl_comment` VALUES (16, 6, '英语二相对简单，阅读理解和翻译难度都低一些', 7, '孙七', 'https://picsum.photos/seed/comment16/100/100', '2026-05-04 17:25:35', '2026-05-04 17:44:46', 0, 3, 6, NULL, NULL, NULL);
INSERT INTO `yl_comment` VALUES (17, 7, '复试逆袭太励志了！请问面试有什么技巧？', 3, '张三', 'https://picsum.photos/seed/comment17/100/100', '2026-05-04 17:25:35', '2026-05-04 17:44:46', 0, 3, 7, NULL, NULL, NULL);
INSERT INTO `yl_comment` VALUES (18, 7, '关键是自信和真诚，不要不懂装懂', 8, '周八', 'https://picsum.photos/seed/comment18/100/100', '2026-05-04 17:25:35', '2026-05-04 17:44:46', 0, 3, 7, NULL, NULL, NULL);
INSERT INTO `yl_comment` VALUES (19, 8, '请问调剂系统什么时候开放？', 4, '李四', 'https://picsum.photos/seed/comment19/100/100', '2026-05-04 17:25:35', '2026-05-04 17:44:46', 0, 3, 8, NULL, NULL, NULL);
INSERT INTO `yl_comment` VALUES (20, 8, '一般在3月中旬，关注研招网通知', 9, '吴九', 'https://picsum.photos/seed/comment20/100/100', '2026-05-04 17:25:35', '2026-05-04 17:44:46', 0, 3, 8, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for yl_guide
-- ----------------------------
DROP TABLE IF EXISTS `yl_guide`;
CREATE TABLE `yl_guide`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '分类',
  `cover_img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `view_count` int NULL DEFAULT 0,
  `status` tinyint NULL DEFAULT 1,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint(1) NULL DEFAULT 0,
  `institution` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '报考院校',
  `major` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '报考专业',
  `comment_count` int NULL DEFAULT 0 COMMENT '评论数',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序值，越大越靠前',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'Admission Guides' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of yl_guide
-- ----------------------------
INSERT INTO `yl_guide` VALUES (1, '2026年清华大学硕士研究生招生简章', '<p>清华大学2026年计划招收硕士研究生约4000名，涵盖工学、理学、管理学等多个学科门类。</p><p>报考条件：国家承认学历的应届本科毕业生及自学考试届时可毕业本科生。</p>', 'zhaoshengjianzhang', 'https://picsum.photos/seed/guide1/400/250', 1255, 1, '2025-12-08 01:04:58', '2025-12-08 18:42:25', 0, '清华大学', '计算机科学与技术', 23, 1);
INSERT INTO `yl_guide` VALUES (2, '2026年北京大学硕士研究生招生简章', '<p>北京大学创建于1898年，是中国近代第一所国立综合性大学。</p><p>2026年招收学术型硕士和专业学位硕士研究生。</p>', 'zhaoshengjianzhang', 'https://picsum.photos/seed/guide2/400/250', 980, 1, '2026-02-23 02:37:58', '2026-02-24 08:31:30', 0, '北京大学', '法学', 18, 2);
INSERT INTO `yl_guide` VALUES (3, '2026年复旦大学硕士研究生招生简章', '<p>复旦大学是国家双一流A类建设高校。</p><p>学制：学术型硕士一般为3年，专业学位硕士一般为2-3年。</p>', 'zhaoshengjianzhang', 'https://picsum.photos/seed/guide3/400/250', 856, 1, '2026-01-02 16:16:41', '2026-01-04 17:36:56', 0, '复旦大学', '新闻传播学', 12, 3);
INSERT INTO `yl_guide` VALUES (4, '2026年上海交通大学硕士研究生招生简章', '<p>上交大拥有船舶与海洋工程、机械工程等优势学科。</p><p>奖助体系完善，提供学业奖学金、国家助学金等多种资助。</p>', 'zhaoshengjianzhang', 'https://picsum.photos/seed/guide4/400/250', 743, 1, '2026-01-19 21:35:39', '2026-01-20 17:58:43', 0, '上海交通大学', '机械工程', 15, 4);
INSERT INTO `yl_guide` VALUES (5, '2026年浙江大学硕士研究生招生简章', '<p>浙江大学涵盖12个学科门类，学科实力雄厚。</p><p>紫金港校区为主校区，玉泉、西溪、华家池等校区协同发展。</p>', 'zhaoshengjianzhang', 'https://picsum.photos/seed/guide5/400/250', 693, 1, '2025-11-26 23:47:20', '2025-11-27 15:03:22', 0, '浙江大学', '控制科学与工程', 9, 5);
INSERT INTO `yl_guide` VALUES (6, '计算机科学与技术专业目录（0812）', '<p>研究方向：计算机系统结构、软件与理论、计算机应用技术。</p><p>主干课程：数据结构、算法分析、操作系统、计算机网络、编译原理。</p>', 'zhuanyemulu', 'https://picsum.photos/seed/guide6/400/250', 1567, 1, '2026-02-21 17:38:42', '2026-02-23 04:15:47', 0, '教育部', '计算机科学与技术', 32, 1);
INSERT INTO `yl_guide` VALUES (7, '软件工程专业目录（0835）', '<p>核心课程：软件需求工程、软件设计与架构、软件测试与质量保证。</p><p>就业方向：互联网公司、软件开发企业、金融机构IT部门。</p>', 'zhuanyemulu', 'https://picsum.photos/seed/guide7/400/250', 1234, 1, '2026-03-17 01:09:17', '2026-03-17 17:29:44', 0, '教育部', '软件工程', 28, 2);
INSERT INTO `yl_guide` VALUES (8, '人工智能专业目录（交叉学科）', '<p>研究领域：机器学习、深度学习、自然语言处理、计算机视觉。</p><p>前沿方向：大语言模型、多模态学习、强化学习、AI for Science。</p>', 'zhuanyemulu', 'https://picsum.photos/seed/guide8/400/250', 1890, 1, '2025-11-10 07:08:24', '2025-11-13 06:03:20', 0, '教育部', '人工智能', 45, 3);
INSERT INTO `yl_guide` VALUES (9, '电子信息类专业目录（0854）', '<p>包含方向：新一代电子信息技术、通信工程、集成电路工程、计算机技术。</p><p>专业学位类别，注重实践能力培养。</p>', 'zhuanyemulu', 'https://picsum.photos/seed/guide9/400/250', 1456, 1, '2026-03-15 05:16:09', '2026-03-17 05:35:53', 0, '教育部', '电子信息', 37, 4);
INSERT INTO `yl_guide` VALUES (10, '临床医学专业目录（1002）', '<p>专业方向：内科学、外科学、妇产科学、儿科学、神经病学。</p><p>培养模式：5+3一体化或5+3+X规范化培训。</p>', 'zhuanyemulu', 'https://picsum.photos/seed/guide10/400/250', 1345, 1, '2026-01-01 11:51:25', '2026-01-04 03:51:53', 0, '教育部', '临床医学', 30, 5);
INSERT INTO `yl_guide` VALUES (11, '2026年考研思想政治理论大纲', '<p>试卷满分100分，考试时间180分钟。</p><p>题型：单选题16分、多选题34分、分析题50分。</p><p>考查范围：马克思主义基本原理、毛泽东思想、中国近现代史纲要等。</p>', 'kaoshidagang', 'https://picsum.photos/seed/guide11/400/250', 2347, 1, '2026-01-27 09:51:39', '2026-01-29 23:03:00', 0, '教育部考试中心', '政治', 67, 1);
INSERT INTO `yl_guide` VALUES (12, '2026年考研英语（一）大纲', '<p>完形填空10分、阅读理解40分（含新题型）、翻译10分、写作40分。</p><p>词汇要求：掌握5500左右词汇及相关词组。</p>', 'kaoshidagang', 'https://picsum.photos/seed/guide12/400/250', 2567, 1, '2025-09-30 08:38:45', '2025-09-30 19:10:16', 0, '教育部考试中心', '英语一', 72, 2);
INSERT INTO `yl_guide` VALUES (13, '2026年考研数学（一）大纲', '<p>高等数学60%、线性代数20%、概率论与数理统计20%。</p><p>适用专业：工学门类中对数学要求较高的专业。</p>', 'kaoshidagang', 'https://picsum.photos/seed/guide13/400/250', 2891, 1, '2025-11-06 03:31:29', '2025-11-06 12:48:19', 0, '教育部考试中心', '数学一', 89, 3);
INSERT INTO `yl_guide` VALUES (14, '2026年计算机408考试大纲', '<p>数据结构45分、计算机组成原理45分、操作系统35分、计算机网络25分。</p><p>总分150分，考试时间180分钟。</p>', 'kaoshidagang', 'https://picsum.photos/seed/guide14/400/250', 3124, 1, '2026-01-27 13:09:29', '2026-01-27 22:45:47', 0, '教育部考试中心', '计算机408', 95, 4);
INSERT INTO `yl_guide` VALUES (15, '2026年西医综合306考试大纲', '<p>总分300分，生理学14%、生物化学12%、病理学12%、内科学33%、外科学23%。</p><p>注重临床思维和综合应用能力的考查。</p>', 'kaoshidagang', 'https://picsum.photos/seed/guide15/400/250', 1876, 1, '2025-12-27 02:17:11', '2025-12-28 05:15:17', 0, '教育部考试中心', '西医综合', 52, 5);
INSERT INTO `yl_guide` VALUES (16, '清华大学计算机系复试细则', '<p>复试分数线：总分380分，单科55/85。</p><p>复试形式：笔试（专业基础）+面试（外语+专业综合）。</p>', 'fushixize', 'https://picsum.photos/seed/guide16/400/250', 1876, 1, '2025-10-14 21:01:14', '2025-10-17 14:58:28', 0, '清华大学', '计算机科学与技术', 56, 1);
INSERT INTO `yl_guide` VALUES (17, '北京大学光华管理学院复试办法', '<p>复试权重：初试50% + 复试50%。</p><p>复试内容：专业课笔试30% + 综合面试50% + 外语测试20%。</p>', 'fushixize', 'https://picsum.photos/seed/guide17/400/250', 1655, 1, '2026-03-21 18:48:57', '2026-03-21 21:13:45', 0, '北京大学', '工商管理(MBA)', 48, 2);
INSERT INTO `yl_guide` VALUES (18, '浙江大学医学院复试录取办法', '<p>复试资格：达到学校公布的复试分数线。</p><p>考核内容：临床技能操作 + 综合素质评价 + 专业笔试。</p>', 'fushixize', 'https://picsum.photos/seed/guide18/400/250', 1567, 1, '2025-11-03 00:18:57', '2025-11-04 04:35:08', 0, '浙江大学', '临床医学', 51, 3);
INSERT INTO `yl_guide` VALUES (19, '复旦大学新闻学院复试方案', '<p>复试流程：签到抽签 -> 专业课笔试 -> 外语面试 -> 专业综合面试。</p><p>资格审查需携带身份证、准考证、学历证书原件。</p>', 'fushixize', 'https://picsum.photos/seed/guide19/400/250', 1432, 1, '2025-09-15 04:39:49', '2025-09-15 16:04:17', 0, '复旦大学', '新闻传播学', 42, 4);
INSERT INTO `yl_guide` VALUES (20, '上海交通大学机械学院复试实施细则', '<p>差额复试比例：不低于120%。</p><p>复试科目：机械设计基础或自动控制原理（二选一）+ 实践环节考核。</p>', 'fushixize', 'https://picsum.photos/seed/guide20/400/250', 1298, 1, '2026-01-03 13:46:09', '2026-01-05 00:05:18', 0, '上海交通大学', '机械工程', 37, 5);

-- ----------------------------
-- Table structure for yl_home_config
-- ----------------------------
DROP TABLE IF EXISTS `yl_home_config`;
CREATE TABLE `yl_home_config`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'CAROUSEL, NOTICE',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `img_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `link_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `sort_order` int NULL DEFAULT 0,
  `status` tinyint NULL DEFAULT 1,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint(1) NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'Home Config' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of yl_home_config
-- ----------------------------
INSERT INTO `yl_home_config` VALUES (1, 'banner', '2026考研招生简章发布', 'https://picsum.photos/seed/home1/800/400', '/guide/list?category=zhaoshengjianzhang', 1, 1, '2026-05-04 17:23:25', '2026-05-04 17:44:46', 0);
INSERT INTO `yl_home_config` VALUES (2, 'banner', '名师直播：考研政治冲刺押题', 'https://picsum.photos/seed/home2/800/400', '/news/1', 2, 1, '2026-05-04 17:23:25', '2026-05-04 17:44:46', 0);
INSERT INTO `yl_home_config` VALUES (3, 'banner', '限时优惠：全套考研资料包', 'https://picsum.photos/seed/home3/800/400', '/material/list?category=kaoshidagang', 3, 1, '2026-05-04 17:23:25', '2026-05-04 17:44:46', 0);
INSERT INTO `yl_home_config` VALUES (4, 'banner', '复试通关秘籍免费领', 'https://picsum.photos/seed/home4/800/400', '/guide/list?category=fushixize', 4, 1, '2026-05-04 17:23:25', '2026-05-04 17:44:46', 0);
INSERT INTO `yl_home_config` VALUES (5, 'banner', '考研院校数据库上线', 'https://picsum.photos/seed/home5/800/400', '/material/list?category=zhaoshengjianzhang', 5, 1, '2026-05-04 17:23:25', '2026-05-04 17:44:46', 0);
INSERT INTO `yl_home_config` VALUES (6, 'notice', '2026年考研初试时间确定：12月20-21日', 'https://picsum.photos/seed/home6/800/400', '/news/6', 1, 1, '2026-05-04 17:23:25', '2026-05-04 17:44:46', 0);
INSERT INTO `yl_home_config` VALUES (7, 'notice', '推免服务系统9月28日开放', 'https://picsum.photos/seed/home7/800/400', '/news/2', 2, 1, '2026-05-04 17:23:25', '2026-05-04 17:44:46', 0);
INSERT INTO `yl_home_config` VALUES (8, 'notice', '网上报名时间：10月8日至25日', 'https://picsum.photos/seed/home8/800/400', '/news/1', 3, 1, '2026-05-04 17:23:25', '2026-05-04 17:44:46', 0);
INSERT INTO `yl_home_config` VALUES (9, 'notice', '少数民族骨干计划招生简章发布', 'https://picsum.photos/seed/home9/800/400', '/news/8', 4, 1, '2026-05-04 17:23:25', '2026-05-04 17:44:46', 0);
INSERT INTO `yl_home_config` VALUES (10, 'notice', '退役大学生士兵专项计划说明', 'https://picsum.photos/seed/home10/800/400', '/news/11', 5, 1, '2026-05-04 17:23:25', '2026-05-04 17:44:46', 0);
INSERT INTO `yl_home_config` VALUES (11, 'hot', '清华大学计算机系复试细则', 'https://picsum.photos/seed/home11/800/400', '/guide/16', 1, 1, '2026-05-04 17:23:25', '2026-05-04 17:44:46', 0);
INSERT INTO `yl_home_config` VALUES (12, 'hot', '2026年考研英语（一）大纲', 'https://picsum.photos/seed/home12/800/400', '/guide/12', 2, 1, '2026-05-04 17:23:25', '2026-05-04 17:44:46', 0);
INSERT INTO `yl_home_config` VALUES (13, 'hot', '计算机408历年真题详解', 'https://picsum.photos/seed/home13/800/400', '/material/4', 3, 1, '2026-05-04 17:23:25', '2026-05-04 17:44:46', 0);
INSERT INTO `yl_home_config` VALUES (14, 'hot', '双非一本逆袭985计算机经验帖', 'https://picsum.photos/seed/home14/800/400', '/post/1', 4, 1, '2026-05-04 17:23:25', '2026-05-04 17:44:46', 0);
INSERT INTO `yl_home_config` VALUES (15, 'hot', '数学一复习全书', 'https://picsum.photos/seed/home15/800/400', '/material/3', 5, 1, '2026-05-04 17:23:25', '2026-05-04 17:44:46', 0);
INSERT INTO `yl_home_config` VALUES (16, 'recommend', '2026考研政治全程班讲义', 'https://picsum.photos/seed/home16/800/400', '/material/1', 1, 1, '2026-05-04 17:23:25', '2026-05-04 17:44:46', 0);
INSERT INTO `yl_home_config` VALUES (17, 'recommend', '北大光华MBA面试宝典', 'https://picsum.photos/seed/home17/800/400', '/material/7', 2, 1, '2026-05-04 17:23:25', '2026-05-04 17:44:46', 0);
INSERT INTO `yl_home_config` VALUES (18, 'recommend', '考研院校信息大全', 'https://picsum.photos/seed/home18/800/400', '/material/12', 3, 1, '2026-05-04 17:23:25', '2026-05-04 17:44:46', 0);
INSERT INTO `yl_home_config` VALUES (19, 'recommend', '在职考研上岸北大的血泪史', 'https://picsum.photos/seed/home19/800/400', '/post/5', 4, 1, '2026-05-04 17:23:25', '2026-05-04 17:44:46', 0);
INSERT INTO `yl_home_config` VALUES (20, 'recommend', '人工智能专业深度解读', 'https://picsum.photos/seed/home20/800/400', '/material/16', 5, 1, '2026-05-04 17:23:25', '2026-05-04 17:44:46', 0);

-- ----------------------------
-- Table structure for yl_material
-- ----------------------------
DROP TABLE IF EXISTS `yl_material`;
CREATE TABLE `yl_material`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `price` decimal(10, 2) NOT NULL,
  `stock` int NULL DEFAULT 0,
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `cover_img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `file_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文件路径',
  `specs` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Specifications',
  `status` tinyint NULL DEFAULT 1 COMMENT '1: On Shelf, 0: Off Shelf',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint(1) NULL DEFAULT 0,
  `sales` int NULL DEFAULT 0 COMMENT '销量',
  `file_size` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文件大小 (如 15.2MB, 2.3GB)',
  `download_count` int NULL DEFAULT 0 COMMENT '下载次数',
  `tags` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标签 (逗号分隔，如 政治,英语,数学)',
  `apply_year` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '适用年份 (如 2026, 2027)',
  `author` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '作者/上传者',
  `rating` double NULL DEFAULT 0 COMMENT '评分 (1-5分)',
  `preview_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '预览内容 (目录或部分预览)',
  `file_format` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '资料格式 (PDF, 视频, 音频, 压缩包等)',
  `original_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '划线价',
  `flash_start_time` datetime NULL DEFAULT NULL COMMENT '活动开始时间',
  `flash_end_time` datetime NULL DEFAULT NULL COMMENT '活动结束时间',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序值，越大越靠前',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'Study Materials' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of yl_material
-- ----------------------------
INSERT INTO `yl_material` VALUES (1, '2026考研政治全程班讲义', '包含马原、毛中特、史纲、思修、形势与政策五大模块精讲', 46.00, 500, 'public', 'https://picsum.photos/seed/material1/400/250', '/files/politics_full.pdf', 'PDF|500页', 1, '2026-05-04 17:23:25', '2026-05-04 19:55:47', 0, 1234, NULL, 3567, '考研政治,全程班', '2026', '名师团队', 4.8, '第一章 马克思主义基本原理...', 'PDF', 57.50, '2026-04-27 17:49:49', '2026-05-11 17:49:49', 1);
INSERT INTO `yl_material` VALUES (2, '2026考研英语一真题解析', '2015-2025年真题详细解析，含阅读长难句图解', 30.00, 800, 'public', 'https://picsum.photos/seed/material2/400/250', 'http://localhost:8080/files/49093c0f3950485dbbfde44771f26429.pdf', 'PDF|380页', 1, '2026-05-04 17:23:25', '2026-05-04 19:55:47', 0, 2345, '0.12MB', 5678, '考研英语,真题', '2026', '新东方团队', 4.9, '2015年 Text 1...', 'PDF', 37.50, '2026-04-27 17:49:49', '2026-05-11 17:49:49', 2);
INSERT INTO `yl_material` VALUES (3, '数学一复习全书', '李永乐团队编写，涵盖高数、线代、概率论全部考点', 25.50, 600, 'public', 'https://picsum.photos/seed/material3/400/250', '/files/math1_complete.pdf', 'PDF|720页', 1, '2026-05-04 17:23:25', '2026-05-04 19:55:47', 0, 3456, NULL, 7890, '考研数学,李永乐', '2026', '李永乐', 4.7, '第一章 函数与极限...', 'PDF', 31.88, '2026-04-27 17:49:49', '2026-05-11 17:49:49', 3);
INSERT INTO `yl_material` VALUES (4, '计算机408历年真题详解', '2009-2025年408真题，每题配有详细解题思路', 50.00, 400, 'major', 'https://picsum.photos/seed/material4/400/250', '/files/cs408_exams.pdf', 'PDF|650页', 1, '2026-05-04 17:23:25', '2026-05-04 19:55:47', 0, 1567, NULL, 4321, '408,计算机', '2026', '王道团队', 4.8, '2015年 数据结构真题...', 'PDF', 50.00, '2026-04-27 17:49:49', '2026-05-11 17:49:49', 4);
INSERT INTO `yl_material` VALUES (5, '西医综合306考点速记', '内科、外科、生化、病理核心考点口袋书', 68.00, 1000, 'kaoshidagang', 'https://picsum.photos/seed/material5/400/250', '/files/medical306_quick.pdf', 'PDF|280页', 1, '2026-05-04 17:23:25', '2026-05-04 17:49:49', 0, 987, NULL, 2345, '西医综合,306', '2026', '贺银成', 4.6, '第一章 内科学总论...', 'PDF', 88.00, '2026-04-27 17:49:49', '2026-05-11 17:49:49', 5);
INSERT INTO `yl_material` VALUES (6, '清华大学计算机考研经验贴', '400+学长学姐亲授备考策略和时间规划', 1.00, 999, 'major', 'https://picsum.photos/seed/material6/400/250', '/files/tsinghua_cs_exp.pdf', 'PDF|120页', 1, '2026-05-04 17:23:25', '2026-05-04 19:55:47', 0, 2345, NULL, 6789, '清华,计算机,经验', '2026', '上岸学长', 4.9, '写在前面：我的基本情况是...', 'PDF', 1.00, '2026-04-27 17:49:49', '2026-05-11 17:49:49', 6);
INSERT INTO `yl_material` VALUES (7, '北大光华MBA面试宝典', '历年真题+模拟面试+案例分析技巧', 13.00, 300, 'interview', 'https://picsum.photos/seed/material7/400/250', '/files/pku_mba_interview.pdf', 'PDF|250页', 1, '2026-05-04 17:23:25', '2026-05-04 19:55:47', 0, 876, NULL, 1987, 'MBA,北大,面试', '2026', '光华校友', 4.7, '第一部分 MBA面试概述...', 'PDF', 13.00, '2026-04-27 17:49:49', '2026-05-11 17:49:49', 7);
INSERT INTO `yl_material` VALUES (8, '浙大医学院复试攻略', '临床技能操作视频+笔试重点+导师信息汇总', 10.00, 450, 'interview', 'https://picsum.photos/seed/material8/400/250', '/files/zju_medical_fushi.zip', 'ZIP|视频+文档', 1, '2026-05-04 17:23:25', '2026-05-04 19:55:47', 0, 654, NULL, 1543, '浙大,医学,复试', '2026', '医学生团队', 4.5, '复试流程概览...', 'ZIP', 10.00, '2026-04-27 17:49:49', '2026-05-11 17:49:49', 8);
INSERT INTO `yl_material` VALUES (9, '复旦新传考研专题整理', '传播学理论、新媒体研究、广告学案例合集', 79.00, 600, 'fushixize', 'https://picsum.photos/seed/material9/400/250', '/files/fudan_news_topic.pdf', 'PDF|320页', 1, '2026-05-04 17:23:25', '2026-05-04 17:49:49', 0, 1123, NULL, 2876, '复旦,新闻,专题', '2026', '新传学姐', 4.8, '专题一 传播学经典理论...', 'PDF', 109.00, '2026-04-27 17:49:49', '2026-05-11 17:49:49', 9);
INSERT INTO `yl_material` VALUES (10, '上交机械复试实验指导', '金工实习、机械设计实验要点总结', 1.00, 700, 'interview', 'https://picsum.photos/seed/material10/400/250', '/files/sjtu_mech_lab.pdf', 'PDF|150页', 1, '2026-05-04 17:23:25', '2026-05-04 19:55:47', 0, 432, NULL, 1098, '上交,机械,实验', '2026', '机原学长', 4.6, '实验一 金工车削操作...', 'PDF', 2.00, '2026-04-27 17:49:49', '2026-05-11 17:49:49', 10);
INSERT INTO `yl_material` VALUES (11, '计算机考研择校指南', '985/211院校CS专业对比分析，含报录比、分数线', 1.00, 9999, 'major', 'https://picsum.photos/seed/material11/400/250', '/files/cs_school_guide.pdf', 'PDF|180页', 1, '2026-05-04 17:23:25', '2026-05-04 19:55:47', 0, 4567, NULL, 12345, '择校,计算机,985', '2026', '研招网整理', 4.7, '前言：如何选择适合自己的院校...', 'PDF', 2.00, '2026-04-27 17:49:49', '2026-05-11 17:49:49', 11);
INSERT INTO `yl_material` VALUES (12, '考研院校信息大全', '全国500+所院校招生简章、专业目录、历年数据', 49.00, 8888, 'zhaoshengjianzhang', 'https://picsum.photos/seed/material12/400/250', 'http://localhost:8080/files/b377d76e10dd4334bcd3d3c430e81e23.pdf', 'Excel|50MB', 1, '2026-05-04 17:23:25', '2026-05-04 17:49:49', 0, 3456, '0.12MB', 9876, '院校,招生,简章', '2026', '研考助手', 4.5, '数据说明...', 'PDF', 69.00, '2026-04-27 17:49:49', '2026-05-11 17:49:49', 12);
INSERT INTO `yl_material` VALUES (13, '保研夏令营申请模板', '个人陈述、推荐信、简历模板合集', 19.00, 9999, 'zhaoshengjianzhang', 'https://picsum.photos/seed/material13/400/250', '/files/baoyan_templates.docx', 'DOCX|50页', 1, '2026-05-04 17:23:25', '2026-05-04 17:49:49', 0, 5678, NULL, 15678, '保研,夏令营,模板', '2026', '保研成功者', 4.9, '个人陈述写作要点...', 'DOCX', 35.00, '2026-04-27 17:49:49', '2026-05-11 17:49:49', 13);
INSERT INTO `yl_material` VALUES (14, '考研调剂信息汇总', '每日更新各校调剂名额、联系方式、复试时间', 0.00, 0, 'interview', 'https://picsum.photos/seed/material14/400/250', 'http://localhost:8080/files/8204f67875774a51b11d72365123a70c.pdf', '在线查看', 1, '2026-05-04 17:23:25', '2026-05-04 19:44:32', 0, 89, '0.12MB', 2345, '调剂,信息,实时', '2026', '系统自动', 4.3, '最新调剂信息...', 'PDF', 0.00, '2026-04-27 17:49:49', '2026-05-11 17:49:49', 14);
INSERT INTO `yl_material` VALUES (15, '推免生面试常见问题', '100道高频问题及参考回答', 0.00, 9999, 'interview', 'https://picsum.photos/seed/material15/400/250', '/files/tuimian_100qa.pdf', 'PDF|80页', 1, '2026-05-04 17:23:25', '2026-05-04 19:44:32', 0, 2345, NULL, 6789, '推免,面试,问答', '2026', '面霸学长', 4.6, 'Q1: 请做一个自我介绍...', 'PDF', 0.00, '2026-04-27 17:49:49', '2026-05-11 17:49:49', 15);
INSERT INTO `yl_material` VALUES (16, '人工智能专业深度解读', 'AI就业前景、课程设置、名校对比', 35.00, 999, '专业课', 'https://picsum.photos/seed/material16/400/250', '/files/ai_major_intro.pdf', 'PDF|95页', 1, '2026-05-04 17:23:25', '2026-05-04 17:49:49', 0, 1890, NULL, 4567, 'AI,人工智能,专业', '2026', '行业分析师', 4.8, '人工智能专业概述...', 'PDF', 55.00, '2026-04-27 17:49:49', '2026-05-11 17:49:49', 16);
INSERT INTO `yl_material` VALUES (17, '金融专硕VS学硕对比', '培养模式、课程差异、就业去向全面分析', 25.00, 1200, 'zhuanyemulu', 'https://picsum.photos/seed/material17/400/250', '/files/finance_compare.pdf', 'PDF|70页', 1, '2026-05-04 17:23:25', '2026-05-04 17:49:49', 0, 1456, NULL, 3876, '金融,专硕,对比', '2026', '金融学姐', 4.7, '金融硕士的两种类型...', 'PDF', 39.00, '2026-04-27 17:49:49', '2026-05-11 17:49:49', 17);
INSERT INTO `yl_material` VALUES (18, '法硕非法学入门必读', '零基础跨考法律硕士完整规划', 42.00, 750, '专业课', 'https://picsum.photos/seed/material18/400/250', '/files/law_master_guide.pdf', 'PDF|130页', 1, '2026-05-04 17:23:25', '2026-05-04 17:49:49', 0, 1234, NULL, 3210, '法硕,非法学,跨考', '2026', '法硕上岸党', 4.6, '什么是法律硕士（非法学）？...', 'PDF', 62.00, '2026-04-27 17:49:49', '2026-05-11 17:49:49', 18);
INSERT INTO `yl_material` VALUES (19, '心理学考研方向选择', '学硕vs专硕、各研究方向详细介绍', 28.00, 900, 'major', 'https://picsum.photos/seed/material19/400/250', 'http://localhost:8080/files/64298f0e675b45f29277b2e2e59e3bb3.pdf', 'PDF|85页', 1, '2026-05-04 17:23:25', '2026-05-04 17:49:49', 0, 987, '0.12MB', 2456, '心理学,方向,选择', '2026', '心理考研君', 4.5, '心理学考研有哪些方向？...', 'PDF', 42.00, '2026-04-27 17:49:49', '2026-05-11 17:49:49', 19);
INSERT INTO `yl_material` VALUES (20, '教育学312备考资料包', '教育学原理、中外教育史、教育心理学笔记', 88.00, 550, 'major', 'https://picsum.photos/seed/material20/400/250', 'http://localhost:8080/files/b4c6642dd68b46ebaeb43842fef84ae9.pdf', 'ZIP|多个文件', 1, '2026-05-04 17:23:25', '2026-05-04 17:49:49', 0, 1678, '0.12MB', 4890, '教育学,312,笔记', '2026', '教育学姐', 4.7, '资料包内容清单...', 'PDF', 118.00, '2026-04-27 17:49:49', '2026-05-11 17:49:49', 20);

-- ----------------------------
-- Table structure for yl_news
-- ----------------------------
DROP TABLE IF EXISTS `yl_news`;
CREATE TABLE `yl_news`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'News Type',
  `cover_img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `view_count` int NULL DEFAULT 0,
  `status` tinyint NULL DEFAULT 1 COMMENT '1: Published, 0: Draft',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint(1) NULL DEFAULT 0,
  `comment_count` int NULL DEFAULT 0 COMMENT '评论数',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序值，越大越靠前',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'News/Info' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of yl_news
-- ----------------------------
INSERT INTO `yl_news` VALUES (1, '2026年考研报名时间及流程公布', '<p>教育部公布2026年全国硕士研究生招生考试报名时间：网上报名时间为2025年10月15日至10月28日，网上确认时间为11月初。</p>', 'notice', 'https://picsum.photos/seed/news1/400/250', 5241, 1, '2026-01-08 03:38:40', '2026-01-10 01:02:25', 0, 45, 1);
INSERT INTO `yl_news` VALUES (2, '2026年各省考研报名费用一览', '<p>各省考研报名费用标准已公布，一般在120-220元之间。北京138元，上海128元，广东200元，江苏120元...</p>', 'notice', 'https://picsum.photos/seed/news2/400/250', 3893, 1, '2025-11-06 17:19:29', '2025-11-08 18:58:29', 0, 23, 2);
INSERT INTO `yl_news` VALUES (3, '2026年推免生接收工作通知', '<p>具有推荐免试资格的考生，须在国家规定时间内登录推免服务系统填报志愿并参加复试。</p>', 'notice', 'https://picsum.photos/seed/news3/400/250', 2562, 1, '2025-12-15 08:22:21', '2025-12-16 15:09:49', 0, 18, 3);
INSERT INTO `yl_news` VALUES (4, '2026年考研初试时间安排', '<p>2026年全国硕士研究生招生考试初试时间为2025年12月27日至28日，超过3小时的考试科目在29日进行。</p>', 'notice', 'https://picsum.photos/seed/news4/400/250', 6120, 1, '2026-01-14 03:09:31', '2026-01-16 21:20:27', 0, 34, 4);
INSERT INTO `yl_news` VALUES (5, '研招网报名系统使用指南', '<p>详细讲解研招网报名系统的使用步骤，包括注册、填写信息、选择报考点、缴费等流程。</p>', 'notice', 'https://picsum.photos/seed/news5/400/250', 4451, 1, '2026-01-21 08:39:46', '2026-01-23 05:00:22', 0, 28, 5);
INSERT INTO `yl_news` VALUES (6, '2026年研究生招生规模扩大政策解读', '<p>教育部表示2026年将继续扩大硕士研究生招生规模，重点向专业学位倾斜，向中西部地区和东北地区倾斜。</p>', 'policy', 'https://picsum.photos/seed/news6/400/250', 6780, 1, '2025-09-12 16:09:58', '2025-09-13 23:03:08', 0, 56, 1);
INSERT INTO `yl_news` VALUES (7, '研究生国家奖学金政策调整通知', '<p>从2026年起，研究生国家奖学金奖励名额将翻倍，硕士研究生国家奖学金标准提高到每生每年2万元。</p>', 'policy', 'https://picsum.photos/seed/news7/400/250', 3452, 1, '2026-03-26 13:11:48', '2026-03-28 08:56:42', 0, 41, 2);
INSERT INTO `yl_news` VALUES (8, '专业学位研究生教育发展方案', '<p>到2030年，专业学位研究生招生规模将扩大到硕士研究生招生总规模的三分之二左右。</p>', 'policy', 'https://picsum.photos/seed/news8/400/250', 2890, 1, '2025-09-22 09:14:32', '2025-09-24 10:29:01', 0, 19, 3);
INSERT INTO `yl_news` VALUES (9, '研究生教育质量提升计划', '<p>教育部启动新一轮研究生教育质量提升计划，加强导师队伍建设，完善研究生培养体系。</p>', 'policy', 'https://picsum.photos/seed/news9/400/250', 1980, 1, '2025-09-25 08:50:05', '2025-09-26 22:41:11', 0, 12, 4);
INSERT INTO `yl_news` VALUES (10, '研究生助学金标准调整方案', '<p>从2026年秋季学期起，研究生国家助学金标准将提高到每生每年1.2万元。</p>', 'policy', 'https://picsum.photos/seed/news10/400/250', 4120, 1, '2025-10-30 12:41:01', '2025-11-02 00:41:37', 0, 33, 5);
INSERT INTO `yl_news` VALUES (11, '研路无忧2026考研备考规划讲座', '<p>特邀北大、清华高分学长分享备考经验，涵盖各科复习规划、时间管理、心态调整等内容。</p>', 'activity', 'https://picsum.photos/seed/news11/400/250', 1562, 1, '2025-11-07 17:28:34', '2025-11-08 01:00:04', 0, 8, 1);
INSERT INTO `yl_news` VALUES (12, '2026考研名师公开课系列', '<p>汤家凤、张宇、肖秀荣等名师免费公开课，涵盖数学、英语、政治各科重点难点讲解。</p>', 'activity', 'https://picsum.photos/seed/news12/400/250', 2342, 0, '2025-12-28 01:53:53', '2025-12-29 12:47:04', 0, 15, 2);
INSERT INTO `yl_news` VALUES (13, '名校学长学姐经验分享会', '<p>邀请985高校在读研究生分享考研经验和读研生活，帮助考生明确目标、科学备考。</p>', 'activity', 'https://picsum.photos/seed/news13/400/250', 1892, 1, '2026-02-04 22:46:55', '2026-02-05 17:54:09', 0, 11, 3);
INSERT INTO `yl_news` VALUES (14, '2026考研模拟考试活动', '<p>全真模拟考试环境，专业老师批改试卷，帮助考生查漏补缺，提前适应考试节奏。</p>', 'activity', 'https://picsum.photos/seed/news14/400/250', 980, 1, '2025-09-21 07:46:31', '2025-09-23 09:05:20', 0, 6, 4);
INSERT INTO `yl_news` VALUES (15, '考研心理调适线上工作坊', '<p>专业心理咨询师带领，帮助考生缓解备考压力，建立积极心态，提高学习效率。</p>', 'activity', 'https://picsum.photos/seed/news15/400/250', 760, 1, '2025-09-29 14:41:49', '2025-10-01 11:34:22', 0, 4, 5);
INSERT INTO `yl_news` VALUES (16, '2026年考研复试分数线预测', '<p>根据历年数据和报考情况，预测2026年各学科门类复试分数线变化趋势。</p>', 'other', 'https://picsum.photos/seed/news16/400/250', 5670, 1, '2026-01-28 17:16:01', '2026-01-30 18:05:05', 0, 67, 1);
INSERT INTO `yl_news` VALUES (17, '考研热门专业报录比分析', '<p>分析计算机、金融、法律等热门专业的报录比情况，为考生择校择专业提供参考。</p>', 'other', 'https://picsum.photos/seed/news17/400/250', 4230, 1, '2025-10-26 03:47:58', '2025-10-26 23:27:17', 0, 38, 2);
INSERT INTO `yl_news` VALUES (18, '跨专业考研难度排行榜', '<p>根据历年数据和考生反馈，整理跨专业考研难度排名及备考建议。</p>', 'other', 'https://picsum.photos/seed/news18/400/250', 3120, 1, '2026-01-01 12:47:21', '2026-01-01 18:53:00', 0, 29, 3);
INSERT INTO `yl_news` VALUES (19, '在职考研与全日制对比分析', '<p>从学习方式、证书含金量、就业前景等方面对比在职考研和全日制的差异。</p>', 'other', 'https://picsum.photos/seed/news19/400/250', 2780, 1, '2026-01-22 16:48:17', '2026-01-23 03:41:50', 0, 22, 4);
INSERT INTO `yl_news` VALUES (20, '考研调剂成功案例分析', '<p>分享10个成功调剂的案例，分析调剂策略和注意事项，帮助考生把握调剂机会。</p>', 'other', 'https://picsum.photos/seed/news20/400/250', 1890, 1, '2026-01-29 12:11:24', '2026-01-29 20:18:09', 0, 16, 5);

-- ----------------------------
-- Table structure for yl_order
-- ----------------------------
DROP TABLE IF EXISTS `yl_order`;
CREATE TABLE `yl_order`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '订单编号',
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户ID',
  `total_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '总金额',
  `status` int NULL DEFAULT 0 COMMENT '状态: 0:待付款, 1:已付款, 2:已取消',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint(1) NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of yl_order
-- ----------------------------
INSERT INTO `yl_order` VALUES (1, 'ORD2026010100001', 3, 199.00, 1, '2026-05-04 17:23:25', '2026-05-04 17:23:25', 0);
INSERT INTO `yl_order` VALUES (2, 'ORD2026010200002', 4, 217.00, 2, '2026-05-04 17:23:25', '2026-05-04 17:23:25', 0);
INSERT INTO `yl_order` VALUES (3, 'ORD2026010300003', 5, 89.00, 1, '2026-05-04 17:23:25', '2026-05-04 17:23:25', 0);
INSERT INTO `yl_order` VALUES (4, 'ORD2026010400004', 6, 158.00, 2, '2026-05-04 17:23:25', '2026-05-04 17:23:25', 0);
INSERT INTO `yl_order` VALUES (5, 'ORD2026010500005', 7, 68.00, 3, '2026-05-04 17:23:25', '2026-05-04 17:23:25', 0);
INSERT INTO `yl_order` VALUES (6, 'ORD2026010600006', 8, 39.00, 1, '2026-05-04 17:23:25', '2026-05-04 17:23:25', 0);
INSERT INTO `yl_order` VALUES (7, 'ORD2026010700007', 9, 168.00, 2, '2026-05-04 17:23:25', '2026-05-04 17:23:25', 0);
INSERT INTO `yl_order` VALUES (8, 'ORD2026010800008', 10, 99.00, 3, '2026-05-04 17:23:25', '2026-05-04 17:23:25', 0);
INSERT INTO `yl_order` VALUES (9, 'ORD2026010900009', 11, 79.00, 1, '2026-05-04 17:23:25', '2026-05-04 17:23:25', 0);
INSERT INTO `yl_order` VALUES (10, 'ORD2026011000010', 12, 59.00, 2, '2026-05-04 17:23:25', '2026-05-04 17:23:25', 0);
INSERT INTO `yl_order` VALUES (11, 'ORD2026011100011', 13, 29.00, 1, '2026-05-04 17:23:25', '2026-05-04 17:23:25', 0);
INSERT INTO `yl_order` VALUES (12, 'ORD2026011200012', 14, 49.00, 2, '2026-05-04 17:23:25', '2026-05-04 17:23:25', 0);
INSERT INTO `yl_order` VALUES (13, 'ORD2026011300013', 15, 19.00, 1, '2026-05-04 17:23:25', '2026-05-04 17:23:25', 0);
INSERT INTO `yl_order` VALUES (14, 'ORD2026011400014', 16, 35.00, 3, '2026-05-04 17:23:25', '2026-05-04 17:23:25', 0);
INSERT INTO `yl_order` VALUES (15, 'ORD2026011500015', 17, 25.00, 1, '2026-05-04 17:23:25', '2026-05-04 17:23:25', 0);
INSERT INTO `yl_order` VALUES (16, 'ORD2026011600016', 18, 42.00, 2, '2026-05-04 17:23:25', '2026-05-04 17:23:25', 0);
INSERT INTO `yl_order` VALUES (17, 'ORD2026011700017', 19, 88.00, 0, '2026-05-04 17:23:25', '2026-05-04 17:23:25', 0);
INSERT INTO `yl_order` VALUES (18, 'ORD2026011800018', 20, 128.00, 1, '2026-05-04 17:23:25', '2026-05-04 17:23:25', 0);
INSERT INTO `yl_order` VALUES (19, 'ORD2026011900019', 3, 218.00, 0, '2026-05-04 17:23:25', '2026-05-04 17:23:25', 0);
INSERT INTO `yl_order` VALUES (20, 'ORD2026012000020', 4, 168.00, 1, '2026-05-04 17:23:25', '2026-05-04 17:23:25', 0);
INSERT INTO `yl_order` VALUES (21, '202605062339442052050692580433920', 3, 25.50, 0, '2026-05-06 23:39:45', '2026-05-06 23:39:45', 0);

-- ----------------------------
-- Table structure for yl_order_item
-- ----------------------------
DROP TABLE IF EXISTS `yl_order_item`;
CREATE TABLE `yl_order_item`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NULL DEFAULT NULL COMMENT '订单ID',
  `material_id` bigint NULL DEFAULT NULL COMMENT '资料ID',
  `material_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '资料名称',
  `price` decimal(10, 2) NULL DEFAULT NULL COMMENT '价格',
  `quantity` int NULL DEFAULT NULL COMMENT '数量',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint(1) NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单项' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of yl_order_item
-- ----------------------------
INSERT INTO `yl_order_item` VALUES (1, 1, 1, '2026考研政治全程班讲义', 199.00, 1, '2026-05-04 17:25:35', '2026-05-04 17:25:35', 0);
INSERT INTO `yl_order_item` VALUES (2, 2, 2, '2026考研英语一真题解析', 89.00, 1, '2026-05-04 17:25:35', '2026-05-04 17:25:35', 0);
INSERT INTO `yl_order_item` VALUES (3, 2, 3, '数学一复习全书', 128.00, 1, '2026-05-04 17:25:35', '2026-05-04 17:25:35', 0);
INSERT INTO `yl_order_item` VALUES (4, 3, 2, '2026考研英语一真题解析', 89.00, 1, '2026-05-04 17:25:35', '2026-05-04 17:25:35', 0);
INSERT INTO `yl_order_item` VALUES (5, 4, 4, '考研英语二词汇闪过', 68.00, 1, '2026-05-04 17:25:35', '2026-05-04 17:25:35', 0);
INSERT INTO `yl_order_item` VALUES (6, 4, 5, '考研数学历年真题详解', 89.00, 1, '2026-05-04 17:25:35', '2026-05-04 17:25:35', 0);
INSERT INTO `yl_order_item` VALUES (7, 5, 6, '考研政治肖秀荣1000题', 39.00, 1, '2026-05-04 17:25:35', '2026-05-04 17:25:35', 0);
INSERT INTO `yl_order_item` VALUES (8, 6, 7, '计算机408统考复习指南', 158.00, 1, '2026-05-04 17:25:35', '2026-05-04 17:25:35', 0);
INSERT INTO `yl_order_item` VALUES (9, 7, 8, '考研英语作文万能模板', 49.00, 1, '2026-05-04 17:25:35', '2026-05-04 17:25:35', 0);
INSERT INTO `yl_order_item` VALUES (10, 7, 9, '考研数学公式手册', 29.00, 1, '2026-05-04 17:25:35', '2026-05-04 17:25:35', 0);
INSERT INTO `yl_order_item` VALUES (11, 8, 10, '考研政治冲刺背诵笔记', 19.00, 1, '2026-05-04 17:25:35', '2026-05-04 17:25:35', 0);
INSERT INTO `yl_order_item` VALUES (12, 9, 11, '数据结构考研复习指南', 59.00, 1, '2026-05-04 17:25:35', '2026-05-04 17:25:35', 0);
INSERT INTO `yl_order_item` VALUES (13, 10, 12, '计算机网络考研复习指南', 49.00, 1, '2026-05-04 17:25:35', '2026-05-04 17:25:35', 0);
INSERT INTO `yl_order_item` VALUES (14, 11, 13, '操作系统考研复习指南', 49.00, 1, '2026-05-04 17:25:35', '2026-05-04 17:25:35', 0);
INSERT INTO `yl_order_item` VALUES (15, 12, 14, '计算机组成原理考研指南', 49.00, 1, '2026-05-04 17:25:35', '2026-05-04 17:25:35', 0);
INSERT INTO `yl_order_item` VALUES (16, 13, 15, '管理类联考综合能力教材', 128.00, 1, '2026-05-04 17:25:35', '2026-05-04 17:25:35', 0);
INSERT INTO `yl_order_item` VALUES (17, 14, 16, '法律硕士联考专业基础课', 168.00, 1, '2026-05-04 17:25:35', '2026-05-04 17:25:35', 0);
INSERT INTO `yl_order_item` VALUES (18, 15, 17, '教育学考研311统考教材', 88.00, 1, '2026-05-04 17:25:35', '2026-05-04 17:25:35', 0);
INSERT INTO `yl_order_item` VALUES (19, 16, 18, '心理学考研312统考教材', 79.00, 1, '2026-05-04 17:25:35', '2026-05-04 17:25:35', 0);
INSERT INTO `yl_order_item` VALUES (20, 17, 19, '金融学考研431综合教材', 99.00, 1, '2026-05-04 17:25:35', '2026-05-04 17:25:35', 0);
INSERT INTO `yl_order_item` VALUES (21, 21, 3, '数学一复习全书', 25.50, 1, '2026-05-06 23:39:45', '2026-05-06 23:39:45', 0);

-- ----------------------------
-- Table structure for yl_post
-- ----------------------------
DROP TABLE IF EXISTS `yl_post`;
CREATE TABLE `yl_post`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标题',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '内容',
  `user_id` bigint NULL DEFAULT NULL COMMENT '作者ID',
  `nickname` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '作者昵称',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '作者头像',
  `view_count` int NULL DEFAULT 0 COMMENT '浏览量',
  `like_count` int NULL DEFAULT 0 COMMENT '点赞数',
  `comment_count` int NULL DEFAULT 0 COMMENT '评论数',
  `category` int NULL DEFAULT NULL COMMENT '板块分类: 1:公共课, 2:专业课, 3:复试经验, 4:其他',
  `status` int NULL DEFAULT 1 COMMENT '状态: 0:待审核, 1:正常(已审核), 2:拒绝',
  `is_top` int NULL DEFAULT 0 COMMENT '是否置顶: 0:否, 1:是',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint(1) NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '论坛帖子' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of yl_post
-- ----------------------------
INSERT INTO `yl_post` VALUES (1, '【经验帖】双非一本逆袭985计算机，我的备考全过程', '<p>本人情况：本科某双非一本，2025年成功上岸清华大学计算机系。</p><p>初试成绩：总分412，政治75，英语82，数学135，专业课120。</p><p>备考时间：2024年3月开始，历时10个月。</p>', 3, '张三', 'https://picsum.photos/seed/postavatar1/100/100', 12347, 2345, 167, 1, 1, 1, '2026-05-04 15:23:25', '2026-05-04 17:44:46', 0);
INSERT INTO `yl_post` VALUES (2, '【求助】数学基础差，现在开始准备来得及吗？', '<p>各位学长学姐好！我是二本工科，高数只学了皮毛。</p><p>想考211的计算机，请问现在开始准备数一来得及吗？</p><p>求推荐靠谱的网课和复习方法！</p>', 4, '李四', 'https://picsum.photos/seed/postavatar2/100/100', 5678, 123, 89, 2, 1, 0, '2026-05-04 12:23:25', '2026-05-04 17:44:46', 0);
INSERT INTO `yl_post` VALUES (3, '【资料分享】2026考研英语一作文模板（免费）', '<p>整理了大小作文万能模板，亲测好用！</p><p>小作文10种类型全覆盖，大作文图画/图表模板都有。</p><p>需要的同学评论区留言，私发~</p>', 5, '王五', 'https://picsum.photos/seed/postavatar3/100/100', 23457, 3457, 234, 3, 1, 0, '2026-05-03 17:23:25', '2026-05-04 17:44:46', 0);
INSERT INTO `yl_post` VALUES (4, '【日常】今天收到拟录取通知书了！！！', '<p>激动的心颤抖的手！终于等到这一天！</p><p>从去年3月开始备考，经历了太多焦虑和自我怀疑。</p><p>感谢一路陪伴的研友和家人！</p>', 6, '赵六', 'https://picsum.photos/seed/postavatar4/100/100', 34567, 5678, 456, 4, 1, 0, '2026-05-02 17:23:25', '2026-05-04 17:44:46', 0);
INSERT INTO `yl_post` VALUES (5, '【经验】在职考研上岸北大的血泪史', '<p>边工作边考研，每天只能利用下班后和周末时间复习。</p><p>最大的敌人不是知识而是时间和精力。</p><p>分享一些时间管理和效率提升的经验。</p>', 7, '孙七', 'https://picsum.photos/seed/postavatar5/100/100', 8765, 2345, 123, 1, 1, 0, '2026-05-01 17:23:25', '2026-05-04 17:44:46', 0);
INSERT INTO `yl_post` VALUES (6, '【求助】选学校纠结症犯了，求大家给建议', '<p>目前纠结三个选项：</p><p>A. 中流985但专业一般</p><p>B. 211王牌专业</p><p>C. 双非但地理位置好</p><p>以后想进互联网大厂，怎么选？</p>', 8, '周八', 'https://picsum.photos/seed/postavatar6/100/100', 4567, 234, 156, 2, 1, 0, '2026-04-30 17:23:25', '2026-05-04 17:44:46', 0);
INSERT INTO `yl_post` VALUES (7, '【资料】408四本书思维导图（超全）', '<p>花了两个月整理的数据结构、计组、操作系统、网络思维导图。</p><p>每个章节的知识点都梳理清楚了，方便背诵和查漏补缺。</p><p>点赞过100就放下载链接！</p>', 9, '吴九', 'https://picsum.photos/seed/postavatar7/100/100', 18900, 4567, 345, 3, 1, 0, '2026-04-29 17:23:25', '2026-05-04 17:44:46', 0);
INSERT INTO `yl_post` VALUES (8, '【吐槽】室友每天打游戏到凌晨三点，我快疯了', '<p>考研期间最怕遇到这种室友...</p><p>键盘声、说话声、笑声，根本睡不着。</p><p>大家都是怎么处理的？要不要换宿舍？</p>', 10, '郑十', 'https://picsum.photos/seed/postavatar8/100/100', 6789, 1234, 267, 4, 1, 0, '2026-04-28 17:23:25', '2026-05-04 17:44:46', 0);
INSERT INTO `yl_post` VALUES (9, '【经验】二战上岸复旦新传，谈谈心态调整', '<p>一战失败后消沉了很久，差点放弃。</p><p>二战最大的挑战不是知识而是心态。</p><p>分享一些克服焦虑和保持专注的方法。</p>', 11, '陈一', 'https://picsum.photos/seed/postavatar9/100/100', 7654, 1876, 145, 1, 1, 0, '2026-04-27 17:23:25', '2026-05-04 17:44:46', 0);
INSERT INTO `yl_post` VALUES (10, '【求助】跨考法硕非法学需要看哪些书？', '<p>本科学的会计，想跨考法硕非法学。</p><p>听说要看分析、指南、法律法规汇编...</p><p>有没有过来人给个书单推荐？</p>', 12, '黄二', 'https://picsum.photos/seed/postavatar10/100/100', 3456, 345, 98, 2, 1, 0, '2026-04-26 17:23:25', '2026-05-04 17:44:46', 0);
INSERT INTO `yl_post` VALUES (11, '【资料】政治1000题刷题表格（可打印）', '<p>用Excel做了个刷题记录表，可以追踪正确率。</p><p>按章节分类，标记错题和易错点。</p><p>有需要的直接拿走！</p>', 13, '刘三', 'https://picsum.photos/seed/postavatar11/100/100', 15678, 2890, 178, 3, 1, 0, '2026-04-25 17:23:25', '2026-05-04 17:44:46', 0);
INSERT INTO `yl_post` VALUES (12, '【日常】图书馆占座大战又开始了', '<p>早上6点半到图书馆门口已经排起长队...</p><p>为了一个靠窗的好位置真是拼了老命。</p><p>你们学校占座也这么卷吗？</p>', 14, '杨四', 'https://picsum.photos/seed/postavatar12/100/100', 9876, 1567, 234, 4, 1, 0, '2026-04-24 17:23:25', '2026-05-04 17:44:46', 0);
INSERT INTO `yl_post` VALUES (13, '【经验】英语二83分，阅读满分心得', '<p>英语基础一般，四级500六级没过。</p><p>通过科学的方法训练，最终阅读拿了满分。</p><p>分享我的阅读训练方法和做题技巧。</p>', 15, '许五', 'https://picsum.photos/seed/postavatar13/100/100', 11234, 2678, 189, 1, 1, 0, '2026-04-23 17:23:25', '2026-05-04 17:44:46', 0);
INSERT INTO `yl_post` VALUES (14, '【求助】专业课背了忘忘了背怎么办？', '<p>文科类专业课知识点太多了，背完一章忘一章。</p><p>有没有什么高效的记忆方法？</p><p>艾宾浩斯遗忘曲线真的有用吗？</p>', 16, '何六', 'https://picsum.photos/seed/postavatar14/100/100', 4321, 234, 87, 2, 1, 0, '2026-04-22 17:23:25', '2026-05-04 17:44:46', 0);
INSERT INTO `yl_post` VALUES (15, '【资料】数学公式手册（便携版）', '<p>把高数、线代、概率论的常用公式整理成小册子。</p><p>适合随身携带，碎片时间随时翻看。</p><p>打印出来只有20页，很实用！</p>', 17, '马七', 'https://picsum.photos/seed/postavatar15/100/100', 14567, 3421, 256, 3, 1, 0, '2026-04-21 17:23:25', '2026-05-04 17:44:46', 0);
INSERT INTO `yl_post` VALUES (16, '【日常】今天去看了目标院校，更加坚定了', '<p>趁着周末去了趟梦想中的学校。</p><p>校园比想象中更美，图书馆超级大！</p><p>一定要考上这里！！</p>', 18, '林八', 'https://picsum.photos/seed/postavatar16/100/100', 8765, 1987, 145, 4, 1, 0, '2026-04-20 17:23:25', '2026-05-04 17:44:46', 0);
INSERT INTO `yl_post` VALUES (17, '【经验】三本逆袭985医学，我的奋斗故事', '<p>高考失利上了三本，但不认命。</p><p>考研是我翻身的唯一机会。</p><p>用两年时间完成了这个看似不可能的目标。</p>', 19, '高九', 'https://picsum.photos/seed/postavatar17/100/100', 15678, 3456, 278, 1, 1, 0, '2026-04-19 17:23:25', '2026-05-04 17:44:46', 0);
INSERT INTO `yl_post` VALUES (18, '【求助】报考点和考场怎么选？', '<p>应届生应该选哪个报考点？</p><p>往届生可以选择户籍地或工作地吗？</p><p>考场分配有什么规律吗？</p>', 20, '罗十', 'https://picsum.photos/seed/postavatar18/100/100', 3456, 123, 65, 2, 1, 0, '2026-04-18 17:23:25', '2026-05-04 17:44:46', 0);
INSERT INTO `yl_post` VALUES (19, '【资料】肖四肖八电子版+配套音频', '<p>收集整理了最近5年的肖四肖八资源。</p><p>包括PDF、音频、带背视频等。</p><p>考研政治必备神器！</p>', 3, '张三', 'https://picsum.photos/seed/postavatar19/100/100', 23456, 4567, 367, 3, 1, 0, '2026-04-17 17:23:25', '2026-05-04 17:44:46', 0);
INSERT INTO `yl_post` VALUES (20, '【日常】考研倒计时100天，加油！', '<p>不知不觉已经到了最后冲刺阶段。</p><p>虽然累但是充实，相信努力会有回报。</p><p>一起加油，岸上见！</p>', 4, '李四', 'https://picsum.photos/seed/postavatar20/100/100', 12345, 2890, 432, 4, 1, 0, '2026-04-16 17:23:25', '2026-05-04 17:44:46', 0);

-- ----------------------------
-- Table structure for yl_user_behavior
-- ----------------------------
DROP TABLE IF EXISTS `yl_user_behavior`;
CREATE TABLE `yl_user_behavior`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `behavior_type` int NOT NULL COMMENT '行为类型: 1-浏览, 2-收藏, 3-购买, 4-搜索, 5-评论',
  `target_type` int NULL DEFAULT NULL COMMENT '目标类型: 1-资料, 2-帖子, 3-指南, 4-资讯',
  `target_id` bigint NOT NULL COMMENT '目标ID',
  `target_title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '目标标题',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint(1) NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_behavior_type`(`behavior_type` ASC) USING BTREE,
  INDEX `idx_target_id`(`target_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 35 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户行为记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of yl_user_behavior
-- ----------------------------
INSERT INTO `yl_user_behavior` VALUES (1, 3, 1, 1, 1, '2026年清华大学硕士研究生招生简章', '2026-05-04 17:25:35', '2026-05-04 17:25:35', 0);
INSERT INTO `yl_user_behavior` VALUES (2, 3, 1, 1, 2, '北京大学2026年研究生招生专业目录', '2026-05-04 17:25:35', '2026-05-04 17:25:35', 0);
INSERT INTO `yl_user_behavior` VALUES (3, 3, 2, 2, 1, '2026考研政治全程班讲义', '2026-05-04 17:25:35', '2026-05-04 17:25:35', 0);
INSERT INTO `yl_user_behavior` VALUES (4, 4, 1, 1, 3, '复旦大学2026年硕士研究生招生简章', '2026-05-04 17:25:35', '2026-05-04 17:25:35', 0);
INSERT INTO `yl_user_behavior` VALUES (5, 4, 3, 3, 1, '2026年考研报名时间及流程公布', '2026-05-04 17:25:35', '2026-05-04 17:25:35', 0);
INSERT INTO `yl_user_behavior` VALUES (6, 5, 2, 2, 2, '2026考研英语一真题解析', '2026-05-04 17:25:35', '2026-05-04 17:25:35', 0);
INSERT INTO `yl_user_behavior` VALUES (7, 5, 1, 1, 5, '浙江大学2026年研究生招生简章', '2026-05-04 17:25:35', '2026-05-04 17:25:35', 0);
INSERT INTO `yl_user_behavior` VALUES (8, 6, 1, 3, 2, '2026年各省考研报名费用一览', '2026-05-04 17:25:35', '2026-05-04 17:25:35', 0);
INSERT INTO `yl_user_behavior` VALUES (9, 6, 2, 2, 3, '数学一复习全书', '2026-05-04 17:25:35', '2026-05-04 17:25:35', 0);
INSERT INTO `yl_user_behavior` VALUES (10, 7, 3, 1, 7, '中国科学技术大学2026年招生简章', '2026-05-04 17:25:35', '2026-05-04 17:25:35', 0);
INSERT INTO `yl_user_behavior` VALUES (11, 7, 1, 2, 4, '考研英语二词汇闪过', '2026-05-04 17:25:35', '2026-05-04 17:25:35', 0);
INSERT INTO `yl_user_behavior` VALUES (12, 8, 2, 3, 3, '研路无忧2026考研备考规划讲座', '2026-05-04 17:25:35', '2026-05-04 17:25:35', 0);
INSERT INTO `yl_user_behavior` VALUES (13, 8, 1, 1, 9, '哈尔滨工业大学2026年研究生招生简章', '2026-05-04 17:25:35', '2026-05-04 17:25:35', 0);
INSERT INTO `yl_user_behavior` VALUES (14, 9, 1, 2, 5, '考研数学历年真题详解', '2026-05-04 17:25:35', '2026-05-04 17:25:35', 0);
INSERT INTO `yl_user_behavior` VALUES (15, 9, 3, 3, 4, '2026年考研复试分数线预测', '2026-05-04 17:25:35', '2026-05-04 17:25:35', 0);
INSERT INTO `yl_user_behavior` VALUES (16, 10, 2, 1, 11, '西安交通大学2026年招生简章', '2026-05-04 17:25:35', '2026-05-04 17:25:35', 0);
INSERT INTO `yl_user_behavior` VALUES (17, 10, 1, 2, 6, '考研政治肖秀荣1000题', '2026-05-04 17:25:35', '2026-05-04 17:25:35', 0);
INSERT INTO `yl_user_behavior` VALUES (18, 11, 1, 3, 5, '2026年推免生接收工作通知', '2026-05-04 17:25:35', '2026-05-04 17:25:35', 0);
INSERT INTO `yl_user_behavior` VALUES (19, 11, 2, 2, 7, '计算机408统考复习指南', '2026-05-04 17:25:35', '2026-05-04 17:25:35', 0);
INSERT INTO `yl_user_behavior` VALUES (20, 12, 3, 1, 13, '北京航空航天大学2026年招生简章', '2026-05-04 17:25:35', '2026-05-04 17:25:35', 0);
INSERT INTO `yl_user_behavior` VALUES (21, 1, 1, 1, 2, '2026考研英语一真题解析', '2026-05-04 17:42:52', '2026-05-04 17:42:52', 0);
INSERT INTO `yl_user_behavior` VALUES (22, 1, 1, 1, 2, '2026考研英语一真题解析', '2026-05-04 19:00:17', '2026-05-04 19:00:17', 0);
INSERT INTO `yl_user_behavior` VALUES (23, 1, 1, 1, 20, '教育学312备考资料包', '2026-05-04 19:05:12', '2026-05-04 19:05:12', 0);
INSERT INTO `yl_user_behavior` VALUES (24, 1, 1, 1, 2, '2026考研英语一真题解析', '2026-05-04 19:35:31', '2026-05-04 19:35:31', 0);
INSERT INTO `yl_user_behavior` VALUES (25, 1, 1, 1, 7, '北大光华MBA面试宝典', '2026-05-04 19:45:00', '2026-05-04 19:45:00', 0);
INSERT INTO `yl_user_behavior` VALUES (26, 1, 1, 1, 9, '复旦新传考研专题整理', '2026-05-04 19:47:13', '2026-05-04 19:47:13', 0);
INSERT INTO `yl_user_behavior` VALUES (27, 3, 1, 1, 5, '西医综合306考点速记', '2026-05-04 22:03:02', '2026-05-04 22:03:02', 0);
INSERT INTO `yl_user_behavior` VALUES (28, 3, 1, 1, 12, '考研院校信息大全', '2026-05-04 22:03:15', '2026-05-04 22:03:15', 0);
INSERT INTO `yl_user_behavior` VALUES (29, 3, 1, 1, 2, '2026考研英语一真题解析', '2026-05-04 23:00:03', '2026-05-04 23:00:03', 0);
INSERT INTO `yl_user_behavior` VALUES (30, 3, 2, 1, 2, '2026考研英语一真题解析', '2026-05-04 23:00:07', '2026-05-04 23:00:07', 0);
INSERT INTO `yl_user_behavior` VALUES (31, 3, 1, 1, 1, '2026考研政治全程班讲义', '2026-05-06 01:18:02', '2026-05-06 01:18:02', 0);
INSERT INTO `yl_user_behavior` VALUES (32, 3, 1, 1, 3, '数学一复习全书', '2026-05-06 23:39:44', '2026-05-06 23:39:44', 0);
INSERT INTO `yl_user_behavior` VALUES (33, 3, 3, 1, 3, '数学一复习全书', '2026-05-06 23:39:45', '2026-05-06 23:39:45', 0);
INSERT INTO `yl_user_behavior` VALUES (34, 3, 1, 1, 3, '数学一复习全书', '2026-05-07 12:37:38', '2026-05-07 12:37:38', 0);

SET FOREIGN_KEY_CHECKS = 1;
