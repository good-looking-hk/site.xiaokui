package site.xiaokui.landlords.server.event;

import cn.hutool.json.JSONObject;
import site.xiaokui.landlords.common.entity.ClientSide;
import site.xiaokui.landlords.common.entity.Room;
import site.xiaokui.landlords.common.entity.enums.ClientEventCode;
import site.xiaokui.landlords.common.entity.enums.ClientRole;
import site.xiaokui.landlords.common.entity.enums.ClientType;
import site.xiaokui.landlords.common.entity.enums.ServerEventCode;
import site.xiaokui.landlords.common.helper.PokerHelper;
import site.xiaokui.landlords.common.util.ChannelUtil;
import site.xiaokui.landlords.server.RoomContainer;
import site.xiaokui.landlords.server.robot.RobotEventListener;

/**
 * 服务端叫地主事件
 * @author HK
 * @date 2020-08-06 13:49
 */
public class ServerEventListener_CODE_GAME_LANDLORD_ELECT extends ServerEventListener {

    @Override
    public void call(ClientSide clientSide, String robLandlord) {
        Room room = RoomContainer.getRoom(clientSide.getRoomId());
        if (room != null) {
            // 客户端是否叫地主
            boolean isY = Boolean.parseBoolean(robLandlord);
            if (isY) {
                clientSide.getPokers().addAll(room.getLandlordPokers());
                PokerHelper.sortPoker(clientSide.getPokers());

                int currentClientId = clientSide.getId();
                room.setLandlordId(currentClientId);
                room.setLastSellClientId(currentClientId);
                room.setCurrentSellClientId(currentClientId);
                clientSide.setType(ClientType.LANDLORD);

                for (ClientSide client : room.getClientSideList()) {
                    JSONObject result = new JSONObject()
                            .set("roomId", room.getId())
                            .set("roomOwnerId", room.getRoomOwnerId())
                            .set("roomOwnerName", room.getRoomOwnerName())
                            .set("roomClientCount", room.getClientSideList().size())
                            .set("landlordNickname", clientSide.getNickname())
                            .set("landlordId", clientSide.getId())
                            .set("additionalPokers", room.getLandlordPokers());
                    // 通知玩家，该客户端叫了地主
                    if (client.getRole() == ClientRole.PLAYER) {
                        ChannelUtil.pushToClient(client.getChannel(), ClientEventCode.CODE_GAME_LANDLORD_CONFIRM, result);
                    } else {
                        // 如果是机器人叫了地主，则让机器人出牌
                        if (currentClientId == client.getId()) {
                            RobotEventListener.get(ClientEventCode.CODE_GAME_POKER_PLAY).call(client, result.toString());
                        }
                    }
                }
            } else {
                // 如果不叫地主，且是下一个是第一个说话的，则说明本轮没人叫地主，轮空
                if (clientSide.getNext().getId() == room.getFirstSellClientId()) {
                    for (ClientSide client : room.getClientSideList()) {
                        if (client.getRole() == ClientRole.PLAYER) {
                            ChannelUtil.pushToClient(client.getChannel(), ClientEventCode.CODE_GAME_LANDLORD_CYCLE);
                        }
                    }
                    ServerEventListener.get(ServerEventCode.CODE_GAME_STARTING).call(clientSide, null);
                } else {
                    // 如果没有轮空，则询问下一个玩家
                    ClientSide turnClientSide = clientSide.getNext();
                    room.setCurrentSellClientId(turnClientSide.getId());
                    JSONObject result = new JSONObject()
                            .set("roomId", room.getId())
                            .set("roomOwnerId", room.getRoomOwnerId())
                            .set("roomOwnerName", room.getRoomOwnerId())
                            .set("roomClientCount", room.getClientSideList().size())
                            .set("preClientNickname", clientSide.getNickname())
                            .set("nextClientNickname", turnClientSide.getNickname())
                            .set("nextClientId", turnClientSide.getId());

                    for (ClientSide client : room.getClientSideList()) {
                        // 通知玩家
                        if (client.getRole() == ClientRole.PLAYER) {
                            ChannelUtil.pushToClient(client.getChannel(), ClientEventCode.CODE_GAME_LANDLORD_ELECT, result);
                        } else {
                            // 通知轮到的机器人出牌
                            if (client.getId() == turnClientSide.getId()) {
                                RobotEventListener.get(ClientEventCode.CODE_GAME_LANDLORD_ELECT).call(client, result.toString());
                            }
                        }
                    }
                }
            }
        }
    }
}
