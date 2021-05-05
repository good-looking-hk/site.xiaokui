package site.xiaokui.spring.bean;

/**
 * @author HK
 * @date 2019-06-12 22:55
 */
public interface BeanFactory {

    Object getBean(String name);

    Object getBean(Class cls);
}
