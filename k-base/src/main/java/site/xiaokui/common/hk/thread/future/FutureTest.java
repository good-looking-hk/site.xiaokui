package site.xiaokui.common.hk.thread.future;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.*;

/**
 * @author HK
 * @date 2020-03-29 14:43
 */
public class FutureTest {

    static int i = 0;

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ThreadFactory factory = new ThreadFactory() {
            @Override
            public Thread newThread(@NotNull Runnable r) {
                return new Thread(r, "线程" + ++i);
            }
        };
        Callable<Integer> callable = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                Thread.sleep(4000);
                return 1;            }
        };
        ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 10, 10, TimeUnit.MINUTES,
                new ArrayBlockingQueue<>(10), factory, new ThreadPoolExecutor.CallerRunsPolicy());

        Future<Integer> future = executor.submit(callable);
        long start = System.nanoTime();
        System.out.println("当前时间：" + start);
        // 会阻塞在这里，相当于Thread.join()
        System.out.println(future.get());
        System.out.println("耗时" + (TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start)) + "毫秒");

        future = executor.submit(callable);
        start = System.nanoTime();
        System.out.println("当前时间：" + start);
        while (!future.isDone()) {
        }
        System.out.println(future.get());
        System.out.println("耗时" + (TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start)) + "毫秒");
    }
}
