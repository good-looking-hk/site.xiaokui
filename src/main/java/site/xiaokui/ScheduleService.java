package site.xiaokui;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.exceptions.UtilException;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import site.xiaokui.module.sys.blog.service.BlogService;

import java.util.Date;

/**
 * 具体cron表达是间http://www.cnblogs.com/peida/archive/2013/01/08/2850483.html
 *
 * @author HK
 * @date 2019-02-21 15:27
 */
@Slf4j
@Component
public class ScheduleService implements ApplicationRunner {

    @Autowired
    private BlogService blogService;

    /**
     * DefaultApplicationArguments
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Spring Boot已完全启动，启动定时任务监听");
        try {
            CronUtil.start();
        } catch (UtilException e) {
            // 已经开始监听，不可再次开始
            return;
        }
//        startTaskPerNightZeroClock("REDIS_TASK", blogService.redisTask());
//        testTask(blogService.redisTask());
    }

    /**
     * 每天23：59分执行Task任务
     * cron = 59 23 * * *
     */
    public void startTaskPerNightZeroClock(String taskName, Task task) {
        log.info("开始任务(每天23：59):" + taskName);
        CronUtil.schedule(taskName, "59 23 * * *", task);
    }

    public void testTask(Task task) {
        Date date = new Date();
        int hour = DateUtil.hour(date, true);
        int minute = DateUtil.minute(date);
        CronUtil.schedule("TEST_TASK", (minute + 1) + " " + hour + " * * *", task);
        log.debug("测试任务将于" + hour + "时" + (minute + 1) + "分开始");
    }
}