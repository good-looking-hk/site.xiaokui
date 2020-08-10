package site.xiaokui.landlords.client.event;

import cn.hutool.json.JSONObject;
import io.netty.channel.Channel;
import site.xiaokui.landlords.client.print.SimpleWriter;
import site.xiaokui.landlords.common.entity.enums.ClientEventCode;
import site.xiaokui.landlords.common.entity.enums.ServerEventCode;
import site.xiaokui.landlords.common.print.SimplePrinter;
import site.xiaokui.landlords.common.util.OptionsUtil;

/**
 * @author HK
 * @date 2020-08-06 10:34
 */
public class ClientEventListener_CODE_SHOW_OPTIONS_PVP extends ClientEventListener {

    @Override
    public void call(Channel channel, JSONObject json) {
        SimplePrinter.printNotice("玩家对战: ");
        SimplePrinter.printNotice("1. 创建房间");
        SimplePrinter.printNotice("2. 房间列表");
        SimplePrinter.printNotice("3. 加入房间");
        SimplePrinter.printNotice("请输入选择选项对应的数字（输入 back 返回上一层）");
        String line = SimpleWriter.write("pvp");

        if ("BACK".equalsIgnoreCase(line)) {
            get(ClientEventCode.CODE_SHOW_OPTIONS).call(channel, json);
        } else {
            int choose = OptionsUtil.getOptions(line);
            if (choose == 1) {
                pushToServer(channel, ServerEventCode.CODE_ROOM_CREATE, null);
            } else if (choose == 2) {
                pushToServer(channel, ServerEventCode.CODE_GET_ROOMS, null);
            } else if (choose == 3) {
                SimplePrinter.printNotice("请输入你想进入的房间ID（输入 back 返回上一层）");
                line = SimpleWriter.write("roomid");

                if ("BACK".equalsIgnoreCase(line)) {
                    call(channel, json);
                } else {
                    int option = OptionsUtil.getOptions(line);
                    if (option < 1) {
                        SimplePrinter.printNotice("非法选项 " + option + " ，请重新选择：");
                        call(channel, json);
                    } else {
                        pushToServer(channel, ServerEventCode.CODE_ROOM_JOIN, String.valueOf(option));
                    }
                }
            } else {
                SimplePrinter.printNotice("非法选项 " + choose + " ，请重新选择：");
                call(channel, json);
            }
        }
    }
}
