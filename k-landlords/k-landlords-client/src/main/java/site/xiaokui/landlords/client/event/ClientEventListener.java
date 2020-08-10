package site.xiaokui.landlords.client.event;

import cn.hutool.json.JSONObject;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import site.xiaokui.landlords.common.entity.Poker;
import site.xiaokui.landlords.common.entity.enums.ClientEventCode;
import site.xiaokui.landlords.common.entity.enums.ServerEventCode;
import site.xiaokui.landlords.common.util.ChannelUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author HK
 * @date 2020-08-04 14:17
 */
public abstract class ClientEventListener {

    public abstract void call(Channel channel, JSONObject json);

    public final static Map<ClientEventCode, ClientEventListener> LISTENER_MAP = new HashMap<>();

    private final static String LISTENER_PREFIX = ClientEventListener.class.getName() + "_";

    protected static List<Poker> lastPokers = null;
    protected static String lastSellClientNickname = null;
    protected static String lastSellClientType = null;

    protected static void initLastSellInfo() {
        lastPokers = null;
        lastSellClientNickname = null;
        lastSellClientType = null;
    }

    @SuppressWarnings("unchecked")
    public static ClientEventListener get(ClientEventCode code) {
        ClientEventListener listener;
        try {
            if (ClientEventListener.LISTENER_MAP.containsKey(code)) {
                listener = ClientEventListener.LISTENER_MAP.get(code);
            } else {
                String eventListener = LISTENER_PREFIX + code.name();
                Class<ClientEventListener> listenerClass = (Class<ClientEventListener>) Class.forName(eventListener);
                listener = listenerClass.newInstance();
                ClientEventListener.LISTENER_MAP.put(code, listener);
            }
            return listener;
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected ChannelFuture pushToServer(Channel channel, ServerEventCode code, String datas) {
        return ChannelUtil.pushToServer(channel, code, datas);
    }

    protected ChannelFuture pushToServer(Channel channel, ServerEventCode code) {
        return pushToServer(channel, code, null);
    }

    public static void main(String[] args) {
        System.out.println(ClientEventListener.class.getPackage().getName() + "--" + ClientEventListener.class.getName());
        ClientEventCode s = ClientEventCode.valueOf("1111111");
        System.out.println(s);
    }
}