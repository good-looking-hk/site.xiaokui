package site.xiaokui.service;

import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author HK
 * @date 2019-02-22 21:09
 */
@Slf4j
public class EmailService {

    private static final MailAccount ACCOUNT = new MailAccount();

    private static final String ANOTHER_ME = "3292028193@qq.com";

    private static final String REAL_ME = "467914950@qq.com";

    static {
        ACCOUNT.setHost("smtp.163.com");
        ACCOUNT.setPort(25);
        ACCOUNT.setAuth(true);
        ACCOUNT.setFrom("hk467914950@163.com");
        ACCOUNT.setUser("hk467914950");
        ACCOUNT.setPass("1q2w3er4m");
    }

    public static void sendInfo(String content) {
        MailUtil.send(ACCOUNT, REAL_ME, "日志消息", content, false);
    }

    public static void sendError(String content) {
        MailUtil.send(ACCOUNT, REAL_ME, "日志报警", content, false);
    }

    public static void send(String content, String title, String userEmail) {
        MailUtil.send(ACCOUNT, userEmail, title, content, false);
    }

    public static void main(String[] args) {
        sendError("测试邮件，请勿回复");
    }
}
