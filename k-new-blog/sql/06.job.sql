-- ----------------------------
-- Table structure for sys_quartz_job
-- ----------------------------
DROP TABLE IF EXISTS `sys_quartz_job`;
CREATE TABLE `sys_quartz_job` (
                                  `job_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                  `bean_name` varchar(255) DEFAULT NULL COMMENT 'Spring Bean名称',
                                  `cron_expression` varchar(255) DEFAULT NULL COMMENT 'cron 表达式',
                                  `is_pause` bit(1) DEFAULT NULL COMMENT '状态：1暂停、0启用',
                                  `job_name` varchar(255) DEFAULT NULL COMMENT '任务名称',
                                  `method_name` varchar(255) DEFAULT NULL COMMENT '方法名称',
                                  `params` varchar(255) DEFAULT NULL COMMENT '参数',
                                  `description` varchar(255) DEFAULT NULL COMMENT '备注',
                                  `person_in_charge` varchar(100) DEFAULT NULL COMMENT '负责人',
                                  `email` varchar(100) DEFAULT NULL COMMENT '报警邮箱',
                                  `sub_task` varchar(100) DEFAULT NULL COMMENT '子任务ID',
                                  `pause_after_failure` bit(1) DEFAULT NULL COMMENT '任务失败后是否暂停',
                                  `create_by` varchar(255) DEFAULT NULL COMMENT '创建者',
                                  `update_by` varchar(255) DEFAULT NULL COMMENT '更新者',
                                  `create_time` datetime DEFAULT NULL COMMENT '创建日期',
                                  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                  PRIMARY KEY (`job_id`) USING BTREE,
                                  KEY `inx_is_pause` (`is_pause`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='定时任务';

-- ----------------------------
-- Records of sys_quartz_job
-- ----------------------------
BEGIN;
INSERT INTO `sys_quartz_job` VALUES (2, 'testTask', '0/5 * * * * ?', b'1', '测试1', 'run1', 'test', '带参测试，多参使用json', '测试', NULL, NULL, NULL, NULL, 'admin', '2019-08-22 14:08:29', '2020-05-24 13:58:33');
INSERT INTO `sys_quartz_job` VALUES (3, 'testTask', '0/5 * * * * ?', b'1', '测试', 'run', '', '不带参测试', 'Zheng Jie', '', '5,6', b'1', NULL, 'admin', '2019-09-26 16:44:39', '2020-05-24 14:48:12');
INSERT INTO `sys_quartz_job` VALUES (5, 'Test', '0/5 * * * * ?', b'1', '任务告警测试', 'run', NULL, '测试', 'test', '', NULL, b'1', 'admin', 'admin', '2020-05-05 20:32:41', '2020-05-05 20:36:13');
INSERT INTO `sys_quartz_job` VALUES (6, 'testTask', '0/5 * * * * ?', b'1', '测试3', 'run2', NULL, '测试3', 'Zheng Jie', '', NULL, b'1', 'admin', 'admin', '2020-05-05 20:35:41', '2020-05-05 20:36:07');
INSERT INTO `sys_quartz_job` VALUES (7, 'BlogViewCountTask', '0 0 1,8,17 * * ?', b'0', '刷新redis缓存访问量到数据库', 'syncRedisViewCountToDb', NULL, '每天1、8、17点执行一次', 'admin', '', NULL, b'0', 'admin', 'admin', '2020-05-05 20:35:41', '2020-05-05 20:36:07');
INSERT INTO `sys_quartz_job` VALUES (8, 'BlogViewCountTask', '0 0 0 * * ?', b'0', '清除redis缓存访问量贡献黑名单', 'clearContributeBlackList', NULL, '每日00点00分执行', 'admin', '', NULL, b'0', 'admin', 'admin', '2020-05-05 20:35:41', '2020-05-05 20:36:07');
INSERT INTO `sys_quartz_job` VALUES (9, 'BlogViewCountTask', '0 1 0 * * ?', b'1', '刷新数据库访问量缓存到redis', 'syncDbViewCountToRedis', NULL, '每日00点01分执行，不建议自动跑', 'admin', '', NULL, b'1', 'admin', 'admin', '2020-05-05 20:35:41', '2020-05-05 20:36:07');

COMMIT;