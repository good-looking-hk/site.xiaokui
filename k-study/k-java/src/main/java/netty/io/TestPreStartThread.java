package netty.io;


import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author HK
 * @date 2020-09-07 14:13
 */
public class TestPreStartThread {

    volatile static int max = 0;

    public static void main(String[] args) throws Exception {
        beginTest();
    }

    private static void beginTest() throws InterruptedException {
        int number = 3000;
        AtomicInteger atomicInteger = new AtomicInteger(0);
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(number, number + 10, 10, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(number / 2), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "线程" + (number - atomicInteger.incrementAndGet()));
            }
        });

        long beginTime = System.currentTimeMillis();
        CyclicBarrier barrier = new CyclicBarrier(number);
        CountDownLatch countDownLatch = new CountDownLatch(number);
        // 使其预热不充分，增大异常的复现率，大概失败率是10分之一
        int preparedThread = threadPoolExecutor.prestartAllCoreThreads();
        System.out.println("线程成功预热数:" + preparedThread);

        // 加上这行，使其预热充分
//        Thread.sleep(2000);
        for (int i = 0; i < number; i++) {
            threadPoolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        int i = barrier.await();
                    } catch (InterruptedException | BrokenBarrierException e) {
                        e.printStackTrace();
                    } finally {
                        countDownLatch.countDown();
                    }
                }
            });
        }
        System.out.println("等待中，当前等待数=" + barrier.getNumberWaiting() + ",期待值= " + barrier.getParties() + ",锁值=" + countDownLatch.getCount());
        countDownLatch.await();
        System.out.println(number + "线程连接服务器，总耗时：" + (System.currentTimeMillis() - beginTime) + "ms");
        System.exit(0);
    }
}
