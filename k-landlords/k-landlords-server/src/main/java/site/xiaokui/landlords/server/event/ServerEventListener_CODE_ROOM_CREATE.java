package site.xiaokui.landlords.server.event;

import cn.hutool.json.JSONObject;
import site.xiaokui.landlords.common.entity.ClientSide;
import site.xiaokui.landlords.common.entity.Room;
import site.xiaokui.landlords.common.entity.enums.ClientEventCode;
import site.xiaokui.landlords.common.entity.enums.RoomStatus;
import site.xiaokui.landlords.common.entity.enums.RoomType;
import site.xiaokui.landlords.common.util.ChannelUtil;
import site.xiaokui.landlords.server.RoomContainer;
import site.xiaokui.landlords.server.ServerContainer;

/**
 * 服务端创建房间事件
 * @author HK
 * @date 2020-08-06 11:20
 */
public class ServerEventListener_CODE_ROOM_CREATE extends ServerEventListener {

    @Override
    public void call(ClientSide clientSide, String data) {

        Room room = new Room(ServerContainer.geneRoomId());
        room.setStatus(RoomStatus.BLANK);
        room.setType(RoomType.PVP);
        room.setRoomOwnerName(clientSide.getNickname());
        room.getClientSideMap().put(clientSide.getId(), clientSide);
        room.getClientSideList().add(clientSide);
        room.setCurrentSellClientId(clientSide.getId());
        room.setCreateTime(System.currentTimeMillis());
        room.setLastFlushTime(System.currentTimeMillis());

        clientSide.setRoomId(room.getId());
        RoomContainer.addRoom(room);
        ChannelUtil.pushToClient(clientSide.getChannel(), ClientEventCode.CODE_ROOM_CREATE_SUCCESS, new JSONObject(room));
    }
}
