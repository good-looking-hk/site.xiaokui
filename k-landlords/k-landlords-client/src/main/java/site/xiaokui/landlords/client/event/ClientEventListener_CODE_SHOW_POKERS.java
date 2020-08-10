package site.xiaokui.landlords.client.event;

import cn.hutool.json.JSONObject;
import io.netty.channel.Channel;
import site.xiaokui.landlords.common.entity.Poker;
import site.xiaokui.landlords.common.entity.enums.ClientType;
import site.xiaokui.landlords.common.print.SimplePrinter;

import java.util.List;
import java.util.Map;

/**
 * @author HK
 * @date 2020-08-07 17:07
 */
public class ClientEventListener_CODE_SHOW_POKERS extends ClientEventListener{

    @Override
    public void call(Channel channel, JSONObject json) {
        lastSellClientNickname = (String) json.get("clientNickname");
        lastSellClientType = ClientType.valueOf(json.getStr("clientType")).getMsg();
        SimplePrinter.printNotice(lastSellClientNickname + "[" + lastSellClientType + "] 出牌:");
        lastPokers = json.getJSONArray("pokers").toList(Poker.class);
        SimplePrinter.printPokers(lastPokers);

        if(json.containsKey("sellClientNickname")) {
            SimplePrinter.printNotice("下一个出的是 " + json.get("sellClientNickname") + "，请耐心等待");
        }
    }

    public static void main(String[] args) {
        ClientType clientType = ClientType.valueOf("PEASANT");
        System.out.println(clientType);
        System.out.println(clientType.getMsg());
        System.out.println(clientType.name());
    }
}
