package site.xiaokui.common.hk.thread.basic;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.concurrent.*;

/**
 * @author HK
 * @date 2018-10-14 18:59
 */
public class ThreadPoolTest {

    static int i;

    HashMap map;

    Hashtable table;

    HashSet set;

    public static void main(String[] args) {
        System.out.println(Runtime.getRuntime().availableProcessors());
    }

    public static void a(String[] args) {
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(10, 20, 2, TimeUnit.HOURS,
                new ArrayBlockingQueue<>(10), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "线程" + ++i);
            }
        });
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                // do something
            }
        });
        ExecutorService executor = Executors.newFixedThreadPool(10);
        Future future = executor.submit(new Runnable() {
            @Override
            public void run() {
                // do something
            }
        });
        try {
            Object o = future.get();
        } catch (InterruptedException e) {
            // 处理中断异常
        } catch (ExecutionException e) {
            // 处理无法执行任务异常
        } finally {
            threadPool.shutdown();
        }
    }
}
