-- ============================================================
-- 便利店管理系统 数据库初始化脚本
-- 适用数据库: MySQL 8.0+
-- 使用方法: mysql -u root -p < init.sql
-- ============================================================

-- 创建数据库
DROP DATABASE IF EXISTS convenience_store;
CREATE DATABASE convenience_store
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE convenience_store;

-- ============================================================
-- 1. 建表语句
-- ============================================================

-- 1.1 用户账号表
CREATE TABLE accounts (
  id INT NOT NULL AUTO_INCREMENT,
  username VARCHAR(255) NOT NULL,
  password VARCHAR(255) NOT NULL,
  role VARCHAR(50) DEFAULT NULL,
  name VARCHAR(255) DEFAULT NULL,
  recommend_enabled TINYINT(1) DEFAULT 1,
  PRIMARY KEY (id),
  UNIQUE KEY UK_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 1.2 商品表
CREATE TABLE products (
  id INT NOT NULL AUTO_INCREMENT,
  name VARCHAR(255) DEFAULT NULL,
  category VARCHAR(100) DEFAULT NULL,
  price DOUBLE DEFAULT NULL,
  stock INT DEFAULT NULL,
  description VARCHAR(500) DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 1.3 订单表
CREATE TABLE purchases (
  id INT NOT NULL AUTO_INCREMENT,
  user_id INT DEFAULT NULL,
  username VARCHAR(255) DEFAULT NULL,
  total DOUBLE DEFAULT NULL,
  date VARCHAR(100) DEFAULT NULL,
  payment_method VARCHAR(50) DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 1.4 订单明细表
CREATE TABLE purchase_items (
  id INT NOT NULL AUTO_INCREMENT,
  purchase_id INT DEFAULT NULL,
  product_id INT DEFAULT NULL,
  name VARCHAR(255) DEFAULT NULL,
  price DOUBLE DEFAULT NULL,
  quantity INT DEFAULT NULL,
  PRIMARY KEY (id),
  KEY FK_purchase_id (purchase_id),
  CONSTRAINT FK_purchase_items_purchases FOREIGN KEY (purchase_id) REFERENCES purchases (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 1.5 商品分类表
CREATE TABLE categories (
  id INT NOT NULL AUTO_INCREMENT,
  name VARCHAR(255) DEFAULT NULL,
  description VARCHAR(500) DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 1.6 出入库记录表
CREATE TABLE inventory_logs (
  id INT NOT NULL AUTO_INCREMENT,
  product_id INT DEFAULT NULL,
  product_name VARCHAR(255) DEFAULT NULL,
  type VARCHAR(20) DEFAULT NULL,
  quantity INT DEFAULT NULL,
  stock_before INT DEFAULT NULL,
  stock_after INT DEFAULT NULL,
  operator VARCHAR(100) DEFAULT NULL,
  note VARCHAR(500) DEFAULT NULL,
  created_at VARCHAR(100) DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 1.7 轮播图表
CREATE TABLE carousels (
  id INT NOT NULL AUTO_INCREMENT,
  title VARCHAR(255) DEFAULT NULL,
  image_url VARCHAR(500) DEFAULT NULL,
  link_url VARCHAR(500) DEFAULT NULL,
  sort_order INT DEFAULT NULL,
  active TINYINT(1) DEFAULT 1,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 1.8 通知公告表
CREATE TABLE announcements (
  id INT NOT NULL AUTO_INCREMENT,
  title VARCHAR(255) DEFAULT NULL,
  content TEXT DEFAULT NULL,
  active TINYINT(1) DEFAULT 1,
  created_at VARCHAR(100) DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 2. 种子数据
-- ============================================================

-- 2.1 账号数据
INSERT INTO accounts (id, username, password, role, name, recommend_enabled) VALUES
(1, 'admin', 'admin123', 'admin', '管理员', 1),
(2, 'user1', 'user123', 'user', '张三', 1),
(3, 'user2', 'user123', 'user', '李四', 1);

-- 2.2 商品数据（70种）
INSERT INTO products (id, name, category, price, stock, description) VALUES
-- 饮料（16种）
(1, '可口可乐', '饮料', 3.5, 120, '330ml罐装 经典畅爽'),
(2, '农夫山泉', '饮料', 2.0, 200, '550ml瓶装 天然矿泉水'),
(9, '百事可乐', '饮料', 3.5, 100, '330ml罐装 激情畅爽'),
(10, '雪碧', '饮料', 3.5, 110, '330ml罐装 柠檬味'),
(11, '美汁源果粒橙', '饮料', 4.0, 80, '420ml瓶装 含真实果粒'),
(12, '王老吉凉茶', '饮料', 5.0, 90, '310ml罐装 怕上火喝王老吉'),
(13, '红牛功能饮料', '饮料', 6.5, 70, '250ml罐装 提神醒脑'),
(14, '统一冰红茶', '饮料', 3.0, 100, '500ml瓶装 冰力十足'),
(15, '康师傅绿茶', '饮料', 3.0, 100, '500ml瓶装 清新自然'),
(16, '蒙牛纯牛奶', '饮料', 4.5, 80, '250ml盒装 纯正奶香'),
(17, '养乐多', '饮料', 2.5, 150, '100ml×5 活性乳酸菌'),
(18, '椰树椰汁', '饮料', 5.0, 60, '330ml盒装 天然椰汁'),
(19, '元气森林苏打水', '饮料', 4.5, 90, '480ml瓶装 0糖0脂'),
(20, '维他柠檬茶', '饮料', 3.5, 85, '250ml盒装 港式风味'),
(21, '星巴克咖啡', '饮料', 8.0, 50, '250ml瓶装 香醇拿铁'),
(22, '宝矿力水特', '饮料', 4.0, 70, '500ml瓶装 电解质饮料'),
-- 速食（16种）
(23, '统一老坛酸菜面', '速食', 4.5, 100, '桶装 酸爽开胃'),
(24, '康师傅鲜虾鱼板面', '速食', 4.0, 90, '桶装 鲜美虾味'),
(25, '汤达人酸辣豚骨面', '速食', 6.0, 75, '杯装 浓郁豚骨汤'),
(26, '自嗨锅麻辣火锅', '速食', 15.0, 40, '自热火锅 麻辣鲜香'),
(27, '双汇玉米热狗肠', '速食', 3.0, 180, '70g 香甜玉米味'),
(28, '双汇王中王火腿肠', '速食', 2.0, 200, '50g 经典原味'),
(29, '湾仔码头水饺', '速食', 12.0, 50, '400g 猪肉白菜馅'),
(30, '三全速冻小笼包', '速食', 10.0, 45, '300g 鲜肉小笼'),
(31, '八宝粥', '速食', 4.0, 80, '360g罐装 即食八宝粥'),
(32, '银鹭花生牛奶', '速食', 3.5, 70, '350ml罐装 香浓丝滑'),
(33, '海底捞自热米饭', '速食', 12.0, 35, '自热米饭 番茄牛肉味'),
(34, '速冻手抓饼', '速食', 8.0, 55, '500g×5片装 原味'),
(35, '酸辣粉', '速食', 5.0, 65, '桶装 重庆风味'),
(36, '螺蛳粉', '速食', 6.5, 50, '袋装 柳州正宗'),
(37, '速冻汤圆', '速食', 9.0, 45, '400g 黑芝麻馅'),
(38, '谷物燕麦片', '速食', 7.0, 60, '500g装 即食冲饮'),
-- 零食（16种）
(39, '乐事薯片', '零食', 7.0, 80, '75g 原味 薄脆可口'),
(40, '奥利奥饼干', '零食', 6.0, 70, '97g 经典巧克力夹心'),
(41, '良品铺子猪肉脯', '零食', 8.0, 50, '100g 蜜汁味'),
(42, '三只松鼠坚果', '零食', 10.0, 45, '120g 每日坚果混合装'),
(43, '旺旺仙贝', '零食', 4.0, 100, '52g 香脆米饼'),
(44, '德芙巧克力', '零食', 8.5, 60, '80g 丝滑牛奶巧克力'),
(45, '好丽友派', '零食', 5.0, 70, '48g×2 巧克力软心'),
(46, '百草味芒果干', '零食', 7.0, 55, '100g 甜蜜软糯'),
(47, '溜溜梅', '零食', 4.5, 80, '60g 原味青梅'),
(48, '卫龙辣条', '零食', 3.0, 120, '65g 经典大面筋'),
(49, '旺仔小馒头', '零食', 3.5, 90, '50g 奶香小馒头'),
(50, '康师傅3+2饼干', '零食', 5.0, 75, '100g 柠檬味'),
(51, '良品铺子海带结', '零食', 4.0, 85, '80g 香辣味'),
(52, '有友泡凤爪', '零食', 6.0, 60, '100g 山椒味'),
(53, '徐福记沙琪玛', '零食', 5.5, 65, '80g 鸡蛋味'),
(54, '格力高百力滋', '零食', 4.5, 70, '50g 番茄味'),
-- 日用品（16种）
(55, '清风抽纸', '日用品', 5.0, 150, '3层120抽 柔韧不易破'),
(56, '维达卷纸', '日用品', 8.0, 100, '4层140g×10卷'),
(57, '蓝月亮洗手液', '日用品', 6.0, 80, '300ml 抑菌配方'),
(58, '舒肤佳香皂', '日用品', 4.0, 90, '115g 经典香型'),
(59, '海飞丝洗发水', '日用品', 10.0, 50, '200ml 去屑止痒'),
(60, '佳洁士牙膏', '日用品', 5.0, 70, '120g 清新薄荷'),
(61, '六神花露水', '日用品', 6.5, 60, '180ml 驱蚊止痒'),
(62, '垃圾袋', '日用品', 3.0, 200, '30只装 加厚耐用'),
(63, '一次性纸杯', '日用品', 4.0, 150, '50只装 250ml'),
(64, '保鲜膜', '日用品', 3.5, 100, '30cm×20m 食品级'),
(65, '洗洁精', '日用品', 5.5, 80, '500ml 柠檬清香'),
(66, '牙签', '日用品', 2.0, 200, '200只装 竹制'),
(67, '棉签', '日用品', 2.5, 180, '100只装 双头棉'),
(68, '一次性口罩', '日用品', 8.0, 300, '50只装 三层防护'),
(69, '拖把替换布', '日用品', 6.0, 60, '平板拖把替换装'),
(70, '空气清新剂', '日用品', 7.0, 50, '320ml 淡雅花香');

-- 2.3 分类数据
INSERT INTO categories (id, name, description) VALUES
(1, '饮料', '各类饮品'),
(2, '速食', '方便速食食品'),
(3, '零食', '休闲零食'),
(4, '日用品', '日常用品');

-- 2.4 订单数据
INSERT INTO purchases (id, user_id, username, total, date, payment_method) VALUES
(1, 2, '张三', 11.5, '2026-05-01 10:30:00', '微信'),
(2, 2, '张三', 22.0, '2026-05-03 14:20:00', '支付宝'),
(3, 3, '李四', 20.0, '2026-05-02 09:15:00', '现金'),
(4, 2, '张三', 22.0, '2026-05-05 16:45:00', '微信'),
(5, 3, '李四', 19.0, '2026-05-06 11:20:00', '支付宝'),
(6, 2, '张三', 29.0, '2026-05-08 19:30:00', '微信');

-- 2.5 订单明细数据
INSERT INTO purchase_items (id, purchase_id, product_id, name, price, quantity) VALUES
(1, 1, 1, '可口可乐', 3.5, 2),
(2, 1, 23, '统一老坛酸菜面', 4.5, 1),
(3, 2, 39, '乐事薯片', 7.0, 2),
(4, 2, 44, '德芙巧克力', 8.5, 1),
(5, 3, 2, '农夫山泉', 2.0, 5),
(6, 3, 55, '清风抽纸', 5.0, 2),
(7, 4, 13, '红牛功能饮料', 6.5, 2),
(8, 4, 48, '卫龙辣条', 3.0, 3),
(9, 5, 16, '蒙牛纯牛奶', 4.5, 2),
(10, 5, 59, '海飞丝洗发水', 10.0, 1),
(11, 6, 25, '汤达人酸辣豚骨面', 6.0, 2),
(12, 6, 67, '棉签', 2.5, 1),
(13, 6, 5, '薯片', 6.0, 2);

-- 2.6 轮播图数据
INSERT INTO carousels (id, title, image_url, link_url, sort_order, active) VALUES
(1, '新品上市', '/images/carousel/1.jpg', '/user/products', 1, 1),
(2, '限时特惠', '/images/carousel/2.jpg', '/user/products', 2, 1);

-- 2.7 通知公告数据
INSERT INTO announcements (id, title, content, active, created_at) VALUES
(1, '欢迎使用便利店管理系统', '感谢您选择我们的便利店管理系统！我们将持续为您提供优质服务。', 1, '2026-05-11 09:00:00'),
(2, '五一促销活动', '全场商品满50减5，活动时间：5月1日-5月7日', 1, '2026-05-01 08:00:00');

-- ============================================================
-- 3. 验证数据
-- ============================================================
SELECT 'accounts' AS table_name, COUNT(*) AS rows FROM accounts
UNION ALL SELECT 'products', COUNT(*) FROM products
UNION ALL SELECT 'purchases', COUNT(*) FROM purchases
UNION ALL SELECT 'purchase_items', COUNT(*) FROM purchase_items
UNION ALL SELECT 'categories', COUNT(*) FROM categories
UNION ALL SELECT 'carousels', COUNT(*) FROM carousels
UNION ALL SELECT 'announcements', COUNT(*) FROM announcements;
