package site.xiaokui.common.hk.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author HK
 * @date 2020-03-26 17:02
 */
public class BIOServer {

    public void init () throws IOException {
        ServerSocket serverSocket = new ServerSocket(2222);
        System.out.println("服务器主线程等待连接....");
        int i = 0;
        while (true) {
            Socket client = serverSocket.accept();
            i += 1;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println(Thread.currentThread().getName() + "与客户端" + client.getInetAddress().toString() + " 建立连接");
                    BufferedReader br;
                    try {
                        br = new BufferedReader(new InputStreamReader(client.getInputStream()));
                        String clientMessage;
                        while ((clientMessage = br.readLine()) != null) {
                            System.out.println(Thread.currentThread().getName() + "收到消息：" + clientMessage);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println(Thread.currentThread().getName() + "连接结束");
                }
            }, "服务器线程" + i).start();
        }
    }
}
