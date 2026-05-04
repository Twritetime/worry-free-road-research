SET NAMES utf8mb4;
USE yanluwuyou;
SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE yl_user_behavior;
TRUNCATE TABLE yl_order_item;
TRUNCATE TABLE yl_order;
TRUNCATE TABLE yl_cart_item;
TRUNCATE TABLE yl_comment;
TRUNCATE TABLE favorite;
TRUNCATE TABLE address;
TRUNCATE TABLE feedback;
TRUNCATE TABLE yl_chat_message;
TRUNCATE TABLE yl_post;
TRUNCATE TABLE yl_guide;
TRUNCATE TABLE yl_material;
TRUNCATE TABLE yl_news;
TRUNCATE TABLE yl_home_config;
TRUNCATE TABLE sys_user;

ALTER TABLE sys_user AUTO_INCREMENT = 1;
ALTER TABLE address AUTO_INCREMENT = 1;
ALTER TABLE favorite AUTO_INCREMENT = 1;
ALTER TABLE feedback AUTO_INCREMENT = 1;
ALTER TABLE yl_cart_item AUTO_INCREMENT = 1;
ALTER TABLE yl_chat_message AUTO_INCREMENT = 1;
ALTER TABLE yl_comment AUTO_INCREMENT = 1;
ALTER TABLE yl_guide AUTO_INCREMENT = 1;
ALTER TABLE yl_home_config AUTO_INCREMENT = 1;
ALTER TABLE yl_material AUTO_INCREMENT = 1;
ALTER TABLE yl_news AUTO_INCREMENT = 1;
ALTER TABLE yl_order AUTO_INCREMENT = 1;
ALTER TABLE yl_order_item AUTO_INCREMENT = 1;
ALTER TABLE yl_post AUTO_INCREMENT = 1;
ALTER TABLE yl_user_behavior AUTO_INCREMENT = 1;

SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO sys_user (username, password, nickname, email, phone, avatar, role, status, last_login_time) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '系统管理员', 'admin@yanluwuyou.com', '13800138000', '/avatar/admin.png', 'ADMIN', 1, NOW()),
('operator01', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '运营小王', 'wang@yanluwuyou.com', '13800138001', '/avatar/operator1.png', 'OPERATOR', 1, NOW()),
('zhangsan', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '张三', 'zhangsan@qq.com', '13811110001', '/avatar/user1.png', 'STUDENT', 1, NOW()),
('lisi', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '李四', 'lisi@qq.com', '13811110002', '/avatar/user2.png', 'STUDENT', 1, NOW()),
('wangwu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '王五', 'wangwu@qq.com', '13811110003', '/avatar/user3.png', 'STUDENT', 1, NOW()),
('zhaoliu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '赵六', 'zhaoliu@qq.com', '13811110004', '/avatar/user4.png', 'STUDENT', 1, NOW()),
('sunqi', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '孙七', 'sunqi@qq.com', '13811110005', '/avatar/user5.png', 'STUDENT', 1, NOW()),
('zhouba', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '周八', 'zhouba@qq.com', '13811110006', '/avatar/user6.png', 'STUDENT', 1, NOW()),
('wujiu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '吴九', 'wujiu@qq.com', '13811110007', '/avatar/user7.png', 'STUDENT', 1, NOW()),
('zhengshi', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '郑十', 'zhengshi@qq.com', '13811110008', '/avatar/user8.png', 'STUDENT', 1, NOW()),
('chenyi', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '陈一', 'chenyi@qq.com', '13811110009', '/avatar/user9.png', 'STUDENT', 1, NULL),
('huanger', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '黄二', 'huanger@qq.com', '13811110010', '/avatar/user10.png', 'STUDENT', 1, NOW()),
('liusann', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '刘三', 'liusann@qq.com', '13811110011', '/avatar/user11.png', 'STUDENT', 1, NOW()),
('yangsi', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '杨四', 'yangsi@qq.com', '13811110012', '/avatar/user12.png', 'STUDENT', 1, NULL),
('xuwu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '许五', 'xuwu@qq.com', '13811110013', '/avatar/user13.png', 'STUDENT', 1, NOW()),
('heliu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '何六', 'heliu@qq.com', '13811110014', '/avatar/user14.png', 'STUDENT', 1, NOW()),
('maqi', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '马七', 'maqi@qq.com', '13811110015', '/avatar/user15.png', 'STUDENT', 1, NULL),
('linba', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '林八', 'linba@qq.com', '13811110016', '/avatar/user16.png', 'STUDENT', 1, NOW()),
('gaojiu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '高九', 'gaojiu@qq.com', '13811110017', '/avatar/user17.png', 'STUDENT', 1, NOW()),
('luoshi', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '罗十', 'luoshi@qq.com', '13811110018', '/avatar/user18.png', 'STUDENT', 1, NOW());

INSERT INTO address (user_id, receiver_name, receiver_phone, province, city, district, detail_address, is_default, deleted) VALUES
(3, '张三', '13811110001', '北京市', '北京市', '海淀区', '中关村大街1号清华园X号楼501室', 1, 0),
(3, '张三', '13811110001', '上海市', '上海市', '浦东新区', '张江高科技园区碧波路690号', 0, 0),
(4, '李四', '13811110002', '广东省', '广州市', '天河区', '天河路228号正佳广场写字楼A座1201', 1, 0),
(5, '王五', '13811110003', '浙江省', '杭州市', '西湖区', '文三路269号华星科技大厦B座802', 1, 0),
(6, '赵六', '13811110004', '江苏省', '南京市', '鼓楼区', '汉中路188号南京大学南园宿舍楼302', 1, 0),
(7, '孙七', '13811110005', '湖北省', '武汉市', '洪山区', '珞喻路152号华中科技大学紫菘学生公寓', 1, 0),
(8, '周八', '13811110006', '四川省', '成都市', '武侯区', '一环路南一段24号四川大学望江校区', 1, 0),
(9, '吴九', '13811110007', '陕西省', '西安市', '雁塔区', '咸宁西路28号西安交通大学兴庆校区', 1, 0),
(10, '郑十', '13811110008', '黑龙江省', '哈尔滨市', '南岗区', '西大直街92号哈尔滨工业大学', 1, 0),
(11, '陈一', '13811110009', '安徽省', '合肥市', '蜀山区', '黄山路443号中国科学技术大学东校区', 1, 0),
(12, '黄二', '13811110010', '福建省', '厦门市', '思明区', '思明南路422号厦门大学芙蓉宿舍', 1, 0),
(13, '刘三', '13811110011', '山东省', '青岛市', '市南区', '香港东路23号中国海洋大学浮山校区', 1, 0),
(14, '杨四', '13811110012', '湖南省', '长沙市', '岳麓区', '麓山南路932号中南大学', 1, 0),
(15, '许五', '13811110013', '重庆市', '重庆市', '沙坪坝区', '沙正街174号重庆大学A区学生公寓', 1, 0),
(16, '何六', '13811110014', '辽宁省', '大连市', '甘井子区', '凌工路2号大连理工大学西山校区', 1, 0),
(17, '马七', '13811110015', '天津市', '天津市', '南开区', '卫津路94号南开大学八里台校区', 1, 0),
(18, '林八', '13811110016', '河南省', '郑州市', '金水区', '科学大道100号郑州大学新校区', 1, 0),
(19, '高九', '13811110017', '云南省', '昆明市', '五华区', '一二一大街298号云南大学呈贡校区', 1, 0),
(20, '罗十', '13811110018', '甘肃省', '兰州市', '城关区', '天水南路222号兰州大学本部', 1, 0),
(4, '李四', '13811110002', '广东省', '深圳市', '南山区', '科技园南区深南大道9988号', 0, 0);

INSERT INTO yl_guide (title, content, category, view_count, comment_count, institution, major, status, sort_order, create_time, update_time) VALUES
('2026年清华大学硕士研究生招生简章', '<p>清华大学2026年计划招收硕士研究生约4000名，涵盖工学、理学、管理学等多个学科门类。</p><p>报考条件：国家承认学历的应届本科毕业生及自学考试届时可毕业本科生。</p>', 'zhaoshengjianzhang', 1250, 23, '清华大学', '计算机科学与技术', 1, 1, NOW(), NOW()),
('2026年北京大学硕士研究生招生简章', '<p>北京大学创建于1898年，是中国近代第一所国立综合性大学。</p><p>2026年招收学术型硕士和专业学位硕士研究生。</p>', 'zhaoshengjianzhang', 980, 18, '北京大学', '法学', 1, 2, NOW(), NOW()),
('2026年复旦大学硕士研究生招生简章', '<p>复旦大学是国家双一流A类建设高校。</p><p>学制：学术型硕士一般为3年，专业学位硕士一般为2-3年。</p>', 'zhaoshengjianzhang', 856, 12, '复旦大学', '新闻传播学', 1, 3, NOW(), NOW()),
('2026年上海交通大学硕士研究生招生简章', '<p>上交大拥有船舶与海洋工程、机械工程等优势学科。</p><p>奖助体系完善，提供学业奖学金、国家助学金等多种资助。</p>', 'zhaoshengjianzhang', 743, 15, '上海交通大学', '机械工程', 1, 4, NOW(), NOW()),
('2026年浙江大学硕士研究生招生简章', '<p>浙江大学涵盖12个学科门类，学科实力雄厚。</p><p>紫金港校区为主校区，玉泉、西溪、华家池等校区协同发展。</p>', 'zhaoshengjianzhang', 692, 9, '浙江大学', '控制科学与工程', 1, 5, NOW(), NOW()),
('计算机科学与技术专业目录（0812）', '<p>研究方向：计算机系统结构、软件与理论、计算机应用技术。</p><p>主干课程：数据结构、算法分析、操作系统、计算机网络、编译原理。</p>', 'zhuanyemulu', 1567, 32, '教育部', '计算机科学与技术', 1, 1, NOW(), NOW()),
('软件工程专业目录（0835）', '<p>核心课程：软件需求工程、软件设计与架构、软件测试与质量保证。</p><p>就业方向：互联网公司、软件开发企业、金融机构IT部门。</p>', 'zhuanyemulu', 1234, 28, '教育部', '软件工程', 1, 2, NOW(), NOW()),
('人工智能专业目录（交叉学科）', '<p>研究领域：机器学习、深度学习、自然语言处理、计算机视觉。</p><p>前沿方向：大语言模型、多模态学习、强化学习、AI for Science。</p>', 'zhuanyemulu', 1890, 45, '教育部', '人工智能', 1, 3, NOW(), NOW()),
('电子信息类专业目录（0854）', '<p>包含方向：新一代电子信息技术、通信工程、集成电路工程、计算机技术。</p><p>专业学位类别，注重实践能力培养。</p>', 'zhuanyemulu', 1456, 37, '教育部', '电子信息', 1, 4, NOW(), NOW()),
('临床医学专业目录（1002）', '<p>专业方向：内科学、外科学、妇产科学、儿科学、神经病学。</p><p>培养模式：5+3一体化或5+3+X规范化培训。</p>', 'zhuanyemulu', 1345, 30, '教育部', '临床医学', 1, 5, NOW(), NOW()),
('2026年考研思想政治理论大纲', '<p>试卷满分100分，考试时间180分钟。</p><p>题型：单选题16分、多选题34分、分析题50分。</p><p>考查范围：马克思主义基本原理、毛泽东思想、中国近现代史纲要等。</p>', 'kaoshidagang', 2345, 67, '教育部考试中心', '政治', 1, 1, NOW(), NOW()),
('2026年考研英语（一）大纲', '<p>完形填空10分、阅读理解40分（含新题型）、翻译10分、写作40分。</p><p>词汇要求：掌握5500左右词汇及相关词组。</p>', 'kaoshidagang', 2567, 72, '教育部考试中心', '英语一', 1, 2, NOW(), NOW()),
('2026年考研数学（一）大纲', '<p>高等数学60%、线性代数20%、概率论与数理统计20%。</p><p>适用专业：工学门类中对数学要求较高的专业。</p>', 'kaoshidagang', 2890, 89, '教育部考试中心', '数学一', 1, 3, NOW(), NOW()),
('2026年计算机408考试大纲', '<p>数据结构45分、计算机组成原理45分、操作系统35分、计算机网络25分。</p><p>总分150分，考试时间180分钟。</p>', 'kaoshidagang', 3124, 95, '教育部考试中心', '计算机408', 1, 4, NOW(), NOW()),
('2026年西医综合306考试大纲', '<p>总分300分，生理学14%、生物化学12%、病理学12%、内科学33%、外科学23%。</p><p>注重临床思维和综合应用能力的考查。</p>', 'kaoshidagang', 1876, 52, '教育部考试中心', '西医综合', 1, 5, NOW(), NOW()),
('清华大学计算机系复试细则', '<p>复试分数线：总分380分，单科55/85。</p><p>复试形式：笔试（专业基础）+面试（外语+专业综合）。</p>', 'fushixize', 1876, 56, '清华大学', '计算机科学与技术', 1, 1, NOW(), NOW()),
('北京大学光华管理学院复试办法', '<p>复试权重：初试50% + 复试50%。</p><p>复试内容：专业课笔试30% + 综合面试50% + 外语测试20%。</p>', 'fushixize', 1654, 48, '北京大学', '工商管理(MBA)', 1, 2, NOW(), NOW()),
('浙江大学医学院复试录取办法', '<p>复试资格：达到学校公布的复试分数线。</p><p>考核内容：临床技能操作 + 综合素质评价 + 专业笔试。</p>', 'fushixize', 1567, 51, '浙江大学', '临床医学', 1, 3, NOW(), NOW()),
('复旦大学新闻学院复试方案', '<p>复试流程：签到抽签 -> 专业课笔试 -> 外语面试 -> 专业综合面试。</p><p>资格审查需携带身份证、准考证、学历证书原件。</p>', 'fushixize', 1432, 42, '复旦大学', '新闻传播学', 1, 4, NOW(), NOW()),
('上海交通大学机械学院复试实施细则', '<p>差额复试比例：不低于120%。</p><p>复试科目：机械设计基础或自动控制原理（二选一）+ 实践环节考核。</p>', 'fushixize', 1298, 37, '上海交通大学', '机械工程', 1, 5, NOW(), NOW());

INSERT INTO yl_material (name, description, price, stock, category, cover_img, file_url, specs, status, sales, download_count, tags, apply_year, author, rating, preview_content, file_format, original_price, sort_order) VALUES
('2026考研政治全程班讲义', '包含马原、毛中特、史纲、思修、形势与政策五大模块精讲', 199.00, 500, 'kaoshidagang', '/material/politics.png', '/files/politics_full.pdf', 'PDF|500页', 1, 1234, 3567, '考研政治,全程班', '2026', '名师团队', 4.8, '第一章 马克思主义基本原理...', 'PDF', 299.00, 1),
('2026考研英语一真题解析', '2015-2025年真题详细解析，含阅读长难句图解', 89.00, 800, 'kaoshidagang', '/material/english1.png', '/files/english1_papers.pdf', 'PDF|380页', 1, 2345, 5678, '考研英语,真题', '2026', '新东方团队', 4.9, '2015年 Text 1...', 'PDF', 129.00, 2),
('数学一复习全书', '李永乐团队编写，涵盖高数、线代、概率论全部考点', 128.00, 600, 'kaoshidagang', '/material/math1_book.png', '/files/math1_complete.pdf', 'PDF|720页', 1, 3456, 7890, '考研数学,李永乐', '2026', '李永乐', 4.7, '第一章 函数与极限...', 'PDF', 168.00, 3),
('计算机408历年真题详解', '2009-2025年408真题，每题配有详细解题思路', 158.00, 400, 'kaoshidagang', '/material/cs408.png', '/files/cs408_exams.pdf', 'PDF|650页', 1, 1567, 4321, '408,计算机', '2026', '王道团队', 4.8, '2015年 数据结构真题...', 'PDF', 218.00, 4),
('西医综合306考点速记', '内科、外科、生化、病理核心考点口袋书', 68.00, 1000, 'kaoshidagang', '/material/medical306.png', '/files/medical306_quick.pdf', 'PDF|280页', 1, 987, 2345, '西医综合,306', '2026', '贺银成', 4.6, '第一章 内科学总论...', 'PDF', 88.00, 5),
('清华大学计算机考研经验贴', '400+学长学姐亲授备考策略和时间规划', 39.00, 999, 'fushixize', '/material/tsinghua_cs.png', '/files/tsinghua_cs_exp.pdf', 'PDF|120页', 1, 2345, 6789, '清华,计算机,经验', '2026', '上岸学长', 4.9, '写在前面：我的基本情况是...', 'PDF', 59.00, 6),
('北大光华MBA面试宝典', '历年真题+模拟面试+案例分析技巧', 168.00, 300, 'fushixize', '/material/pku_mba.png', '/files/pku_mba_interview.pdf', 'PDF|250页', 1, 876, 1987, 'MBA,北大,面试', '2026', '光华校友', 4.7, '第一部分 MBA面试概述...', 'PDF', 228.00, 7),
('浙大医学院复试攻略', '临床技能操作视频+笔试重点+导师信息汇总', 99.00, 450, 'fushixize', '/material/zju_medical.png', '/files/zju_medical_fushi.zip', 'ZIP|视频+文档', 1, 654, 1543, '浙大,医学,复试', '2026', '医学生团队', 4.5, '复试流程概览...', 'ZIP', 139.00, 8),
('复旦新传考研专题整理', '传播学理论、新媒体研究、广告学案例合集', 79.00, 600, 'fushixize', '/material/fudan_news.png', '/files/fudan_news_topic.pdf', 'PDF|320页', 1, 1123, 2876, '复旦,新闻,专题', '2026', '新传学姐', 4.8, '专题一 传播学经典理论...', 'PDF', 109.00, 9),
('上交机械复试实验指导', '金工实习、机械设计实验要点总结', 59.00, 700, 'fushixize', '/material/sjtu_mech.png', '/files/sjtu_mech_lab.pdf', 'PDF|150页', 1, 432, 1098, '上交,机械,实验', '2026', '机原学长', 4.6, '实验一 金工车削操作...', 'PDF', 79.00, 10),
('计算机考研择校指南', '985/211院校CS专业对比分析，含报录比、分数线', 29.00, 9999, 'zhaoshengjianzhang', '/material/cs_school.png', '/files/cs_school_guide.pdf', 'PDF|180页', 1, 4567, 12345, '择校,计算机,985', '2026', '研招网整理', 4.7, '前言：如何选择适合自己的院校...', 'PDF', 49.00, 11),
('考研院校信息大全', '全国500+所院校招生简章、专业目录、历年数据', 49.00, 8888, 'zhaoshengjianzhang', '/material/school_info.png', '/files/all_school_info.xlsx', 'Excel|50MB', 1, 3456, 9876, '院校,招生,简章', '2026', '研考助手', 4.5, '数据说明...', 'XLSX', 69.00, 12),
('保研夏令营申请模板', '个人陈述、推荐信、简历模板合集', 19.00, 9999, 'zhaoshengjianzhang', '/material/baoyan_template.png', '/files/baoyan_templates.docx', 'DOCX|50页', 1, 5678, 15678, '保研,夏令营,模板', '2026', '保研成功者', 4.9, '个人陈述写作要点...', 'DOCX', 35.00, 13),
('考研调剂信息汇总', '每日更新各校调剂名额、联系方式、复试时间', 0.00, 0, 'zhaoshengjianzhang', '/material/tiaoji.png', '', '在线查看', 1, 89, 2345, '调剂,信息,实时', '2026', '系统自动', 4.3, '最新调剂信息...', '-', 0, 14),
('推免生面试常见问题', '100道高频问题及参考回答', 25.00, 9999, 'zhaoshengjianzhang', '/material/tuimian_qa.png', '/files/tuimian_100qa.pdf', 'PDF|80页', 1, 2345, 6789, '推免,面试,问答', '2026', '面霸学长', 4.6, 'Q1: 请做一个自我介绍...', 'PDF', 39.00, 15),
('人工智能专业深度解读', 'AI就业前景、课程设置、名校对比', 35.00, 999, 'zhuanyemulu', '/material/ai_major.png', '/files/ai_major_intro.pdf', 'PDF|95页', 1, 1890, 4567, 'AI,人工智能,专业', '2026', '行业分析师', 4.8, '人工智能专业概述...', 'PDF', 55.00, 16),
('金融专硕VS学硕对比', '培养模式、课程差异、就业去向全面分析', 25.00, 1200, 'zhuanyemulu', '/material/finance_compare.png', '/files/finance_compare.pdf', 'PDF|70页', 1, 1456, 3876, '金融,专硕,对比', '2026', '金融学姐', 4.7, '金融硕士的两种类型...', 'PDF', 39.00, 17),
('法硕非法学入门必读', '零基础跨考法律硕士完整规划', 42.00, 750, 'zhuanyemulu', '/material/law_master.png', '/files/law_master_guide.pdf', 'PDF|130页', 1, 1234, 3210, '法硕,非法学,跨考', '2026', '法硕上岸党', 4.6, '什么是法律硕士（非法学）？...', 'PDF', 62.00, 18),
('心理学考研方向选择', '学硕vs专硕、各研究方向详细介绍', 28.00, 900, 'zhuanyemulu', '/material/psychology_dir.png', '/files/psychology_directions.pdf', 'PDF|85页', 1, 987, 2456, '心理学,方向,选择', '2026', '心理考研君', 4.5, '心理学考研有哪些方向？...', 'PDF', 42.00, 19),
('教育学312备考资料包', '教育学原理、中外教育史、教育心理学笔记', 88.00, 550, 'zhuanyemulu', '/material/edu312_notes.png', '/files/edu312_notes.zip', 'ZIP|多个文件', 1, 1678, 4890, '教育学,312,笔记', '2026', '教育学姐', 4.7, '资料包内容清单...', 'ZIP', 118.00, 20);

INSERT INTO yl_news (title, content, type, cover_img, view_count, status, comment_count, sort_order, create_time, update_time) VALUES
('2026年全国硕士研究生招生工作管理规定发布', '<p>教育部近日发布《2026年全国硕士研究生招生工作管理规定》，对报名、初试、复试等工作作出部署。</p><p>网上预报名时间：2025年9月24日至27日。</p><p>网上报名时间：2025年10月8日至25日。</p>', 'notice', '/news/policy2026.png', 5678, 1, 45, 1, DATE_SUB(NOW(), INTERVAL 1 DAY), NOW()),
('关于做好2026年推免生接收工作的通知', '<p>各招生单位要严格按照规定开展推免生接收工作，确保公平公正。</p><p>推免服务系统开放时间：2025年9月28日至10月20日。</p>', 'notice', '/news/tuimian2026.png', 3456, 1, 23, 2, DATE_SUB(NOW(), INTERVAL 3 DAY), NOW()),
('2026年研招网报系统使用说明', '<p>考生须在规定时间内登录中国研究生招生信息网进行网上报名。</p><p>注意事项：准确填写个人信息，认真核对报考点信息。</p>', 'notice', '/news/baoming_guide.png', 4567, 1, 34, 3, DATE_SUB(NOW(), INTERVAL 5 DAY), NOW()),
('关于调整部分专业学位硕士招生规模的通知', '<p>为进一步优化研究生教育结构，2026年将适度扩大专业学位硕士招生规模。</p><p>重点扩大集成电路、人工智能、储能等国家急需领域招生。</p>', 'policy', '/news/scale_adjust.png', 2890, 1, 18, 4, DATE_SUB(NOW(), INTERVAL 7 DAY), NOW()),
('教育部部署2026年研究生考试安全工作', '<p>会议强调，要严厉打击助考犯罪团伙，确保考试公平公正。</p><p>加强考场管理，严防高科技作弊行为。</p>', 'policy', '/news/security_work.png', 2345, 1, 12, 5, DATE_SUB(NOW(), INTERVAL 10 DAY), NOW()),
('2026年考研初试时间确定', '<p>初试时间为2025年12月20日至21日。</p><p>超过3小时的考试科目在12月22日进行。</p>', 'notice', '/news/exam_time.png', 6789, 1, 67, 6, DATE_SUB(NOW(), INTERVAL 14 DAY), NOW()),
('关于开展研究生教育学科专业目录修订工作的通知', '<p>新版学科专业目录将于2026年起实施。</p><p>新增多个交叉学科门类，优化学科布局。</p>', 'policy', '/news/catalog_revise.png', 1789, 1, 8, 7, DATE_SUB(NOW(), INTERVAL 18 DAY), NOW()),
('2026年少数民族骨干计划招生简章', '<p>面向西部民族地区定向招生，毕业后回定向地区就业。</p><p>报考条件、招生计划详见各省教育厅通知。</p>', 'notice', '/news/minority_plan.png', 1987, 1, 15, 8, DATE_SUB(NOW(), INTERVAL 21 DAY), NOW()),
('研究生国家奖学金评审办法修订说明', '<p>提高奖励标准，博士研究生每人每年3万元，硕士研究生每人每年2万元。</p><p>评审更加注重科研创新能力和综合素质。</p>', 'policy', '/news/scholarship.png', 3456, 1, 28, 9, DATE_SUB(NOW(), INTERVAL 25 DAY), NOW()),
('关于规范研究生招生宣传工作的通知', '<p>各招生单位不得发布虚假招生信息，严禁违规承诺。</p><p>加强对社会培训机构的管理，维护考生合法权益。</p>', 'policy', '/news/publicity_rule.png', 1234, 1, 5, 10, DATE_SUB(NOW(), INTERVAL 28 DAY), NOW()),
('2026年退役大学生士兵专项计划说明', '<p>专门面向应征入伍退出现役的高校学生招生。</p><p>实行单独划线，招生计划单列。</p>', 'notice', '/news/soldier_plan.png', 2134, 1, 11, 11, DATE_SUB(NOW(), INTERVAL 32 DAY), NOW()),
('研究生教育国际交流合作项目申报通知', '<p>支持研究生赴海外联合培养、参加国际学术会议。</p><p>申请截止日期：2025年11月30日。</p>', 'activity', '/news/international.png', 1567, 1, 9, 12, DATE_SUB(NOW(), INTERVAL 35 DAY), NOW()),
('关于做好2026年硕士研究生招生考试命题工作的通知', '<p>自命题科目须按照《全国硕士研究生招生考试自命题工作指导规范》组织命题。</p><p>加强试题保密管理，确保命题安全。</p>', 'notice', '/news/exam_paper.png', 1876, 1, 14, 13, DATE_SUB(NOW(), INTERVAL 40 DAY), NOW()),
('2026年研究生招生咨询周活动安排', '<p>研招网将于2025年9月19日至23日举办全国硕士研究生招生宣传咨询周。</p><p>各招生单位在线解答考生疑问。</p>', 'activity', '/news/consult_week.png', 4321, 1, 41, 14, DATE_SUB(NOW(), INTERVAL 45 DAY), NOW()),
('关于进一步规范和加强研究生培养管理的意见', '<p>严格过程管理，完善分流淘汰机制。</p><p>强化学位论文质量监控，严肃处理学术不端行为。</p>', 'policy', '/news/cultivation_manage.png', 1654, 1, 7, 15, DATE_SUB(NOW(), INTERVAL 50 DAY), NOW()),
('2026年港澳台研究生招生办法公布', '<p>面向香港、澳门、台湾地区招收研究生。</p><p>报名方式、考试安排详见招生简章。</p>', 'notice', '/news/gat_recruit.png', 1432, 1, 6, 16, DATE_SUB(NOW(), INTERVAL 55 DAY), NOW()),
('研究生教育创新实践系列大赛通知', '<p>包括中国研究生创新实践系列大赛多项赛事。</p><p>鼓励研究生积极参与，提升创新能力。</p>', 'activity', '/news/innovation_contest.png', 2098, 1, 19, 17, DATE_SUB(NOW(), INTERVAL 58 DAY), NOW()),
('关于做好2026年硕士研究生招生考试考务工作的通知', '<p>各地要统筹做好疫情防控常态化下的研考组考工作。</p><p>确保考生健康安全和考试平稳顺利。</p>', 'notice', '/news/exam_affairs.png', 2765, 1, 22, 18, DATE_SUB(NOW(), INTERVAL 62 DAY), NOW()),
('研究生教育数字化转型升级推进会召开', '<p>推进研究生教育管理信息系统建设，提升信息化水平。</p><p>建设智慧教室、虚拟仿真实验室等新型教学环境。</p>', 'other', '/news/digital_transform.png', 1123, 1, 3, 19, DATE_SUB(NOW(), INTERVAL 65 DAY), NOW()),
('2025年度研究生教育工作总结暨表彰大会举行', '<p>表彰优秀研究生导师、优秀研究生教育管理者。</p><p>总结经验，部署下一年度重点工作。</p>', 'other', '/news/summary_meeting.png', 987, 1, 2, 20, DATE_SUB(NOW(), INTERVAL 70 DAY), NOW());

INSERT INTO yl_post (title, content, user_id, nickname, avatar, view_count, like_count, comment_count, category, status, is_top, create_time, update_time) VALUES
('【经验帖】双非一本逆袭985计算机，我的备考全过程', '<p>本人情况：本科某双非一本，2025年成功上岸清华大学计算机系。</p><p>初试成绩：总分412，政治75，英语82，数学135，专业课120。</p><p>备考时间：2024年3月开始，历时10个月。</p>', 3, '张三', '/avatar/user1.png', 12345, 2345, 167, 1, 1, 1, DATE_SUB(NOW(), INTERVAL 2 HOUR), NOW()),
('【求助】数学基础差，现在开始准备来得及吗？', '<p>各位学长学姐好！我是二本工科，高数只学了皮毛。</p><p>想考211的计算机，请问现在开始准备数一来得及吗？</p><p>求推荐靠谱的网课和复习方法！</p>', 4, '李四', '/avatar/user2.png', 5678, 123, 89, 2, 1, 0, DATE_SUB(NOW(), INTERVAL 5 HOUR), NOW()),
('【资料分享】2026考研英语一作文模板（免费）', '<p>整理了大小作文万能模板，亲测好用！</p><p>小作文10种类型全覆盖，大作文图画/图表模板都有。</p><p>需要的同学评论区留言，私发~</p>', 5, '王五', '/avatar/user3.png', 23456, 3456, 234, 3, 1, 0, DATE_SUB(NOW(), INTERVAL 1 DAY), NOW()),
('【日常】今天收到拟录取通知书了！！！', '<p>激动的心颤抖的手！终于等到这一天！</p><p>从去年3月开始备考，经历了太多焦虑和自我怀疑。</p><p>感谢一路陪伴的研友和家人！</p>', 6, '赵六', '/avatar/user4.png', 34567, 5678, 456, 4, 1, 0, DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
('【经验】在职考研上岸北大的血泪史', '<p>边工作边考研，每天只能利用下班后和周末时间复习。</p><p>最大的敌人不是知识而是时间和精力。</p><p>分享一些时间管理和效率提升的经验。</p>', 7, '孙七', '/avatar/user5.png', 8765, 2345, 123, 1, 1, 0, DATE_SUB(NOW(), INTERVAL 3 DAY), NOW()),
('【求助】选学校纠结症犯了，求大家给建议', '<p>目前纠结三个选项：</p><p>A. 中流985但专业一般</p><p>B. 211王牌专业</p><p>C. 双非但地理位置好</p><p>以后想进互联网大厂，怎么选？</p>', 8, '周八', '/avatar/user6.png', 4567, 234, 156, 2, 1, 0, DATE_SUB(NOW(), INTERVAL 4 DAY), NOW()),
('【资料】408四本书思维导图（超全）', '<p>花了两个月整理的数据结构、计组、操作系统、网络思维导图。</p><p>每个章节的知识点都梳理清楚了，方便背诵和查漏补缺。</p><p>点赞过100就放下载链接！</p>', 9, '吴九', '/avatar/user7.png', 18900, 4567, 345, 3, 1, 0, DATE_SUB(NOW(), INTERVAL 5 DAY), NOW()),
('【吐槽】室友每天打游戏到凌晨三点，我快疯了', '<p>考研期间最怕遇到这种室友...</p><p>键盘声、说话声、笑声，根本睡不着。</p><p>大家都是怎么处理的？要不要换宿舍？</p>', 10, '郑十', '/avatar/user8.png', 6789, 1234, 267, 4, 1, 0, DATE_SUB(NOW(), INTERVAL 6 DAY), NOW()),
('【经验】二战上岸复旦新传，谈谈心态调整', '<p>一战失败后消沉了很久，差点放弃。</p><p>二战最大的挑战不是知识而是心态。</p><p>分享一些克服焦虑和保持专注的方法。</p>', 11, '陈一', '/avatar/user9.png', 7654, 1876, 145, 1, 1, 0, DATE_SUB(NOW(), INTERVAL 7 DAY), NOW()),
('【求助】跨考法硕非法学需要看哪些书？', '<p>本科学的会计，想跨考法硕非法学。</p><p>听说要看分析、指南、法律法规汇编...</p><p>有没有过来人给个书单推荐？</p>', 12, '黄二', '/avatar/user10.png', 3456, 345, 98, 2, 1, 0, DATE_SUB(NOW(), INTERVAL 8 DAY), NOW()),
('【资料】政治1000题刷题表格（可打印）', '<p>用Excel做了个刷题记录表，可以追踪正确率。</p><p>按章节分类，标记错题和易错点。</p><p>有需要的直接拿走！</p>', 13, '刘三', '/avatar/user11.png', 15678, 2890, 178, 3, 1, 0, DATE_SUB(NOW(), INTERVAL 9 DAY), NOW()),
('【日常】图书馆占座大战又开始了', '<p>早上6点半到图书馆门口已经排起长队...</p><p>为了一个靠窗的好位置真是拼了老命。</p><p>你们学校占座也这么卷吗？</p>', 14, '杨四', '/avatar/user12.png', 9876, 1567, 234, 4, 1, 0, DATE_SUB(NOW(), INTERVAL 10 DAY), NOW()),
('【经验】英语二83分，阅读满分心得', '<p>英语基础一般，四级500六级没过。</p><p>通过科学的方法训练，最终阅读拿了满分。</p><p>分享我的阅读训练方法和做题技巧。</p>', 15, '许五', '/avatar/user13.png', 11234, 2678, 189, 1, 1, 0, DATE_SUB(NOW(), INTERVAL 11 DAY), NOW()),
('【求助】专业课背了忘忘了背怎么办？', '<p>文科类专业课知识点太多了，背完一章忘一章。</p><p>有没有什么高效的记忆方法？</p><p>艾宾浩斯遗忘曲线真的有用吗？</p>', 16, '何六', '/avatar/user14.png', 4321, 234, 87, 2, 1, 0, DATE_SUB(NOW(), INTERVAL 12 DAY), NOW()),
('【资料】数学公式手册（便携版）', '<p>把高数、线代、概率论的常用公式整理成小册子。</p><p>适合随身携带，碎片时间随时翻看。</p><p>打印出来只有20页，很实用！</p>', 17, '马七', '/avatar/user15.png', 14567, 3421, 256, 3, 1, 0, DATE_SUB(NOW(), INTERVAL 13 DAY), NOW()),
('【日常】今天去看了目标院校，更加坚定了', '<p>趁着周末去了趟梦想中的学校。</p><p>校园比想象中更美，图书馆超级大！</p><p>一定要考上这里！！</p>', 18, '林八', '/avatar/user16.png', 8765, 1987, 145, 4, 1, 0, DATE_SUB(NOW(), INTERVAL 14 DAY), NOW()),
('【经验】三本逆袭985医学，我的奋斗故事', '<p>高考失利上了三本，但不认命。</p><p>考研是我翻身的唯一机会。</p><p>用两年时间完成了这个看似不可能的目标。</p>', 19, '高九', '/avatar/user17.png', 15678, 3456, 278, 1, 1, 0, DATE_SUB(NOW(), INTERVAL 15 DAY), NOW()),
('【求助】报考点和考场怎么选？', '<p>应届生应该选哪个报考点？</p><p>往届生可以选择户籍地或工作地吗？</p><p>考场分配有什么规律吗？</p>', 20, '罗十', '/avatar/user18.png', 3456, 123, 65, 2, 1, 0, DATE_SUB(NOW(), INTERVAL 16 DAY), NOW()),
('【资料】肖四肖八电子版+配套音频', '<p>收集整理了最近5年的肖四肖八资源。</p><p>包括PDF、音频、带背视频等。</p><p>考研政治必备神器！</p>', 3, '张三', '/avatar/user1.png', 23456, 4567, 367, 3, 1, 0, DATE_SUB(NOW(), INTERVAL 17 DAY), NOW()),
('【日常】考研倒计时100天，加油！', '<p>不知不觉已经到了最后冲刺阶段。</p><p>虽然累但是充实，相信努力会有回报。</p><p>一起加油，岸上见！</p>', 4, '李四', '/avatar/user2.png', 12345, 2890, 432, 4, 1, 0, DATE_SUB(NOW(), INTERVAL 18 DAY), NOW());

INSERT INTO yl_home_config (type, title, img_url, link_url, sort_order, status) VALUES
('banner', '2026考研招生简章发布', '/banner/banner1.jpg', '/guide/list?category=zhaoshengjianzhang', 1, 1),
('banner', '名师直播：考研政治冲刺押题', '/banner/banner2.jpg', '/news/1', 2, 1),
('banner', '限时优惠：全套考研资料包', '/banner/banner3.jpg', '/material/list?category=kaoshidagang', 3, 1),
('banner', '复试通关秘籍免费领', '/banner/banner4.jpg', '/guide/list?category=fushixize', 4, 1),
('banner', '考研院校数据库上线', '/banner/banner5.jpg', '/material/list?category=zhaoshengjianzhang', 5, 1),
('notice', '2026年考研初试时间确定：12月20-21日', '', '/news/6', 1, 1),
('notice', '推免服务系统9月28日开放', '', '/news/2', 2, 1),
('notice', '网上报名时间：10月8日至25日', '', '/news/1', 3, 1),
('notice', '少数民族骨干计划招生简章发布', '', '/news/8', 4, 1),
('notice', '退役大学生士兵专项计划说明', '', '/news/11', 5, 1),
('hot', '清华大学计算机系复试细则', '', '/guide/16', 1, 1),
('hot', '2026年考研英语（一）大纲', '', '/guide/12', 2, 1),
('hot', '计算机408历年真题详解', '', '/material/4', 3, 1),
('hot', '双非一本逆袭985计算机经验帖', '', '/post/1', 4, 1),
('hot', '数学一复习全书', '', '/material/3', 5, 1),
('recommend', '2026考研政治全程班讲义', '', '/material/1', 1, 1),
('recommend', '北大光华MBA面试宝典', '', '/material/7', 2, 1),
('recommend', '考研院校信息大全', '', '/material/12', 3, 1),
('recommend', '在职考研上岸北大的血泪史', '', '/post/5', 4, 1),
('recommend', '人工智能专业深度解读', '', '/material/16', 5, 1);

INSERT INTO yl_order (order_no, user_id, total_amount, status, deleted) VALUES
('ORD2026010100001', 3, 199.00, 1, 0),
('ORD2026010200002', 4, 217.00, 2, 0),
('ORD2026010300003', 5, 89.00, 1, 0),
('ORD2026010400004', 6, 158.00, 2, 0),
('ORD2026010500005', 7, 68.00, 3, 0),
('ORD2026010600006', 8, 39.00, 1, 0),
('ORD2026010700007', 9, 168.00, 2, 0),
('ORD2026010800008', 10, 99.00, 3, 0),
('ORD2026010900009', 11, 79.00, 1, 0),
('ORD2026011000010', 12, 59.00, 2, 0),
('ORD2026011100011', 13, 29.00, 1, 0),
('ORD2026011200012', 14, 49.00, 2, 0),
('ORD2026011300013', 15, 19.00, 1, 0),
('ORD2026011400014', 16, 35.00, 3, 0),
('ORD2026011500015', 17, 25.00, 1, 0),
('ORD2026011600016', 18, 42.00, 2, 0),
('ORD2026011700017', 19, 88.00, 0, 0),
('ORD2026011800018', 20, 128.00, 1, 0),
('ORD2026011900019', 3, 218.00, 0, 0),
('ORD2026012000020', 4, 168.00, 1, 0);

INSERT INTO yl_order_item (order_id, material_id, material_name, price, quantity) VALUES
(1, 1, '2026考研政治全程班讲义', 199.00, 1),
(2, 2, '2026考研英语一真题解析', 89.00, 1),
(2, 3, '数学一复习全书', 128.00, 1),
(3, 2, '2026考研英语一真题解析', 89.00, 1),
(4, 4, '计算机408历年真题详解', 158.00, 1),
(5, 5, '西医综合306考点速记', 68.00, 1),
(6, 6, '清华大学计算机考研经验贴', 39.00, 1),
(7, 7, '北大光华MBA面试宝典', 168.00, 1),
(8, 8, '浙大医学院复试攻略', 99.00, 1),
(9, 9, '复旦新传考研专题整理', 79.00, 1),
(10, 10, '上交机械复试实验指导', 59.00, 1),
(11, 11, '计算机考研择校指南', 29.00, 1),
(12, 12, '考研院校信息大全', 49.00, 1),
(13, 13, '保研夏令营申请模板', 19.00, 1),
(14, 16, '人工智能专业深度解读', 35.00, 1),
(15, 17, '金融专硕VS学硕对比', 25.00, 1),
(16, 18, '法硕非法学入门必读', 42.00, 1),
(17, 20, '教育学312备考资料包', 88.00, 1),
(18, 3, '数学一复习全书', 128.00, 1),
(19, 4, '计算机408历年真题详解', 158.00, 1),
(20, 7, '北大光华MBA面试宝典', 168.00, 1);

INSERT INTO yl_cart_item (user_id, material_id, quantity) VALUES
(3, 3, 1), (3, 4, 1), (4, 1, 1), (4, 6, 2), (5, 2, 1), (5, 5, 1),
(6, 3, 1), (6, 8, 1), (7, 4, 1), (7, 9, 1), (8, 1, 1), (8, 7, 1),
(9, 2, 1), (9, 10, 1), (10, 3, 1), (10, 11, 1), (11, 5, 1), (11, 12, 1),
(12, 6, 1), (12, 13, 1), (13, 4, 1), (13, 14, 1), (14, 7, 1), (14, 15, 1),
(15, 8, 1), (15, 16, 1), (16, 9, 1), (16, 17, 1), (17, 10, 1), (17, 18, 1),
(18, 11, 1), (18, 19, 1), (19, 12, 1), (19, 20, 1), (20, 13, 1), (20, 1, 2),
(3, 15, 1), (4, 17, 1), (5, 19, 1), (6, 11, 1);

INSERT INTO favorite (user_id, target_type, target_id, target_title, target_cover) VALUES
(3, 1, 1, '2026年清华大学硕士研究生招生简章', '/guide/covers/tsinghua.png'),
(3, 2, 1, '2026考研政治全程班讲义', '/material/covers/politics.png'),
(3, 3, 1, '【经验帖】双非一本逆袭985计算机', '/post/covers/exp1.png'),
(4, 1, 2, '2026年北京大学硕士研究生招生简章', '/guide/covers/pku.png'),
(4, 2, 3, '数学一复习全书', '/material/covers/math1.png'),
(4, 3, 3, '【资料分享】2026考研英语一作文模板', '/post/covers/share1.png'),
(5, 1, 6, '2026年考研思想政治理论大纲', '/guide/covers/politics.png'),
(5, 2, 4, '计算机408历年真题详解', '/material/covers/cs408.png'),
(5, 3, 4, '【日常】今天收到拟录取通知书了！！！', '/post/covers/daily1.png'),
(6, 1, 8, '清华大学计算机系复试细则', '/guide/covers/tsinghua_cs.png'),
(6, 2, 7, '北大光华MBA面试宝典', '/material/covers/pku_mba.png'),
(6, 3, 5, '【经验】在职考研上岸北大的血泪史', '/post/covers/exp2.png'),
(7, 1, 10, '浙江大学医学院复试录取办法', '/guide/covers/zju_medical.png'),
(7, 2, 9, '复旦新传考研专题整理', '/material/covers/fudan_news.png'),
(7, 3, 7, '【资料】408四本书思维导图（超全）', '/post/covers/share2.png'),
(8, 1, 12, '北京大学光华管理学院复试办法', '/guide/covers/pku_guanghua.png'),
(8, 2, 11, '计算机考研择校指南', '/material/covers/cs_school.png'),
(8, 3, 9, '【吐槽】室友每天打游戏到凌晨三点', '/post/covers/daily2.png'),
(9, 1, 14, '复旦大学新闻学院复试方案', '/guide/covers/fudan_news.png'),
(9, 2, 16, '人工智能专业深度解读', '/material/covers/ai_major.png');

INSERT INTO feedback (user_id, type, contact, content, reply, status, reply_time) VALUES
(3, 'suggest', 'zhangsan@qq.com', '希望增加资料预览功能，购买前能看部分内容', '感谢您的建议，我们正在开发此功能，预计下周上线。', 1, DATE_SUB(NOW(), INTERVAL 5 DAY)),
(4, 'bug', '13811110002', '购物车数量修改后价格没有实时更新', '已修复，请刷新页面重试。', 1, DATE_SUB(NOW(), INTERVAL 4 DAY)),
(5, 'suggest', 'wangwu@qq.com', '希望能添加夜间模式，晚上看书不伤眼', '好的，已加入开发计划。', 1, DATE_SUB(NOW(), INTERVAL 3 DAY)),
(6, 'complaint', '13811110003', '购买的资料下载链接失效了', '非常抱歉，链接已修复并发送到您的邮箱。', 1, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(7, 'other', 'sunqi@qq.com', '网站整体体验很好，加油！', '谢谢您的支持！', 1, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(8, 'suggest', 'zhouba@qq.com', '建议增加社区帖子搜索功能', '已采纳，正在开发中。', 0, NULL),
(9, 'bug', '13811110005', '移动端首页轮播图显示异常', '已定位问题，正在修复。', 0, NULL),
(10, 'other', 'zhengshi@qq.com', '客服响应速度很快，点赞！', '感谢认可，我们会继续努力！', 1, NOW()),
(11, 'suggest', 'chenyi@qq.com', '希望能增加资料评分和评论功能', '好主意，已列入产品路线图。', 0, NULL),
(12, 'bug', '13811110010', '订单列表分页加载有问题', '已修复，请刷新重试。', 1, DATE_SUB(NOW(), INTERVAL 6 HOUR)),
(13, 'complaint', 'liusann@qq.com', '资料描述与实际内容不符', '已核实并更新资料详情页，给您带来的不便敬请谅解。', 1, DATE_SUB(NOW(), INTERVAL 12 HOUR)),
(14, 'suggest', 'yangsi@qq.com', '希望增加考研倒计时功能', '已添加至首页顶部，请查看。', 1, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(15, 'other', 'xuwu@qq.com', '社区氛围很好，认识了很多研友', '很高兴能为大家提供交流平台！', 1, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(16, 'bug', '13811110014', '收藏夹删除后还在显示', '缓存问题，清除浏览器缓存即可解决。', 1, DATE_SUB(NOW(), INTERVAL 3 DAY)),
(17, 'suggest', 'maqi@qq.com', '希望能增加资料分类筛选功能', '已在资料页实现多维度筛选。', 1, DATE_SUB(NOW(), INTERVAL 4 DAY)),
(18, 'other', 'linba@qq.com', '界面设计很清爽，用户体验不错', '谢谢反馈！', 1, DATE_SUB(NOW(), INTERVAL 5 DAY)),
(19, 'bug', '13811110017', '支付完成后订单状态未及时更新', '已优化支付回调逻辑。', 0, NULL),
(20, 'suggest', 'luoshi@qq.com', '建议增加学习打卡功能', '有趣的想法，我们会考虑实现。', 0, NULL),
(3, 'other', 'zhangsan@qq.com', '第二次反馈，夜间模式什么时候上线？', '预计本周五上线，敬请期待。', 1, NOW()),
(5, 'complaint', 'wangwu@qq.com', '购买的资料有缺页', '已补发完整版，请查收邮件。', 1, DATE_SUB(NOW(), INTERVAL 8 HOUR));

INSERT INTO yl_chat_message (user_id, session_id, role, content) VALUES
(3, 'session_001', 0, '你好，我想咨询一下考研政治的复习方法'),
(3, 'session_001', 1, '您好！考研政治复习建议分为三轮：基础阶段打牢基础，强化阶段攻克重难点，冲刺阶段刷题押题。您目前处于哪个阶段呢？'),
(3, 'session_001', 0, '我现在刚开始准备，基础比较薄弱'),
(3, 'session_001', 1, '那建议您先从马克思主义基本原理入手，这是政治的基础。推荐配合徐涛老师的网课+肖秀荣的《精讲精练》。每天1-2小时即可。'),
(4, 'session_002', 0, '请问计算机408怎么复习比较好？'),
(4, 'session_002', 1, '408复习建议：数据结构和计组是重中之重，建议先学好这两门。推荐教材：严蔚敏《数据结构》、袁春风《计算机组成原理》。配合王道的辅导书效果更好。'),
(5, 'session_003', 0, '英语一阅读总是做错怎么办？'),
(5, 'session_003', 1, '阅读理解提分技巧：1.先看题目再读文章；2.注意同义替换；3.分析长难句结构；4.总结错误类型。推荐唐迟老师的阅读逻辑课。'),
(6, 'session_004', 0, '数学一高数部分怎么学？'),
(6, 'session_004', 1, '高数复习建议跟汤家凤或张宇老师。重点是极限、导数、积分、级数这几大块。一定要动手算题，不能只看不练。'),
(7, 'session_005', 0, '择校方面有什么建议吗？'),
(7, 'session_005', 1, '择校要考虑：自身实力评估、地域偏好、专业排名、报录比、就业导向。建议使用我们的择校测评工具进行匹配。'),
(8, 'session_006', 0, '复试一般考什么内容？'),
(8, 'session_006', 1, '复试通常包括：专业课笔试、英语口语听力、综合面试。部分专业还有实验/实操考核。具体要求请查看目标院校的复试细则。'),
(9, 'session_007', 0, '调剂需要注意什么？'),
(9, 'session_007', 1, '调剂要点：1.关注研招网调剂服务系统开放时间；2.提前了解目标院校调剂政策；3.准备好个人材料；4.主动联系导师；5.不要错过黄金48小时。'),
(10, 'session_008', 0, '在职考研时间怎么安排？'),
(10, 'session_008', 1, '在职考研建议：早起1小时+晚睡1小时+周末全天。通勤时间可以听网课或背单词。关键是提高效率，利用碎片化时间。'),
(3, 'session_001', 0, '政治需要买很多书吗？'),
(3, 'session_001', 1, '必备书籍：《精讲精练》《1000题》《肖四》《肖八》。其他根据需要补充。前期以理解为主，后期以刷题为主。'),
(4, 'session_002', 0, '408四本书先学哪本？'),
(4, 'session_002', 1, '建议顺序：数据结构->计组->操作系统->计算机网络。数据结构是基础，必须先掌握。计组最难，需要多花时间。');

INSERT INTO yl_comment (post_id, content, user_id, nickname, avatar, target_type, target_id, parent_id, reply_to_user_id, reply_to_nickname) VALUES
(1, '楼主太厉害了！请问每天学习多久？', 4, '李四', '/avatar/user2.png', 3, 1, NULL, NULL, NULL),
(1, '回复@李四：大概每天10-12小时，前期少点后期多点', 3, '张三', '/avatar/user1.png', 3, 1, 1, 4, '李四'),
(1, '请问专业课是怎么准备的？用的什么教材？', 5, '王五', '/avatar/user3.png', 3, 1, NULL, NULL, NULL),
(1, '回复@王五：主要用王道的四本书，配合清华的历年真题', 3, '张三', '/avatar/user1.png', 3, 1, 3, 5, '王五'),
(2, '来得及！我也是二本，现在开始完全没问题', 3, '张三', '/avatar/user1.png', 3, 2, NULL, NULL, NULL),
(2, '推荐汤家凤的高数课，适合基础差的同学', 7, '孙七', '/avatar/user5.png', 3, 2, NULL, NULL, NULL),
(2, '关键是要坚持，每天至少保证4-5小时有效学习', 6, '赵六', '/avatar/user4.png', 3, 2, NULL, NULL, NULL),
(3, '求分享！楼主好人一生平安', 6, '赵六', '/avatar/user4.png', 3, 3, NULL, NULL, NULL),
(3, '已私信你了~', 5, '王五', '/avatar/user3.png', 3, 3, 1, 6, '赵六'),
(3, '我也想要一份，谢谢楼主', 8, '周八', '/avatar/user6.png', 3, 3, NULL, NULL, NULL),
(4, '恭喜恭喜！是哪个学校呀？', 3, '张三', '/avatar/user1.png', 3, 4, NULL, NULL, NULL),
(4, '回复@张三：是北京大学软件与微电子学院，方向是集成电路', 5, '王五', '/avatar/user3.png', 3, 4, 12, 3, '张三'),
(5, '同焦虑，一起加油！', 4, '李四', '/avatar/user2.png', 3, 5, NULL, NULL, NULL),
(5, '建议制定详细计划，按部就班来就不慌', 6, '赵六', '/avatar/user4.png', 3, 5, NULL, NULL, NULL),
(6, '请问英语一和英语二难度差多少？', 5, '王五', '/avatar/user3.png', 3, 6, NULL, NULL, NULL),
(6, '英语二相对简单，阅读理解和翻译难度都低一些', 7, '孙七', '/avatar/user5.png', 3, 6, NULL, NULL, NULL),
(7, '复试逆袭太励志了！请问面试有什么技巧？', 3, '张三', '/avatar/user1.png', 3, 7, NULL, NULL, NULL),
(7, '关键是自信和真诚，不要不懂装懂', 8, '周八', '/avatar/user6.png', 3, 7, NULL, NULL, NULL);