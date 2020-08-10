package site.xiaokui.landlords.client.handler;

import com.google.protobuf.MessageLite;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import site.xiaokui.landlords.common.entity.ClientTransferData;

import java.util.List;

/**
 * @author HK
 * @date 2020-08-04 14:16
 */
public class SecondProtobufCodec extends MessageToMessageCodec<ClientTransferData.ClientTransferDataProtoc, MessageLite> {

    @Override
    protected void encode(ChannelHandlerContext ctx, MessageLite msg, List<Object> out) throws Exception {
        out.add(msg);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ClientTransferData.ClientTransferDataProtoc msg, List<Object> out) throws Exception {
        out.add(msg);
    }

}
