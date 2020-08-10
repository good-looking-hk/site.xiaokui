package site.xiaokui.landlords.client.event;

import cn.hutool.json.JSONObject;
import io.netty.channel.Channel;
import site.xiaokui.landlords.common.entity.Poker;
import site.xiaokui.landlords.common.entity.enums.ServerEventCode;
import site.xiaokui.landlords.common.print.SimplePrinter;

import java.util.List;
import java.util.Map;

/**
 * @author HK
 * @date 2020-08-06 14:22
 */
public class ClientEventListener_CODE_GAME_LANDLORD_CONFIRM extends ClientEventListener {

    @Override
    public void call(Channel channel, JSONObject jsonObject) {
        String landlordNickname = String.valueOf(jsonObject.get("landlordNickname"));

        SimplePrinter.printNotice(landlordNickname + " 叫了地主，并额外获得3张牌");

        List<Poker> additionalPokers = jsonObject.getJSONArray("additionalPokers").toList(Poker.class);
        SimplePrinter.printPokers(additionalPokers);

        pushToServer(channel, ServerEventCode.CODE_GAME_POKER_PLAY_REDIRECT);
    }
}