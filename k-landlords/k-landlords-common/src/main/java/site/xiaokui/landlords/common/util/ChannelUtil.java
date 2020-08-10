package site.xiaokui.landlords.common.util;

import cn.hutool.json.JSONObject;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import site.xiaokui.landlords.common.entity.ClientTransferData;
import site.xiaokui.landlords.common.entity.ServerTransferData;
import site.xiaokui.landlords.common.entity.enums.ClientEventCode;
import site.xiaokui.landlords.common.entity.enums.ServerEventCode;

/**
 * @author HK
 * @date 2020-08-03 17:20
 */
public class ChannelUtil {

    private static final Log log = LogFactory.get(ChannelUtil.class);

    public static void pushToClient(Channel channel, ClientEventCode code) {
        pushToClient(channel, code, null, null);
    }

    public static void pushToClient(Channel channel, ClientEventCode code, String value) {
        pushToClient(channel, code, "{\"value\":\"" + value + "\"}", null);
    }

    public static void pushToClient(Channel channel, ClientEventCode code, JSONObject jsonObject) {
        pushToClient(channel, code, jsonObject.toString(), null);
    }

    public static void pushToClient(Channel channel, ClientEventCode code, String data, String info) {
        if (log.isInfoEnabled()) {
            log.info("向客户端推送code: {}, data: {}, info: {}", code, data, info);
        }
        if (channel != null) {
            ClientTransferData.ClientTransferDataProtoc.Builder clientTransferData = ClientTransferData.ClientTransferDataProtoc.newBuilder();
            if (code != null) {
                clientTransferData.setCode(code.toString());
            }
            if (data != null) {
                clientTransferData.setData(data);
            }
            if (info != null) {
                clientTransferData.setInfo(info);
            }
            channel.writeAndFlush(clientTransferData);
        }
    }

    public static ChannelFuture pushToServer(Channel channel, ServerEventCode code, String data) {
        if (log.isInfoEnabled()) {
            log.info("向服务端推送code: {}, data: {}", code, data);
        }
        ServerTransferData.ServerTransferDataProtoc.Builder serverTransferData = ServerTransferData.ServerTransferDataProtoc.newBuilder();
        if (code != null) {
            serverTransferData.setCode(code.toString());
        }
        if (data != null) {
            serverTransferData.setData(data);
        }
        return channel.writeAndFlush(serverTransferData);
    }
}
