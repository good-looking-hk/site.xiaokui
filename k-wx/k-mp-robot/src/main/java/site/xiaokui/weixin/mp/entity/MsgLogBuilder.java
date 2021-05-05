package site.xiaokui.weixin.mp.entity;

import me.chanjar.weixin.mp.bean.message.*;
import site.xiaokui.base.util.DateUtil;

import java.util.Date;

/**
 * @author HK
 * @date 2020-11-17 16:11
 */
public class MsgLogBuilder {

    private MsgLog msgLog;

    public MsgLogBuilder() {
        msgLog = new MsgLog();
    }

    public MsgLogBuilder(WxMpXmlMessage inMessage) {
        msgLog = new MsgLog();
        msgLog.setFromUser(inMessage.getFromUser());
        msgLog.setToUser(inMessage.getToUser());
        msgLog.setMsgContent(inMessage.getContent());
        msgLog.setMsgType(inMessage.getMsgType());
        msgLog.setMsgId(inMessage.getMsgId());
        msgLog.setIsReceive(true);
    }

    public MsgLogBuilder(WxMpXmlOutMessage outMessage) {
        msgLog = new MsgLog();
        msgLog.setFromUser(outMessage.getFromUserName());
        msgLog.setToUser(outMessage.getToUserName());
        msgLog.setMsgType(outMessage.getMsgType());
        msgLog.setIsReceive(false);
        if (outMessage instanceof WxMpXmlOutTextMessage) {
            msgLog.setMsgContent(((WxMpXmlOutTextMessage) outMessage).getContent());
        }
        if (outMessage instanceof WxMpXmlOutMusicMessage) {
            WxMpXmlOutMusicMessage musicMessage = (WxMpXmlOutMusicMessage) outMessage;
            msgLog.setMsgContent(musicMessage.getTitle() + " " + musicMessage.getDescription());
        }
        if (outMessage instanceof WxMpXmlOutNewsMessage) {
            WxMpXmlOutNewsMessage newsMessage = (WxMpXmlOutNewsMessage) outMessage;
            msgLog.setMsgContent(newsMessage.getArticles().get(0).getTitle());
        }
    }

    public MsgLogBuilder toUser(String toUser) {
        msgLog.setToUser(toUser);
        return this;
    }

    public MsgLogBuilder fromUser(String fromUser) {
        msgLog.setFromUser(fromUser);
        return this;
    }

    public MsgLogBuilder content(String content) {
        msgLog.setMsgContent(content);
        return this;
    }

    public MsgLogBuilder msgType(String msgType) {
        msgLog.setMsgType(msgType);
        return this;
    }

    public MsgLogBuilder receive(boolean receive) {
        msgLog.setIsReceive(receive);
        return this;
    }

    public MsgLogBuilder msgId(Long msgId) {
        msgLog.setMsgId(msgId);
        return this;
    }

    public MsgLog build() {
        Date date = new Date();
        msgLog.setOccurDate(DateUtil.parseIntDate(date));
        msgLog.setOccurTime(date);
        return this.msgLog;
    }
}
