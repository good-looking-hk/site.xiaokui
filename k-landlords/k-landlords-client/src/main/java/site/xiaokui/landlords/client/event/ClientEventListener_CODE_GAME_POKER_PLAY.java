package site.xiaokui.landlords.client.event;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import io.netty.channel.Channel;
import site.xiaokui.landlords.client.print.SimpleWriter;
import site.xiaokui.landlords.common.entity.Poker;
import site.xiaokui.landlords.common.entity.enums.PokerLevel;
import site.xiaokui.landlords.common.entity.enums.ServerEventCode;
import site.xiaokui.landlords.common.print.SimplePrinter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author HK
 * @date 2020-08-06 14:25
 */
public class ClientEventListener_CODE_GAME_POKER_PLAY extends ClientEventListener {

    @Override
    public void call(Channel channel, JSONObject json) {

        SimplePrinter.printNotice("\n现在轮到你出牌，这是你的牌：");
        List<Poker> pokers = json.getJSONArray("pokers").toList(Poker.class);

        SimplePrinter.printPokers(pokers);

        SimplePrinter.printNotice("请输入你想出的牌（输入 exit 退出当前房间，pass 跳过当前回合)");
        String line = SimpleWriter.write("card");

        if (line == null) {
            SimplePrinter.printNotice("Invalid enter");
            call(channel, json);
        } else {
            if ("PASS".equalsIgnoreCase(line)) {
                pushToServer(channel, ServerEventCode.CODE_GAME_POKER_PLAY_PASS);
            } else if ("EXIT".equalsIgnoreCase(line)) {
                pushToServer(channel, ServerEventCode.CODE_CLIENT_EXIT);
            } else {
                String[] strs = line.split(" ");
                List<Character> options = new ArrayList<>();
                boolean access = true;
                for (int index = 0; index < strs.length; index++) {
                    String str = strs[index];
                    for (char c : str.toCharArray()) {
                        if (c == ' ' || c == '\t') {
                        } else {
                            if (!PokerLevel.aliasContains(c)) {
                                access = false;
                                break;
                            } else {
                                options.add(c);
                            }
                        }
                    }
                }
                if (access) {
                    String data = new JSONArray(options).toString();
                    pushToServer(channel, ServerEventCode.CODE_GAME_POKER_PLAY, data);
                } else {
                    SimplePrinter.printNotice("非法输入，请重新输入");
                    if (lastPokers != null) {
                        SimplePrinter.printNotice(lastSellClientNickname + "[" + lastSellClientType + "] played:");
                        SimplePrinter.printPokers(lastPokers);
                    }
                    call(channel, json);
                }
            }
        }
    }
}
