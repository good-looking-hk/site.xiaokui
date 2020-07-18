package site.xiaokui.common.exception;

import org.apache.shiro.authc.AuthenticationException;

/**
 * 密码尝试次数过多异常
 * @author HK
 * @date 2018-06-06 23:01
 */
public class TooMuchPasswordRetryException extends AuthenticationException {

    public TooMuchPasswordRetryException() {
        super();
    }

    public TooMuchPasswordRetryException(String msg) {
        super(msg);
    }

}
