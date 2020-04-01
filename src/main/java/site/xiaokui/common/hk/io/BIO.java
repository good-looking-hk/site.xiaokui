package site.xiaokui.common.hk.io;

import java.io.IOException;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

/**
 * @author HK
 * @date 2020-03-26 15:12
 */
public class BIO {

    public static void main(String[] args) throws IOException, InterruptedException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    new BIOServer().init();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, "服务端线程").start();

        CyclicBarrier barrier = new CyclicBarrier(4);
        for (int i = 0; i < 4; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        barrier.await();
                        new Client().init();
                    } catch (IOException | InterruptedException | BrokenBarrierException e) {
                        e.printStackTrace();
                    }
                }
            }, "客户端线程" + i).start();
        }
    }
}
