DROP DATABASE IF EXISTS sqlnew;
CREATE DATABASE IF NOT EXISTS `sqlnew` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `sqlnew`;
SET NAMES utf8mb4;
SET character_set_client = utf8mb4;
SET character_set_connection = utf8mb4;
SET character_set_results = utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;
SET sql_mode = 'NO_ENGINE_SUBSTITUTION';

-- 1. 商品分类表
DROP TABLE IF EXISTS `type`;
CREATE TABLE `type` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类名称',
  `description` TEXT COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '分类描述',
  `img` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '分类图标',
  `sort` int(11) DEFAULT 0 COMMENT '排序值',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品分类表';

-- 分类数据
INSERT INTO `type` (`id`, `name`, `description`, `img`, `sort`) VALUES
(1, '小新系列', '小新系列笔记本', 'https://p3.lefile.cn/product/adminweb/2025/06/23/uyomsml97qI9FamPMKnY59GqZ-8259.jpg', 1),
(2, '拯救者系列', '拯救者系列游戏本', 'https://p1.lefile.cn/product/adminweb/2024/03/13/RDvWrB2y20iRmFmvYmpTkeLDc-5190.jpg', 2),
(3, 'Think系列','Think系列商务本','https://p3.lefile.cn/product/adminweb/2025/03/04/gQxTGsZrIAcAzuDP5yJpmgdBb-1521.jpg', 3),
(4, 'YOGA系列',  'YOGA 系列轻薄本','https://p2.lefile.cn/product/adminweb/2024/04/18/0E2gNU6mizqEL2PaGbDKcGEsC-2633.jpg', 4),
(5, '其他系列',  '其他相关产品','https://p2.lefile.cn/product/adminweb/2025/08/19/TQmQujsA98JHhDdGjoC8q2yqW-0834.jpg', 5);

-- 2. 管理员表
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '管理员ID',
  `username` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
  `name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '姓名',
  `avatar` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '头像',
  `phone` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '电话',
  `email` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '邮箱',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员表';

INSERT INTO `admin` (`id`, `username`, `password`, `name`, `avatar`, `phone`, `email`) VALUES
(1, 'admin', 'e10adc3949ba59abbe56e057f20f883e', '联想商城管理员', '', '400-990-8888', 'admin@lenovo.com');

-- 3. 商家表
DROP TABLE IF EXISTS `business`;
CREATE TABLE `business` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '商家ID',
  `name` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '店铺名称',
  `phone` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '联系电话',
  `email` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '邮箱',
  `description` TEXT COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '店铺介绍',
  `status` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '商家状态',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商家表';

-- old insert removed; now insert updated to include description/status
INSERT INTO `business` (`id`, `name`, `phone`, `email`, `description`, `status`) VALUES
(1, '联想官方旗舰店', '888-888-8888', 'shop@lenovo.com', '联想笔记本电脑商城', '');

-- 4. 用户表
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
  `avatar` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '头像',
  `phone` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '电话', 
  `email` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '邮箱',
  `is_member` tinyint(1) DEFAULT 0 COMMENT '是否会员(0:否, 1:是)', 
  `member_since` datetime DEFAULT NULL COMMENT '成为会员时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 5. 地址表
DROP TABLE IF EXISTS `address`;
CREATE TABLE `address` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '地址ID',
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `phone` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '联系电话',
  `detail` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '详细地址',
  `business_id` int(11) DEFAULT 1 COMMENT '店铺ID(固定为1)',
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `business_id` (`business_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='地址表';

-- 外键
ALTER TABLE `address`
  ADD CONSTRAINT `address_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `address_ibfk_2` FOREIGN KEY (`business_id`) REFERENCES `business` (`id`);

-- 6. 商品表
DROP TABLE IF EXISTS `goods`;
CREATE TABLE `goods` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '商品名称',
  `img` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '商品主图',
  `description` longtext COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '商品详情', 
  `price` decimal(10,2) NOT NULL COMMENT '售价',
  `original_price` decimal(10,2) DEFAULT 0.00 COMMENT '原价',
  `type_id` int(11) DEFAULT NULL COMMENT '分类ID',
  `business_id` int(11) DEFAULT 1 COMMENT '商家ID(固定为1)',
  PRIMARY KEY (`id`),
  KEY `type_id` (`type_id`),
  KEY `business_id` (`business_id`),
  CONSTRAINT `goods_ibfk_1` FOREIGN KEY (`type_id`) REFERENCES `type` (`id`),
  CONSTRAINT `goods_ibfk_2` FOREIGN KEY (`business_id`) REFERENCES `business` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品表';

-- 7. 购物车表
DROP TABLE IF EXISTS `cart`;
CREATE TABLE `cart` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '购物车ID',
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `goods_id` int(11) NOT NULL COMMENT '商品ID',
  `num` int(11) DEFAULT 1 COMMENT '数量',
  `business_id` int(11) DEFAULT 1 COMMENT '店铺ID(固定为1)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id_goods_id` (`user_id`,`goods_id`),
  KEY `goods_id` (`goods_id`),
  KEY `business_id` (`business_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='购物车表';

-- 购物车表外键
ALTER TABLE `cart`
  ADD CONSTRAINT `cart_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `cart_ibfk_2` FOREIGN KEY (`goods_id`) REFERENCES `goods` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `cart_ibfk_3` FOREIGN KEY (`business_id`) REFERENCES `business` (`id`);


-- 8. 订单表
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '订单编号',
  `goods_id` int(11) DEFAULT NULL COMMENT '商品ID',
  `business_id` int(11) DEFAULT 1 COMMENT '商家ID(固定为1)',
  `num` int(11) DEFAULT 1 COMMENT '数量',
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `address_id` int(11) NOT NULL COMMENT '地址ID',
  `total_price` decimal(10,2) NOT NULL COMMENT '订单总价',
  `status` tinyint(4) DEFAULT 0 COMMENT '订单状态(0:待付款,1:待发货,2:待收货,3:已完成)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `order_no` (`order_no`),
  KEY `user_id` (`user_id`),
  KEY `address_id` (`address_id`),
  KEY `goods_id` (`goods_id`),
  KEY `business_id` (`business_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

-- 订单表外键
ALTER TABLE `orders`
  ADD CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `orders_ibfk_2` FOREIGN KEY (`address_id`) REFERENCES `address` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `orders_ibfk_3` FOREIGN KEY (`goods_id`) REFERENCES `goods` (`id`),
  ADD CONSTRAINT `orders_ibfk_4` FOREIGN KEY (`business_id`) REFERENCES `business` (`id`);

-- 9. 订单详情表
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '详情ID',
  `order_id` int(11) NOT NULL COMMENT '订单ID',
  `goods_id` int(11) NOT NULL COMMENT '商品ID',
  `num` int(11) NOT NULL COMMENT '购买数量',
  `price` decimal(10,2) NOT NULL COMMENT '购买单价',
  PRIMARY KEY (`id`),
  KEY `order_id` (`order_id`),
  KEY `goods_id` (`goods_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单详情表';

-- 订单详情表外键
ALTER TABLE `order_item`
  ADD CONSTRAINT `order_item_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `order_item_ibfk_2` FOREIGN KEY (`goods_id`) REFERENCES `goods` (`id`) ON DELETE CASCADE;

-- 10. 评论表
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `goods_id` int(11) NOT NULL COMMENT '商品ID',
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `business_id` int(11) DEFAULT 1 COMMENT '店铺ID(固定为1)',
  `content` text COLLATE utf8mb4_unicode_ci COMMENT '评论内容',
  `score` tinyint(4) DEFAULT 5 COMMENT '评分(1-5)',
  `time` varchar(500) DEFAULT NULL COMMENT '评论时间',
  PRIMARY KEY (`id`),
  KEY `goods_id` (`goods_id`),
  KEY `user_id` (`user_id`),
  KEY `business_id` (`business_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论表';

-- 评论表外键
ALTER TABLE `comment`
  ADD CONSTRAINT `comment_ibfk_1` FOREIGN KEY (`goods_id`) REFERENCES `goods` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `comment_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `comment_ibfk_3` FOREIGN KEY (`business_id`) REFERENCES `business` (`id`);

-- 11. 公告表
DROP TABLE IF EXISTS `notice`;
CREATE TABLE `notice` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标题', -- 加长长度
  `content` text COLLATE utf8mb4_unicode_ci COMMENT '内容',
  `time` varchar(500) DEFAULT NULL COMMENT '创建时间', -- 加长长度
  `admin_id` int(11) NOT NULL COMMENT '创建人ID',
  PRIMARY KEY (`id`),
  KEY `admin_id` (`admin_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='公告表';

-- 公告表外键
ALTER TABLE `notice`
  ADD CONSTRAINT `notice_ibfk_1` FOREIGN KEY (`admin_id`) REFERENCES `admin` (`id`) ON DELETE CASCADE;


-- 设置商家ID为1
UPDATE goods SET business_id = 1 WHERE business_id IS NULL OR business_id != 1;
UPDATE cart SET business_id = 1 WHERE business_id IS NULL OR business_id != 1;
UPDATE comment SET business_id = 1 WHERE business_id IS NULL OR business_id != 1;
UPDATE orders SET business_id = 1 WHERE business_id IS NULL OR business_id != 1;

-- 设置商家ID字段默认值1
ALTER TABLE goods MODIFY business_id int(11) DEFAULT 1;
ALTER TABLE cart MODIFY business_id int(11) DEFAULT 1;
ALTER TABLE comment MODIFY business_id int(11) DEFAULT 1;
ALTER TABLE orders MODIFY business_id int(11) DEFAULT 1;
