SET NAMES utf8mb4;
USE yanluwuyou;

-- 使用 picsum.photos 作为随机图片源，每次请求不同尺寸和种子确保图片不同
-- sys_user 表 avatar
UPDATE sys_user SET avatar = CONCAT('https://picsum.photos/seed/user', id, '/200/200') WHERE id <= 20;

-- yl_guide 表 cover_img
UPDATE yl_guide SET cover_img = CONCAT('https://picsum.photos/seed/guide', id, '/400/250') WHERE id <= 20;

-- yl_material 表 cover_img
UPDATE yl_material SET cover_img = CONCAT('https://picsum.photos/seed/material', id, '/400/250') WHERE id <= 20;

-- yl_news 表 cover_img
UPDATE yl_news SET cover_img = CONCAT('https://picsum.photos/seed/news', id, '/400/250') WHERE id <= 20;

-- yl_post 表 avatar
UPDATE yl_post SET avatar = CONCAT('https://picsum.photos/seed/postavatar', id, '/100/100') WHERE id <= 20;

-- yl_home_config 表 img_url
UPDATE yl_home_config SET img_url = CONCAT('https://picsum.photos/seed/home', id, '/800/400') WHERE id <= 20;

-- favorite 表 target_cover
UPDATE favorite SET target_cover = CONCAT('https://picsum.photos/seed/fav', id, '/300/200') WHERE id <= 20;

-- feedback 表 cover_img (如果有数据)
UPDATE feedback SET cover_img = CONCAT('https://picsum.photos/seed/feedback', id, '/300/200') WHERE id <= 20;

-- yl_comment 表 avatar
UPDATE yl_comment SET avatar = CONCAT('https://picsum.photos/seed/comment', id, '/100/100') WHERE id <= 20;
