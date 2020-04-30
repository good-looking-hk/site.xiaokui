package site.xiaokui.module.weixin.rebot;

/**
 * @author HK
 * @date 2020-04-22 11:16
 */
public enum  AnswerType {
    /**
     * 微信机器人返回的消息类型
     */
    TEXT("text"), MUSIC("music"), NEWS("news");

    private String answerType;

    AnswerType (String answerType) {
        this.answerType = answerType;
    }

    public String getType() {
        return this.answerType;
    }
}
