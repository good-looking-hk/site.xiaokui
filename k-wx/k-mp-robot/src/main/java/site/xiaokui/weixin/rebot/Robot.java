package site.xiaokui.weixin.rebot;

import cn.hutool.core.net.URLDecoder;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutNewsMessage;
import me.chanjar.weixin.mp.builder.outxml.MusicBuilder;
import me.chanjar.weixin.mp.builder.outxml.NewsBuilder;
import me.chanjar.weixin.mp.builder.outxml.TextBuilder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {"access_token":"32_bqrs-oYQ-fs6NTvb4bATnWdqAR1xhFhWF306UF8oq65hKPCGDyYkqMkpX01nSFmM5o_k5keBhFxbq80EBf52BAu4Sb9EJUhY8dKn5FkINUCN-xaA1iwH0tDUCM2TTE8KYgC0fwLVhu4vB4hAFSGhADAANB","expires_in":7200}
 * @author HK
 * @date 2020-04-21 16:19
 */
@Slf4j
@Service
public class Robot {

    private static final String APP_ID = "5XuLhpSLLI52Wij";

    private static String token = "w6gEiV0aN6tccJjZlQejN5CHQroTh0";

    private static final String ENCODING_AES_KEY = "DPrUz0QYqaLlkvl8sN2IuapEyNuGfoQNB0CH6KSBcap";

    /**
     * 获取signature签名，暂时未用到
     */
    private static final String SIGN_URL = "https://openai.weixin.qq.com/openapi/sign/";

    /**
     * 普通消息接口(只签名不加密)
     */
    private static final String NORMAL_MSG_URL = "https://openai.weixin.qq.com/openapi/message/";


    static String sss = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + "wxda9a290c4b3e91fd" + "&secret=" + "7f299f10bac94a0c7b2020e11327cf76";

    private static final String ss = "https://api.weixin.qq.com/cgi-bin/material/get_materialcount?access_token=" + "32_bqrs-oYQ-fs6NTvb4bATnWdqAR1xhFhWF306UF8oq65hKPCGDyYkqMkpX01nSFmM5o_k5keBhFxbq80EBf52BAu4Sb9EJUhY8dKn5FkINUCN-xaA1iwH0tDUCM2TTE8KYgC0fwLVhu4vB4hAFSGhADAANB";

    public static WxMpXmlOutMessage sendToRobot (String user, String me, String msg) {
        Map<String, Object> map = new HashMap<>(2);
        map.put("username", user);
        map.put("msg", msg);
        return sendMsgToRobotWithNoSign(map, me);
    }

    public static WxMpXmlOutMessage sendMsgToRobotWithNoSign(Map<String, Object> map, String me) {
        String encodeStr = createSign(map);
        HttpRequest httpRequest = HttpRequest.post(NORMAL_MSG_URL + token).form("query", encodeStr);
        HttpResponse httpResponse = httpRequest.execute();
        JSONObject json = new JSONObject(httpResponse.body());
        System.out.println(json);
        // 调用微信机器人出错
        if (json.get("errcode") != null || json.get("code") != null) {
            EmailService.sendToRealMe(json.toString());
            return new TextBuilder().content("小冰冰正在休息中，请稍后再试").build();
        }
        if ("no access".equals(json.get("title"))) {
            return new TextBuilder().content("小冰冰不知道说什么了，不好意思哈>_>").build();
        }
        String answerType = json.getStr("answer_type");
        String answer = json.get("answer").toString();
        String toUser = new String(URLDecoder.decode(json.getStr("from_user_name").getBytes(StandardCharsets.UTF_8)));
        log.info("请求消息:{}, 返回消息:{}, body内容:{}", map, answer, httpResponse.body());

        if (AnswerType.TEXT.getType().equals(answerType)) {
            return new TextBuilder().content(answer).toUser(toUser).fromUser(me).build();
        }
        else if (AnswerType.MUSIC.getType().equals(answerType)) {
            JSONObject msgContent = (JSONObject) json.getJSONArray("msg").get(0);
            String url = msgContent.getStr("music_url");
            String picUrl = msgContent.getStr("pic_url");
            String singerName = msgContent.getStr("singer_name");
            String songName = msgContent.getStr("song_name");
            String respTitle = msgContent.getStr("resp_title");
            if (StrUtil.isEmpty(singerName)) {
                return new MusicBuilder().musicUrl(url).title(songName)
                        .description("\n点击播放").hqMusicUrl(url).toUser(toUser).fromUser(me).build();
            }
            return new MusicBuilder().musicUrl(url).title(singerName + " - " + songName)
                .description("\n点击播放").hqMusicUrl(url).toUser(toUser).fromUser(me).build();
        }
        else if (AnswerType.NEWS.getType().equals(answerType)) {
            List<WxMpXmlOutNewsMessage.Item> articles = new ArrayList<>();
            WxMpXmlOutNewsMessage.Item item = new WxMpXmlOutNewsMessage.Item();

            JSONObject msgContent = (JSONObject) json.getJSONArray("msg").get(0);
            msgContent = (JSONObject) msgContent.getJSONArray("articles").get(0);

            item.setDescription(msgContent.getStr("description"));
            item.setPicUrl(msgContent.getStr("pic_url"));
            item.setTitle(msgContent.getStr("title"));
            item.setUrl(msgContent.getStr("url"));
            articles.add(item);
            return new NewsBuilder().articles(articles).toUser(toUser).fromUser(me).build();
        }
        return new TextBuilder().content("小冰冰正在独自发呆中....").toUser(toUser).fromUser(me).build();
    }

    private static String createSign(Map<String, Object> map) {
        Map<String, Object> header = new HashMap<>(2);
        header.put("typ", "JWT");
        header.put("alg", "HS256");
        JwtBuilder jwtBuilder = Jwts.builder().setHeader(header);
        jwtBuilder.setClaims(map);
        jwtBuilder.signWith(SignatureAlgorithm.HS256, ENCODING_AES_KEY.getBytes(StandardCharsets.UTF_8));
        return jwtBuilder.compact();
    }

    public static void main(String[] args) {
        String str = HttpUtil.get(ss);
        System.out.println(str);

//        HashMap<String, Object> paramMap = new HashMap<>(4);
//        paramMap.put("username", "一般般帅");
//        paramMap.put("msg", "今天疫情");
//        sendMsgToRobotWithNoSign(paramMap, "");
    }
}
