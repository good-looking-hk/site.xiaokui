package site.xiaokui.landlords.server.event;

import site.xiaokui.landlords.common.entity.ClientSide;
import site.xiaokui.landlords.common.entity.enums.ServerEventCode;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author HK
 * @date 2020-08-04 13:58
 */
public abstract class ServerEventListener {

    public abstract void call(ClientSide client, String data);

    public final static Map<ServerEventCode, ServerEventListener> LISTENER_MAP = new HashMap<>();

    final static String LISTENER_PREFIX = ServerEventListener.class.getName() + "_";

    @SuppressWarnings("unchecked")
    public static ServerEventListener get(ServerEventCode code) {
        ServerEventListener listener = null;
        try {
            if (ServerEventListener.LISTENER_MAP.containsKey(code)) {
                listener = ServerEventListener.LISTENER_MAP.get(code);
            } else {
                String eventListener = LISTENER_PREFIX + code.name();
                Class<ServerEventListener> listenerClass = (Class<ServerEventListener>) Class.forName(eventListener);
                try {
                    listener = listenerClass.getDeclaredConstructor().newInstance();
                } catch (InvocationTargetException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
                ServerEventListener.LISTENER_MAP.put(code, listener);
            }
            return listener;
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
