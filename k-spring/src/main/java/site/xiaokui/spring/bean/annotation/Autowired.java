package site.xiaokui.spring.bean.annotation;

import java.lang.annotation.*;

/**
 * 支持继承
 * @author HK
 * @date 2019-06-15 10:38
 */
@Target({ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Autowired {
}
