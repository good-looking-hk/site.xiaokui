package site.xiaokui.spring.core.util;

/**
 * @author HK
 * @date 2019-09-01 15:42
 */
public enum LogType {

    /**
     * 内置日志级别
     */
    TRACE(0, "trace"), DEBUG(1, "debug"), INFO(2, "info"), WARN(3, "warn"), ERROR(4, "error");

    int weight;
    String type;
    LogType(int weight, String type) {
        this.weight = weight;
        this.type = type;
    }
}
