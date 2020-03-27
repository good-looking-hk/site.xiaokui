package site.xiaokui.common.hk.io;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

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

        CountDownLatch begin = new CountDownLatch(1);
        CountDownLatch end = new CountDownLatch(10);
        for (int i = 0; i < 3; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        begin.await();
                        new Client().init();
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }, "客户端线程" + i).start();
        }
    }
}
