package site.xiaokui.weixin.mp.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import site.xiaokui.base.util.DateUtil;
import site.xiaokui.weixin.mp.entity.MsgLog;
import site.xiaokui.weixin.mp.entity.MsgLogBuilder;
import site.xiaokui.weixin.mp.service.MsgLogService;

import java.util.Date;

/**
 * @author Binary Wang(https://github.com/binarywang)
 */
@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/wx/portal/{appid}")
public class WxPortalController {

    private final WxMpService wxService;
    private final WxMpMessageRouter messageRouter;
    private final MsgLogService msgLogService;

    @GetMapping(produces = "text/plain;charset=utf-8")
    public String authGet(@PathVariable String appid,
                          @RequestParam(name = "signature", required = false) String signature,
                          @RequestParam(name = "timestamp", required = false) String timestamp,
                          @RequestParam(name = "nonce", required = false) String nonce,
                          @RequestParam(name = "echostr", required = false) String echostr) {

        log.info("\n接收到来自微信服务器的认证消息：[{}, {}, {}, {}]", signature,
            timestamp, nonce, echostr);
        if (StringUtils.isAnyBlank(signature, timestamp, nonce, echostr)) {
            throw new IllegalArgumentException("请求参数非法，请核实!");
        }
        if (!this.wxService.switchover(appid)) {
            throw new IllegalArgumentException(String.format("未找到对应appid=[%s]的配置，请核实！", appid));
        }

        if (wxService.checkSignature(timestamp, nonce, signature)) {
            MsgLog msgLog = new MsgLogBuilder().fromUser("微信官方").toUser(appid).content("认证成功")
                    .msgType("认证消息").receive(true).build();
            msgLogService.insert(msgLog);
            return echostr;
        }
        return "非法请求";
    }

    @PostMapping(produces = "application/xml; charset=UTF-8")
    public String post(@PathVariable String appid,
                       @RequestBody String requestBody,
                       @RequestParam("signature") String signature,
                       @RequestParam("timestamp") String timestamp,
                       @RequestParam("nonce") String nonce,
                       @RequestParam("openid") String openid,
                       @RequestParam(name = "encrypt_type", required = false) String encType,
                       @RequestParam(name = "msg_signature", required = false) String msgSignature) {
        log.info("\n接收微信请求：[openid=[{}], [signature=[{}], encType=[{}], msgSignature=[{}],"
                + " timestamp=[{}], nonce=[{}], requestBody=[\n{}\n] ",
            openid, signature, encType, msgSignature, timestamp, nonce, requestBody);

        if (!this.wxService.switchover(appid)) {
            throw new IllegalArgumentException(String.format("未找到对应appid=[%s]的配置，请核实！", appid));
        }
        if (!wxService.checkSignature(timestamp, nonce, signature)) {
            throw new IllegalArgumentException("非法请求，可能属于伪造的请求！");
        }

        String out;
        if (encType == null) {
            // 明文传输的消息
            WxMpXmlMessage inMessage = WxMpXmlMessage.fromXml(requestBody);
            WxMpXmlOutMessage outMessage = this.route(inMessage);
            if (outMessage == null) {
                return "";
            }
            outMessage.setFromUserName(inMessage.getToUser());
            outMessage.setToUserName(inMessage.getFromUser());

            // 记录输入日志
            MsgLog msgLog = new MsgLogBuilder(inMessage).build();
            msgLogService.insert(msgLog);

            out = outMessage.toXml();
            log.info("\n组装回复信息：{}", out);
            System.out.println(out.toString());

            // 记录输出日志
            msgLog = new MsgLogBuilder(outMessage).build();
            msgLogService.insert(msgLog);
            return out;
        } else if ("aes".equalsIgnoreCase(encType)) {
            // aes加密的消息
            WxMpXmlMessage inMessage = WxMpXmlMessage.fromEncryptedXml(requestBody, wxService.getWxMpConfigStorage(),
                timestamp, nonce, msgSignature);
            log.info("\n消息解密后内容为：\n{} ", inMessage.toString());
            WxMpXmlOutMessage outMessage = this.route(inMessage);
            if (outMessage == null) {
                return "";
            }
            outMessage.setFromUserName(inMessage.getToUser());
            outMessage.setToUserName(inMessage.getFromUser());
            out = outMessage.toEncryptedXml(wxService.getWxMpConfigStorage());
            log.info("\n组装回复信息：{}", out);
        }
        return null;
    }

    private WxMpXmlOutMessage route(WxMpXmlMessage message) {
        try {
            return this.messageRouter.route(message);
        } catch (Exception e) {
            log.error("路由消息时出现异常！", e);
        }
        return null;
    }
}
