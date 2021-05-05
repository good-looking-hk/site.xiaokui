package site.xiaokui.spring.boot;

import site.xiaokui.spring.bean.DefaultWebBeanFactory;
import site.xiaokui.spring.core.util.Assert;
import site.xiaokui.spring.core.util.LogUtil;
import site.xiaokui.spring.core.util.StringUtil;
import site.xiaokui.spring.web.server.MiniHttpServer;

import java.io.File;
import java.net.URL;

/**
 * @author HK
 * @date 2020-03-26 14:17
 */
public class SpringApplicationRunner {

    protected LogUtil log = LogUtil.getLogger(this.getClass());

    private DefaultWebBeanFactory defaultWebBeanFactory = new DefaultWebBeanFactory();

    private RuntimeEnvironment runtimeEnvironment;

    public void run(Class appCls, String... args) {
        Assert.notNull(appCls);
        String scanPackage = appCls.getPackage().getName();
        runtimeEnvironment = new RuntimeEnvironment(args, scanPackage);
        doScannerAndRegister(scanPackage, defaultWebBeanFactory);
        defaultWebBeanFactory.init();
        initServer(defaultWebBeanFactory);
        log.info("SpringBoot应用已完全启动");
    }

    private void initServer(DefaultWebBeanFactory defaultBeanFactory) {
        int port = Integer.parseInt(runtimeEnvironment.getProperty("server.port"));
        MiniHttpServer miniHttpServer = new MiniHttpServer(port, defaultBeanFactory);
        new Thread(new Runnable() {
            @Override
            public void run() {
                log.info("监听端口" + port);
                miniHttpServer.beginListen();
            }
        }).start();
    }

    /**
     * 扫描包下面的所有Spring需要的类
     *
     * @param scanPackage 形如site.xiaokui.spring.main
     */
    private void doScannerAndRegister(String scanPackage, DefaultWebBeanFactory defaultBeanFactory) {
        URL url = StringUtil.checkPackage(scanPackage);

        File classpath = new File(url.getFile());
        File[] listFiles = classpath.listFiles();
        if (listFiles == null) {
            return;
        }
        for (File file : listFiles) {
            if (file.isDirectory()) {
                doScannerAndRegister(scanPackage + "." + file.getName(), defaultBeanFactory);
            } else {
                if (!file.getName().endsWith(".class")) {
                    continue;
                }
                String className = scanPackage + "." + file.getName().replaceAll(".class", "");
                try {
                    Class cls = Class.forName(className);
                    if (defaultBeanFactory.isInnerClass(cls)) {
                        defaultBeanFactory.registerBeanDefinition(cls);
                        log.debug(cls + "注册到容器");
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
