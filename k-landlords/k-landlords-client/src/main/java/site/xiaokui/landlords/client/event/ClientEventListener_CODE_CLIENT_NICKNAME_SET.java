package site.xiaokui.landlords.client.event;

import cn.hutool.json.JSONObject;
import io.netty.channel.Channel;
import site.xiaokui.landlords.client.ClientContainer;
import site.xiaokui.landlords.client.print.SimpleWriter;
import site.xiaokui.landlords.common.entity.enums.ClientEventCode;
import site.xiaokui.landlords.common.entity.enums.ServerEventCode;
import site.xiaokui.landlords.common.print.SimplePrinter;

/**
 * 客服端设置昵称事件
 *
 * @author HK
 * @date 2020-08-04 15:17
 */
public class ClientEventListener_CODE_CLIENT_NICKNAME_SET extends ClientEventListener {

    public static final int NICKNAME_MAX_LENGTH = 10;

    @Override
    public void call(Channel channel, JSONObject json) {
        if (json != null) {
            if (json.containsKey("error")) {
                SimplePrinter.printNotice("非法的昵称：" + json.get("error") + "，" + json.get("errorInfo"));
            }
        }
        SimplePrinter.printNotice("请输入您的昵称（最多 " + NICKNAME_MAX_LENGTH + "个字）");
        String nickname = SimpleWriter.write("nickname").trim();

        if (nickname.length() > NICKNAME_MAX_LENGTH) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.set("error", nickname);
            jsonObject.set("errorInfo", "昵称过长");
            call(channel, jsonObject);
        } else {
            pushToServer(channel, ServerEventCode.CODE_CLIENT_NICKNAME_SET, nickname);
            ClientContainer.nickname = nickname;
        }
    }
}
