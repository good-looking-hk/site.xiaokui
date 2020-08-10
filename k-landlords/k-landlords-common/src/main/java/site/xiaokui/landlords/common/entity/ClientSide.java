package site.xiaokui.landlords.common.entity;

import io.netty.channel.Channel;
import site.xiaokui.landlords.common.entity.enums.ClientRole;
import site.xiaokui.landlords.common.entity.enums.ClientStatus;
import site.xiaokui.landlords.common.entity.enums.ClientType;

import java.net.SocketAddress;
import java.util.List;

/**
 * @author HK
 * @date 2020-08-03 17:11
 */
public class ClientSide {

    /**
     * 客户端ID
     */
    private int id;

    /**
     * 所在房间ID
     */
    private int roomId;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 用户的手牌
     */
    private List<Poker> pokers;

    /**
     * 客户端状态，如 选择中、游戏中、等待游戏开始，准备游戏开始
     */
    private ClientStatus status;

    /**
     * 客户端角色，是否是 机器人
     */
    private ClientRole role;

    /**
     * 客户端类型，农民或地主
     */
    private ClientType type;

    /**
     * 出牌的下家
     */
    private ClientSide next;

    /**
     * 出牌的上家
     */
    private ClientSide pre;

    /**
     * 客户端ip地址
     */
    private String ip;

    /**
     * 通信渠道
     */
    private transient Channel channel;

    public ClientSide() {
    }

    public ClientSide(int id, ClientStatus status, Channel channel) {
        this.id = id;
        this.status = status;
        this.channel = channel;
    }

    public void init() {
        roomId = 0;
        pokers = null;
        status = ClientStatus.TO_CHOOSE;
        type = null;
        next = null;
        pre = null;
    }

    public final ClientRole getRole() {
        return role;
    }

    public final void setRole(ClientRole role) {
        this.role = role;
    }

    public final String getNickname() {
        return nickname;
    }

    public final void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public final Channel getChannel() {
        return channel;
    }

    public final void setChannel(Channel channel) {
        this.channel = channel;
    }

    public final int getRoomId() {
        return roomId;
    }

    public final void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public final List<Poker> getPokers() {
        return pokers;
    }

    public final void setPokers(List<Poker> pokers) {
        this.pokers = pokers;
    }

    public final ClientStatus getStatus() {
        return status;
    }

    public final void setStatus(ClientStatus status) {
        this.status = status;
    }

    public final ClientType getType() {
        return type;
    }

    public final void setType(ClientType type) {
        this.type = type;
    }

    public final int getId() {
        return id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public final void setId(int id) {
        this.id = id;
    }

    public final ClientSide getNext() {
        return next;
    }

    public final void setNext(ClientSide next) {
        this.next = next;
    }

    public final ClientSide getPre() {
        return pre;
    }

    public final void setPre(ClientSide pre) {
        this.pre = pre;
    }

    @Override
    public String toString() {
        return this.id + "-" + this.nickname;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }
}
