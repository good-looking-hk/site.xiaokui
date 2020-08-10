package site.xiaokui.landlords.client.event;

import cn.hutool.json.JSONObject;
import io.netty.channel.Channel;
import site.xiaokui.landlords.client.ClientContainer;
import site.xiaokui.landlords.common.entity.enums.ClientEventCode;
import site.xiaokui.landlords.common.entity.enums.ClientType;
import site.xiaokui.landlords.common.print.FormatPrinter;
import site.xiaokui.landlords.common.print.SimplePrinter;

/**
 * @author HK
 * @date 2020-08-06 14:23
 */
public class ClientEventListener_CODE_GAME_POKER_PLAY_REDIRECT extends ClientEventListener {

    private static String[] choose = new String[]{"上家", "下家"};

    private static String format = "\n[%s] %s  剩余 %s [%s]";

    @Override
    public void call(Channel channel, JSONObject json) {
        int sellClientId = json.getInt("sellClientId");

        for (int index = 0; index < 2; index++) {
            for (int i = 0; i < json.getJSONArray("clientInfos").size(); i++) {
                JSONObject clientInfo = json.getJSONArray("clientInfos").getJSONObject(i);
                String position = (String) clientInfo.get("position");
                if (position.equalsIgnoreCase(choose[index])) {
                    String type = ClientType.valueOf(clientInfo.getStr("type")).getMsg();
                    FormatPrinter.printNotice(format, clientInfo.get("position"), clientInfo.get("clientNickname"), clientInfo.get("surplus"), type);
                }
            }
        }
        SimplePrinter.printNotice("");

        if (sellClientId == ClientContainer.clientId) {
            get(ClientEventCode.CODE_GAME_POKER_PLAY).call(channel, json);
        } else {
            SimplePrinter.printNotice("\n请耐心等待下家　" + json.get("sellClientNickname") + "　出牌");
        }
    }
}
