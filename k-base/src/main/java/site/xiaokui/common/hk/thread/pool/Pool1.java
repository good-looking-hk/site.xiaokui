package site.xiaokui.common.hk.thread.pool;

import java.util.concurrent.*;

/**
 * 10个运动员在赛场跑步，直到10个运动员全部到场后，宣布比赛终止
 * @author HK
 * @date 2020-03-27 17:19
 */
public class Pool1 {

    public static void main(String[] args) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(10);
        CyclicBarrier barrier = new CyclicBarrier(10);
        ExecutorService executor = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10; i++) {
            executor.execute(new Thread("少年" + i) {
                @Override
                public void run() {
                    try {
                        barrier.await();
                        System.out.println(Thread.currentThread().getName() + "开始出发（同时）");
                        Thread.sleep(1000);
                        System.out.println(Thread.currentThread().getName() + "到达终点");
                        latch.countDown();
                    } catch (InterruptedException | BrokenBarrierException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        latch.await();
        System.out.println("比赛完成");
    }
}
