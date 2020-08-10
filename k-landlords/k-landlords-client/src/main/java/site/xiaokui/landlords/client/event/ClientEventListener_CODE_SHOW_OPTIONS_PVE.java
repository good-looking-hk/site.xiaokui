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
 * @date 2020-08-06 13:28
 */
public class ClientEventListener_CODE_SHOW_OPTIONS_PVE extends ClientEventListener {

    @Override
    public void call(Channel channel, JSONObject json) {
        SimplePrinter.printNotice("人机对战: ");
        SimplePrinter.printNotice("1. 简单模式");
        SimplePrinter.printNotice("2. 普通模式");
        SimplePrinter.printNotice("3. 困难模式");
        SimplePrinter.printNotice("请输入选择选项对应的数字（输入 back 返回上一层）");
        String line = SimpleWriter.write("pve");

        if ("BACK".equalsIgnoreCase(line)) {
            get(ClientEventCode.CODE_SHOW_OPTIONS).call(channel, json);
        } else {
            int choose = OptionsUtil.getOptions(line);
            if (0 < choose && choose < 4) {
                initLastSellInfo();
                pushToServer(channel, ServerEventCode.CODE_ROOM_CREATE_PVE, String.valueOf(choose));
            } else {
                SimplePrinter.printNotice("非法选项 " + choose + " ，请重新选择：");
                call(channel, json);
            }
        }
    }
}
