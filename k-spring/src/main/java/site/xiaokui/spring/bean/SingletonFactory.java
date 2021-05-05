package site.xiaokui.spring.bean;

/**
 * @author HK
 * @date 2019-06-12 23:01
 */
public interface SingletonFactory {

    void addBean(String name, Object obj);

    void addBean(Object obj);

    void registerBeanDefinition(Class cls);

    void remove(String name);

    Object getBean(String name);

}
