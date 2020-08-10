package site.xiaokui.landlords.server.robot;

import site.xiaokui.landlords.common.entity.ClientSide;
import site.xiaokui.landlords.common.entity.Poker;
import site.xiaokui.landlords.common.entity.Room;
import site.xiaokui.landlords.common.entity.enums.ServerEventCode;
import site.xiaokui.landlords.common.helper.PokerHelper;
import site.xiaokui.landlords.common.robot.RobotDecisionMakers;
import site.xiaokui.landlords.server.RoomContainer;
import site.xiaokui.landlords.server.ServerContainer;
import site.xiaokui.landlords.server.event.ServerEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 机器人第一次说话事件，是否叫地主
 * @author HK
 * @date 2020-08-06 13:46
 */
public class RobotEventListener_CODE_GAME_LANDLORD_ELECT implements RobotEventListener{

    @Override
    public void call(ClientSide robot, String data) {
        ServerContainer.THREAD_EXCUTER.execute(() -> {
            Room room = RoomContainer.getRoom(robot.getRoomId());

            List<Poker> landlordPokers = new ArrayList<>(20);
            landlordPokers.addAll(robot.getPokers());
            landlordPokers.addAll(room.getLandlordPokers());

            List<Poker> leftPokers = new ArrayList<>(17);
            leftPokers.addAll(robot.getPre().getPokers());

            List<Poker> rightPokers = new ArrayList<>(17);
            rightPokers.addAll(robot.getNext().getPokers());

            PokerHelper.sortPoker(landlordPokers);
            PokerHelper.sortPoker(leftPokers);
            PokerHelper.sortPoker(rightPokers);
            boolean robLandlord = RobotDecisionMakers.howToChooseLandlord(room.getDifficultyCoefficient(), leftPokers, rightPokers, landlordPokers);
            ServerEventListener.get(ServerEventCode.CODE_GAME_LANDLORD_ELECT).call(robot, String.valueOf(robLandlord));
        });
    }}
