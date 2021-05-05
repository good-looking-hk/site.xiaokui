package site.xiaokui.spring.web;

import site.xiaokui.spring.core.util.PatternMatchUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author HK
 * @date 2020-04-01 19:18
 */
public class MappingHandler {

    private String[] parentUrl, sonUrl;

    private Object controllerObj;

    private Method invokeMethod;

    public MappingHandler(String[] parentUrl, String[] sonUrl, Object controllerObj, Method invokeMethod){
        this.parentUrl = parentUrl;
        this.sonUrl = sonUrl;
        this.controllerObj = controllerObj;
        this.invokeMethod = invokeMethod;
    }

    public boolean matchUrl(String url) {
        for (String p : parentUrl) {
            for (String s : sonUrl) {
                return PatternMatchUtils.simpleMatch(p  + s, url);
            }
        }
        return false;
    }

    /**
     * TODO 暂时支持空方法
     *   对于 varargs 调用, 应使用 Object
     *   对于非 varargs 调用, 应使用 Object[], 这样也可以抑制此警告
     */
    public Object invokeRequestMethod() {
        try {
            invokeMethod.setAccessible(true);
            return invokeMethod.invoke(controllerObj, (Object) null);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}
