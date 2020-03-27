package site.xiaokui.common.hk.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * @author HK
 * @date 2020-03-26 17:03
 */
public class Client {

    public void init() throws IOException, InterruptedException {
        Socket socket = new Socket(InetAddress.getLocalHost(), 2222);
        PrintWriter pw = new PrintWriter(socket.getOutputStream());

        String req = "hello";
        pw.println(req);
        pw.flush();

        req = "hahaha";
        pw.println(req);
        pw.flush();
        pw.close();
        System.out.println("客户端：连接结束");
    }
}
