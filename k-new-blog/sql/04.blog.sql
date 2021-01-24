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

 Date: 21/01/2021 14:50:05
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
) ENGINE=InnoDB AUTO_INCREMENT=213 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sys_blog
-- ----------------------------
BEGIN;
INSERT INTO `sys_blog` VALUES (44, 'Linux', 'linux常用命令', 'linux常用命令', 1, '1', 20181004, '2020-06-26 15:18:44', 2, 129, 0, 3620, 1, '2021-01-20 16:33:44');
INSERT INTO `sys_blog` VALUES (46, 'Java基础', '数据类型*', '数据类型*', 1, '1', 20180401, '2020-06-25 15:37:37', 1, 178, 0, 4366, 1, '2021-01-20 16:33:42');
INSERT INTO `sys_blog` VALUES (47, 'Java基础', '异常', '异常', 1, '1', 20180405, '2020-06-25 15:37:37', 3, 149, 0, 2158, 1, '2021-01-20 16:33:42');
INSERT INTO `sys_blog` VALUES (49, 'Java基础', 'File类', 'File类', 1, '1', 20180407, '2020-06-25 15:37:37', 4, 134, 0, 1904, 1, '2021-01-20 16:33:42');
INSERT INTO `sys_blog` VALUES (50, 'Java基础', '流', '流', 1, '1', 20180409, '2020-06-25 15:37:37', 5, 152, 0, 2986, 1, '2021-01-20 16:33:42');
INSERT INTO `sys_blog` VALUES (51, 'Java基础', '网络编程', '网络编程', 1, '1', 20180414, '2020-06-25 15:37:37', 7, 140, 0, 2529, 1, '2021-01-20 16:33:43');
INSERT INTO `sys_blog` VALUES (52, 'Java容器类', '容器类概述', '容器类概述', 1, '1', 20170911, '2020-06-25 15:37:37', 1, 133, 0, 2554, 1, '2021-01-20 16:33:43');
INSERT INTO `sys_blog` VALUES (53, 'Java容器类', 'ArrayList详解', 'ArrayList详解', 1, '1', 20170917, '2020-06-25 15:37:37', 3, 117, 0, 1816, 1, '2021-01-20 16:33:43');
INSERT INTO `sys_blog` VALUES (54, 'Java容器类', 'LinkedList详解', 'LinkedList详解', 1, '1', 20170920, '2020-06-25 15:37:37', 4, 124, 0, 2061, 1, '2021-01-20 16:33:43');
INSERT INTO `sys_blog` VALUES (55, 'Java容器类', 'HashMap详解*', 'HashMap详解*', 1, '1', 20170925, '2020-06-25 15:37:37', 5, 93, 0, 5622, 1, '2021-01-20 16:33:43');
INSERT INTO `sys_blog` VALUES (56, 'Java容器类', 'String详解', 'String详解', 1, '1', 20170915, '2020-06-25 15:37:37', 2, 113, 0, 3488, 1, '2021-01-20 16:33:43');
INSERT INTO `sys_blog` VALUES (57, 'Java虚拟机', 'Java内存回收', 'Java内存回收', 1, '1', 20171003, '2020-06-25 15:37:37', 2, 112, 0, 4915, 1, '2021-01-20 16:33:43');
INSERT INTO `sys_blog` VALUES (58, 'Java虚拟机', '类文件结构', '类文件结构', 1, '1', 20171008, '2020-06-25 15:37:37', 4, 111, 0, 8753, 1, '2021-01-20 16:33:43');
INSERT INTO `sys_blog` VALUES (59, 'Java虚拟机', 'Java内存分配', 'Java内存分配', 1, '1', 20171005, '2020-06-25 15:37:37', 3, 130, 0, 2778, 1, '2021-01-20 16:33:43');
INSERT INTO `sys_blog` VALUES (60, 'Java虚拟机', 'Java内存区域', 'Java内存区域', 1, '1', 20171001, '2020-06-25 15:37:37', 1, 119, 0, 2614, 1, '2021-01-20 16:33:43');
INSERT INTO `sys_blog` VALUES (61, 'Java虚拟机', '类加载机制', '类加载机制', 1, '1', 20171011, '2020-06-25 15:37:37', 5, 120, 0, 7452, 1, '2021-01-20 16:33:43');
INSERT INTO `sys_blog` VALUES (62, '剑指Offer', '7~12题', '7~12题', 1, '1', 20181112, '2020-06-25 15:37:37', 2, 89, 0, 936, 1, '2021-01-20 16:33:44');
INSERT INTO `sys_blog` VALUES (63, '剑指Offer', '43~48题', '43~48题', 1, '1', 20181124, '2020-06-25 15:37:37', 8, 99, 0, 1305, 1, '2021-01-20 16:33:45');
INSERT INTO `sys_blog` VALUES (64, '剑指Offer', '13~18题', '13~18题', 1, '1', 20181113, '2020-06-25 15:37:37', 3, 90, 0, 1340, 1, '2021-01-20 16:33:45');
INSERT INTO `sys_blog` VALUES (65, '剑指Offer', '25~30题', '25~30题', 1, '1', 20181119, '2020-06-25 15:37:37', 5, 95, 0, 2245, 1, '2021-01-20 16:33:45');
INSERT INTO `sys_blog` VALUES (66, '剑指Offer', '31~36题', '31~36题', 1, '1', 20181120, '2020-06-25 15:37:37', 6, 99, 0, 1383, 1, '2021-01-20 16:33:45');
INSERT INTO `sys_blog` VALUES (67, '剑指Offer', '19~24题', '19~24题', 1, '1', 20181116, '2020-06-25 15:37:37', 4, 93, 0, 2036, 1, '2021-01-20 16:33:45');
INSERT INTO `sys_blog` VALUES (68, '剑指Offer', '37~42题', '37~42题', 1, '1', 20181121, '2020-06-25 15:37:37', 7, 91, 0, 2402, 1, '2021-01-20 16:33:45');
INSERT INTO `sys_blog` VALUES (69, '剑指Offer', '1~6题', '1~6题', 1, '1', 20181111, '2020-06-25 15:37:37', 1, 108, 0, 2139, 1, '2021-01-20 16:33:45');
INSERT INTO `sys_blog` VALUES (70, 'Java相关', 'git常用命令', 'git常用命令', 1, '1', 20180411, '2020-06-26 11:56:08', 3, 161, 0, 3509, 1, '2021-01-20 16:33:43');
INSERT INTO `sys_blog` VALUES (71, '剑指Offer', '49~54题', '49~54题', 1, '1', 20181127, '2020-06-25 15:37:37', 9, 90, 0, 1535, 1, '2021-01-20 16:33:45');
INSERT INTO `sys_blog` VALUES (72, 'Java相关', 'idea快捷键', 'idea快捷键', 1, '1', 20171011, '2020-06-25 15:37:37', 1, 147, 0, 1322, 1, '2021-01-20 16:33:43');
INSERT INTO `sys_blog` VALUES (73, '剑指Offer', '55~60题', '55~60题', 1, '1', 20181201, '2020-06-25 15:37:37', 10, 80, 0, 1898, 1, '2021-01-20 16:33:45');
INSERT INTO `sys_blog` VALUES (86, 'Java多线程', '线程池*', '线程池*', 1, '1', 20181006, '2020-06-25 15:37:37', 3, 123, 0, 5505, 1, '2021-01-20 16:33:43');
INSERT INTO `sys_blog` VALUES (87, 'Java多线程', '进阶必读*', '进阶必读*', 1, '1', 20180721, '2020-06-25 15:37:37', 1, 109, 0, 13332, 1, '2021-01-20 16:33:43');
INSERT INTO `sys_blog` VALUES (88, 'Java多线程', 'Lock接口', 'Lock接口', 1, '1', 20181004, '2020-06-25 15:37:37', 2, 146, 0, 15110, 1, '2021-01-20 16:33:43');
INSERT INTO `sys_blog` VALUES (89, '剑指Offer', '61~66题', '61~66题', 1, '1', 20181203, '2020-06-25 15:37:37', 11, 100, 0, 2453, 1, '2021-01-20 16:33:45');
INSERT INTO `sys_blog` VALUES (90, 'MySQL', '数据库基础', '数据库基础', 1, '1', 20180316, '2020-06-26 15:49:44', 1, 130, 0, 4729, 1, '2021-01-20 16:33:44');
INSERT INTO `sys_blog` VALUES (91, '剑指Offer', '记CVTE一道算法题', '记CVTE一道算法题', 1, '1', 20181220, '2020-06-25 15:37:37', 12, 96, 0, 957, 1, '2021-01-20 16:33:45');
INSERT INTO `sys_blog` VALUES (92, 'Linux', '初识vim', '初识vim', 1, '1', 20181110, '2020-06-25 15:37:37', 3, 106, 0, 2408, 1, '2021-01-20 16:33:44');
INSERT INTO `sys_blog` VALUES (93, 'Linux', 'deepin快捷键', 'deepin快捷键', 1, '1', 20180602, '2020-06-25 15:37:37', 1, 161, 0, 617, 1, '2021-01-20 16:33:44');
INSERT INTO `sys_blog` VALUES (94, '人生篇', '三种书上学不到的能力', '三种书上学不到的能力', 1, '1', 20181118, '2021-01-10 17:35:38', 1, 82, 0, 2504, 1, '2021-01-20 16:33:44');
INSERT INTO `sys_blog` VALUES (95, '计算机网络', '基础知识', '基础知识', 1, '1', 20180311, '2020-06-25 15:37:37', 1, 105, 0, 4176, 1, '2021-01-20 16:33:45');
INSERT INTO `sys_blog` VALUES (97, 'Java虚拟机', '线程安全与锁优化', '线程安全与锁优化', 1, '1', 20181230, '2020-07-05 10:57:27', 7, 142, 0, 8527, 1, '2021-01-20 16:33:43');
INSERT INTO `sys_blog` VALUES (98, 'Java虚拟机', 'Java内存模型与线程', 'Java内存模型与线程', 1, '1', 20181229, '2020-06-25 15:37:37', 6, 145, 0, 8912, 1, '2021-01-20 16:33:43');
INSERT INTO `sys_blog` VALUES (101, 'Java进阶', 'Object类详解', 'Object类详解', 1, '1', 20180306, '2020-06-25 15:37:37', 2, 99, 0, 4032, 1, '2021-01-20 16:33:44');
INSERT INTO `sys_blog` VALUES (102, 'Java进阶', '泛型详解', '泛型详解', 1, '1', 20170610, '2020-06-25 15:37:37', 1, 88, 0, 2460, 1, '2021-01-20 16:33:44');
INSERT INTO `sys_blog` VALUES (105, 'Java基础', '面试笔试常考', '面试笔试常考', 1, '1', 20180403, '2020-06-25 15:37:37', 2, 125, 0, 3183, 1, '2021-01-20 16:33:43');
INSERT INTO `sys_blog` VALUES (106, '设计模式', '创建型模式', '创建型模式', 1, '1', 20180316, '2020-06-25 15:37:37', 1, 91, 0, 1949, 1, '2021-01-20 16:33:45');
INSERT INTO `sys_blog` VALUES (107, '设计模式', '结构型模式', '结构型模式', 1, '1', 20180318, '2020-06-25 15:37:37', 2, 86, 0, 1287, 1, '2021-01-20 16:33:45');
INSERT INTO `sys_blog` VALUES (135, 'Python学习', '我的贪吃蛇', '我的贪吃蛇', 1, '1', 20190729, '2020-06-25 15:37:37', 1, 125, 0, 1202, 1, '2021-01-20 16:33:44');
INSERT INTO `sys_blog` VALUES (141, 'Python学习', '基础巩固一', '基础巩固一', 1, '1', 20190806, '2020-06-25 15:37:37', 2, 132, 0, 4872, 1, '2021-01-20 16:33:44');
INSERT INTO `sys_blog` VALUES (144, 'Java相关', 'nginx基础', 'nginx基础', 1, '1', 20190907, '2020-06-25 15:37:37', 4, 168, 0, 891, 1, '2021-01-20 16:33:43');
INSERT INTO `sys_blog` VALUES (147, '设计模式', '行为型模式', '行为型模式', 1, '1', 20190930, '2020-06-25 15:37:37', 3, 118, 1, 1219, 1, '2021-01-20 16:33:45');
INSERT INTO `sys_blog` VALUES (153, '随笔', '实习周报一', '实习周报一', 1, '2', 20190628, '2020-06-25 15:37:37', NULL, 10, 0, 5280, 2, '2021-01-20 16:33:45');
INSERT INTO `sys_blog` VALUES (154, '随笔', '实习周报二', '实习周报二', 1, '2', 20190705, '2020-06-25 15:37:37', NULL, 10, 0, 8213, 2, '2021-01-20 16:33:45');
INSERT INTO `sys_blog` VALUES (155, '随笔', '实习周报三', '实习周报三', 1, '2', 20190712, '2020-06-25 15:37:37', NULL, 9, 0, 329, 2, '2021-01-20 16:33:45');
INSERT INTO `sys_blog` VALUES (156, '随笔', '《新喜剧之王》观后感', '《新喜剧之王》观后感', 1, '2', 20190721, '2020-06-25 15:37:37', NULL, 8, 0, 1559, 2, '2021-01-20 16:33:45');
INSERT INTO `sys_blog` VALUES (163, '随笔', 'Python爬虫', 'Python爬虫', 1, '2', 20190906, '2020-06-25 15:37:37', NULL, 10, 0, 1092, 2, '2021-01-20 16:33:45');
INSERT INTO `sys_blog` VALUES (164, '随笔', '小额支付业务总结', '小额支付业务总结', 1, '2', 20190912, '2020-06-25 15:37:37', NULL, 20, 0, 4762, 2, '2021-01-20 16:33:45');
INSERT INTO `sys_blog` VALUES (167, '随笔', 'Datax插件编写', 'Datax插件编写', 1, '2', 20190920, '2020-06-25 15:37:37', NULL, 7, 0, 2412, 2, '2021-01-20 16:33:45');
INSERT INTO `sys_blog` VALUES (169, 'Java虚拟机', '编译OpenJDK', '编译OpenJDK', 1, '1', 20191210, '2020-06-25 15:37:37', 8, 112, 0, 1052, 1, '2021-01-20 16:33:43');
INSERT INTO `sys_blog` VALUES (170, 'Java虚拟机', '性能监控与调优实战', '性能监控与调优实战', 1, '1', 20191220, '2020-06-25 15:37:37', 9, 99, 0, 2675, 1, '2021-01-20 16:33:43');
INSERT INTO `sys_blog` VALUES (172, 'Java多线程', '基础知识', '基础知识', 1, '1', 20180411, '2020-06-25 15:37:37', 0, 96, 0, 2451, 1, '2021-01-20 16:33:43');
INSERT INTO `sys_blog` VALUES (180, 'Spring源码', 'bean的加载-创建bean', 'bean的加载-创建bean', 1, '1', 20171211, '2020-06-25 15:37:37', 8, 53, 0, 9942, 1, '2021-01-20 16:33:44');
INSERT INTO `sys_blog` VALUES (181, 'Spring源码', 'bean的加载-准备阶段', 'bean的加载-准备阶段', 1, '1', 20171209, '2020-06-25 15:37:37', 7, 65, 0, 9095, 1, '2021-01-20 16:33:44');
INSERT INTO `sys_blog` VALUES (182, 'Spring源码', 'Spring AOP**', 'Spring AOP**', 1, '1', 20191216, '2020-06-25 15:37:37', 10, 80, 0, 14622, 1, '2021-01-20 16:33:44');
INSERT INTO `sys_blog` VALUES (183, 'Spring源码', 'Spring MVC之DispatchServlet**', 'Spring MVC之DispatchServlet**', 1, '1', 20200227, '2020-08-03 11:26:00', 12, 101, 0, 16802, 1, '2021-01-20 16:33:44');
INSERT INTO `sys_blog` VALUES (184, 'Spring源码', 'Spring MVC之Web请求处理流程**', 'Spring MVC之Web请求处理流程**', 1, '1', 20200402, '2020-09-12 19:55:05', 13, 189, 0, 29436, 1, '2021-01-20 16:33:44');
INSERT INTO `sys_blog` VALUES (185, 'Spring源码', '初识ApplicationContext', '初识ApplicationContext', 1, '1', 20171212, '2020-06-25 15:37:37', 9, 58, 0, 3159, 1, '2021-01-20 16:33:44');
INSERT INTO `sys_blog` VALUES (186, 'Spring源码', '初识DefaultListableBeanFactory', '初识DefaultListableBeanFactory', 1, '1', 20171202, '2020-06-25 15:37:37', 2, 67, 0, 10071, 1, '2021-01-20 16:33:44');
INSERT INTO `sys_blog` VALUES (187, 'Spring源码', '初识Spring架构', '初识Spring架构', 1, '1', 20171201, '2020-06-25 15:37:37', 1, 72, 0, 1954, 1, '2021-01-20 16:33:44');
INSERT INTO `sys_blog` VALUES (188, 'Spring源码', '加载解析XML', '加载解析XML', 1, '1', 20171203, '2020-06-25 15:37:37', 3, 33, 0, 4700, 1, '2021-01-20 16:33:44');
INSERT INTO `sys_blog` VALUES (189, 'Spring源码', '默认标签的解析', '默认标签的解析', 1, '1', 20171204, '2020-06-25 15:37:37', 4, 43, 0, 12231, 1, '2021-01-20 16:33:44');
INSERT INTO `sys_blog` VALUES (190, 'Spring源码', '自定义标签的解析', '自定义标签的解析', 1, '1', 20171205, '2020-06-25 15:37:37', 5, 74, 0, 4599, 1, '2021-01-20 16:33:44');
INSERT INTO `sys_blog` VALUES (191, 'Spring源码', 'Spring MVC之WebApplicationContext**', 'Spring MVC之WebApplicationContext**', 1, '1', 20200115, '2020-08-03 10:58:37', 11, 119, 0, 15276, 1, '2021-01-20 16:33:44');
INSERT INTO `sys_blog` VALUES (192, '随笔', '积分中心业务总结', '积分中心业务总结', 1, '2', 20191123, '2020-06-25 15:37:37', NULL, 11, 0, 831, 2, '2021-01-20 16:33:45');
INSERT INTO `sys_blog` VALUES (193, '云与容器', 'Docker常用命令', 'Docker常用命令', 1, '1', 20190714, '2021-01-14 15:03:24', 1, 97, 0, 1692, 1, '2021-01-20 16:33:44');
INSERT INTO `sys_blog` VALUES (211, '人生篇', '打工人的赚钱逻辑', '打工人的赚钱逻辑', 1, '1', 20210101, '2021-01-17 17:49:03', 4, NULL, NULL, 5543, NULL, '2021-01-17 17:49:53');
INSERT INTO `sys_blog` VALUES (212, 'MySQL', '锁', '锁', 1, '1', 20210110, '2021-01-10 17:28:07', 3, NULL, NULL, 3300, NULL, '2021-01-20 16:33:44');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
