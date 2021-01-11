-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `key` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `value` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `modified_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sys_config
-- ----------------------------
BEGIN;
INSERT INTO `sys_config` VALUES (1, '网站首页', 'index', '/blog', '2019-09-26 13:15:30', '2020-05-31 09:38:47');
INSERT INTO `sys_config` VALUES (2, '公司名称', 'company', '金微蓝', '2019-09-26 13:16:16', '2019-10-07 12:52:58');
INSERT INTO `sys_config` VALUES (3, '显示关于', 'showAbout', 'false', '2019-10-07 12:53:32', '2020-04-01 18:51:48');
INSERT INTO `sys_config` VALUES (4, '显示简历', 'showResume', 'false', '2019-10-07 12:53:28', '2019-12-13 13:37:12');
INSERT INTO `sys_config` VALUES (5, '博客首页', 'blogIndex', '/blog/good-looking-hk', '2019-11-23 16:02:13', '2020-07-29 17:12:13');
INSERT INTO `sys_config` VALUES (6, 'nginx访问日志', 'nginxAccessLogPath', '/var/log/nginx/access.log', '2020-05-23 10:04:30', '2020-05-31 18:20:09');
INSERT INTO `sys_config` VALUES (7, 'nginx错误日志', 'nginxErrorLogPath', '/var/log/nginx/error.log', '2020-05-23 10:05:56', '2020-05-23 10:05:58');
COMMIT;