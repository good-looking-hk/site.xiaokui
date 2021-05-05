package site.xiaokui.spring.core.util;

import java.io.File;
import java.net.URL;

/**
 * @author HK
 * @date 2019-09-27 16:57
 */
public class StringUtil {

    public static String lowerFirst(String str) {
        if (str == null || str.length() == 0) {
            return str;
        }
        String h = (str.charAt(0) + "").toLowerCase();
        if (str.length() == 1) {
            return h;
        }
        return h + str.subSequence(1, str.length());
    }

    public static URL checkPackage(String scanPackage) {
        String separator = File.separator;
        URL url = StringUtil.class.getClassLoader().getResource(scanPackage.replaceAll("\\.", separator));
        if (url == null) {
            throw new RuntimeException("不存在的路径" + scanPackage);
        }
        return url;
    }

}
