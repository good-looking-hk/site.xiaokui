package site.xiaokui.landlords.client.event;

import cn.hutool.json.JSONObject;
import io.netty.channel.Channel;
import site.xiaokui.landlords.common.entity.enums.ServerEventCode;
import site.xiaokui.landlords.common.print.SimplePrinter;

/**
 * @author HK
 * @date 2020-08-07 15:06
 */
public class ClientEventListener_CODE_GAME_POKER_PLAY_INVALID extends ClientEventListener {

    @Override
    public void call(Channel channel, JSONObject json) {
        SimplePrinter.printNotice("非法出牌，请重新出牌！！");
        if (lastPokers != null) {
            SimplePrinter.printNotice(lastSellClientNickname + "[" + lastSellClientType + "] 出:");
            SimplePrinter.printPokers(lastPokers);
        }
        pushToServer(channel, ServerEventCode.CODE_GAME_POKER_PLAY_REDIRECT);
    }
}
