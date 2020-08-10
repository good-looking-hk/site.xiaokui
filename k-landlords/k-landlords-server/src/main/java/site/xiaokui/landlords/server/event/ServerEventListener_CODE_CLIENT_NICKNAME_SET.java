package site.xiaokui.landlords.server.event;

import cn.hutool.json.JSONObject;
import site.xiaokui.landlords.common.entity.ClientSide;
import site.xiaokui.landlords.common.entity.enums.ClientEventCode;
import site.xiaokui.landlords.common.util.ChannelUtil;
import site.xiaokui.landlords.server.ServerContainer;

/**
 * 客服端设置昵称事件
 * @author HK
 * @date 2020-08-04 15:30
 */
public class ServerEventListener_CODE_CLIENT_NICKNAME_SET extends ServerEventListener {

    public static final int NICKNAME_MAX_LENGTH = 10;

    @Override
    public void call(ClientSide client, String nickname) {
        if (nickname.length() > NICKNAME_MAX_LENGTH) {
            JSONObject json = new JSONObject();
            json.set("error", nickname);
            json.set("errorInfo", "昵称过长");
            ChannelUtil.pushToClient(client.getChannel(), ClientEventCode.CODE_CLIENT_NICKNAME_SET, json.toString());
        } else {
            ServerContainer.CLIENT_SIDE_MAP.get(client.getId()).setNickname(nickname);
            ChannelUtil.pushToClient(client.getChannel(), ClientEventCode.CODE_SHOW_OPTIONS);
        }
    }
}
