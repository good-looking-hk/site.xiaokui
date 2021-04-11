package thread;

import lombok.extern.slf4j.Slf4j;

import javax.management.openmbean.TabularType;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * "main" #1 prio=5 os_prio=0 tid=0x00007f163400c000 nid=0x3e09 waiting on condition [0x00007f163a32c000]
 *    java.lang.Thread.State: TIMED_WAITING (sleeping)
 *         at java.lang.Thread.sleep(Native Method)
 *         at thread.SleepTest.main(SleepTest.java:11)
 *
 *  "main" #1 prio=5 os_prio=0 tid=0x00007fd79000c000 nid=0x4fd2 in Object.wait() [0x00007fd7942cf000]
 *    java.lang.Thread.State: TIMED_WAITING (on object monitor)
 *         at java.lang.Object.wait(Native Method)
 *         - waiting on <0x000000071a076118> (a java.lang.Class for thread.SleepTest)
 *         at thread.SleepTest.main(SleepTest.java:28)
 *         - locked <0x000000071a076118> (a java.lang.Class for thread.SleepTest)
 * @author HK
 * @date 2021-04-11 17:23
 */
@Slf4j
public class SleepTest {

    static int i;

    private final static Object lock = new Object();

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(4, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "线程" + i++);
            }
        });
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                log.info("线程1开始运行");
                synchronized (lock) {
                    log.info("线程1获取锁，准备休眠10s");
                    try {
                        Thread.sleep(10000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    log.info("线程1休眠结束");
                }
                log.info("线程1释放锁");
            }
        });
        Thread.sleep(1000);
        log.info("继续运行主线程");
        synchronized (lock) {
            log.info("主线程获取锁");
        }
    }
}
