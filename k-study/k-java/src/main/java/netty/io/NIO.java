package netty.io;

import java.io.IOException;

/**
 * @author HK
 * @date 2020-03-26 15:32
 */
public class NIO {

    public static void main(String[] args) throws IOException, InterruptedException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    new NIOServer().initServer(4444);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        Thread.sleep(1000);
        new Client().beginTest(4444);
    }
}
