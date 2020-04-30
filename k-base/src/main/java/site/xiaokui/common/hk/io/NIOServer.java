package site.xiaokui.common.hk.io;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * @author HK
 * @date 2020-03-30 10:48
 */
public class NIOServer {

    private Selector selector;

    public void initServer(int port) throws IOException{
        // 打开ServerSocket通道
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.socket().bind(new InetSocketAddress(port));

        // 获取一个选择器
        this.selector = Selector.open();
        // 将通道管理器与该通道进行绑定，并为该通道注册SelectionKey.OP_ACCEPT事件
        // 注册事件后，当该事件触发时会使selector.select()返回，
        // 否则selector.select()一直阻塞
        serverSocketChannel.register(this.selector, SelectionKey.OP_ACCEPT);

    }

    public void listen() throws IOException{
        System.out.println("启动服务器！");
        while (true) {
            // select()方法一直阻塞直到有注册的通道准备好了才会返回
            selector.select();
            Iterator<?> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = (SelectionKey) iterator.next();
                // 删除已选的key，防止重复处理
                iterator.remove();
                handler(key);
            }
        }
    }

    public void handler(SelectionKey key)throws IOException{
        if (key.isAcceptable()) {
            handlerAccept(key);
        }else if (key.isReadable()){
            handlerRead(key);
        }else if (key.isWritable()){
            System.out.println("can write!");
        }else if (key.isConnectable()){
            System.out.println("is connectable");
        }
    }


    public void handlerAccept(SelectionKey key) throws IOException{
        // 从SelectionKey中获取ServerSocketChannel
        ServerSocketChannel server = (ServerSocketChannel) key.channel();
        // 获取SocketChannel
        SocketChannel socketChannel = server.accept();
        // 设置成非阻塞
        socketChannel.configureBlocking(false);
        System.out.println("与客户端" + socketChannel.socket().getInetAddress() + "建立连接");
        // 为socketChannel通道建立 OP_READ 读操作，使客户端发送的内容可以被读到
        socketChannel.register(selector, SelectionKey.OP_READ);
        // 往客户端发送发送信息
        socketChannel.write(ByteBuffer.wrap("connected\n".getBytes()));
    }

    public void handlerRead(SelectionKey key)throws IOException{
        SocketChannel socketChannel = (SocketChannel) key.channel();
        // 创建读取缓冲区
        ByteBuffer byteBuffer = ByteBuffer.allocate(512);
        // 从通道读取可读取的字节数
        try {
            int readCount = socketChannel.read(byteBuffer);
            if (readCount > 0) {
                byte[] data = byteBuffer.array();
                String msg = new String(data);
                System.out.println(Thread.currentThread().getName() + ":服务端收到的信息为：" + msg);
                ByteBuffer outBuffer = ByteBuffer.wrap("收到\n".getBytes());
                socketChannel.write(outBuffer);
            } else {
                System.out.println("客户端异常退出");

            }
        } catch (IOException e) {
            System.out.println("异常信息：" + e.getMessage());
            key.cancel();
        }
    }
}
