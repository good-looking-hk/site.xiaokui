delete from sys_quartz_job where job_id in (7, 8，9);

INSERT INTO `sys_quartz_job` VALUES (7, 'BlogViewCountTask', '0 0 1,8,17 * * ?', b'0', '刷新redis缓存访问量到数据库', 'syncRedisViewCountToDb', NULL, '每天1、8、17点执行一次', 'admin', '', NULL, b'0', 'admin', 'admin', '2020-05-05 20:35:41', '2020-05-05 20:36:07');
INSERT INTO `sys_quartz_job` VALUES (8, 'BlogViewCountTask', '0 0 0 * * ?', b'0', '清除redis缓存访问量贡献黑名单', 'clearContributeBlackList', NULL, '每日00点00分执行', 'admin', '', NULL, b'0', 'admin', 'admin', '2020-05-05 20:35:41', '2020-05-05 20:36:07');
INSERT INTO `sys_quartz_job` VALUES (9, 'BlogViewCountTask', '0 1 0 * * ?', b'1', '刷新数据库访问量缓存到redis', 'syncDbViewCountToRedis', NULL, '每日00点01分执行，不建议自动跑', 'admin', '', NULL, b'1', 'admin', 'admin', '2020-05-05 20:35:41', '2020-05-05 20:36:07');

