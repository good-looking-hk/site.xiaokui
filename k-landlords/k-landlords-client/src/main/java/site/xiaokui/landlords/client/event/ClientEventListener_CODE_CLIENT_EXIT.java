package site.xiaokui.landlords.client.event;

import cn.hutool.json.JSONObject;
import io.netty.channel.Channel;
import site.xiaokui.landlords.client.ClientContainer;
import site.xiaokui.landlords.common.entity.enums.ClientEventCode;
import site.xiaokui.landlords.common.print.SimplePrinter;

/**
 * @author HK
 * @date 2020-08-06 14:19
 */
public class ClientEventListener_CODE_CLIENT_EXIT extends ClientEventListener {

    @Override
    public void call(Channel channel, JSONObject jsonObject) {
        Integer exitClientId = jsonObject.getInt("exitClientId");

        String role = null;
        if (exitClientId == ClientContainer.clientId) {
            role = "你";
        } else {
            role = String.valueOf(jsonObject.get("exitClientNickname"));
        }
        SimplePrinter.printNotice(role + "已离开房间！\n");

        get(ClientEventCode.CODE_SHOW_OPTIONS).call(channel, jsonObject);
    }
}
