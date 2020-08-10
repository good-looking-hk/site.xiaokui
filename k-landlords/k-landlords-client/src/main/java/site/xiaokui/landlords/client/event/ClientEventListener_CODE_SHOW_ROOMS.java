package site.xiaokui.landlords.client.event;

import cn.hutool.json.JSONObject;
import io.netty.channel.Channel;
import site.xiaokui.landlords.common.entity.Poker;
import site.xiaokui.landlords.common.entity.Room;
import site.xiaokui.landlords.common.entity.enums.ClientEventCode;
import site.xiaokui.landlords.common.entity.enums.ClientType;
import site.xiaokui.landlords.common.print.FormatPrinter;
import site.xiaokui.landlords.common.print.SimplePrinter;

import java.util.List;
import java.util.Map;

/**
 * @author HK
 * @date 2020-08-10 14:31
 */
public class ClientEventListener_CODE_SHOW_ROOMS extends ClientEventListener {

    @Override
    public void call(Channel channel, JSONObject json) {
        if (json != null && json.getJSONArray("data").size() != 0) {
            String format = "#\t%s\t|\t%-" + 8 + "s\t|\t%-6s\t|\t%-6s\t|\t%-6s\t#\n";
            FormatPrinter.printNotice(format, "房间ID", "创建者ID", "创建者名称", "人数", "类型");

            for (int i = 0; i < json.getJSONArray("data").size(); i++) {
                JSONObject room = json.getJSONArray("data").getJSONObject(i);
                FormatPrinter.printNotice(format, room.get("roomId"), room.get("roomOwnerId"), room.get("roomOwnerName"), room.get("roomClientCount"), room.get("roomType"));
            }
            SimplePrinter.printNotice("");
            get(ClientEventCode.CODE_SHOW_OPTIONS_PVP).call(channel, json);
        } else {
            SimplePrinter.printNotice("没有可用的房间，请先创建房间！\n");
            get(ClientEventCode.CODE_SHOW_OPTIONS_PVP).call(channel, json);
        }
    }
}
