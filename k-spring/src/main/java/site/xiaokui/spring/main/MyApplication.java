package site.xiaokui.spring.main;

import site.xiaokui.spring.boot.SpringApplicationRunner;
import site.xiaokui.spring.boot.annotation.SpringBootApplication;
import site.xiaokui.spring.core.util.LogType;
import site.xiaokui.spring.core.util.LogUtil;

/**
 * @author HK
 * @date 2020-03-26 14:12
 */
@SpringBootApplication
public class MyApplication {

    public static void main(String[] args) {
        LogUtil.setMode(LogType.DEBUG);
        new SpringApplicationRunner().run(MyApplication.class, args);
    }
}
