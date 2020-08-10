package site.xiaokui.landlords.client.event;

import cn.hutool.json.JSONObject;
import io.netty.channel.Channel;
import site.xiaokui.landlords.client.ClientContainer;
import site.xiaokui.landlords.client.SimpleClient;
import site.xiaokui.landlords.common.entity.enums.ServerEventCode;
import site.xiaokui.landlords.common.print.SimplePrinter;

import java.util.Map;

/**
 * @author HK
 * @date 2020-08-10 09:23
 */
public class ClientEventListener_CODE_GAME_POKER_PLAY_PASS extends ClientEventListener{

    @Override
    public void call(Channel channel, JSONObject json) {
        SimplePrinter.printNotice(json.get("clientNickname") + " 不出，现在轮到 " + json.get("nextClientNickname") + " 说话");

        int turnClientId = (int) json.get("nextClientId");
        if(ClientContainer.clientId == turnClientId) {
            pushToServer(channel, ServerEventCode.CODE_GAME_POKER_PLAY_REDIRECT);
        }
    }
}
