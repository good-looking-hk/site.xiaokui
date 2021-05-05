package site.xiaokui.spring.core.util;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author HK
 * @date 2019-08-31 17:01
 * 简单模仿主流日志框架的实现
 */
public class LogUtil {

    private static LogType defaultMode = LogType.DEBUG;

    private Class cls;

    private LogUtil(Class cls){
        this.cls = cls;
    }

    public static LogUtil getLogger(Class cls) {
        return new LogUtil(cls);
    }

    public static void setMode(LogType logType) {
        if (logType != null) {
            LogUtil.defaultMode = logType;
        }
    }

    private void printMsg(LogType logType,String msg, boolean needColor) {
        PrintStream printStream = needColor ? System.err : System.out;
        printStream.println("[" + logType.type + "][" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) +
                "][" + this.cls + " " + Thread.currentThread().getStackTrace()[3].getLineNumber() + "]:" + msg);
    }

    public void trace(String msg) {
        if (LogUtil.defaultMode.weight > LogType.TRACE.weight) {
            return;
        }
        printMsg(LogType.TRACE, msg, false);
    }

    public void debug(String msg) {
        if (LogUtil.defaultMode.weight > LogType.DEBUG.weight) {
            return;
        }
        printMsg(LogType.DEBUG, msg, false);
    }

    public void info(String msg) {
        if (LogUtil.defaultMode.weight > LogType.INFO.weight) {
            return;
        }
        printMsg(LogType.INFO, msg, true);
    }

    public void warn(String msg) {
        if (LogUtil.defaultMode.weight > LogType.WARN.weight) {
            return;
        }
        printMsg(LogType.WARN, msg, true);
    }

    public void error(String msg) {
        if (LogUtil.defaultMode.weight > LogType.ERROR.weight) {
            return;
        }
        printMsg(LogType.ERROR, msg, true);
    }
}
