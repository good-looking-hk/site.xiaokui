package site.xiaokui.spring.core.util;

/**
 * @author HK
 * @date 2019-06-29 10:47
 */
public class ClassUtil {

    /**
     * getName返回格式如：site.xiaokui.TestAbstractBeanFactory$Room
     * getCanonicalName返回格式如：site.xiaokui.TestAbstractBeanFactory.Room
     * 尝试获取内部类的外部父类
     */
    public static String getOutClassFromInnerClass(Class cls) {
        String name = cls.getName();
        String canonicalName = cls.getCanonicalName();
        String simpleName = cls.getSimpleName();
        int nl = name.length();
        int cl = canonicalName.length();
        if (nl == cl && !simpleName.equals(canonicalName) && name.charAt(nl - simpleName.length() - 1) == '$') {
            return name.substring(0, nl - simpleName.length() - 1);
        }
        return null;
    }
}
