package site.xiaokui.weixin.mp.handler;

import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.builder.outxml.TextBuilder;
import org.springframework.stereotype.Component;
import site.xiaokui.weixin.rebot.Robot;

import java.util.Map;

import static me.chanjar.weixin.common.api.WxConsts.XmlMsgType;

/**
 * @author Binary Wang(https://github.com/binarywang)
 */
@Component
public class MsgHandler extends AbstractHandler {

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage,
                                    Map<String, Object> context, WxMpService weixinService,
                                    WxSessionManager sessionManager) {

        if (!wxMessage.getMsgType().equals(XmlMsgType.EVENT)) {
            //TODO 可以选择将消息保存到本地
        }

        if (wxMessage.getMsgType().equals(XmlMsgType.VOICE)) {
            String content = "这是声音消息";
            return new TextBuilder().content(content).build();
        } else if (wxMessage.getMsgType().equals(XmlMsgType.VIDEO)) {
            String content = "这是视频消息";
            return new TextBuilder().content(content).build();
        } else if (wxMessage.getMsgType().equals(XmlMsgType.IMAGE)) {
            String content = "这是图片消息";
            return new TextBuilder().content(content).build();
        }

        if (!wxMessage.getMsgType().equals(XmlMsgType.TEXT)) {
            String content = "这是" + wxMessage.getMsgType() + "消息";
            return new TextBuilder().content(content).build();
        }


        String fromUser = wxMessage.getFromUser();
        String me = wxMessage.getToUser();
        String msg = wxMessage.getContent();
        return Robot.sendToRobot(fromUser, me,  msg);
    }
}
