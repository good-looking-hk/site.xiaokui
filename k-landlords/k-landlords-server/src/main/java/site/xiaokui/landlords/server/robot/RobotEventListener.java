package site.xiaokui.landlords.server.robot;

import site.xiaokui.landlords.common.entity.ClientSide;
import site.xiaokui.landlords.common.entity.enums.ClientEventCode;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author HK
 * @date 2020-08-06 13:34
 */
public interface RobotEventListener {

    final static String LISTENER_PREFIX = RobotEventListener.class.getName() + "_";

    public final static Map<ClientEventCode, RobotEventListener> LISTENER_MAP = new HashMap<>();

    public void call(ClientSide robot, String data);

    @SuppressWarnings("unchecked")
    public static RobotEventListener get(ClientEventCode code) {
        RobotEventListener listener = null;
        try {
            if (RobotEventListener.LISTENER_MAP.containsKey(code)) {
                listener = RobotEventListener.LISTENER_MAP.get(code);
            } else {
                String eventListener = LISTENER_PREFIX + code.name();
                Class<RobotEventListener> listenerClass = (Class<RobotEventListener>) Class.forName(eventListener);
                try {
                    listener = listenerClass.getDeclaredConstructor().newInstance();
                } catch (NoSuchMethodException | InvocationTargetException e) {
                    e.printStackTrace();
                }
                RobotEventListener.LISTENER_MAP.put(code, listener);
            }
            return listener;
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
