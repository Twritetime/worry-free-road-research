-- 重置管理员密码为 123456
-- MD5(123456) = e10adc3949ba59abbe56e057f20f883e

UPDATE sys_user SET password = 'e10adc3949ba59abbe56e057f20f883e' WHERE username = 'admin';

-- 验证修改
SELECT id, username, password, role FROM sys_user WHERE username = 'admin';
