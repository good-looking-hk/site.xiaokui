package site.xiaokui.landlords.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import site.xiaokui.landlords.client.handler.DefaultChannelInitializer;
import site.xiaokui.landlords.common.print.SimplePrinter;

import java.io.IOException;

/**
 * @author HK
 * @date 2020-08-04 14:09
 */
public class SimpleClient {

    public static String serverAddress = "127.0.0.1";

    public static int port = 9876;

    public static void main(String[] args) throws InterruptedException, IOException {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new DefaultChannelInitializer());
            SimplePrinter.printNotice("正在连接到服务器" + serverAddress + ":" + port + "....");
            Channel channel = bootstrap.connect(serverAddress, port).sync().channel();
            channel.closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }
}
