package netty.io;


import java.io.IOException;

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
                    new BIOServer().initServer();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        Thread.sleep(1000);
        if (args.length != 0) {
            new Client(Integer.parseInt(args[0])).beginTest(2222);
        } else {
            new Client().beginTest(2222);
        }
    }
}
