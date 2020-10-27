package netty.io;


import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author HK
 * @date 2020-03-26 17:03
 */
public class Client {

    private int number = 10;

    public Client() {
    }

    public Client(int thread) {
        this.number = thread;
    }

    private void initClient(int port) throws IOException, InterruptedException {
        Socket socket = new Socket(InetAddress.getLocalHost(), port);
        PrintWriter pw = new PrintWriter(socket.getOutputStream());
        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        String req = "hello";
        pw.print(req);
        pw.flush();

        String temp;
        while ((temp = br.readLine()) != null) {
            System.out.println("收到服务端回声消息:" + temp);
        }
        pw.close();
    }

    public void beginTest(int port) throws InterruptedException {
        AtomicInteger atomicInteger = new AtomicInteger(0);
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(number, number + 10, 10, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(number), new ThreadFactory() {
            @Override
            public Thread newThread( Runnable r) {
                return new Thread(r, "线程" + (number - atomicInteger.incrementAndGet()));
            }
        });
        int preparedThread = threadPoolExecutor.prestartAllCoreThreads();
        System.out.println("线程成功预热数:" + preparedThread);

        long beginTime = System.currentTimeMillis();
        CyclicBarrier barrier = new CyclicBarrier(number);
        CountDownLatch countDownLatch = new CountDownLatch(number);

        for (int i = 0; i < number; i++) {
            threadPoolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        barrier.await();
                        initClient(port);
                    } catch (IOException | InterruptedException | BrokenBarrierException e) {
                        e.printStackTrace();
                    } finally {
                        countDownLatch.countDown();
                    }
                }
            });
        }
        countDownLatch.await();
        System.out.println(number + "线程连接服务器，总耗时： " + (System.currentTimeMillis() - beginTime) + " ms");
        System.exit(0);
    }
}
