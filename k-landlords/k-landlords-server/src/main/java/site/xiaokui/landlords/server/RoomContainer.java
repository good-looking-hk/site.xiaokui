package site.xiaokui.landlords.server;

import site.xiaokui.landlords.common.entity.Room;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * @author HK
 * @date 2020-08-06 11:24
 */
public class RoomContainer {

    private final static Map<Integer, Room> ROOM_MAP = new ConcurrentSkipListMap<>();

    public final static Room getRoom(int id){
        Room room = ROOM_MAP.get(id);
        if(room != null){
            room.setLastFlushTime(System.currentTimeMillis());
        }
        return room;
    }

    public final static Map<Integer, Room> getRoomMap(){
        return ROOM_MAP;
    }

    public final static Room removeRoom(int id){
        return ROOM_MAP.remove(id);
    }

    public final static Room addRoom(Room room){
        return ROOM_MAP.put(room.getId(), room);
    }
}
