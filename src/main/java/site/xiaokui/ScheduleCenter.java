package site.xiaokui;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.exceptions.UtilException;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import lombok.extern.slf4j.Slf4j;
import org.beetl.sql.core.SQLReady;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;
import site.xiaokui.common.aop.annotation.Log;
import site.xiaokui.module.base.service.EmailService;
import site.xiaokui.module.base.service.RedisService;
import site.xiaokui.module.sys.blog.RedisKey;
import site.xiaokui.module.sys.blog.entity.SysBlog;
import site.xiaokui.module.sys.blog.service.BlogService;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import static site.xiaokui.module.base.BaseConstants.PROFILE_REMOTE;

/**
 * 具体cron表达是间 http://www.cnblogs.com/peida/archive/2013/01/08/2850483.html
 *
 * @author HK
 * @date 2019-02-21 15:27
 */
@Slf4j
@Component
public class ScheduleCenter implements ApplicationRunner, DisposableBean {

    @Autowired
    private BlogService blogService;

    @Autowired
    private RedisService redisService;

    @Value("${spring.profiles.active}")
    private String profile;


    @Override
    public void run(ApplicationArguments args) {
        log.info("Spring Boot已完全启动，启动定时任务监听");
        try {
            CronUtil.start();
        } catch (UtilException e) {
            // 已经开始监听，不可再次开始
            return;
        }
        Task taks1 = clearContributeBlackListTask();
        Task task2 = syncRedisViewCountToDbTask();
        if (PROFILE_REMOTE.equals(profile)) {
            startTaskPerNightZeroClock("清除黑名单任务",taks1);
            startTaskPerFourHour("同步博客访问量任务", task2);
        } else {
            testTask(taks1);
            testTask(task2);
        }
    }

    /**
     * Spring会调用java.lang.Runtime.addShutdownHook(Thread hook)注册一个钩子，在以下几种情况会主动调用：
     *  1.程序正常停止
     *  2.Reach the end of program
     *  3.System.exit
     *  4.程序异常退出
     *  5.NPE
     *  6.OutOfMemory
     *  7.受到外界影响停止
     *  8.Ctrl+C
     *  9.用户注销或者关机
     */
    @Override
    public void destroy() throws Exception {
    }

    /**
     * 每天00 : 00分执行清空博客贡献访问量黑名单任务
     * cron = 24 00 * * *
     */
    public void startTaskPerNightZeroClock(String taskName, Task task) {
        log.info("开始任务(每天24:00):" + taskName);
        CronUtil.schedule(taskName, "00 24 * * *", task);
    }

    public void startTaskPerFourHour(String taskName, Task task) {
        log.info("开始任务(每4小时):" + taskName);
        CronUtil.schedule(taskName, "* */4 * * *", task);
    }

    public void testTask(Task task) {
        Date date = new Date();
        int hour = DateUtil.hour(date, true);
        int minute = DateUtil.minute(date);
        CronUtil.schedule ((minute + 1) + " " + hour + " * * *", task);
        log.debug("测试任务将于" + hour + "时" + (minute + 1) + "分开始");
    }

    public Task clearContributeBlackListTask() {
        return new Task() {
            @Override
            public void execute() {
                blogService.clearContributeBlackList();
            }
        };
    }

    public Task syncRedisViewCountToDbTask() {
        return new Task() {
            @Override
            public void execute() {
                blogService.syncRedisViewCountToDb();
            }
        };
    }
}