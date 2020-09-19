package netty.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author HK
 * @date 2020-03-26 17:02
 */
public class BIOServer {

    public void initServer () throws IOException {
        ServerSocket serverSocket = new ServerSocket(2222);
        System.out.println("服务器主线程等待连接....");
        AtomicInteger i = new AtomicInteger(0);
        Socket client;
        while ((client = serverSocket.accept()) != null) {
            i.incrementAndGet();
            Socket finalClient = client;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        byte[] bytes = new byte["hello".getBytes().length];
                        while (finalClient.getInputStream().read(bytes) != -1) {
                            finalClient.getOutputStream().write(bytes);
                            finalClient.close();
                            return;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }, "服务器线程" + i.get()).start();
        }
    }
}
