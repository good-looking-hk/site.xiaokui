package site.xiaokui.landlords.client.event;

import cn.hutool.json.JSONObject;
import io.netty.channel.Channel;
import site.xiaokui.landlords.client.ClientContainer;
import site.xiaokui.landlords.common.print.SimplePrinter;

/**
 * @author HK
 * @date 2020-08-10 14:46
 */
public class ClientEventListener_CODE_ROOM_JOIN_SUCCESS extends ClientEventListener {

    @Override
    public void call(Channel channel, JSONObject json) {
        initLastSellInfo();
        int joinClientId = (int) json.get("clientId");
        if (ClientContainer.clientId == joinClientId) {
            SimplePrinter.printNotice("你已成功加入房间：" + json.get("roomId") + "，当前房间人数：" + json.get("roomClientCount"));
            SimplePrinter.printNotice("请耐心等待其他玩家加入，满3人开始！");
        } else {
            SimplePrinter.printNotice(json.get("clientNickname") + " 成功加入房间，当前房间人数：" + json.get("roomClientCount"));
        }
    }
}
