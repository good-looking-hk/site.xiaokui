package netty.io;

import lombok.SneakyThrows;

/**
 * @author HK
 * @date 2020-03-30 14:23
 */
public class AIO {

    public static void main(String[] args) throws Exception {
        new Thread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                new AIOServer().init();
            }
        }, "服务端线程").start();

        new Client().beginTest(3333);
    }
}
