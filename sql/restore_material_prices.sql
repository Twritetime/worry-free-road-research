-- 恢复被智能分类损坏的资料价格
-- 执行前请确认数据库名称，如需修改请替换 `yanluwuyou` 为你的数据库名

USE yanluwuyou;

-- 根据原始数据恢复价格（基于 SQL 备份）
UPDATE yl_material SET price = 46.00, original_price = 57.50 WHERE id = 1 AND (price = 0 OR price IS NULL);
UPDATE yl_material SET price = 30.00, original_price = 37.50 WHERE id = 2 AND (price = 0 OR price IS NULL);
UPDATE yl_material SET price = 25.50, original_price = 31.88 WHERE id = 3 AND (price = 0 OR price IS NULL);
UPDATE yl_material SET price = 50.00, original_price = 50.00 WHERE id = 4 AND (price = 0 OR price IS NULL);
UPDATE yl_material SET price = 15.00, original_price = 15.00 WHERE id = 5 AND (price = 0 OR price IS NULL);
UPDATE yl_material SET price = 1.00, original_price = 1.00 WHERE id = 6 AND (price = 0 OR price IS NULL);
UPDATE yl_material SET price = 13.00, original_price = 13.00 WHERE id = 7 AND (price = 0 OR price IS NULL);
UPDATE yl_material SET price = 10.00, original_price = 10.00 WHERE id = 8 AND (price = 0 OR price IS NULL);
UPDATE yl_material SET price = 2.00, original_price = 3.00 WHERE id = 9 AND (price = 0 OR price IS NULL);
UPDATE yl_material SET price = 1.00, original_price = 2.00 WHERE id = 10 AND (price = 0 OR price IS NULL);
UPDATE yl_material SET price = 1.00, original_price = 2.00 WHERE id = 11 AND (price = 0 OR price IS NULL);

-- 如果有新增资料价格也为0，需要手动设置
-- 可以使用以下查询找出所有价格为0的资料：
SELECT id, name, category, price, original_price FROM yl_material WHERE price = 0 OR price IS NULL;
