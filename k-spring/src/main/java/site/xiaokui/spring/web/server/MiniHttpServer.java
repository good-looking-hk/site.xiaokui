package site.xiaokui.spring.web.server;


import site.xiaokui.spring.bean.DefaultWebBeanFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author HK
 * @date 2020-04-01 16:01
 */
public class MiniHttpServer {

    private int port;

    private ServerThreadHandler serverThread;

    private volatile boolean shutdown = false;

    public MiniHttpServer(int port, DefaultWebBeanFactory defaultBeanFactory) {
        this.port = port;
        serverThread  = new ServerThreadHandler(defaultBeanFactory);
    }

    public void beginListen() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        while (!shutdown) {
            Socket socket = null;
            try {
                socket = serverSocket.accept();
                serverThread.handle(socket);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void shutdown() {
        this.shutdown = true;
    }
}
