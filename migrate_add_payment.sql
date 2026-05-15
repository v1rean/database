-- ============================================================
-- 迁移脚本：为 purchases 表添加 payment_method 列
-- 使用方法: mysql -u root -p convenience_store < migrate_add_payment.sql
-- ============================================================

-- 添加支付方式列（如果不存在）
ALTER TABLE purchases
  ADD COLUMN payment_method VARCHAR(50) DEFAULT NULL
  AFTER date;

-- 更新已有历史订单的支付方式（将空值设为默认）
UPDATE purchases SET payment_method = '微信' WHERE payment_method IS NULL;
