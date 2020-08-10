package site.xiaokui.landlords.server.handler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import site.xiaokui.landlords.common.entity.ServerTransferData;

import java.util.concurrent.TimeUnit;

/**
 * 半包/粘包总结: https://blog.csdn.net/qq_28822933/article/details/83713560
 * @author HK
 * @date 2020-08-03 16:42
 */
public class DefaultChannelInitializer extends ChannelInitializer<SocketChannel> {

    /**
     * ProtobufEncoder：用于对Probuf类型序列化。
     * ProtobufVarint32LengthFieldPrepender：用于在序列化的字节数组前加上一个简单的包头，只包含序列化的字节长度。
     * ProtobufVarint32FrameDecoder：用于decode前解决半包和粘包问题（利用包头中的包含数组长度来识别半包粘包）
     * ProtobufDecoder：反序列化指定的Probuf字节数组为protobuf类型。
     */
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline()
                .addLast(new IdleStateHandler(60 * 30, 0, 0, TimeUnit.SECONDS))
                .addLast(new ProtobufVarint32FrameDecoder())
                .addLast(new ProtobufDecoder(ServerTransferData.ServerTransferDataProtoc.getDefaultInstance()))
                .addLast(new ProtobufVarint32LengthFieldPrepender())
                .addLast(new ProtobufEncoder())
                .addLast(new SecondProtobufCodec())
                .addLast(new TransferHandler());
    }
}
