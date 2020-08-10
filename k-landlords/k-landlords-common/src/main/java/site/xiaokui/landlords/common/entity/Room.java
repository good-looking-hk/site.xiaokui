package site.xiaokui.landlords.common.entity;

import site.xiaokui.landlords.common.entity.enums.RoomStatus;
import site.xiaokui.landlords.common.entity.enums.RoomType;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * @author HK
 * @date 2020-08-06 10:35
 */
public class Room {

    /**
     * 房间ID
     */
    private int id;

    /**
     * 拥有者ID
     */
    private int roomOwnerId;

    /**
     * 拥有者名称
     */
    private String roomOwnerName;

    /**
     * 房间状态
     */
    private RoomStatus status;

    /**
     * 房间类型
     */
    private RoomType type;

    /**
     * 记录了房间玩家客户端信息，键为玩家ID
     */
    private Map<Integer, ClientSide> clientSideMap;

    /**
     * 房间玩家客户端列表
     */
    private LinkedList<ClientSide> clientSideList;

    /**
     * 地主ID
     */
    private int landlordId = -1;

    /**
     * 地主的3张底牌
     */
    private List<Poker> landlordPokers;

    /**
     * 上一手出的牌
     */
    private PokerSell lastPokerShell;

    /**
     * 上一手牌出的客户端ID
     */
    private int lastSellClientId = -1;

    /**
     * 当前出牌的客户端ID
     */
    private int currentSellClientId = -1;

    /**
     * 房间的难度级别、模式
     */
    private int difficultyCoefficient;

    private long lastFlushTime;

    /**
     * 创建时间
     */
    private long createTime;

    /**
     * 第一个出牌的客户端ID，即地主客户端ID
     */
    private int firstSellClientId;

    public Room() {
    }

    public Room(int id) {
        this.id = id;
        this.clientSideMap = new ConcurrentSkipListMap<>();
        this.clientSideList = new LinkedList<>();
        this.status = RoomStatus.BLANK;
    }

    public final long getCreateTime() {
        return createTime;
    }

    public final void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public final int getDifficultyCoefficient() {
        return difficultyCoefficient;
    }

    public final void setDifficultyCoefficient(int difficultyCoefficient) {
        this.difficultyCoefficient = difficultyCoefficient;
    }

    public final RoomType getType() {
        return type;
    }

    public final void setType(RoomType type) {
        this.type = type;
    }

    public final PokerSell getLastPokerShell() {
        return lastPokerShell;
    }

    public final void setLastPokerShell(PokerSell lastPokerShell) {
        this.lastPokerShell = lastPokerShell;
    }

    public long getLastFlushTime() {
        return lastFlushTime;
    }

    public void setLastFlushTime(long lastFlushTime) {
        this.lastFlushTime = lastFlushTime;
    }

    public int getLandlordId() {
        return landlordId;
    }

    public void setLandlordId(int landlordId) {
        this.landlordId = landlordId;
    }

    public LinkedList<ClientSide> getClientSideList() {
        return clientSideList;
    }

    public void setClientSideList(LinkedList<ClientSide> clientSideList) {
        this.clientSideList = clientSideList;
    }

    public List<Poker> getLandlordPokers() {
        return landlordPokers;
    }

    public void setLandlordPokers(List<Poker> landlordPokers) {
        this.landlordPokers = landlordPokers;
    }

    public int getRoomOwnerId() {
        return roomOwnerId;
    }

    public void setRoomOwnerId(int roomOwnerId) {
        this.roomOwnerId = roomOwnerId;
    }

    public String getRoomOwnerName() {
        return roomOwnerName;
    }

    public void setRoomOwnerName(String roomOwnerName) {
        this.roomOwnerName = roomOwnerName;
    }

    public final int getId() {
        return id;
    }

    public final void setId(int id) {
        this.id = id;
    }

    public final RoomStatus getStatus() {
        return status;
    }

    public final void setStatus(RoomStatus status) {
        this.status = status;
    }

    public final Map<Integer, ClientSide> getClientSideMap() {
        return clientSideMap;
    }

    public final void setClientSideMap(Map<Integer, ClientSide> clientSideMap) {
        this.clientSideMap = clientSideMap;
    }

    public int getLastSellClientId() {
        return lastSellClientId;
    }

    public void setLastSellClientId(int lastSellClientId) {
        this.lastSellClientId = lastSellClientId;
    }

    public int getCurrentSellClientId() {
        return currentSellClientId;
    }

    public void setCurrentSellClientId(int currentSellClientId) {
        this.currentSellClientId = currentSellClientId;
    }

    public int getFirstSellClientId() {
        return firstSellClientId;
    }

    public void setFirstSellClientId(int firstSellClientId) {
        this.firstSellClientId = firstSellClientId;
    }
}
