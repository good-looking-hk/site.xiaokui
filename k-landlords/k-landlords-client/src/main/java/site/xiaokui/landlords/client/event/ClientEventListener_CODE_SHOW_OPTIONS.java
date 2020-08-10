package site.xiaokui.landlords.client.event;

import cn.hutool.json.JSONObject;
import io.netty.channel.Channel;
import site.xiaokui.landlords.client.print.SimpleWriter;
import site.xiaokui.landlords.common.entity.enums.ClientEventCode;
import site.xiaokui.landlords.common.print.SimplePrinter;
import site.xiaokui.landlords.common.util.OptionsUtil;

/**
 * @author HK
 * @date 2020-08-06 10:21
 */
public class ClientEventListener_CODE_SHOW_OPTIONS extends ClientEventListener {

    @Override
    public void call(Channel channel, JSONObject json) {
        SimplePrinter.printNotice("选项： ");
        SimplePrinter.printNotice("1. 玩家对战");
        SimplePrinter.printNotice("2. 人机对战");
        SimplePrinter.printNotice("3. 设置");
        SimplePrinter.printNotice("请输入选择选项对应的数字（输入 exit 退出登录）");
        String line = SimpleWriter.write("options");

        if ("EXIT".equalsIgnoreCase(line)) {
            System.exit(0);
        } else {
            int choose = OptionsUtil.getOptions(line);
            if (choose == 1) {
                get(ClientEventCode.CODE_SHOW_OPTIONS_PVP).call(channel, json);
            } else if (choose == 2) {
                get(ClientEventCode.CODE_SHOW_OPTIONS_PVE).call(channel, json);
            } else if (choose == 3) {
                get(ClientEventCode.CODE_SHOW_OPTIONS_SETTING).call(channel, json);
            } else {
                SimplePrinter.printNotice("非法选项 " + choose + " ，请重新输入:");
                call(channel, json);
            }
        }
    }
}
