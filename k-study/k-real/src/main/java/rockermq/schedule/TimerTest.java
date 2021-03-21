package rockermq.schedule;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 单线程 + 最小堆任务消费队列（完全二叉平衡树，各根节点小于其子节点，且优先插入左侧节点，中序遍历可得排序结果集） + 轮询去堆顶取最小值执行任务。
 *
 * 不推荐使用，原因有二：
 *
 * - 对耗时任务及多任务非常不友好
 * - 对于运行时异常不友好
 * @author HK
 * @date 2021-03-15 10:27
 */
public class TimerTest {

    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) {
        test3();
    }

    private static void test1() {
        // 测试schedule、scheduleAtFixedRate的区别
        Timer timer = new Timer("定时任务timer", false);
        System.out.println("main当前时间为:" + sdf.format(new Date()));
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("schedule当前时间为:" + sdf.format(new Date()));
            }
        }, new Date(System.currentTimeMillis() + -2 * 1000), 5 * 1000);

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                System.out.println("scheduleAtFixedRate当前时间为:" + sdf.format(new Date()));
            }
        }, new Date(System.currentTimeMillis() + -2 * 1000), 5 * 1000);
    }

    private static void test2() {
        // 测试对耗时任务及多任务不友好
        Timer timer = new Timer("定时任务timer", false);
        System.out.println("main当前时间为:" + sdf.format(new Date()));
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("schedule1当前时间为:" + sdf.format(new Date()));
            }
        }, new Date(System.currentTimeMillis() + 2 * 1000), 2 * 1000);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("schedule2当前时间为:" + sdf.format(new Date()));
            }
        }, new Date(System.currentTimeMillis() + 2 * 1000), 2 * 1000);
    }

    private static void test3() {
        // 测试对耗时任务及多任务不友好
        Timer timer = new Timer("定时任务timer", false);
        System.out.println("main当前时间为:" + sdf.format(new Date()));
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("schedule1当前时间为:" + sdf.format(new Date()));
            }
        }, new Date(System.currentTimeMillis() + 2 * 1000), 2 * 1000);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("schedule2当前时间为:" + sdf.format(new Date()));
                throw new RuntimeException();
            }
        }, new Date(System.currentTimeMillis() + 3 * 1000), 2 * 1000);
    }
}
