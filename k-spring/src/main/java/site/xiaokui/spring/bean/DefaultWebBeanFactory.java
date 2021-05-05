package site.xiaokui.spring.bean;

import site.xiaokui.spring.bean.annotation.Bean;
import site.xiaokui.spring.bean.annotation.Configuration;
import site.xiaokui.spring.bean.factory.AbstractBeanFactory;
import site.xiaokui.spring.web.MappingHandler;
import site.xiaokui.spring.web.WebApplicationContext;
import site.xiaokui.spring.web.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author HK
 * @date 2019-06-12 22:58
 */
public class DefaultWebBeanFactory extends AbstractBeanFactory {

    private static final HashSet<Class<? extends Annotation>> INNER_ANNOTATION_SET = new HashSet<>(8);

    private List<MappingHandler> mappingHandlerList = new ArrayList<>(4);

    private WebApplicationContext webApplicationContext;

    static {
        INNER_ANNOTATION_SET.add(Bean.class);
        INNER_ANNOTATION_SET.add(Service.class);
        INNER_ANNOTATION_SET.add(Controller.class);
        INNER_ANNOTATION_SET.add(Configuration.class);
    }

    public DefaultWebBeanFactory() {
        webApplicationContext = new WebApplicationContext(this);
    }

    /**
     * 初始化各种系统内置注解
     */
    public void init() {
        initBeans();
        initServices();
        initConfigurations();
        initControllers();
    }

    private void initBeans() {
    }

    private void initServices() {
    }

    private void initConfigurations() {
    }

    private void initControllers() {
        ConcurrentHashMap<String, Class> registerClass = this.getRegisterClassMap();
        for (Map.Entry<String, Class> entry : registerClass.entrySet()) {
            Class cls = entry.getValue();
            Object obj = getBean(cls);
            if (!cls.isAnnotationPresent(Controller.class)) {
                continue;
            }
            Controller controller = (Controller) cls.getAnnotation(Controller.class);
            this.getNameMap().put(controller.name(), obj);

            String[] parentUrl = new String[]{""};
            if(cls.isAnnotationPresent(RequestMapping.class)){
                RequestMapping requestMapping = (RequestMapping) cls.getAnnotation(RequestMapping.class);
                if (requestMapping.value().length != 0) {
                    parentUrl = requestMapping.value();
                }
            }

            //获取所有的公共方法
            for (Method method : cls.getMethods()) {
                if(!method.isAnnotationPresent(RequestMapping.class)){ continue;}
                RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                String[] sonUrl = requestMapping.value();
                mappingHandlerList.add(new MappingHandler(parentUrl, sonUrl, obj, method));
                for (String p : parentUrl) {
                    for (String s : sonUrl) {
                        log.info("路径" + p + s + "匹配到方法" + method);
                    }
                }
            }
        }
    }

    /**
     * 如果是系统内置bean注解，则返回beanName，否则返回null
     */
    public boolean isInnerClass(Class cls) {
        Annotation[] annotations = cls.getDeclaredAnnotations();
        for (Annotation annotation : annotations) {
            if (INNER_ANNOTATION_SET.contains(annotation.annotationType())) {
                return true;
            }
        }
        return false;
    }

    public List<MappingHandler> getMappingHandlerList() {
        return mappingHandlerList;
    }

    public WebApplicationContext getWebApplicationContext() {
        return webApplicationContext;
    }
}
