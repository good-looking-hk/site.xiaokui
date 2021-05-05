package site.xiaokui.spring.test.aop;

import org.springframework.cglib.core.DebuggingClassWriter;
import site.xiaokui.spring.aop.IProxy;
import site.xiaokui.spring.aop.JdkProxy;
import site.xiaokui.spring.aop.SimpleCglibProxy;
import site.xiaokui.spring.aop.SimpleJdkProxy;

/**
 * @author HK
 * @date 2020-04-11 14:36
 */
public class TestAop {

    public static void main(String[] args) {
        System.getProperties().put("sun.misc.ProxyGenerator.saveGeneratedFiles", "true");
        System.getProperties().put("jdk.proxy.ProxyGenerator.saveGeneratedFiles", "true");
        System.setProperty(DebuggingClassWriter.DEBUG_LOCATION_PROPERTY, "/home/hk/TEMP");
        UserService userService = new UserServiceImpl();
        IProxy proxy = new SimpleCglibProxy();
        proxy.setObject(userService);
        userService = (UserService) proxy.getProxy();
        userService.testA();
    }
}
