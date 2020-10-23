package study.hk.thread.pool;

import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.*;

/**
 * 考虑如下场景：某奢侈品为保证购物体验，最多只能进入6人，其他人必须在外等候，另一个出来了才能进去一个。
 * @author HK
 * @date 2020-03-27 17:25
 */
public class Pool2 {

    static int j = 0;
    static CyclicBarrier barrier = new CyclicBarrier(10);
    static Semaphore semaphore = new Semaphore(6, false);

    public static void visit() throws InterruptedException, BrokenBarrierException {
        barrier.await();
        semaphore.acquire();
        try {
            System.out.println(Thread.currentThread().getName() + "进去购物奢侈品");
            Thread.sleep(500);
            System.out.println(Thread.currentThread().getName() + "购物奢侈品成功，出来了 ");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            semaphore.release();
        }
    }

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(10, new ThreadFactory() {
            @Override
            public Thread newThread(@NotNull Runnable r) {
                return new Thread(r, "顾客" + ++j);
            }
        });
        for (int i = 0; i < 10; i++) {
            executor.execute(new Runnable() {
                @SneakyThrows
                @Override
                public void run() {
                    Pool2.visit();
                }
            });
        }
    }
}
