package site.xiaokui.spring.bean.config;

/**
 * @author HK
 * @date 2019-06-15 10:43
 */
public interface BeanProcessor {

    void applyBeanProcessor(String beanName, Object obj);
}
