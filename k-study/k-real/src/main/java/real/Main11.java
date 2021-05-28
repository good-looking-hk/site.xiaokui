package real;

import java.util.concurrent.CyclicBarrier;

/**
 * 不适用 volatile 最终 i = 9915   终 i = 9961   最终 i = 79997  最终 i = 76071  最终 i = 65608  最终 i = 69633  最终 i = 64370
 * 使用 volatile  最终 i = 71429   最终 i = 82330  最终 i = 71526  最终 i = 73630  最终 i = 55290  最终 i = 90220
 * @author HK
 * @date 2021-05-25 21:04
 */
public class Main11 {

    static int i = 0;

    static CyclicBarrier barrier = new CyclicBarrier(100);

//    static CountDownLatch latch = new CountDownLatch(1000);

    public static void main(String[] args) throws Exception {
        for (int i = 0; i < 100; i++) {
            new TestThread(barrier).start();
        }
        Thread.sleep(5000);
        System.out.println("最终 i = " + i);
    }

    static class TestThread extends Thread {
        CyclicBarrier barrier;

        public TestThread(CyclicBarrier barrier) {
            this.barrier = barrier;
        }

        @Override
        public void run() {
            try {
                barrier.await();
            } catch (Exception e) {
                e.printStackTrace();
            }
            for (int j = 0; j < 1000; j++) {
                i++;
            }
        }
    }
}
