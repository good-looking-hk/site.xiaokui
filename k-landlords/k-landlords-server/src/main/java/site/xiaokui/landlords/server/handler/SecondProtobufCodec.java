package site.xiaokui.landlords.server.handler;

import com.google.protobuf.MessageLite;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import site.xiaokui.landlords.common.entity.ServerTransferData;

import java.util.List;

/**
 * @author HK
 * @date 2020-08-03 16:47
 */
public class SecondProtobufCodec extends MessageToMessageCodec<ServerTransferData.ServerTransferDataProtoc, MessageLite> {

    @Override
    protected void encode(ChannelHandlerContext ctx, MessageLite msg, List<Object> out) throws Exception {
        out.add(msg);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ServerTransferData.ServerTransferDataProtoc msg, List<Object> out) throws Exception {
        out.add(msg);
    }
}
