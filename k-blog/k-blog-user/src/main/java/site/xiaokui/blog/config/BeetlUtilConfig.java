package site.xiaokui.blog.config;

import org.beetl.ext.spring.BeetlGroupUtilConfiguration;
import site.xiaokui.blog.config.shiro.ShiroKit;

/**
 * @author HK
 * @date 2018-05-22 22:41
 */
public class BeetlUtilConfig extends BeetlGroupUtilConfiguration {

    @Override
    public void initOther() {
        groupTemplate.registerFunctionPackage("shiro", ShiroKit.getInstance());
//        groupTemplate.registerFunction("printFile", new PrintFile());
    }
}
