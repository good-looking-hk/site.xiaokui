package netty.io;

/**
 * @author HK
 * @date 2020-03-30 14:23
 */
public class AIO {

    public static void main(String[] args) throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    new AIOServer().init();
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
        }, "服务端线程").start();
        Thread.sleep(1000);
        if (args.length != 0) {
            new Client(Integer.parseInt(args[0])).beginTest(3333);
        } else {
            new Client().beginTest(3333);
        }
    }
}
