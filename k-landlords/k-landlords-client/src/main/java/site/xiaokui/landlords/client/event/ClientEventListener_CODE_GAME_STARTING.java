package site.xiaokui.landlords.client.event;

import cn.hutool.json.JSONObject;
import io.netty.channel.Channel;
import site.xiaokui.landlords.common.entity.Poker;
import site.xiaokui.landlords.common.entity.enums.ClientEventCode;
import site.xiaokui.landlords.common.print.SimplePrinter;

import java.util.List;

/**
 * @author HK
 * @date 2020-08-06 13:38
 */
public class ClientEventListener_CODE_GAME_STARTING extends ClientEventListener {

    @Override
    public void call(Channel channel, JSONObject json) {
        SimplePrinter.printNotice("游戏开始！！");
        List<Poker> pokers = json.getJSONArray("pokers").toList(Poker.class);

        SimplePrinter.printNotice("");
        SimplePrinter.printNotice("你的牌是");
        SimplePrinter.printPokers(pokers);

        get(ClientEventCode.CODE_GAME_LANDLORD_ELECT).call(channel, json);
    }
}