package site.xiaokui.common.hk.io;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author HK
 * @date 2020-03-30 14:23
 */
public class AIOServer {

    private static ExecutorService executorService = Executors.newFixedThreadPool(5);

    public static void init() throws IOException {
        AsynchronousChannelGroup group = AsynchronousChannelGroup.withThreadPool(executorService);
        AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open(group);
        server.bind(new InetSocketAddress(2222));
        server.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {
            @Override
            public void completed(AsynchronousSocketChannel client, Object attachment) {
                server.accept(null, this);
                try {
                    System.out.println(Thread.currentThread().getName() + ":服务器与客户端" + client.getRemoteAddress() + "建立连接");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                client.read(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {
                    @Override
                    public void completed(Integer index, ByteBuffer buffer) {
                        try {
                            buffer.flip();
                            int i = buffer.getInt(0);
                            System.out.println(Thread.currentThread().getName() + " 服务器收到客户端消息" + client.getRemoteAddress().toString() + "   " + new String(buffer.array(), 0, index));
                            buffer.putInt(0, i + 1);
                            client.write(buffer).get();//这个是异步的，一定要用get 确保执行结束 才能clear
                            buffer.clear();
                            client.read(buffer, buffer, this);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void failed(Throwable exc, ByteBuffer attachment) {
                        System.out.println(exc.getMessage());
                    }
                });
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                throw new RuntimeException(exc.getMessage());
            }
        });
    }
}
