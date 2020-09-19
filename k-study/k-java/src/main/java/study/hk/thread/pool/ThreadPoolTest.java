package study.hk.thread.pool;

import java.util.concurrent.*;

/**
 * @author HK
 * @date 2020-03-27 15:38
 */
public class ThreadPoolTest {

    static int i;

    public static void main(String[] args) {
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(10, 20, 1, TimeUnit.HOURS,
                new ArrayBlockingQueue<>(10), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "线程" + ++i);
            }
        });
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread() + "正在执行我");
            }
        });
//        ExecutorService executor = Executors.newFixedThreadPool(10);
//        Future future = executor.submit(new Runnable() {
//            @Override
//            public void run() {
//                System.out.println(Thread.currentThread() + "正在执行我");
//            }
//        });
//        try {
//            // 正常情况下返回null
//            Object o = future.get();
//            System.out.println("返回结果：" + o);
//        } catch (InterruptedException | ExecutionException e) {
//            // 处理中断异常、处理无法执行任务异常
//            e.printStackTrace();
//        }
//        finally {
//            threadPool.shutdown();
//        }
    }
}