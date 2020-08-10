package site.xiaokui.landlords.server.event;

import cn.hutool.json.JSONObject;
import site.xiaokui.landlords.common.entity.ClientSide;
import site.xiaokui.landlords.common.entity.Room;
import site.xiaokui.landlords.common.entity.enums.ClientEventCode;
import site.xiaokui.landlords.common.entity.enums.RoomStatus;
import site.xiaokui.landlords.common.entity.enums.ServerEventCode;
import site.xiaokui.landlords.common.util.ChannelUtil;
import site.xiaokui.landlords.server.RoomContainer;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * @author HK
 * @date 2020-08-10 14:42
 */
public class ServerEventListener_CODE_ROOM_JOIN extends ServerEventListener {

    @Override
    public void call(ClientSide clientSide, String data) {
        Room room = RoomContainer.getRoom(Integer.valueOf(data));

        if (room == null) {
            JSONObject result = new JSONObject()
                    .set("roomId", data);
            ChannelUtil.pushToClient(clientSide.getChannel(), ClientEventCode.CODE_ROOM_JOIN_FAIL_BY_INEXIST, result);
        } else {
            if (room.getClientSideList().size() == 3) {
                JSONObject result = new JSONObject()
                        .set("roomId", room.getId())
                        .set("roomOwnerId", room.getRoomOwnerId())
                        .set("roomOwnerName", room.getRoomOwnerName());
                ChannelUtil.pushToClient(clientSide.getChannel(), ClientEventCode.CODE_ROOM_JOIN_FAIL_BY_FULL, result);
            } else {
                clientSide.setRoomId(room.getId());
                ConcurrentSkipListMap<Integer, ClientSide> roomClientMap = (ConcurrentSkipListMap<Integer, ClientSide>) room.getClientSideMap();
                LinkedList<ClientSide> roomClientList = room.getClientSideList();

                if (roomClientList.size() > 0) {
                    ClientSide pre = roomClientList.getLast();
                    pre.setNext(clientSide);
                    clientSide.setPre(pre);
                }

                roomClientList.add(clientSide);
                roomClientMap.put(clientSide.getId(), clientSide);

                if (roomClientMap.size() == 3) {
                    clientSide.setNext(roomClientList.getFirst());
                    roomClientList.getFirst().setPre(clientSide);

                    ServerEventListener.get(ServerEventCode.CODE_GAME_STARTING).call(clientSide, String.valueOf(room.getId()));
                } else {
                    room.setStatus(RoomStatus.WAIT);

                    JSONObject result = new JSONObject()
                            .set("clientId", clientSide.getId())
                            .set("clientNickname", clientSide.getNickname())
                            .set("roomId", room.getId())
                            .set("roomOwnerId", room.getRoomOwnerId())
                            .set("roomOwnerName", room.getRoomOwnerName())
                            .set("roomClientCount", room.getClientSideList().size());
                    for (ClientSide client : roomClientMap.values()) {
                        ChannelUtil.pushToClient(client.getChannel(), ClientEventCode.CODE_ROOM_JOIN_SUCCESS, result);
                    }
                }
            }
        }
    }
}
