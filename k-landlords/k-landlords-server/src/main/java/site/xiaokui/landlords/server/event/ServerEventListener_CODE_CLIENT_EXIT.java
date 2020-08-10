package site.xiaokui.landlords.server.event;

import cn.hutool.json.JSONObject;
import site.xiaokui.landlords.common.entity.ClientSide;
import site.xiaokui.landlords.common.entity.Room;
import site.xiaokui.landlords.common.entity.enums.ClientEventCode;
import site.xiaokui.landlords.common.entity.enums.ClientRole;
import site.xiaokui.landlords.common.util.ChannelUtil;
import site.xiaokui.landlords.server.RoomContainer;

/**
 * @author HK
 * @date 2020-08-10 09:59
 */
public class ServerEventListener_CODE_CLIENT_EXIT extends ServerEventListener {

    @Override
    public void call(ClientSide clientSide, String data) {

        Room room = RoomContainer.getRoom(clientSide.getRoomId());

        if (room != null) {
            JSONObject result = new JSONObject()
                    .set("roomId", room.getId())
                    .set("exitClientId", clientSide.getId())
                    .set("exitClientNickname", clientSide.getNickname());
            for (ClientSide client : room.getClientSideList()) {
                if (client.getRole() == ClientRole.PLAYER) {
                    ChannelUtil.pushToClient(client.getChannel(), ClientEventCode.CODE_CLIENT_EXIT, result);
                    client.init();
                }
            }
            RoomContainer.removeRoom(room.getId());
        }
    }
}
