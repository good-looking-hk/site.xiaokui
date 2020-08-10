package site.xiaokui.landlords.client.handler;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import site.xiaokui.landlords.client.event.ClientEventListener;
import site.xiaokui.landlords.common.entity.ClientTransferData;
import site.xiaokui.landlords.common.entity.enums.ClientEventCode;
import site.xiaokui.landlords.common.entity.enums.ServerEventCode;
import site.xiaokui.landlords.common.print.SimplePrinter;
import site.xiaokui.landlords.common.util.ChannelUtil;

/**
 * @author HK
 * @date 2020-08-04 14:16
 */
public class TransferHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (msg instanceof ClientTransferData.ClientTransferDataProtoc) {
            ClientTransferData.ClientTransferDataProtoc clientTransferData = (ClientTransferData.ClientTransferDataProtoc) msg;
            if (StrUtil.isNotEmpty(clientTransferData.getInfo())) {
                SimplePrinter.printNotice(clientTransferData.getInfo());
            }
            try {
                // 如果根据名称找不到对应枚举类型，则会抛错 Exception in thread "main" java.lang.IllegalArgumentException: No enum constant
                ClientEventCode code = ClientEventCode.valueOf(clientTransferData.getCode());
                String data = clientTransferData.getData();
                // 如果是json字符串内容
                if (data != null && data.startsWith("{") && data.endsWith("}")) {
                    ClientEventListener.get(code).call(ctx.channel(), new JSONObject(data));
                } else {
                    ClientEventListener.get(code).call(ctx.channel(), null);
                }
            } catch (IllegalArgumentException e) {
                // 忽略即可
                e.printStackTrace();
            }
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.WRITER_IDLE) {
                ChannelUtil.pushToServer(ctx.channel(), ServerEventCode.CODE_CLIENT_HEAD_BEAT, "heartbeat");
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof java.io.IOException) {
            SimplePrinter.printNotice("The network is not good or did not operate for a long time, has been offline");
            System.exit(0);
        }
    }
}
