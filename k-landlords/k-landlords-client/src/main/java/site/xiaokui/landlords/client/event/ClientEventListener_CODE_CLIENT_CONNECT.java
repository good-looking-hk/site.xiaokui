package site.xiaokui.landlords.client.event;

import cn.hutool.json.JSONObject;
import io.netty.channel.Channel;
import site.xiaokui.landlords.client.ClientContainer;
import site.xiaokui.landlords.common.print.SimplePrinter;

/**
 * 与远程服务端建立连接事件
 * @author HK
 * @date 2020-08-04 14:59
 */
public class ClientEventListener_CODE_CLIENT_CONNECT extends ClientEventListener {

    @Override
    public void call(Channel channel, JSONObject jsonObject) {
        ClientContainer.clientId = Integer.parseInt(jsonObject.getStr("value"));
        SimplePrinter.printNotice("连接服务器成功，客户端ID为" + ClientContainer.clientId + "，欢迎来到 KK斗地主！！");
    }
}
