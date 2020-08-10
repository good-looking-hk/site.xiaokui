package site.xiaokui.landlords.client.event;

import cn.hutool.json.JSONObject;
import io.netty.channel.Channel;
import site.xiaokui.landlords.common.entity.Room;
import site.xiaokui.landlords.common.print.SimplePrinter;

/**
 * @author HK
 * @date 2020-08-06 10:35
 */
public class ClientEventListener_CODE_ROOM_CREATE_SUCCESS extends ClientEventListener{

    @Override
    public void call(Channel channel, JSONObject json) {
        initLastSellInfo();
        SimplePrinter.printNotice("成功创建房间，房间ID " + json.get("id"));
        SimplePrinter.printNotice("请耐心等待其他玩家加入！！");
    }
}
