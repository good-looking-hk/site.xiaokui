package site.xiaokui.exception;

import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

/**
 * 继承自BadRequestException，表示验证异常，需要打印日志
 * @author HK
 * @date 2020-12-09 22:44
 */
public class ErrorRequestException extends BadRequestException {

    private final Integer status = INTERNAL_SERVER_ERROR.value();

    public ErrorRequestException(String msg) {
        super(msg);
    }

    public ErrorRequestException(HttpStatus status, String msg) {
        super(status, msg);
    }
}
