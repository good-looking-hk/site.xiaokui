package site.xiaokui.landlords.server.event;

import cn.hutool.json.JSONObject;
import site.xiaokui.landlords.common.entity.ClientSide;
import site.xiaokui.landlords.common.entity.Room;
import site.xiaokui.landlords.common.entity.enums.ClientEventCode;
import site.xiaokui.landlords.common.util.ChannelUtil;
import site.xiaokui.landlords.server.RoomContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author HK
 * @date 2020-08-10 14:24
 */
public class ServerEventListener_CODE_GET_ROOMS extends ServerEventListener{

    @Override
    public void call(ClientSide clientSide, String data) {
        List<Map<String, Object>> roomList = new ArrayList<>(RoomContainer.getRoomMap().size());
        for(Map.Entry<Integer, Room> entry: RoomContainer.getRoomMap().entrySet()) {
            Room room = entry.getValue();
            roomList.add(new JSONObject()
                    .set("roomId", room.getId())
                    .set("roomOwnerId", room.getRoomOwnerId())
                    .set("roomOwnerName", room.getRoomOwnerName())
                    .set("roomClientCount", room.getClientSideList().size())
                    .set("roomType", room.getType()));
        }
        ChannelUtil.pushToClient(clientSide.getChannel(), ClientEventCode.CODE_SHOW_ROOMS, new JSONObject().set("data", roomList));
    }
}
