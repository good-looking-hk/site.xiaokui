package site.xiaokui.module.base.service;

import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;

/**
 * @author HK
 * @date 2019-02-22 21:09
 */
public class EmailService {

    private static final MailAccount account = new MailAccount();
    private static final String ME = "3292028193@qq.com";
    static {
        account.setHost("smtp.163.com");
        account.setPort(25);
        account.setAuth(true);
        account.setFrom("hk467914950@163.com");
        account.setUser("hk467914950");
        account.setPass("1q2w3er4m");
    }

    public static void sendToMe(String content) {
        MailUtil.send(account, ME, "小葵博客消息", content, false);
    }

    public static void main(String[] args) {
        sendToMe("hello");
    }
}
