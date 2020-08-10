package site.xiaokui.landlords.server.event;

import cn.hutool.json.JSONObject;
import site.xiaokui.landlords.common.entity.ClientSide;
import site.xiaokui.landlords.common.entity.Room;
import site.xiaokui.landlords.common.entity.enums.ClientEventCode;
import site.xiaokui.landlords.common.util.ChannelUtil;
import site.xiaokui.landlords.server.RoomContainer;
import site.xiaokui.landlords.server.ServerContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author HK
 * @date 2020-08-06 14:29
 */
public class ServerEventListener_CODE_GAME_POKER_PLAY_REDIRECT extends ServerEventListener {

    @Override
    public void call(ClientSide clientSide, String data) {
        Room room = RoomContainer.getRoom(clientSide.getRoomId());

        List<Map<String, Object>> clientInfos = new ArrayList<Map<String, Object>>(3);
        for (ClientSide client : room.getClientSideList()) {
            if (clientSide.getId() != client.getId()) {
                clientInfos.add(new JSONObject()
                        .set("clientId", client.getId())
                        .set("clientNickname", client.getNickname())
                        .set("type", client.getType())
                        .set("surplus", client.getPokers().size())
                        .set("position", clientSide.getPre().getId() == client.getId() ? "上家" : "下家"));
            }
        }

        JSONObject result = new JSONObject()
                .set("pokers", clientSide.getPokers())
                .set("clientInfos", clientInfos)
                .set("sellClientId", room.getCurrentSellClientId())
                .set("sellClientNickname", ServerContainer.CLIENT_SIDE_MAP.get(room.getCurrentSellClientId()).getNickname());

        ChannelUtil.pushToClient(clientSide.getChannel(), ClientEventCode.CODE_GAME_POKER_PLAY_REDIRECT, result);
    }
}
