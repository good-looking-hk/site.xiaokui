package thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * RequestContextHolder
 * @author HK
 * @date 2021-03-10 17:42
 */
public class ThreadLocalTest {

    private static final ThreadLocal<String> threadLocal = new ThreadLocal(){};

    static int i = 0;

    public static void main(String[] args) throws Exception {

        ExecutorService executorService = Executors.newFixedThreadPool(4, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "线程" + i++);
            }
        });
        for (int i = 0; i < 4; i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    threadLocal.set(Thread.currentThread().getName());
                    System.out.println(Thread.currentThread().getName() +  ": threadLocal的get结果=" + threadLocal.get());
                }
            });
        }
        Thread.sleep(1000);
        for (int i = 0; i < 8; i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    threadLocal.set(Thread.currentThread().getName());
                    System.out.println(Thread.currentThread().getName() +  ": threadLocal的get结果=" + threadLocal.get());
                }
            });
        }
    }
}
