package site.xiaokui.landlords.server.event;

import site.xiaokui.landlords.common.entity.ClientSide;
import site.xiaokui.landlords.common.entity.Room;
import site.xiaokui.landlords.common.entity.enums.*;
import site.xiaokui.landlords.common.robot.RobotDecisionMakers;
import site.xiaokui.landlords.common.util.ChannelUtil;
import site.xiaokui.landlords.server.RoomContainer;
import site.xiaokui.landlords.server.ServerContainer;

/**
 * 创建人机对战房间
 *
 * @author HK
 * @date 2020-08-06 13:21
 */
public class ServerEventListener_CODE_ROOM_CREATE_PVE extends ServerEventListener {

    /**
     * mode:
     *  1 简单 2 普通 3困难
     */
    @Override
    public void call(ClientSide clientSide, String mode) {
        int difficultyCoefficient = Integer.parseInt(mode);
        // 是否支持该人机模式
        if (RobotDecisionMakers.contains(difficultyCoefficient)) {
            // 初始化房间信息
            Room room = new Room(ServerContainer.geneRoomId());
            room.setType(RoomType.PVE);
            room.setStatus(RoomStatus.BLANK);
            room.setRoomOwnerId(clientSide.getId());
            room.setRoomOwnerName(clientSide.getNickname());
            room.getClientSideMap().put(clientSide.getId(), clientSide);
            room.getClientSideList().add(clientSide);
            room.setCurrentSellClientId(clientSide.getId());
            room.setCreateTime(System.currentTimeMillis());
            room.setDifficultyCoefficient(difficultyCoefficient);

            // 记录房间信息
            clientSide.setRoomId(room.getId());
            RoomContainer.addRoom(room);

            ClientSide preClient = clientSide;
            // 添加机器人
            for (int index = 1; index < 3; index++) {
                ClientSide robot = new ClientSide(-ServerContainer.geneClientId(), ClientStatus.PLAYING, null);
                robot.setNickname("机器人" + index + "号");
                robot.setRole(ClientRole.ROBOT);
                // 记录玩家座位信息
                preClient.setNext(robot);
                robot.setPre(preClient);
                robot.setRoomId(room.getId());
                room.getClientSideMap().put(robot.getId(), robot);
                room.getClientSideList().add(robot);
                // 记录机器人客户端信息
                preClient = robot;
                ServerContainer.CLIENT_SIDE_MAP.put(robot.getId(), robot);
            }
            preClient.setNext(clientSide);
            clientSide.setPre(preClient);
            ServerEventListener.get(ServerEventCode.CODE_GAME_STARTING).call(clientSide, String.valueOf(room.getId()));
        } else {
            ChannelUtil.pushToClient(clientSide.getChannel(), ClientEventCode.CODE_PVE_DIFFICULTY_NOT_SUPPORT);
        }
    }
}
