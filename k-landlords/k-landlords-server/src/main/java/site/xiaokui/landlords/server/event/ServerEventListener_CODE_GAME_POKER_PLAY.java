package site.xiaokui.landlords.server.event;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import site.xiaokui.landlords.common.entity.ClientSide;
import site.xiaokui.landlords.common.entity.Poker;
import site.xiaokui.landlords.common.entity.PokerSell;
import site.xiaokui.landlords.common.entity.Room;
import site.xiaokui.landlords.common.entity.enums.ClientEventCode;
import site.xiaokui.landlords.common.entity.enums.ClientRole;
import site.xiaokui.landlords.common.entity.enums.SellType;
import site.xiaokui.landlords.common.entity.enums.ServerEventCode;
import site.xiaokui.landlords.common.helper.PokerHelper;
import site.xiaokui.landlords.common.util.ChannelUtil;
import site.xiaokui.landlords.server.RoomContainer;
import site.xiaokui.landlords.server.robot.RobotEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 服务端出牌事件
 * @author HK
 * @date 2020-08-06 14:04
 */
public class ServerEventListener_CODE_GAME_POKER_PLAY extends ServerEventListener {

    @Override
    public void call(ClientSide clientSide, String data) {
        System.out.println("客户端 " + clientSide + " 出牌：" + data);
        Room room = RoomContainer.getRoom(clientSide.getRoomId());
        if (room != null) {
            if (room.getCurrentSellClientId() == clientSide.getId()) {
                Character[] options = new JSONArray(data).toBean(Character[].class);
                int[] indexes = PokerHelper.getIndexes(options, clientSide.getPokers());
                if (PokerHelper.checkPokerIndex(indexes, clientSide.getPokers())) {
                    boolean sellFlag = true;

                    List<Poker> currentPokers = PokerHelper.getPoker(indexes, clientSide.getPokers());
                    PokerSell currentPokerShell = PokerHelper.checkPokerType(currentPokers);
                    if (currentPokerShell.getSellType() != SellType.ILLEGAL) {
                        if (room.getLastSellClientId() != clientSide.getId() && room.getLastPokerShell() != null) {
                            PokerSell lastPokerShell = room.getLastPokerShell();

                            if ((lastPokerShell.getSellType() != currentPokerShell.getSellType() || lastPokerShell.getSellPokers().size() != currentPokerShell.getSellPokers().size()) && currentPokerShell.getSellType() != SellType.BOMB && currentPokerShell.getSellType() != SellType.KING_BOMB) {
                                JSONObject result = new JSONObject()
                                        .set("playType", currentPokerShell.getSellType())
                                        .set("playCount", currentPokerShell.getSellPokers().size())
                                        .set("preType", lastPokerShell.getSellType())
                                        .set("preCount", lastPokerShell.getSellPokers().size());
                                sellFlag = false;
                                // 出牌不合法，如类型不匹配、个数不匹配
                                ChannelUtil.pushToClient(clientSide.getChannel(), ClientEventCode.CODE_GAME_POKER_PLAY_MISMATCH, result);
                            } else if (lastPokerShell.getScore() >= currentPokerShell.getScore()) {
                                JSONObject result = new JSONObject()
                                        .set("playScore", currentPokerShell.getScore())
                                        .set("preScore", lastPokerShell.getScore());
                                sellFlag = false;
                                ChannelUtil.pushToClient(clientSide.getChannel(), ClientEventCode.CODE_GAME_POKER_PLAY_LESS, result);
                            }
                        }
                    } else {
                        sellFlag = false;
                        ChannelUtil.pushToClient(clientSide.getChannel(), ClientEventCode.CODE_GAME_POKER_PLAY_INVALID);
                    }

                    if (sellFlag) {
                        ClientSide next = clientSide.getNext();

                        room.setLastSellClientId(clientSide.getId());
                        room.setLastPokerShell(currentPokerShell);
                        room.setCurrentSellClientId(next.getId());

                        clientSide.getPokers().removeAll(currentPokers);
                        JSONObject result = new JSONObject()
                                .set("clientId", clientSide.getId())
                                .set("clientNickname", clientSide.getNickname())
                                .set("clientType", clientSide.getType())
                                .set("pokers", currentPokers);

                        if (!clientSide.getPokers().isEmpty()) {
                            result.set("sellClinetNickname", next.getNickname());
                        }

                        for (ClientSide client : room.getClientSideList()) {
                            if (client.getRole() == ClientRole.PLAYER) {
                                ChannelUtil.pushToClient(client.getChannel(), ClientEventCode.CODE_SHOW_POKERS, result);
                            }
                        }

                        if (clientSide.getPokers().isEmpty()) {
                            result = new JSONObject()
                                    .set("winnerNickname", clientSide.getNickname())
                                    .set("winnerType", clientSide.getType());

                            for (ClientSide client : room.getClientSideList()) {
                                if (client.getRole() == ClientRole.PLAYER) {
                                    ChannelUtil.pushToClient(client.getChannel(), ClientEventCode.CODE_GAME_OVER, result);
                                }
                            }
                            ServerEventListener.get(ServerEventCode.CODE_CLIENT_EXIT).call(clientSide, data);
                        } else {
                            if (next.getRole() == ClientRole.PLAYER) {
                                ServerEventListener.get(ServerEventCode.CODE_GAME_POKER_PLAY_REDIRECT).call(next, result.toString());
                            } else {
                                RobotEventListener.get(ClientEventCode.CODE_GAME_POKER_PLAY).call(next, data);
                            }
                        }
                    }
                } else {
                    ChannelUtil.pushToClient(clientSide.getChannel(), ClientEventCode.CODE_GAME_POKER_PLAY_INVALID);
                }
            } else {
                ChannelUtil.pushToClient(clientSide.getChannel(), ClientEventCode.CODE_GAME_POKER_PLAY_ORDER_ERROR);
            }
        }
    }

    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        list.add("aa");
        list.add("bb");
        list.add("cc");
        JSONArray jsonArray = new JSONArray(list);
        System.out.println(jsonArray.toString());
        jsonArray = new JSONArray(jsonArray.toString());
        System.out.println(jsonArray.toString());
    }
}
