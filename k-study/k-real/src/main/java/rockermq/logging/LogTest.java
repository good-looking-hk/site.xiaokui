package rockermq.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.Util;
import org.slf4j.impl.StaticLoggerBinder;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedHashSet;

/**
 * slf4j simple logging facade for java
 *
 * 参考链接：https://blog.csdn.net/Mr_Mocha/article/details/90482164
 *
 * slf4j-log4j12-x.x.x.jar是使用org.apache.log4j.Logger提供的驱动
 * slf4j-jdk14-x.x.x.jar是使用java.util.logging提供的驱动
 * slf4j-simple-x.x.x.jar直接绑定System.err
 * slf4j-jcl-x.x.x.jar是使用commons-logging提供的驱动
 * logback-classic-x.x.x.jar是使用logback提供的驱动
 *
 * slf4j实现原理：具体代码见类LoggerFactory的bind方法，大致过程如下
 * 1.先 findPossibleStaticLoggerBinderPathSet 方法尝试去加载类 org/slf4j/impl/StaticLoggerBinder.class
 * 2.如果出现 NoClassDefFoundError 则说明类路径下没有 第三方日志框架对 StaticLoggerBinder 的具体实现，slf4j输出找不到第三方适配，流程结束
 * 3.如果找得到 StaticLoggerBinder 的实现类，则 StaticLoggerBinder.getSingleton().getLoggerFactory()，返回第三方Logger的具体实现类
 * 4.这里还有一个分支，如果找到多个 StaticLoggerBinder 实现类，怎么办？ 答案是能跑，但会出现红色警告 Class path contains multiple SLF4J bindings
 * 5.如果出现了两个相同包相同类，那么用的是哪个呢？ 答案是只会加载第一个，并且正常运行，与export顺序有关（可以通过调换logback、log4j的导入顺序来使用不同架包）
 *
 * 浅谈两个jar包中包含完全相同的包名和类名的加载问题
 * 1.自定义两个jar包，其中包含相同包名和类名：与export的导入顺序有关。只会加载第一个，并且运行正常
 * 2.自定义jar和jdk包，其中包含相同的包名和类名：与export的导入顺序有关。同样是只会加载第一个，但是如果加载自定义的jar运行会报错。加载jdk正常。
 * @author HK
 * @date 2021-03-09 11:21
 */
public class LogTest {

    private static String STATIC_LOGGER_BINDER_PATH = "org/slf4j/impl/StaticLoggerBinder.class";

    static Logger logger = LoggerFactory.getLogger(LogTest.class);

    public static void main(String[] args) {
        LinkedHashSet staticLoggerBinderPathSet = new LinkedHashSet();

        try {
            ClassLoader loggerFactoryClassLoader = LoggerFactory.class.getClassLoader();
            Enumeration paths;
            if (loggerFactoryClassLoader == null) {
                paths = ClassLoader.getSystemResources(STATIC_LOGGER_BINDER_PATH);
            } else {
                paths = loggerFactoryClassLoader.getResources(STATIC_LOGGER_BINDER_PATH);
            }

            while(paths.hasMoreElements()) {
                URL path = (URL)paths.nextElement();
                staticLoggerBinderPathSet.add(path);
            }

        } catch (IOException var4) {
            Util.report("Error getting resources from path", var4);
        }
        System.out.println(staticLoggerBinderPathSet);

        System.out.println(StaticLoggerBinder.class);
        // logback=1.7.16    log4j=1.6
        System.out.println(StaticLoggerBinder.REQUESTED_API_VERSION);

        logger.info(LoggerFactory.getILoggerFactory().toString() + " " + logger.getName() + ": 这是一条测试消息");
        System.out.println("这是一条测试消息");
    }
}
