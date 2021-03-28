package rockermq.schedule;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author HK
 * @date 2021-03-28 15:37
 */
public class ScheduledExecutorServiceTest {

    public static void main(String[] args) {
        test2();
    }

    /**
     * 测试基本使用
     */
    private static void test1() {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.schedule(new Runnable() {
            @Override
            public void run() {
                System.out.println(new Date());
            }
        }, 1, TimeUnit.SECONDS);
    }

    /**
     * 测试 断点位置
     */
    private static void test2() {
        ScheduledThreadPoolExecutor scheduledExecutorService = new ScheduledThreadPoolExecutor(1);
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                System.out.println(new Date());
            }
        }, 1, 2, TimeUnit.SECONDS);
    }
}
