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
        File dir = new File("/home/hk/我的博客/我的周报/md");
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.getName().contains("试用")) {
                file.renameTo(new File(file.getParent(), file.getName().replace("试用", "工作")));
            }
        }
    }
}
