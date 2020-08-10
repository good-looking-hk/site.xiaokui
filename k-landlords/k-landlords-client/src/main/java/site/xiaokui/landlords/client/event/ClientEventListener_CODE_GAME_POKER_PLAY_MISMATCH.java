package site.xiaokui.landlords.client.event;

import cn.hutool.json.JSONObject;
import io.netty.channel.Channel;
import site.xiaokui.landlords.common.entity.enums.ServerEventCode;
import site.xiaokui.landlords.common.print.SimplePrinter;

import java.util.Map;

/**
 * @author HK
 * @date 2020-08-07 17:50
 */
public class ClientEventListener_CODE_GAME_POKER_PLAY_MISMATCH extends ClientEventListener{

    @Override
    public void call(Channel channel, JSONObject json) {
        SimplePrinter.printNotice("出牌不合法，当前出牌为：" + json + "，而上手牌为：" + json);
        if(lastPokers != null) {
            SimplePrinter.printNotice(lastSellClientNickname + "[" + lastSellClientType + "] 出牌:");
            SimplePrinter.printPokers(lastPokers);
        }

        pushToServer(channel, ServerEventCode.CODE_GAME_POKER_PLAY_REDIRECT);
    }
}
