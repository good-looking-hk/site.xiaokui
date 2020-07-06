SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `mall_user`;
CREATE TABLE `mall_user` (
  `uid` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `status` char(1) DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


DROP TABLE IF EXISTS `mall_product`;
CREATE TABLE `mall_product` (
                             `pid` bigint(20) NOT NULL AUTO_INCREMENT,
                             `name` varchar(255) DEFAULT NULL,
                             `price` decimal(19, 2) DEFAULT NULL,
                             `stock` int(1) DEFAULT NULL,
                             `create_time` datetime DEFAULT NULL,
                             `remark` varchar(255) DEFAULT NULL,
                             PRIMARY KEY (`pid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


DROP TABLE IF EXISTS `mall_order`;
CREATE TABLE `mall_order` (
                             `oid` bigint(20) NOT NULL AUTO_INCREMENT,
                             `uid` bigint(255) DEFAULT NULL,
                             `pid` bigint(255) DEFAULT NULL,
                             `price` decimal(19, 2) DEFAULT NULL,
                             `status` int(1) DEFAULT NULL,
                             `pay_msg` varchar(255) DEFAULT NULL,
                             `remark` varchar(255) DEFAULT NULL,
                             `create_time` datetime DEFAULT NULL,
                             PRIMARY KEY (`oid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

BEGIN;
INSERT INTO `mall_user`(`uid`, `username`, `password`, `status`, `remark`) VALUES (1, 'admin', 'admin', '1', '测试用户1');
INSERT INTO `mall_user`(`uid`, `username`, `password`, `status`, `remark`) VALUES (2, 'test', 'test', '1', '测试用户2');
INSERT INTO `mall_user`(`uid`, `username`, `password`, `status`, `remark`) VALUES (3, 'other', 'other', '1', '测试用户3');
COMMIT;

BEGIN;
INSERT INTO `mall_product`(`pid`, `name`, `price`, `stock`, `create_time`, `remark`) VALUES (1, '小米手机', 3999.00, 1000, '2020-10-10 10:10:10', '小米手机，发烧友必备！');
INSERT INTO `mall_product`(`pid`, `name`, `price`, `stock`, `create_time`, `remark`) VALUES (2, '小米笔记本', 6999.00, 1000, '2020-07-07 07:07:07', '小米笔记本，轻薄版，伴你度过美好青春！');
INSERT INTO `mall_product`(`pid`, `name`, `price`, `stock`, `create_time`, `remark`) VALUES (3, '苹果笔记本', 16999.00, 100, '2020-03-03 03:03:03', '低调奢华有内涵！');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
