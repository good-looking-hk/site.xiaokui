package site.xiaokui.common.hk;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.extra.mail.MailAccount;
import site.xiaokui.config.shiro.ShiroKit;

import java.io.File;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author HK
 * @date 2019-02-21 18:56
 */
public class Main {


    public static class A {
        public String getA() {
            return "A";
        }
    }

    public static class B extends A {
        public String getB() {
            return "B";
        }
    }

    public static void main(String[] args) {
//        A a = new A();
//        String result = ((B) a).getB();
//        System.out.println(result);

        String result = ShiroKit.getInstance().md5("金证-黄葵-199710-467", "zh6l4tq0f4");
        System.out.println(result);
    }
}
