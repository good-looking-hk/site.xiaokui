package rockermq.schedule;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 *
 * @author HK
 * @date 2021-03-15 10:27
 */
public class TimerTest {

    public static void main(String[] args) {
        Timer timer = new Timer("定时任务timer", false);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("timer任务在固定延迟后运行");
            }
        }, 1000L);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("timer任务在指定时间运行");
            }
        }, new Date(System.currentTimeMillis() + 1000L * 2));
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("1-timer任务以固定频率运行，当前时间为:" + new Date());
            }
        }, 1000, 3 * 1000);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                System.out.println("2-time任务以固定频率运行，当前时间为:" + new Date());
            }
        }, 1000, 3 * 1000);
    }
}
