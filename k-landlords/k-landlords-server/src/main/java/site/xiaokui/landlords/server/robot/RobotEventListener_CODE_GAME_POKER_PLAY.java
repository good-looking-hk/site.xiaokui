package site.xiaokui.landlords.server.robot;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import site.xiaokui.landlords.common.entity.ClientSide;
import site.xiaokui.landlords.common.entity.PokerSell;
import site.xiaokui.landlords.common.entity.Room;
import site.xiaokui.landlords.common.entity.enums.SellType;
import site.xiaokui.landlords.common.entity.enums.ServerEventCode;
import site.xiaokui.landlords.common.helper.PokerHelper;
import site.xiaokui.landlords.common.print.SimplePrinter;
import site.xiaokui.landlords.common.robot.RobotDecisionMakers;
import site.xiaokui.landlords.server.RoomContainer;
import site.xiaokui.landlords.server.ServerContainer;
import site.xiaokui.landlords.server.event.ServerEventListener;

import java.util.Arrays;

/**
 * 机器人出牌事件
 * @author HK
 * @date 2020-08-06 14:00
 */
public class RobotEventListener_CODE_GAME_POKER_PLAY implements RobotEventListener {

    @Override
    public void call(ClientSide robot, String roomInfo) {
        ServerContainer.THREAD_EXCUTER.execute(() -> {
            Room room = RoomContainer.getRoom(robot.getRoomId());
            // 上一手牌
            PokerSell lastPokerSell = null;
            // 要出的牌
            PokerSell pokerSell = null;
            // 如果上一手牌不是自己出的，如果本次要出的话，即本次出牌要大于上一手牌
            if (room.getLastSellClientId() != robot.getId()) {
                lastPokerSell = room.getLastPokerShell();
                pokerSell = RobotDecisionMakers.howToPlayPokers(room.getDifficultyCoefficient(), lastPokerSell, robot.getPokers());
            } else {
                // 如果上一手牌，没有人断，则继续出
                pokerSell = RobotDecisionMakers.howToPlayPokers(room.getDifficultyCoefficient(), null, robot.getPokers());
            }

            SimplePrinter.serverLog(robot.getNickname() + "出牌:" + pokerSell + "，上一手牌:" + lastPokerSell);

            if (pokerSell == null || pokerSell.getSellType() == SellType.ILLEGAL) {
                ServerEventListener.get(ServerEventCode.CODE_GAME_POKER_PLAY_PASS).call(robot, roomInfo);
            } else {
                Character[] cs = new Character[pokerSell.getSellPokers().size()];
                for (int index = 0; index < cs.length; index++) {
                    cs[index] = pokerSell.getSellPokers().get(index).getLevel().getAlias()[0];
                }
                ServerEventListener.get(ServerEventCode.CODE_GAME_POKER_PLAY).call(robot, new JSONArray(cs).toString());
            }
        });
    }
}
