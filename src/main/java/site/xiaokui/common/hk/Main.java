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

    public static void main(String[] args) {
        int i = 0, j = 0;
        System.out.println(i++);
        System.out.println(i);
        System.out.println(i++ == i);
        System.out.println(i + j++ == 2);
    }
}
