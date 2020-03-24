package site.xiaokui.common.hk;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.extra.mail.MailAccount;

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
        A a = new A();
        String result = ((B) a).getB();
        System.out.println(result);
    }
}
