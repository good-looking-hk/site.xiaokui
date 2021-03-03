package study.hk.thread.pool;

import com.sun.istack.internal.NotNull;

import java.util.Comparator;
import java.util.concurrent.*;

/**
 * @author HK
 * @date 2020-03-27 15:27
 */
public class Pool {

    static int i = 0;

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + "打印了这条信息");
            }
        };
        ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 20, 1, TimeUnit.HOURS,
                new ArrayBlockingQueue<>(10), new ThreadFactory() {
            @Override
            public Thread newThread(@NotNull Runnable r) {
                return new Thread(r, "线程" + i++);
            }
        });
        executor.execute(runnable);
        System.out.println(executor.getActiveCount());
        executor.shutdown();

        ThreadPoolExecutor executor1 = new ThreadPoolExecutor(5, 10, 10, TimeUnit.MINUTES,
                new LinkedBlockingDeque<>(10), new ThreadFactory() {
            @Override
            public Thread newThread(@NotNull Runnable r) {
                return new Thread(r,"线程" + i++);
            }
        }, new ThreadPoolExecutor.DiscardPolicy());
        executor1.prestartAllCoreThreads();
        executor1.execute(runnable);

        ThreadPoolExecutor executor2 = new ThreadPoolExecutor(5, 10, 10, TimeUnit.MINUTES,
                new PriorityBlockingQueue<>(10, new Comparator<Runnable>() {
                    @Override
                    public int compare(Runnable o1, Runnable o2) {
                        return 0;
                    }
                }), new ThreadFactory() {
            @Override
            public Thread newThread(@NotNull Runnable r) {
                return new Thread(r,"线程" + i++);
            }
        }, new ThreadPoolExecutor.DiscardPolicy());
        executor2.execute(runnable);

        ThreadPoolExecutor executor3 = new ThreadPoolExecutor(5, 10, 10, TimeUnit.MINUTES,
                new SynchronousQueue<>(true), new ThreadFactory() {
            @Override
            public Thread newThread(@NotNull Runnable r) {
                Thread thread = new Thread(r,"线程" + i++);
                thread.setDaemon(true);
                return thread;
            }
        }, new ThreadPoolExecutor.DiscardOldestPolicy());
        executor3.execute(runnable);

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        Future future = executorService.submit(runnable);
        System.out.println(future.get());
    }
}
