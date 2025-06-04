/*
 Navicat Premium Dump SQL

 Source Server         : SQL
 Source Server Type    : MySQL
 Source Server Version : 80041 (8.0.41)
 Source Host           : localhost:3306
 Source Schema         : wmstest

 Target Server Type    : MySQL
 Target Server Version : 80041 (8.0.41)
 File Encoding         : 65001

 Date: 04/06/2025 17:24:41
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for customer
-- ----------------------------
DROP TABLE IF EXISTS `customer`;
CREATE TABLE `customer` (
  `customer_id` int NOT NULL AUTO_INCREMENT,
  `customer_name` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `contact_phone` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
  `contact_address` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
  PRIMARY KEY (`customer_id`)
) ENGINE=InnoDB AUTO_INCREMENT=117 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin;

-- ----------------------------
-- Table structure for delivery_order
-- ----------------------------
DROP TABLE IF EXISTS `delivery_order`;
CREATE TABLE `delivery_order` (
  `delivery_id` int NOT NULL AUTO_INCREMENT COMMENT '发货单ID',
  `stock_out_id` int DEFAULT NULL COMMENT '关联的订单ID (销售单或出库单)',
  `delivery_date` date NOT NULL COMMENT '发货日期',
  `employee_id` int DEFAULT NULL COMMENT '操作员ID',
  `status` int DEFAULT '0' COMMENT '发货状态：0-待处理，1-已发货，2-异常',
  PRIMARY KEY (`delivery_id`),
  KEY `stock_out_id` (`stock_out_id`),
  KEY `employee_id` (`employee_id`),
  CONSTRAINT `delivery_order_ibfk_1` FOREIGN KEY (`stock_out_id`) REFERENCES `stock_out` (`stock_out_id`),
  CONSTRAINT `delivery_order_ibfk_user` FOREIGN KEY (`employee_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='发货单主表';

-- ----------------------------
-- Table structure for delivery_order_item
-- ----------------------------
DROP TABLE IF EXISTS `delivery_order_item`;
CREATE TABLE `delivery_order_item` (
  `delivery_item_id` int NOT NULL AUTO_INCREMENT COMMENT '发货明细项ID',
  `delivery_id` int NOT NULL COMMENT '所属发货单ID，外键关联delivery_order表',
  `product_id` int NOT NULL COMMENT '商品ID',
  `quantity` int NOT NULL COMMENT '发货数量',
  PRIMARY KEY (`delivery_item_id`),
  KEY `delivery_id` (`delivery_id`),
  KEY `product_id` (`product_id`),
  CONSTRAINT `delivery_order_item_ibfk_1` FOREIGN KEY (`delivery_id`) REFERENCES `delivery_order` (`delivery_id`) ON DELETE CASCADE,
  CONSTRAINT `delivery_order_item_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='发货单明细表';

-- ----------------------------
-- Table structure for product
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
  `product_id` int NOT NULL AUTO_INCREMENT,
  `product_name` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `specifications` text CHARACTER SET utf8mb3 COLLATE utf8mb3_bin,
  `unit_price` decimal(10,2) NOT NULL,
  `stock_quantity` int DEFAULT NULL,
  `supplier_id` int NOT NULL,
  PRIMARY KEY (`product_id`),
  KEY `product` (`supplier_id`),
  CONSTRAINT `product` FOREIGN KEY (`supplier_id`) REFERENCES `supplier` (`supplier_id`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin;

-- ----------------------------
-- Table structure for purchase_order
-- ----------------------------
DROP TABLE IF EXISTS `purchase_order`;
CREATE TABLE `purchase_order` (
  `order_id` int NOT NULL AUTO_INCREMENT,
  `supplier_id` int NOT NULL,
  `order_date` date NOT NULL,
  `delivery_date` date DEFAULT NULL,
  `status` int DEFAULT NULL,
  PRIMARY KEY (`order_id`),
  KEY `supplier_id` (`supplier_id`),
  CONSTRAINT `purchase_order_ibfk_2` FOREIGN KEY (`supplier_id`) REFERENCES `supplier` (`supplier_id`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin;

-- ----------------------------
-- Table structure for purchase_order_item
-- ----------------------------
DROP TABLE IF EXISTS `purchase_order_item`;
CREATE TABLE `purchase_order_item` (
  `order_item_id` int NOT NULL AUTO_INCREMENT,
  `order_id` int NOT NULL,
  `product_id` int NOT NULL,
  `quantity` int NOT NULL,
  `purchase_price` decimal(10,2) NOT NULL,
  PRIMARY KEY (`order_item_id`),
  KEY `order_id` (`order_id`),
  KEY `product_id` (`product_id`),
  CONSTRAINT `purchase_order_item_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `purchase_order` (`order_id`) ON DELETE CASCADE,
  CONSTRAINT `purchase_order_item_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=53 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin;

-- ----------------------------
-- Table structure for purchase_plan
-- ----------------------------
DROP TABLE IF EXISTS `purchase_plan`;
CREATE TABLE `purchase_plan` (
  `plan_id` int NOT NULL AUTO_INCREMENT,
  `purchase_date` date NOT NULL,
  `stock_out_id` int NOT NULL,
  `status` int DEFAULT NULL,
  PRIMARY KEY (`plan_id`),
  KEY `purchase_plan` (`stock_out_id`),
  CONSTRAINT `purchase_plan` FOREIGN KEY (`stock_out_id`) REFERENCES `stock_out` (`stock_out_id`)
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin;

-- ----------------------------
-- Table structure for purchase_plan_item
-- ----------------------------
DROP TABLE IF EXISTS `purchase_plan_item`;
CREATE TABLE `purchase_plan_item` (
  `plan_item_id` int NOT NULL AUTO_INCREMENT,
  `plan_id` int NOT NULL,
  `product_id` int NOT NULL,
  `plan_quantity` int NOT NULL,
  `status` int DEFAULT NULL,
  PRIMARY KEY (`plan_item_id`),
  KEY `plan_id` (`plan_id`),
  KEY `product_id` (`product_id`),
  CONSTRAINT `purchase_plan_item_ibfk_1` FOREIGN KEY (`plan_id`) REFERENCES `purchase_plan` (`plan_id`) ON DELETE CASCADE,
  CONSTRAINT `purchase_plan_item_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=54 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin;

-- ----------------------------
-- Table structure for sales_order
-- ----------------------------
DROP TABLE IF EXISTS `sales_order`;
CREATE TABLE `sales_order` (
  `sales_order_id` int NOT NULL AUTO_INCREMENT,
  `customer_id` int NOT NULL,
  `sale_date` date NOT NULL,
  `sales_order_items` varchar(255) COLLATE utf8mb3_bin DEFAULT NULL,
  `status` int DEFAULT NULL,
  PRIMARY KEY (`sales_order_id`),
  KEY `customer_id` (`customer_id`),
  CONSTRAINT `sales_order_ibfk_1` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`customer_id`)
) ENGINE=InnoDB AUTO_INCREMENT=67 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin;

-- ----------------------------
-- Table structure for sales_order_item
-- ----------------------------
DROP TABLE IF EXISTS `sales_order_item`;
CREATE TABLE `sales_order_item` (
  `sales_item_id` int NOT NULL AUTO_INCREMENT,
  `sales_order_id` int NOT NULL,
  `product_id` int NOT NULL,
  `quantity` int NOT NULL,
  `sale_price` decimal(10,2) NOT NULL,
  PRIMARY KEY (`sales_item_id`),
  KEY `sales_order_id` (`sales_order_id`),
  KEY `product_id` (`product_id`),
  CONSTRAINT `sales_order_item_ibfk_1` FOREIGN KEY (`sales_order_id`) REFERENCES `sales_order` (`sales_order_id`) ON DELETE CASCADE,
  CONSTRAINT `sales_order_item_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=178 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin;

-- ----------------------------
-- Table structure for stock_in
-- ----------------------------
DROP TABLE IF EXISTS `stock_in`;
CREATE TABLE `stock_in` (
  `stock_in_id` int NOT NULL AUTO_INCREMENT,
  `order_id` int NOT NULL,
  `employee_id` int NOT NULL,
  `stock_in_date` date NOT NULL,
  `status` int DEFAULT NULL,
  PRIMARY KEY (`stock_in_id`),
  KEY `order_id` (`order_id`),
  KEY `employee_id` (`employee_id`),
  CONSTRAINT `stock_in_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `purchase_order` (`order_id`),
  CONSTRAINT `stock_in_ibfk_2` FOREIGN KEY (`employee_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin;

-- ----------------------------
-- Table structure for stock_in_item
-- ----------------------------
DROP TABLE IF EXISTS `stock_in_item`;
CREATE TABLE `stock_in_item` (
  `stock_in_item_id` int NOT NULL AUTO_INCREMENT,
  `stock_in_id` int NOT NULL,
  `product_id` int NOT NULL,
  `quantity` int NOT NULL,
  `accepted_quantity` int DEFAULT NULL,
  PRIMARY KEY (`stock_in_item_id`),
  KEY `stock_in_id` (`stock_in_id`),
  KEY `product_id` (`product_id`),
  CONSTRAINT `stock_in_item_ibfk_1` FOREIGN KEY (`stock_in_id`) REFERENCES `stock_in` (`stock_in_id`) ON DELETE CASCADE,
  CONSTRAINT `stock_in_item_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=84 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin;

-- ----------------------------
-- Table structure for stock_out
-- ----------------------------
DROP TABLE IF EXISTS `stock_out`;
CREATE TABLE `stock_out` (
  `stock_out_id` int NOT NULL AUTO_INCREMENT,
  `sales_order_id` int NOT NULL,
  `employee_id` int NOT NULL,
  `stock_out_date` date NOT NULL,
  `status` int DEFAULT '0' COMMENT '出库状态：0-待处理，1-已出库，2-缺货等',
  PRIMARY KEY (`stock_out_id`),
  KEY `sales_order_id` (`sales_order_id`),
  KEY `employee_id` (`employee_id`),
  CONSTRAINT `stock_out_ibfk_1` FOREIGN KEY (`sales_order_id`) REFERENCES `sales_order` (`sales_order_id`),
  CONSTRAINT `stock_out_ibfk_2` FOREIGN KEY (`employee_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=53 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin;

-- ----------------------------
-- Table structure for stock_out_item
-- ----------------------------
DROP TABLE IF EXISTS `stock_out_item`;
CREATE TABLE `stock_out_item` (
  `stock_out_item_id` int NOT NULL AUTO_INCREMENT,
  `stock_out_id` int NOT NULL,
  `product_id` int NOT NULL,
  `quantity` int NOT NULL,
  PRIMARY KEY (`stock_out_item_id`),
  KEY `stock_out_id` (`stock_out_id`),
  KEY `product_id` (`product_id`),
  CONSTRAINT `stock_out_item_ibfk_1` FOREIGN KEY (`stock_out_id`) REFERENCES `stock_out` (`stock_out_id`) ON DELETE CASCADE,
  CONSTRAINT `stock_out_item_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=176 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin;

-- ----------------------------
-- Table structure for supplier
-- ----------------------------
DROP TABLE IF EXISTS `supplier`;
CREATE TABLE `supplier` (
  `supplier_id` int NOT NULL AUTO_INCREMENT,
  `supplier_name` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `contact_person` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
  `contact_info` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
  `address` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
  PRIMARY KEY (`supplier_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'primary key',
  `no` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT 'user id',
  `name` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT 'name',
  `password` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT 'password',
  `age` int DEFAULT NULL COMMENT 'age',
  `sex` int DEFAULT NULL COMMENT 'sex, 0-female, 1-male',
  `phone` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT 'phone number',
  `role_id` int DEFAULT NULL COMMENT 'role, 0-owner, 1-administrator, 2-user',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin;

SET FOREIGN_KEY_CHECKS = 1;
