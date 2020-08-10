package site.xiaokui.landlords.server;

import cn.hutool.core.util.NumberUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import site.xiaokui.landlords.common.print.SimplePrinter;
import site.xiaokui.landlords.server.handler.DefaultChannelInitializer;

import java.net.InetSocketAddress;

/**
 * @author HK
 * @date 2020-08-03 15:56
 */
public class SimpleServer {


    public static void main(String[] args) throws InterruptedException {
        // 如果不指定，则采取 ServerContainer.port 中的默认端口
        if (args != null && args.length > 1) {
            if ("-p".equalsIgnoreCase(args[0]) || "-port".equalsIgnoreCase(args[0])) {
                if (!NumberUtil.isInteger(args[1])) {
                    SimplePrinter.serverLog("非法端口:" + args[1]);
                    return;
                }
                ServerContainer.port = Integer.parseInt(args[1]);
            }
        }

        // 是否使用Epoll替换Nio，以获取更好的io性能
        // parentGroup类似于服务端的accept的，会不停的轮训是否有请求事件发生，然后将请求分发给childGroup
        EventLoopGroup parentGroup = Epoll.isAvailable() ? new EpollEventLoopGroup() : new NioEventLoopGroup();
        // 真正干活的线程池
        EventLoopGroup childGroup = Epoll.isAvailable() ? new EpollEventLoopGroup() : new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap()
                    .group(parentGroup, childGroup)
                    .channel(Epoll.isAvailable() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(ServerContainer.port))
                    // handler()是发生在初始化的时候，childHandler()是发生在客户端连接之后
                    .childHandler(new DefaultChannelInitializer());
            // 绑定端口
            ChannelFuture channelFuture = bootstrap.bind().sync();

            SimplePrinter.serverLog("服务端成功运行在端口:" + ServerContainer.port);
            channelFuture.channel().closeFuture().sync();
        } finally {
            parentGroup.shutdownGracefully();
            childGroup.shutdownGracefully();
        }
    }
}
