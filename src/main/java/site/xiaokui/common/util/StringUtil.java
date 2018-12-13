package site.xiaokui.common.util;

import cn.hutool.core.util.StrUtil;

import java.util.Map;

/**
 * 本工具类继承自HuTool的字符串工具类，不做特别重构
 *
 * @author HK
 * @date 2018-05-24 20:14
 */
public class StringUtil extends StrUtil {

    public static void print(Object... objects) {
        for (Object o : objects) {
            System.out.print(o + " ");
        }
    }

    public static void print(String... objces) {
        for (String str : objces) {
            System.out.print(str + " ");
        }
    }

    public static boolean checkEmailPass(String email) {
        if (email == null) {
            return false;
        }
        return email.length() > 6 && email.contains("@");
    }

    public static boolean moreThanZero(Integer num) {
        if (num == null) {
            return false;
        }
        return num > 0;
    }

    /**
     * 是否包含空字符串及小于等于0的数字
     */
    public static boolean hasEmptyStrOrLessThanEqualsZeroNumber(final Object... objects) {
        if (objects == null || objects.length == 0) {
            return true;
        }
        for (Object temp : objects) {
            if (temp == null) {
                return true;
            }
            if (temp instanceof String) {
                if (isEmpty((String) temp)) {
                    return true;
                }
            } else if (temp instanceof Integer) {
                if ((Integer)temp <= 0) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 在两个字符串之间加入一个英文逗号（形如"aa".addDot("bb")为"aa,bb"）
     * 如果两者之间有一个为null，则直接返回另一个字符串
     */
    public static String addDot(String str1, String str2) {
        if (str1 != null && str2 != null) {
            return str1 + "," +str2;
        } else if (str1 != null || str2 != null) {
            return str1 != null ? str1 : str2;
        }
        return null;
    }
}
