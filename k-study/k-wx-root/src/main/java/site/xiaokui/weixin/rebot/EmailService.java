package site.xiaokui.weixin.rebot;

import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;

/**
 * @author HK
 * @date 2019-02-22 21:09
 */
public class EmailService {

    private static final MailAccount ACCOUNT = new MailAccount();

    private static final String ME = "3292028193@qq.com";

    private static final String REAL_ME = "467914950@qq.com";

    static {
        ACCOUNT.setHost("smtp.163.com");
        ACCOUNT.setPort(25);
        ACCOUNT.setAuth(true);
        ACCOUNT.setFrom("hk467914950@163.com");
        ACCOUNT.setUser("hk467914950");
        ACCOUNT.setPass("1q2w3er4m");
    }

    public static void sendToMe(String content) {
        MailUtil.send(ACCOUNT, ME, "小葵博客消息", content, false);
    }

    public static void sendToRealMe(String content) {
        MailUtil.send(ACCOUNT, REAL_ME, "小葵日志报警", content, false);
    }

    public static void main(String[] args) {
        sendToMe("hello");
    }
}
