package site.xiaokui.common.exception;

/**
 * @author HK
 * @date 2019-02-21 13:50
 */
public class NotCloseException extends RuntimeException {

    public NotCloseException() {
        super("没有关闭连接");
    }

    public NotCloseException(String msg) {
        super(msg);
    }
}
