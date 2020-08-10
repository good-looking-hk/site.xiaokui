package site.xiaokui.landlords.server;

import site.xiaokui.landlords.common.entity.ClientSide;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author HK
 * @date 2020-08-03 16:02
 */
public class ServerContainer {

    /**
     * 客户端ID自增生成器，主要用户标识客户端
     */
    private final static AtomicInteger CLIENT_ATOMIC_ID = new AtomicInteger(1);

    /**
     * 服务端服务ID自增生成器，主要用于房间服务
     */
    private final static AtomicInteger SERVER_ROOM_ATOMIC_ID = new AtomicInteger(1);


    /**
     * 服务端端口
     */
    public static int port = 9876;

    /**
     * 客户端列表，根据客户端ID找到客户端信息
     */
    public final static Map<Integer, ClientSide> CLIENT_SIDE_MAP = new ConcurrentSkipListMap<>();

    /**
     * 渠道ID-客户端ID，
     */
    public final static Map<String, Integer> CHANNEL_CLIENT_ID_MAP = new ConcurrentHashMap<>();

    /**
     * 生成客户端ID
     */
    public static int geneClientId() {
        return CLIENT_ATOMIC_ID.getAndIncrement();
    }

    public static int geneRoomId() {
        return SERVER_ROOM_ATOMIC_ID.getAndIncrement();
    }

    public final static ThreadPoolExecutor THREAD_EXCUTER = new ThreadPoolExecutor(500, 500, 0, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>());

}
