package site.xiaokui.landlords.client.event;

import cn.hutool.json.JSONObject;
import io.netty.channel.Channel;
import site.xiaokui.landlords.common.entity.enums.ClientType;
import site.xiaokui.landlords.common.print.SimplePrinter;

/**
 * @author HK
 * @date 2020-08-10 09:30
 */
public class ClientEventListener_CODE_GAME_OVER extends ClientEventListener{

    @Override
    public void call(Channel channel, JSONObject json) {
        String type = ClientType.valueOf(json.getStr("winnerType")).getMsg();
        SimplePrinter.printNotice("\n玩家 " + json.get("winnerNickname") + "[" + type+ "]" + " 赢得游戏！");
        SimplePrinter.printNotice("游戏结束，友谊第一，比赛第二！\n");
    }
}
