package site.xiaokui.landlords.server.event;

import cn.hutool.json.JSONObject;
import site.xiaokui.landlords.common.entity.ClientSide;
import site.xiaokui.landlords.common.entity.Poker;
import site.xiaokui.landlords.common.entity.Room;
import site.xiaokui.landlords.common.entity.enums.ClientEventCode;
import site.xiaokui.landlords.common.entity.enums.ClientRole;
import site.xiaokui.landlords.common.entity.enums.ClientType;
import site.xiaokui.landlords.common.entity.enums.RoomStatus;
import site.xiaokui.landlords.common.helper.PokerHelper;
import site.xiaokui.landlords.common.util.ChannelUtil;
import site.xiaokui.landlords.server.RoomContainer;
import site.xiaokui.landlords.server.robot.RobotEventListener;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * 房间创建成功之后，服务端游戏开始前的准备工作
 *
 * @author HK
 * @date 2020-08-06 13:33
 */
public class ServerEventListener_CODE_GAME_STARTING extends ServerEventListener {

    @Override
    public void call(ClientSide clientSide, String roomId) {
        Room room = RoomContainer.getRoom(clientSide.getRoomId());
        LinkedList<ClientSide> roomClientList = room.getClientSideList();

        // 生成3副17张玩家牌、3张底牌
        List<List<Poker>> pokersList = PokerHelper.distributePoker();
        int cursor = 0;
        for (ClientSide client : roomClientList) {
            client.setPokers(pokersList.get(cursor++));
        }
        room.setLandlordPokers(pokersList.get(3));

        // 随机决定玩家谁是第一个说话的，范围为0 ~ 2
        int startGrabIndex = new Random().nextInt(3);
        ClientSide startGrabClient = roomClientList.get(startGrabIndex);
        room.setCurrentSellClientId(startGrabClient.getId());

        // 标识 当前房间正在游戏中
        room.setStatus(RoomStatus.STARTING);
        // 记录 谁是第一个说话的
        room.setFirstSellClientId(startGrabClient.getId());

        for (ClientSide client : roomClientList) {
            // 刚开始时，都是农民，谁叫了底牌才是地主
            client.setType(ClientType.PEASANT);

            JSONObject result = new JSONObject()
                    .set("roomId", room.getId())
                    .set("roomOwnerId", room.getRoomOwnerId())
                    .set("roomOwnerName", room.getRoomOwnerName())
                    .set("roomClientCount", room.getClientSideList().size())
                    .set("nextClientNickname", startGrabClient.getNickname())
                    .set("nextClientId", startGrabClient.getId())
                    .set("pokers", client.getPokers());

            if (client.getRole() == ClientRole.PLAYER) {
                // 通知玩家，游戏开始
                ChannelUtil.pushToClient(client.getChannel(), ClientEventCode.CODE_GAME_STARTING, result);
            } else {
                // 如果轮到机器人说话
                if (startGrabClient.getId() == client.getId()) {
                    RobotEventListener.get(ClientEventCode.CODE_GAME_LANDLORD_ELECT).call(client, result.toString());
                }
            }
        }
    }
}
