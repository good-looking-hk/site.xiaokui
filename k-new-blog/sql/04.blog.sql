/*
 Navicat Premium Data Transfer

 Source Server         : local
 Source Server Type    : MySQL
 Source Server Version : 100323
 Source Host           : localhost:3306
 Source Schema         : eladmin

 Target Server Type    : MySQL
 Target Server Version : 100323
 File Encoding         : 65001

 Date: 11/01/2021 17:54:31
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_blog
-- ----------------------------
DROP TABLE IF EXISTS `sys_blog`;
CREATE TABLE `sys_blog` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '博客id',
  `dir` varchar(32) DEFAULT NULL COMMENT '博客目录',
  `title` varchar(48) DEFAULT NULL COMMENT '博客标题',
  `file_name` varchar(64) DEFAULT NULL COMMENT '博客文件名称',
  `user_id` int(11) DEFAULT NULL COMMENT '拥有者id',
  `blog_type` varchar(2) DEFAULT NULL COMMENT '博客类型',
  `create_date` int(11) DEFAULT NULL COMMENT '创建时间',
  `last_upload_time` datetime DEFAULT NULL COMMENT '上次上传时间',
  `order_num` int(11) DEFAULT NULL COMMENT '排序号',
  `view_count` int(11) DEFAULT NULL COMMENT '总浏览次数',
  `yesterday_view` int(11) DEFAULT NULL COMMENT '昨日访问',
  `character_count` int(11) DEFAULT NULL COMMENT '字符数',
  `status` int(11) DEFAULT NULL COMMENT '状态',
  `update_time` datetime DEFAULT NULL ON UPDATE current_timestamp() COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `index_dir_title` (`dir`,`title`) COMMENT '博客目录 + 标题'
) ENGINE=InnoDB AUTO_INCREMENT=194 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sys_blog
-- ----------------------------
BEGIN;
INSERT INTO `sys_blog` VALUES (44, 'Linux', 'linux常用命令', 'linux常用命令', 1, '1', 20181004, NULL, 2, 129, 0, 10469, 1, '2021-01-11 17:04:51');
INSERT INTO `sys_blog` VALUES (46, 'Java基础', '数据类型*', '数据类型*', 1, '1', 20180401, NULL, 1, 178, 0, 8578, 1, '2020-12-25 10:35:30');
INSERT INTO `sys_blog` VALUES (47, 'Java基础', '异常', '异常', 1, '1', 20180405, NULL, 3, 149, 0, NULL, 1, '2021-01-11 16:55:31');
INSERT INTO `sys_blog` VALUES (49, 'Java基础', 'File类', 'File类', 1, '1', 20180407, NULL, 4, 134, 0, NULL, 1, '2021-01-11 16:55:31');
INSERT INTO `sys_blog` VALUES (50, 'Java基础', '流', '流', 1, '1', 20180409, NULL, 5, 152, 0, NULL, 1, '2021-01-11 16:55:31');
INSERT INTO `sys_blog` VALUES (51, 'Java基础', '网络编程', '网络编程', 1, '1', 20180414, NULL, 7, 140, 0, NULL, 1, '2021-01-11 16:55:38');
INSERT INTO `sys_blog` VALUES (52, 'Java容器类', '容器类概述', '容器类概述', 1, '1', 20170911, NULL, 1, 133, 0, NULL, 1, '2021-01-11 16:55:31');
INSERT INTO `sys_blog` VALUES (53, 'Java容器类', 'ArrayList详解', 'ArrayList详解', 1, '1', 20170917, NULL, 3, 117, 0, NULL, 1, '2021-01-11 17:17:16');
INSERT INTO `sys_blog` VALUES (54, 'Java容器类', 'LinkedList详解', 'LinkedList详解', 1, '1', 20170920, NULL, 4, 124, 0, NULL, 1, '2021-01-11 16:55:31');
INSERT INTO `sys_blog` VALUES (55, 'Java容器类', 'HashMap详解*', 'HashMap详解*', 1, '1', 20170925, NULL, 5, 93, 0, 15756, 1, '2020-12-25 10:35:31');
INSERT INTO `sys_blog` VALUES (56, 'Java容器类', 'String详解', 'String详解', 1, '1', 20170915, NULL, 2, 113, 0, NULL, 1, '2021-01-11 17:17:33');
INSERT INTO `sys_blog` VALUES (57, 'Java虚拟机', 'Java内存回收', 'Java内存回收', 1, '1', 20171003, NULL, 2, 112, 0, NULL, 1, '2021-01-11 16:55:31');
INSERT INTO `sys_blog` VALUES (58, 'Java虚拟机', '类文件结构', '类文件结构', 1, '1', 20171008, NULL, 4, 111, 0, NULL, 1, '2021-01-11 16:55:31');
INSERT INTO `sys_blog` VALUES (59, 'Java虚拟机', 'Java内存分配', 'Java内存分配', 1, '1', 20171005, NULL, 3, 130, 0, NULL, 1, '2021-01-11 16:55:31');
INSERT INTO `sys_blog` VALUES (60, 'Java虚拟机', 'Java内存区域', 'Java内存区域', 1, '1', 20171001, NULL, 1, 119, 0, 4352, 1, '2020-12-25 10:35:31');
INSERT INTO `sys_blog` VALUES (61, 'Java虚拟机', '类加载机制', '类加载机制', 1, '1', 20171011, NULL, 5, 120, 0, 11357, 1, '2020-12-25 10:35:31');
INSERT INTO `sys_blog` VALUES (62, '剑指Offer', '7~12题', '7~12题', 1, '1', 20181112, NULL, 2, 89, 0, 3421, 1, '2020-12-25 10:35:31');
INSERT INTO `sys_blog` VALUES (63, '剑指Offer', '43~48题', '43~48题', 1, '1', 20181124, NULL, 8, 99, 0, NULL, 1, '2021-01-11 16:55:31');
INSERT INTO `sys_blog` VALUES (64, '剑指Offer', '13~18题', '13~18题', 1, '1', 20181113, NULL, 3, 90, 0, 6303, 1, '2020-12-25 10:35:31');
INSERT INTO `sys_blog` VALUES (65, '剑指Offer', '25~30题', '25~30题', 1, '1', 20181119, NULL, 5, 95, 0, 10541, 1, '2020-12-25 10:35:32');
INSERT INTO `sys_blog` VALUES (66, '剑指Offer', '31~36题', '31~36题', 1, '1', 20181120, NULL, 6, 99, 0, NULL, 1, '2021-01-11 16:55:31');
INSERT INTO `sys_blog` VALUES (67, '剑指Offer', '19~24题', '19~24题', 1, '1', 20181116, NULL, 4, 93, 0, 9169, 1, '2020-12-25 10:35:32');
INSERT INTO `sys_blog` VALUES (68, '剑指Offer', '37~42题', '37~42题', 1, '1', 20181121, NULL, 7, 91, 0, NULL, 1, '2021-01-11 16:55:31');
INSERT INTO `sys_blog` VALUES (69, '剑指Offer', '1~6题', '1~6题', 1, '1', 20181111, NULL, 1, 108, 0, 9525, 1, '2020-12-25 10:35:32');
INSERT INTO `sys_blog` VALUES (70, 'Java相关', 'git常用命令', 'git常用命令', 1, '1', 20180411, NULL, 3, 161, 0, 9269, 1, '2020-12-25 10:35:32');
INSERT INTO `sys_blog` VALUES (71, '剑指Offer', '49~54题', '49~54题', 1, '1', 20181127, NULL, 9, 90, 0, NULL, 1, '2021-01-11 16:55:31');
INSERT INTO `sys_blog` VALUES (72, 'Java相关', 'idea快捷键', 'idea快捷键', 1, '1', 20171011, NULL, 1, 147, 0, NULL, 1, '2021-01-11 16:55:31');
INSERT INTO `sys_blog` VALUES (73, '剑指Offer', '55~60题', '55~60题', 1, '1', 20181201, NULL, 10, 80, 0, NULL, 1, '2021-01-11 16:55:38');
INSERT INTO `sys_blog` VALUES (86, 'Java多线程', '线程池*', '线程池*', 1, '1', 20181006, NULL, 3, 123, 0, 17802, 1, '2020-12-25 10:35:32');
INSERT INTO `sys_blog` VALUES (87, 'Java多线程', '进阶必读*', '进阶必读*', 1, '1', 20180721, NULL, 1, 109, 0, 43148, 1, '2021-01-11 17:04:51');
INSERT INTO `sys_blog` VALUES (88, 'Java多线程', 'Lock接口', 'Lock接口', 1, '1', 20181004, NULL, 2, 146, 0, 36035, 1, '2020-12-25 10:35:32');
INSERT INTO `sys_blog` VALUES (89, '剑指Offer', '61~66题', '61~66题', 1, '1', 20181203, NULL, 11, 100, 0, NULL, 1, '2021-01-11 16:55:31');
INSERT INTO `sys_blog` VALUES (90, 'Java相关', 'mysql基础', 'mysql基础', 1, '1', 20180315, NULL, 2, 130, 0, NULL, 1, '2021-01-11 17:04:51');
INSERT INTO `sys_blog` VALUES (91, '剑指Offer', '记CVTE一道算法题', '记CVTE一道算法题', 1, '1', 20181220, NULL, 12, 96, 0, NULL, 1, '2021-01-11 16:55:31');
INSERT INTO `sys_blog` VALUES (92, 'Linux', '初识vim', '初识vim', 1, '1', 20181110, NULL, 3, 106, 0, NULL, 1, '2021-01-11 17:18:38');
INSERT INTO `sys_blog` VALUES (93, 'Linux', 'deepin快捷键', 'deepin快捷键', 1, '1', 20180602, NULL, 1, 261, 0, NULL, 1, '2021-01-11 17:18:52');
INSERT INTO `sys_blog` VALUES (94, '人生篇', '三种书上学不到的能力', '三种书上学不到的能力', 1, '1', 20181118, NULL, 1, 82, 0, 2383, 1, '2020-12-25 10:35:33');
INSERT INTO `sys_blog` VALUES (95, '计算机网络', '基础知识', '基础知识', 1, '1', 20180310, NULL, 1, 105, 0, NULL, 1, '2021-01-11 17:04:51');
INSERT INTO `sys_blog` VALUES (97, 'Java虚拟机', '线程安全与锁优化', '线程安全与锁优化', 1, '1', 20181230, NULL, 7, 142, 0, NULL, 1, '2021-01-11 16:55:31');
INSERT INTO `sys_blog` VALUES (98, 'Java虚拟机', 'Java内存模型与线程', 'Java内存模型与线程', 1, '1', 20181229, NULL, 6, 145, 0, NULL, 1, '2021-01-11 16:55:31');
INSERT INTO `sys_blog` VALUES (99, '测试', '测试', '测试', 2, '1', 20181231, NULL, 1, NULL, NULL, NULL, 1, '2021-01-11 17:04:51');
INSERT INTO `sys_blog` VALUES (101, 'Java进阶', 'Object类详解', 'Object类详解', 1, '1', 20180306, NULL, 2, 99, 0, NULL, 1, '2020-12-25 10:35:33');
INSERT INTO `sys_blog` VALUES (102, 'Java进阶', '泛型详解', '泛型详解', 1, '1', 20170610, NULL, 1, 88, 0, NULL, 1, '2021-01-11 16:55:31');
INSERT INTO `sys_blog` VALUES (103, '转载系列', '服务器部署SpringBoot项目耗时巨长', '服务器部署SpringBoot项目耗时巨长', 1, '1', 20190219, NULL, 1, 95, 0, NULL, 1, '2021-01-11 17:04:51');
INSERT INTO `sys_blog` VALUES (105, 'Java基础', '面试笔试常考', '面试笔试常考', 1, '1', 20180403, NULL, 2, 225, 0, NULL, 1, '2021-01-11 16:55:31');
INSERT INTO `sys_blog` VALUES (106, '设计模式', '创建型模式', '创建型模式', 1, '1', 20180316, NULL, 1, 91, 0, NULL, 1, '2020-12-25 10:35:34');
INSERT INTO `sys_blog` VALUES (107, '设计模式', '结构型模式', '结构型模式', 1, '1', 20180318, NULL, 2, 86, 0, NULL, 1, '2020-12-25 10:35:34');
INSERT INTO `sys_blog` VALUES (135, 'Python学习', '我的贪吃蛇', '我的贪吃蛇', 1, '1', 20190729, NULL, 1, 125, 0, NULL, 1, '2021-01-11 17:04:51');
INSERT INTO `sys_blog` VALUES (141, 'Python学习', '基础巩固一', '基础巩固一', 1, '1', 20190806, NULL, 2, 132, 0, NULL, 1, '2021-01-11 17:04:51');
INSERT INTO `sys_blog` VALUES (144, 'Java相关', 'nginx基础', 'nginx基础', 1, '1', 20190907, NULL, 4, 168, 0, NULL, 1, '2020-12-25 10:35:34');
INSERT INTO `sys_blog` VALUES (147, '设计模式', '行为型模式', '行为型模式', 1, '1', 20190930, NULL, 3, 118, 1, NULL, 1, '2020-12-25 10:35:34');
INSERT INTO `sys_blog` VALUES (153, '金微蓝', '实习周报一', '实习周报一', 1, '2', 20190628, NULL, NULL, 10, 0, 28492, 2, '2021-01-11 17:04:51');
INSERT INTO `sys_blog` VALUES (154, '金微蓝', '实习周报二', '实习周报二', 1, '2', 20190705, NULL, NULL, 10, 0, 35874, 2, '2021-01-11 17:04:51');
INSERT INTO `sys_blog` VALUES (155, '金微蓝', '实习周报三', '实习周报三', 1, '2', 20190712, NULL, NULL, 9, 0, 378, 2, '2021-01-11 17:04:51');
INSERT INTO `sys_blog` VALUES (156, '金微蓝', '《新喜剧之王》观后感', '《新喜剧之王》观后感', 1, '2', 20190721, NULL, NULL, 8, 0, 1697, 2, '2021-01-11 17:04:51');
INSERT INTO `sys_blog` VALUES (163, '金微蓝', 'Python爬虫', 'Python爬虫', 1, '2', 20190906, NULL, NULL, 10, 0, 4418, 2, '2021-01-11 17:04:51');
INSERT INTO `sys_blog` VALUES (164, '金微蓝', '小额支付业务总结', '小额支付业务总结', 1, '2', 20190912, NULL, NULL, 20, 0, 9063, 2, '2021-01-11 17:04:51');
INSERT INTO `sys_blog` VALUES (167, '金微蓝', 'Datax插件编写', 'Datax插件编写', 1, '2', 20190920, NULL, NULL, 7, 0, 17201, 2, '2021-01-11 17:04:51');
INSERT INTO `sys_blog` VALUES (169, 'Java虚拟机', '编译OpenJDK', '编译OpenJDK', 1, '1', 20191210, NULL, 8, 112, 0, 2776, 1, '2020-12-25 10:35:35');
INSERT INTO `sys_blog` VALUES (170, 'Java虚拟机', '性能监控与调优实战', '性能监控与调优实战', 1, '1', 20191220, NULL, 9, 99, 0, 8121, 1, '2020-12-25 10:35:35');
INSERT INTO `sys_blog` VALUES (172, 'Java多线程', '基础知识', '基础知识', 1, '1', 20180411, NULL, 0, 96, 0, 8327, 1, '2020-12-25 10:35:35');
INSERT INTO `sys_blog` VALUES (180, 'Spring源码', 'bean的加载-创建bean', 'bean的加载-创建bean', 1, '1', 20171211, NULL, 8, 53, 0, 43734, 1, '2021-01-11 17:04:51');
INSERT INTO `sys_blog` VALUES (181, 'Spring源码', 'bean的加载-准备阶段', 'bean的加载-准备阶段', 1, '1', 20171209, NULL, 7, 65, 0, 31058, 1, '2021-01-11 17:04:51');
INSERT INTO `sys_blog` VALUES (182, 'Spring源码', 'Spring AOP**', 'Spring AOP**', 1, '1', 20191216, NULL, 10, 80, 0, 65675, 1, '2021-01-11 17:04:51');
INSERT INTO `sys_blog` VALUES (183, 'Spring源码', 'Spring MVC之DispatchServlet**', 'Spring MVC之DispatchServlet**', 1, '1', 20200227, NULL, 12, 101, 0, 82039, 1, '2021-01-11 17:04:51');
INSERT INTO `sys_blog` VALUES (184, 'Spring源码', 'Spring MVC之Web请求处理流程**', 'Spring MVC之Web请求处理流程**', 1, '1', 20200402, NULL, 13, 189, 0, 197822, 1, '2021-01-11 17:04:51');
INSERT INTO `sys_blog` VALUES (185, 'Spring源码', '初识ApplicationContext', '初识ApplicationContext', 1, '1', 20171212, NULL, 9, 58, 0, 19189, 1, '2021-01-11 17:04:51');
INSERT INTO `sys_blog` VALUES (186, 'Spring源码', '初识DefaultListableBeanFactory', '初识DefaultListableBeanFactory', 1, '1', 20171202, NULL, 2, 67, 0, 60631, 1, '2021-01-11 17:04:51');
INSERT INTO `sys_blog` VALUES (187, 'Spring源码', '初识Spring架构', '初识Spring架构', 1, '1', 20171201, NULL, 1, 72, 0, 2976, 1, '2021-01-11 17:04:51');
INSERT INTO `sys_blog` VALUES (188, 'Spring源码', '加载解析XML', '加载解析XML', 1, '1', 20171203, NULL, 3, 33, 0, 20028, 1, '2021-01-11 17:04:51');
INSERT INTO `sys_blog` VALUES (189, 'Spring源码', '默认标签的解析', '默认标签的解析', 1, '1', 20171204, NULL, 4, 43, 0, 56514, 1, '2021-01-11 17:04:51');
INSERT INTO `sys_blog` VALUES (190, 'Spring源码', '自定义标签的解析', '自定义标签的解析', 1, '1', 20171205, NULL, 5, 74, 0, 17151, 1, '2021-01-11 17:04:51');
INSERT INTO `sys_blog` VALUES (191, 'Spring源码', 'Spring MVC之WebApplicationContext**', 'Spring MVC之WebApplicationContext**', 1, '1', 20200115, NULL, 11, 119, 0, 79743, 1, '2021-01-11 17:04:51');
INSERT INTO `sys_blog` VALUES (192, '金微蓝', '积分中心业务总结', '积分中心业务总结', 1, '2', 20191123, NULL, NULL, 11, 0, 1039, 2, '2021-01-11 17:04:51');
INSERT INTO `sys_blog` VALUES (193, '云与虚拟化', 'Docker常用命令', 'Docker常用命令', 1, '1', 20200714, NULL, 1, 97, 0, 5775, 1, '2021-01-11 17:04:51');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
