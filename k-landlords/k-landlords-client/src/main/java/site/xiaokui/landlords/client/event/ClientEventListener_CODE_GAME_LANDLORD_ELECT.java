package site.xiaokui.landlords.client.event;

import cn.hutool.json.JSONObject;
import io.netty.channel.Channel;
import site.xiaokui.landlords.client.ClientContainer;
import site.xiaokui.landlords.client.print.SimpleWriter;
import site.xiaokui.landlords.common.entity.enums.ServerEventCode;
import site.xiaokui.landlords.common.print.SimplePrinter;

/**
 * 叫地主事件
 *
 * @author HK
 * @date 2020-08-06 14:17
 */
public class ClientEventListener_CODE_GAME_LANDLORD_ELECT extends ClientEventListener {

    @Override
    public void call(Channel channel, JSONObject json) {
        int turnClientId = json.getInt("nextClientId");

        if (json.containsKey("preClientNickname")) {
            SimplePrinter.printNotice(json.get("preClientNickname") + " 不叫地主！");
        }

        if (turnClientId == ClientContainer.clientId) {
            SimplePrinter.printNotice("现在轮到你，是否叫地主？[y/n] (输入 exit 以退出房间)");
            String line = SimpleWriter.write("y/n");
            if ("EXIT".equalsIgnoreCase(line)) {
                pushToServer(channel, ServerEventCode.CODE_CLIENT_EXIT);
            } else if ("Y".equalsIgnoreCase(line)) {
                pushToServer(channel, ServerEventCode.CODE_GAME_LANDLORD_ELECT, "TRUE");
            } else if ("N".equalsIgnoreCase(line)) {
                pushToServer(channel, ServerEventCode.CODE_GAME_LANDLORD_ELECT, "FALSE");
            } else {
                SimplePrinter.printNotice("Invalid options");
                call(channel, json);
            }
        } else {
            SimplePrinter.printNotice("现在轮到 " + json.get("nextClientNickname") +" 叫地主，请耐心等待他的选择！");
        }
    }
}
