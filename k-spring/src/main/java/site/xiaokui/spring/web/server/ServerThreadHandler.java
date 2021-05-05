package site.xiaokui.spring.web.server;

import site.xiaokui.spring.bean.DefaultWebBeanFactory;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author HK
 * @date 2020-04-01 16:01
 */
public class ServerThreadHandler {

    private DefaultWebBeanFactory defaultWebBeanFactory;

    private int i = 0;

    private ThreadPoolExecutor executor = new ThreadPoolExecutor(4, 10, 10,
            TimeUnit.MINUTES, new ArrayBlockingQueue<>(10), new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "服务端线程" + ++i);
        }
    });

    private static final String RESP_MSG_HEADER = "HTTP/1.1 200 OK\r\nContent-Type: text/html\r\nContent-Length: ";

    public ServerThreadHandler(DefaultWebBeanFactory defaultWebBeanFactory) {
        this.defaultWebBeanFactory = defaultWebBeanFactory;
    }

    public void handle(Socket socket) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    StringBuilder msg = new StringBuilder();
                    String temp = null;
                    // 注意设置跳出循环的条件，即注意协议规定的结束符,否则会一直等待客户端的输入
                    while (!"".equals((temp = reader.readLine())) && temp != null) {
                        msg.append(temp).append("\n");
                    }
                    if (ServerThreadHandler.this.defaultWebBeanFactory != null) {
                        HttpRequest request = new HttpRequest(msg.toString());
                        HttpResponse response = new HttpResponse();
                        ServerThreadHandler.this.defaultWebBeanFactory.getWebApplicationContext().doRequest(request, response);
                        String respMsg = buildRespMsg(response.getMsg());
                        socket.getOutputStream().write(respMsg.getBytes());
                    } else {
                        String respMsg = buildRespMsg("hello");
                        socket.getOutputStream().write(respMsg.getBytes());
                    }
                    socket.getOutputStream().flush();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public String buildRespMsg(String msg) {
        return RESP_MSG_HEADER + msg.length() + "\r\n\r\n" + msg;
    }
}
