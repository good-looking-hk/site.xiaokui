-- MySQL dump 10.13  Distrib 5.7.18, for Linux (x86_64)
--
-- Host: localhost    Database: newxiaokui
-- ------------------------------------------------------
-- Server version	5.7.18-1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `goods`
--

DROP TABLE IF EXISTS `goods`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `goods` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `factory_price` decimal(10,2) DEFAULT NULL,
  `size` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `sale_price` decimal(10,2) DEFAULT NULL,
  `mian_liao` varchar(255) DEFAULT NULL,
  `color` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `goods`
--

LOCK TABLES `goods` WRITE;
/*!40000 ALTER TABLE `goods` DISABLE KEYS */;
INSERT INTO `goods` VALUES (1,'小学生校服','2018-06-28 01:02:26',12.20,'S','正常',20.00,'纯棉','红色'),(4,'大学生衣服','2018-06-28 08:44:50',100.00,'S','已删除',200.00,'20%棉','白色'),(10,'中学生校服','2018-05-28 20:15:12',200.00,'S','正常',400.00,'纯棉','红色'),(11,'前一次测试','2018-06-28 21:27:11',111.00,'M','正常',223.00,'20%棉','黑色'),(12,'最后一次测试','2018-06-29 08:12:33',100.00,'L','正常',200.00,'纯棉','紫色'),(13,'小学生','2018-06-29 08:28:40',11.00,'M','正常',122.00,'纯棉','黑色'),(14,'小学生校服','2018-06-29 08:28:46',11.00,'M','正常',122.00,'20%棉','红色');
/*!40000 ALTER TABLE `goods` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `goods_input`
--

DROP TABLE IF EXISTS `goods_input`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `goods_input` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `color` varchar(255) DEFAULT NULL,
  `size` varchar(255) DEFAULT NULL,
  `count` int(11) DEFAULT NULL,
  `input_id` int(11) DEFAULT NULL,
  `goods_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `goods_input`
--

LOCK TABLES `goods_input` WRITE;
/*!40000 ALTER TABLE `goods_input` DISABLE KEYS */;
INSERT INTO `goods_input` VALUES (1,'小学生校服','红色','S',201,1,1),(11,'大学生衣服','白色','S',640,1,4),(12,'中学生校服','红色','S',100,1,10),(13,'前一次测试','黑色','M',18,6,11),(14,'小学生校服','红色','M',12,6,14);
/*!40000 ALTER TABLE `goods_input` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `goods_output`
--

DROP TABLE IF EXISTS `goods_output`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `goods_output` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `goods_id` int(11) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `color` varchar(255) DEFAULT NULL,
  `size` varchar(255) DEFAULT NULL,
  `count` int(11) DEFAULT NULL,
  `output_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `goods_output`
--

LOCK TABLES `goods_output` WRITE;
/*!40000 ALTER TABLE `goods_output` DISABLE KEYS */;
INSERT INTO `goods_output` VALUES (1,1,'小学生校服','红色','S',200,1),(3,1,'小学生校服','红色','S',210,3),(4,4,'大学生衣服','白色','S',4,1),(5,11,'前一次测试','黑色','M',301,3);
/*!40000 ALTER TABLE `goods_output` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `input`
--

DROP TABLE IF EXISTS `input`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `input` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `input_date` datetime DEFAULT NULL,
  `person` varchar(255) DEFAULT NULL,
  `from` varchar(255) DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `warehouse` varchar(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `input`
--

LOCK TABLES `input` WRITE;
/*!40000 ALTER TABLE `input` DISABLE KEYS */;
INSERT INTO `input` VALUES (1,'2018-06-28 00:00:00','一般般帅','中国长沙','好货，不外卖的','待提交','总仓库'),(2,'2018-06-30 00:00:00','一般般帅','中国北京','不外卖的','待提交','总仓库'),(3,'2018-06-30 00:00:00','一般般帅','中国北京','不外卖的11','待提交','总仓库'),(4,'2018-06-30 00:00:00','一般般帅','中国北京','不外卖的22','待提交','总仓库'),(6,'2018-06-30 00:00:00','一般般帅','中国北京','不外卖的44','已入库','总仓库'),(7,'2018-06-13 00:00:00','一般般帅','中国北京','北京来的','待提交','一号仓库'),(8,'2018-06-22 00:00:00','一般般帅','深圳111','123','已入库','总仓库');
/*!40000 ALTER TABLE `input` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `output`
--

DROP TABLE IF EXISTS `output`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `output` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `output_date` datetime DEFAULT NULL,
  `person` varchar(255) DEFAULT NULL,
  `warehouse` varchar(255) DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  `status` varchar(11) DEFAULT NULL,
  `to_person` varchar(255) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `tel` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `output`
--

LOCK TABLES `output` WRITE;
/*!40000 ALTER TABLE `output` DISABLE KEYS */;
INSERT INTO `output` VALUES (1,'2018-06-29 00:00:00','一般般帅','总仓库','测试用的1','已出库','15197479670','中国上海','12312312312'),(2,'2018-06-15 00:00:00','一般般帅','总仓库','123','待提交','123','123','123'),(3,'2018-06-15 00:00:00','一般般帅','总仓库','123','已出库','123','123','123'),(4,'2018-06-30 00:00:00','一般般帅','二号仓库','中国深圳','待提交','送人的','中国深圳','32132132132');
/*!40000 ALTER TABLE `output` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_blog`
--

DROP TABLE IF EXISTS `sys_blog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_blog` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(32) DEFAULT NULL,
  `dir` varchar(32) DEFAULT NULL,
  `title` varchar(48) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `modified_time` datetime DEFAULT NULL,
  `order_num` int(11) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_blog`
--

LOCK TABLES `sys_blog` WRITE;
/*!40000 ALTER TABLE `sys_blog` DISABLE KEYS */;
INSERT INTO `sys_blog` VALUES (4,'初识Spring架构','Spring源码','Spring源码：初识Spring架构',1,'2018-06-30 16:05:38','2018-06-30 16:05:38',1,1),(5,'数据类型与运算','Java基础','Java基础：数据类型与运算',1,'2018-06-30 21:46:11','2018-06-30 21:46:11',1,1),(6,'基本概念与关键字','Java基础','Java基础：基本概念与关键字',1,'2018-06-30 23:05:31','2018-06-30 23:05:31',2,1),(7,'多线程','Java基础','Java基础：多线程',1,'2018-06-30 23:05:31','2018-06-30 23:05:31',6,1),(8,'异常','Java基础','Java基础：异常',1,'2018-06-30 23:05:31','2018-06-30 23:05:31',3,1),(9,'网络编程','Java基础','Java基础：网络编程',1,'2018-06-30 23:05:31','2018-06-30 23:05:31',7,1),(10,'流','Java基础','Java基础：流',1,'2018-06-30 23:05:31','2018-06-30 23:05:31',5,1),(11,'File类','Java基础','Java基础：File类',1,'2018-06-30 23:05:31','2018-06-30 23:05:31',4,1),(12,'类文件结构','Java虚拟机','Java虚拟机：类文件结构',1,'2018-07-01 10:13:34','2018-07-01 10:13:34',4,1),(13,'容器类概述','Java容器类','Java容器类：容器类概述',1,'2018-07-01 10:52:31','2018-07-01 10:52:31',1,1),(14,'ArrayList详解','Java容器类','Java容器类：ArrayList详解',1,'2018-07-01 10:52:31','2018-07-01 10:52:31',3,1),(15,'LinkedList详解','Java容器类','Java容器类：LinkedList详解',1,'2018-07-01 10:52:31','2018-07-01 10:52:31',4,1),(16,'String详解','Java容器类','Java容器类：String详解',1,'2018-07-01 10:52:31','2018-07-01 10:52:31',2,1),(17,'HashMap详解','Java容器类','Java容器类：HashMap详解',1,'2018-07-01 10:52:31','2018-07-01 10:52:31',5,1),(18,'类加载机制','Java虚拟机','Java虚拟机：类加载机制',1,'2018-07-01 10:52:40','2018-07-01 10:52:40',5,1),(19,'Java内存回收','Java虚拟机','Java虚拟机：Java内存回收',1,'2018-07-01 10:52:40','2018-07-01 10:52:40',2,1),(20,'Java内存分配','Java虚拟机','Java虚拟机：Java内存分配',1,'2018-07-01 10:52:40','2018-07-01 10:52:40',3,1),(21,'Java内存区域','Java虚拟机','Java虚拟机：Java内存区域',1,'2018-07-01 10:52:40','2018-07-01 10:52:40',1,1),(22,'自定义标签的解析','Spring源码','Spring源码：自定义标签的解析',1,'2018-07-01 16:10:40','2018-07-01 16:10:40',5,1),(23,'加载解析XML','Spring源码','Spring源码：加载解析XML',1,'2018-07-01 16:10:40','2018-07-01 16:10:40',3,1),(24,'加载XML代码分析','Spring源码','Spring源码：加载XML代码分析',1,'2018-07-01 16:10:40','2018-07-01 16:10:40',6,1),(26,'bean的加载','Spring源码','Spring源码：bean的加载',1,'2018-07-01 16:10:41','2018-07-01 16:10:41',7,1),(28,'默认标签的解析','Spring源码','Spring源码：默认标签的解析',1,'2018-07-01 16:10:41','2018-07-01 16:10:41',4,1),(29,'初识DefaultListableBeanFactory','Spring源码','Spring源码：初识DefaultListableBeanFactory',1,'2018-07-01 16:27:05','2018-07-01 16:27:05',2,1),(30,'初识ApplicationContext','Spring源码','Spring源码：初识ApplicationContext',1,'2018-07-09 00:00:00','2018-07-11 16:07:42',9,1),(31,'使用ApplicationContext','Spring源码','Spring源码：使用ApplicationContext',1,'2018-07-09 00:00:00','2018-07-11 16:07:52',10,1),(32,'deepin快捷键','Linux','Linux：deepin快捷键',1,'2018-07-11 15:29:34','2018-07-11 15:29:34',1,1),(33,'idea快捷键','Java小技巧','Java小技巧：idea快捷键',1,'2018-07-11 15:29:34','2018-07-11 15:29:34',1,1),(34,'创建bean','Spring源码','Spring源码：创建bean',1,'2018-07-11 16:06:57','2018-07-11 16:06:57',8,1);
/*!40000 ALTER TABLE `sys_blog` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_dept`
--

DROP TABLE IF EXISTS `sys_dept`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_dept` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(16) DEFAULT NULL,
  `orderNum` int(11) DEFAULT NULL,
  `parent_id` int(11) DEFAULT NULL,
  `description` varchar(32) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `modified_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_dept`
--

LOCK TABLES `sys_dept` WRITE;
/*!40000 ALTER TABLE `sys_dept` DISABLE KEYS */;
INSERT INTO `sys_dept` VALUES (1,'总公司',1,0,'总部',NULL,NULL),(2,'长沙分公司',2,0,'长沙分部',NULL,NULL),(3,'开发部',1,1,'写代码',NULL,NULL),(4,'测试部',2,1,'测试代码',NULL,NULL),(5,'长沙分部',1,2,'分部办公点',NULL,NULL);
/*!40000 ALTER TABLE `sys_dept` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_menu`
--

DROP TABLE IF EXISTS `sys_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_menu` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `parent_id` int(11) DEFAULT NULL,
  `name` varchar(16) DEFAULT NULL,
  `icon` varchar(16) DEFAULT NULL,
  `url` varchar(32) DEFAULT NULL,
  `enabled` tinyint(4) DEFAULT NULL,
  `order_num` int(11) DEFAULT NULL,
  `type` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `modified_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=80 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_menu`
--

LOCK TABLES `sys_menu` WRITE;
/*!40000 ALTER TABLE `sys_menu` DISABLE KEYS */;
INSERT INTO `sys_menu` VALUES (1,0,'系统管理','fa fa-user','#',1,1,1,NULL,NULL),(2,1,'菜单管理',NULL,'/sys/menu',1,3,2,NULL,NULL),(4,2,'添加菜单','','/sys/menu/add',1,1,3,NULL,NULL),(5,1,'用户管理','','/sys/user',1,1,2,NULL,NULL),(6,2,'编辑菜单','','/sys/menu/edit',1,2,3,NULL,NULL),(7,1,'角色管理','','/sys/role',1,2,2,NULL,NULL),(10,2,'删除菜单','','/sys/menu/remove',1,3,3,NULL,NULL),(12,7,'分配权限','','/sys/role/setAuthority',1,4,3,NULL,'2018-06-23 23:35:43'),(13,7,'添加角色','','/sys/role/add',1,1,3,NULL,NULL),(14,7,'修改角色','','/sys/role/edit',1,2,3,NULL,NULL),(15,7,'删除角色','','/sys/role/remove',1,3,3,NULL,NULL),(16,5,'修改用户','','/sys/user/edit',1,2,3,NULL,NULL),(17,5,'添加用户','','/sys/user/add',1,1,3,NULL,NULL),(18,5,'删除用户','','/sys/user/remove',1,3,3,NULL,NULL),(19,5,'重置密码','','/sys/user/resetPwd',1,4,3,NULL,NULL),(20,5,'分配角色','','/sys/user/setRole',1,5,3,NULL,'2018-06-23 23:35:26'),(47,0,'我的博客','fa fa-th-list','#',1,2,1,'2018-06-24 15:01:32','2018-06-24 16:06:49'),(48,47,'博客管理','','/sys/blog',1,1,2,'2018-06-24 16:03:51','2018-06-24 16:07:42'),(50,47,'上传博客',NULL,'/sys/blog/add',1,2,2,'2018-06-24 16:08:35',NULL),(51,47,'关于使用',NULL,'/sys/blog/help',1,3,2,'2018-06-24 16:09:29',NULL),(52,0,'服装管理','fa fa-bank','#',1,3,1,'2018-06-27 23:43:26','2018-06-28 19:39:52'),(53,52,'货号管理','','/csms/goods',1,1,2,'2018-06-27 23:45:48','2018-06-28 19:41:20'),(54,52,'入库管理','','/csms/input',1,1,2,'2018-06-27 23:47:00','2018-06-28 20:34:28'),(55,52,'出库管理','','/csms/output',1,3,2,'2018-06-27 23:47:51','2018-06-28 20:34:51'),(56,53,'编辑货号','','/csms/goods/edit',1,2,3,'2018-06-28 00:26:00','2018-06-28 19:42:40'),(57,53,'添加货号','','/csms/goods/add',1,1,3,'2018-06-28 00:31:50','2018-06-28 19:42:01'),(58,53,'删除货号','','/csms/goods/remove',1,3,3,'2018-06-28 00:32:11','2018-06-28 19:43:29'),(60,55,'删除出库单','','/sys/output/remove',1,3,3,'2018-06-28 08:39:23','2018-06-29 06:59:28'),(61,54,'添加入库单','','/csms/input/add',1,2,3,'2018-06-28 08:40:15','2018-06-28 21:21:44'),(62,54,'编辑入库单',NULL,'/csms/input/edit',1,2,3,'2018-06-28 21:22:48',NULL),(63,54,'删除入库单',NULL,'/csms/input/remove',1,3,3,'2018-06-28 21:23:02',NULL),(64,54,'提交入库单',NULL,'/csms/input/submit',1,4,3,'2018-06-28 22:47:02',NULL),(65,52,'入库明细',NULL,'/csms/inorder',1,1,2,'2018-06-28 23:08:17',NULL),(66,65,'添加明细',NULL,'/csms/inorder/add',1,1,3,'2018-06-29 00:58:11',NULL),(67,65,'编辑明细',NULL,'/csms/inorder/edit',1,2,3,'2018-06-29 00:58:26',NULL),(68,65,'删除明细','','/csms/inorder/remove',1,3,3,'2018-06-29 00:58:35','2018-06-29 00:58:58'),(69,55,'添加出库单',NULL,'/csms/output/add',1,1,3,'2018-06-29 06:59:58',NULL),(70,55,'编辑出库单',NULL,'/csms/output/edit',1,2,3,'2018-06-29 07:00:16',NULL),(71,55,'提交出库单',NULL,'/csms/output/submit',1,4,3,'2018-06-29 07:00:33',NULL),(72,52,'出库明细',NULL,'/csms/outorder',1,5,2,'2018-06-29 07:01:31',NULL),(73,72,'添加出库明细',NULL,'/csms/outorder/add',1,1,3,'2018-06-29 07:02:03',NULL),(74,72,'编辑出库明细',NULL,'/csms/outorder/edit',1,2,3,'2018-06-29 07:02:16',NULL),(75,72,'删除出库明细',NULL,'/csms/outorder/remove',1,3,3,'2018-06-29 07:02:30',NULL),(76,54,'删除出库单',NULL,'/csms/output/remove',1,3,3,'2018-06-28 21:23:02',NULL),(77,48,'编辑博客',NULL,'/sys/blog/edit',1,1,3,'2018-06-30 15:01:38',NULL),(78,48,'删除博客',NULL,'/sys/blog/remove',1,3,3,'2018-06-30 15:01:55',NULL),(79,48,'查看博客',NULL,'/sys/blog/show',1,1,3,'2018-06-30 19:05:42',NULL);
/*!40000 ALTER TABLE `sys_menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role`
--

DROP TABLE IF EXISTS `sys_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(16) DEFAULT NULL,
  `parent_id` int(11) DEFAULT NULL,
  `order_num` int(11) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `modified_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role`
--

LOCK TABLES `sys_role` WRITE;
/*!40000 ALTER TABLE `sys_role` DISABLE KEYS */;
INSERT INTO `sys_role` VALUES (1,'root',0,1,'超级管理员',NULL,NULL),(2,'admin',1,2,'管理员',NULL,NULL),(3,'user',2,3,'用户',NULL,NULL),(4,'temp',2,4,'临时用户',NULL,NULL),(8,'test',2,3,'test_test','2018-06-23 22:11:04',NULL),(9,'test',2,4,'test1','2018-06-23 22:12:13',NULL),(10,'test_tet',2,4,'tt','2018-06-23 22:17:49',NULL),(11,'test_tet',2,2,'tt','2018-06-23 22:18:03',NULL),(12,'test_tet',2,2,'tt','2018-06-23 22:18:05',NULL),(14,'test_tet',2,2,'tt','2018-06-23 22:18:06',NULL),(23,'11',2,1,'11','2018-06-23 22:29:02',NULL);
/*!40000 ALTER TABLE `sys_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role_menu`
--

DROP TABLE IF EXISTS `sys_role_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_role_menu` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `role_id` int(11) DEFAULT NULL,
  `menu_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=114 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role_menu`
--

LOCK TABLES `sys_role_menu` WRITE;
/*!40000 ALTER TABLE `sys_role_menu` DISABLE KEYS */;
INSERT INTO `sys_role_menu` VALUES (57,1,0),(58,1,1),(59,1,5),(60,1,17),(61,1,16),(62,1,18),(63,1,19),(64,1,20),(65,1,7),(66,1,13),(67,1,14),(68,1,15),(69,1,12),(70,1,2),(71,1,4),(72,1,6),(73,1,10),(74,23,0),(75,23,1),(76,23,21),(77,23,32),(78,23,42),(79,23,43),(80,23,45),(81,1,47),(82,1,48),(83,1,49),(84,1,50),(85,1,51),(86,1,52),(87,1,53),(88,1,54),(89,1,55),(90,1,56),(91,1,57),(92,1,58),(93,1,59),(94,1,60),(95,1,61),(96,1,62),(97,1,63),(98,1,64),(99,1,65),(100,1,66),(101,1,67),(102,1,68),(103,1,69),(104,1,70),(105,1,71),(106,1,72),(107,1,73),(108,1,74),(109,1,75),(110,1,76),(111,1,77),(112,1,78),(113,1,79);
/*!40000 ALTER TABLE `sys_role_menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user`
--

DROP TABLE IF EXISTS `sys_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(32) NOT NULL,
  `email` varchar(32) NOT NULL,
  `password` varchar(32) NOT NULL,
  `blog_space` varchar(16) DEFAULT NULL,
  `self_description` varchar(32) DEFAULT NULL,
  `avatar` varchar(64) DEFAULT NULL,
  `salt` varchar(16) DEFAULT NULL,
  `role_id` int(11) DEFAULT NULL,
  `status` tinyint(4) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `last_login_time` datetime DEFAULT NULL,
  `last_login_ip` char(16) DEFAULT NULL,
  `dept_id` int(11) DEFAULT NULL,
  `sex` int(11) DEFAULT NULL,
  `modified_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user`
--

LOCK TABLES `sys_user` WRITE;
/*!40000 ALTER TABLE `sys_user` DISABLE KEYS */;
INSERT INTO `sys_user` VALUES (1,'一般般帅','11','bedd6d20c168659d7ceb5a7b11e61b06','good-looking-hk','22','/img/rabbit.gif','zh6l4tq0f4',1,1,'2018-05-24 21:36:36','2018-07-11 16:05:12','127.0.0.1',1,NULL,NULL),(2,'test','test@qq.com','222cf9a2e558b9eebb4aca420397158a',NULL,'我最萌，哼','/img/rabbit.gif','a4o1klplck',4,1,'2018-06-23 13:06:34',NULL,NULL,NULL,3,NULL),(3,'test1','test1@qq.com','24e1f284cacf953ca9a0bc9ba9aa5802',NULL,'我最萌，哼','/img/rabbit.gif','q0aqvis3v1',3,1,'2018-06-23 13:19:03',NULL,NULL,NULL,3,NULL),(4,'test2','test2@qq.com','b49cbf977dbf62476035009e476aa559',NULL,'我最萌，哼','/img/rabbit.gif','dwz09mw4s3',3,1,'2018-06-23 13:19:08',NULL,NULL,NULL,3,NULL),(5,'test3','test3@qq.com','016bfa68fe0695f888241ab7741af3c2',NULL,'我最萌，哼','/img/rabbit.gif','sqabm07jq3',3,1,'2018-06-23 13:19:16',NULL,NULL,NULL,3,NULL),(6,'test4','test4@qq.com','6f145389e11d30916c97b70866c85f1c',NULL,'我最萌，哼','/img/rabbit.gif','xbcoo5nwl3',3,1,'2018-06-23 13:19:24',NULL,NULL,NULL,3,NULL),(7,'test5','test5@qq.com','4c3c53e7cbb1ee8b321a5ce80a4e01d5',NULL,'我最萌，哼','/img/rabbit.gif','tn9oimw0mo',3,1,'2018-06-23 13:19:29',NULL,NULL,NULL,3,NULL),(8,'test6','test6@qq.com','7753282b654fb564f0c0d95b3885059e',NULL,'我最萌，哼','/img/rabbit.gif','iajqb9w6vj',3,1,'2018-06-23 13:19:35',NULL,NULL,NULL,3,NULL),(10,'test8','test8@qq.com','11577e9e9477022c53d31c603b91732c',NULL,'我最萌，哼','/img/rabbit.gif','xai7dowg34',3,1,'2018-06-23 13:19:50',NULL,NULL,NULL,3,NULL),(11,'test9','test9@qq.com','6fa27221e42b911bbc727fdb1e2a3b57',NULL,'我最萌，哼','/img/rabbit.gif','v5xncom190',3,1,'2018-06-23 13:19:58',NULL,NULL,NULL,3,NULL),(12,'test10','test10@qq.com','6bc166111fd24628efd42ffd4c205835',NULL,'我最萌，哼','/img/rabbit.gif','17uh6emk1n',3,1,'2018-06-23 13:20:06',NULL,NULL,NULL,3,NULL),(13,'test11','test11@qq.com','962af2f7e793e86c0a40e00a0d38d640',NULL,'我最萌，哼','/img/rabbit.gif','lojum5te7t',3,1,'2018-06-23 13:20:13',NULL,NULL,NULL,3,NULL),(14,'test12','test12@qq.com','a760f55ce8faf7e7d8acc4713a4df038',NULL,'我最萌，哼','/img/rabbit.gif','0grtrn4wpe',3,1,'2018-06-23 13:20:23',NULL,NULL,NULL,3,NULL),(15,'test13','test13@qq.com','8a5f95ebca50ca1ae1022f8109a144b1',NULL,'我最萌，哼','/img/rabbit.gif','2i4mwsxwdj',3,1,'2018-06-23 13:20:28',NULL,NULL,NULL,3,NULL),(16,'test14','test14@qq.com','ba2039a841676e48491ef461c71ead87',NULL,'我最萌，哼','/img/rabbit.gif','1b4ae2rsky',3,1,'2018-06-23 13:20:39',NULL,NULL,NULL,3,NULL),(17,'test15','test15@qq.com','37be059c256895a1c503cdb3243c1270',NULL,'我最萌，哼','/img/rabbit.gif','qpfg4d3o1s',3,1,'2018-06-23 13:20:45',NULL,NULL,NULL,3,NULL),(18,'test16','test16@qq.com','00706e10b59c565d547d1c93318523da',NULL,'我最萌，哼','/img/rabbit.gif','i2onkrxpds',3,1,'2018-06-23 13:20:54',NULL,NULL,NULL,3,NULL),(19,'2222','1111@qq.com','e46ac2a910ddc50df252ff839d0a62b2',NULL,'我最萌，哼','/img/rabbit.gif','acpkvkreoa',3,1,'2018-06-28 20:58:54',NULL,NULL,NULL,2,NULL);
/*!40000 ALTER TABLE `sys_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `warehouse`
--

DROP TABLE IF EXISTS `warehouse`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `warehouse` (
  `id` int(11) NOT NULL,
  `warehouse` varchar(255) DEFAULT NULL,
  `belong` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `max_count` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `warehouse`
--

LOCK TABLES `warehouse` WRITE;
/*!40000 ALTER TABLE `warehouse` DISABLE KEYS */;
INSERT INTO `warehouse` VALUES (1,'总仓库','一般般帅','15197479670',1000),(2,'一号仓库','一般般可爱','12312312311',1000),(3,'二号仓库','一般般聪明','12312312312',500);
/*!40000 ALTER TABLE `warehouse` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2018-07-12 16:56:29
