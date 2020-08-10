package site.xiaokui.landlords.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import site.xiaokui.landlords.common.entity.ClientSide;
import site.xiaokui.landlords.common.entity.ServerTransferData;
import site.xiaokui.landlords.common.entity.enums.ClientEventCode;
import site.xiaokui.landlords.common.entity.enums.ClientRole;
import site.xiaokui.landlords.common.entity.enums.ClientStatus;
import site.xiaokui.landlords.common.entity.enums.ServerEventCode;
import site.xiaokui.landlords.common.print.SimplePrinter;
import site.xiaokui.landlords.common.util.ChannelUtil;
import site.xiaokui.landlords.server.ServerContainer;
import site.xiaokui.landlords.server.event.ServerEventListener;

/**
 * @author HK
 * @date 2020-08-03 17:00
 */
public class TransferHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        Channel ch = ctx.channel();
        // 第一次连接进入，初始化客户端信息
        int id = geneOrGetClientId(ctx.channel());
        ClientSide clientSide = new ClientSide(id, ClientStatus.TO_CHOOSE, ch);
        clientSide.setRole(ClientRole.PLAYER);
        clientSide.setIp(ctx.channel().remoteAddress().toString().split(":")[0].substring(1));

        ServerContainer.CLIENT_SIDE_MAP.put(clientSide.getId(), clientSide);
        SimplePrinter.serverLog("有新的客户端连接进入服务器，IP:" + clientSide.getIp() + "，CLIENT_ID:" + clientSide.getId());
        // 连接成功事件
        ChannelUtil.pushToClient(ch, ClientEventCode.CODE_CLIENT_CONNECT, String.valueOf(clientSide.getId()));
        // 设置昵称事件
        ChannelUtil.pushToClient(ch, ClientEventCode.CODE_CLIENT_NICKNAME_SET);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof ServerTransferData.ServerTransferDataProtoc) {
            ServerTransferData.ServerTransferDataProtoc serverTransferData = (ServerTransferData.ServerTransferDataProtoc) msg;
            ServerEventCode code = ServerEventCode.valueOf(serverTransferData.getCode());
            if (code != ServerEventCode.CODE_CLIENT_HEAD_BEAT) {
                ClientSide client = ServerContainer.CLIENT_SIDE_MAP.get(geneOrGetClientId(ctx.channel()));
                SimplePrinter.serverLog(client.getId() + "-" + client.getNickname() + " do:" + code + "-" + code.getMsg());
                ServerEventListener.get(code).call(client, serverTransferData.getData());
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof java.io.IOException) {
            clientOfflineEvent(ctx.channel());
        } else {
            SimplePrinter.serverLog("ERROR：" + cause.getMessage());
            cause.printStackTrace();
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                try {
                    clientOfflineEvent(ctx.channel());
                    ctx.channel().close();
                } catch (Exception e) {
                }
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    /**
     * 生成客户端ID
     */
    private int geneOrGetClientId(Channel channel) {
        String longId = channel.id().asLongText();
        Integer clientId = ServerContainer.CHANNEL_CLIENT_ID_MAP.get(longId);
        if (null == clientId) {
            clientId = ServerContainer.geneClientId();
            ServerContainer.CHANNEL_CLIENT_ID_MAP.put(longId, clientId);
        }
        return clientId;
    }

    private void clientOfflineEvent(Channel channel) {
        int clientId = geneOrGetClientId(channel);
        ClientSide client = ServerContainer.CLIENT_SIDE_MAP.get(clientId);
        if (client != null) {
            SimplePrinter.serverLog("Has client exit to the server：" + clientId + " | " + client.getNickname());
//            ServerEventListener.get(ServerEventCode.CODE_CLIENT_OFFLINE).call(client, null);
        }
    }
}
