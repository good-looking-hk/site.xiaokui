package site.xiaokui.landlords.client.event;

import cn.hutool.json.JSONObject;
import io.netty.channel.Channel;
import site.xiaokui.landlords.common.entity.enums.ClientEventCode;
import site.xiaokui.landlords.common.print.SimplePrinter;

/**
 * @author HK
 * @date 2020-08-07 14:29
 */
public class ClientEventListener_CODE_ROOM_PLAY_FAIL_BY_INEXIST extends ClientEventListener{

    @Override
    public void call(Channel channel, JSONObject json) {
        SimplePrinter.printNotice("出牌失败，房间已经解散！");
        ClientEventListener.get(ClientEventCode.CODE_SHOW_OPTIONS).call(channel, json);
    }
}
