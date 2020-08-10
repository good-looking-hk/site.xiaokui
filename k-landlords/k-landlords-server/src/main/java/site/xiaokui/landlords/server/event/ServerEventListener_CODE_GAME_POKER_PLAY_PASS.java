package site.xiaokui.landlords.server.event;

import cn.hutool.json.JSONObject;
import site.xiaokui.landlords.common.entity.ClientSide;
import site.xiaokui.landlords.common.entity.Room;
import site.xiaokui.landlords.common.entity.enums.ClientEventCode;
import site.xiaokui.landlords.common.entity.enums.ClientRole;
import site.xiaokui.landlords.common.util.ChannelUtil;
import site.xiaokui.landlords.server.RoomContainer;
import site.xiaokui.landlords.server.robot.RobotEventListener;

/**
 * @author HK
 * @date 2020-08-07 17:33
 */
public class ServerEventListener_CODE_GAME_POKER_PLAY_PASS extends ServerEventListener {

    @Override
    public void call(ClientSide clientSide, String data) {
        Room room = RoomContainer.getRoom(clientSide.getRoomId());

        if (room != null) {
            if (room.getCurrentSellClientId() == clientSide.getId()) {
                if (clientSide.getId() != room.getLastSellClientId()) {
                    ClientSide turnClient = clientSide.getNext();
                    room.setCurrentSellClientId(turnClient.getId());

                    for (ClientSide client : room.getClientSideList()) {
                        JSONObject result = new JSONObject()
                                .set("clientId", clientSide.getId())
                                .set("clientNickname", clientSide.getNickname())
                                .set("nextClientId", turnClient.getId())
                                .set("nextClientNickname", turnClient.getNickname());
                        if (client.getRole() == ClientRole.PLAYER) {
                            ChannelUtil.pushToClient(client.getChannel(), ClientEventCode.CODE_GAME_POKER_PLAY_PASS, result);
                        } else {
                            if (client.getId() == turnClient.getId()) {
                                RobotEventListener.get(ClientEventCode.CODE_GAME_POKER_PLAY).call(turnClient, data);
                            }
                        }
                    }
                } else {
                    ChannelUtil.pushToClient(clientSide.getChannel(), ClientEventCode.CODE_GAME_POKER_PLAY_CANT_PASS);
                }
            } else {
                ChannelUtil.pushToClient(clientSide.getChannel(), ClientEventCode.CODE_GAME_POKER_PLAY_ORDER_ERROR);
            }
        }
    }
}
