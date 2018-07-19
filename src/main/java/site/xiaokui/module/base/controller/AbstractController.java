package site.xiaokui.module.base.controller;

import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 通过继承这个类，子类可以拥有跟多特性，本类除了继承父类和父接口的某些属性外，还对字段和方法做了更多的补充，大多情况下，这是您的首选
 * @author HK
 * @date 2018-06-24 22:50
 */
public abstract class AbstractController extends BaseController implements CrudController {

    private final String PREFIX = setPrefix();

    /**
     * 子类必须返回其对应的web路径前缀
     * @return 路径前缀
     */
    protected abstract String setPrefix();

    protected final String TO_INDEX = PREFIX + INDEX;

    protected final String TO_ADD = PREFIX + ADD;

    protected final String TO_EDIT = PREFIX + EDIT;

    protected final String TO_REMOVE = PREFIX + REMOVE;

    /**
     * 根据实践，@GetMappding可以被继承，那么子类只需要指定权限即可
     * 又由于可以在方法体种判断权限，故子类可以免费愉快地获取到这两个方法
     */
    @GetMapping({EMPTY, INDEX})
    @Override
    public String index() {
        Subject subject = SHIRO.getSubject();
        if (subject.isPermitted(PREFIX)) {
            System.out.println(TO_INDEX);
            return TO_INDEX;
        }
        return FORWARD_UNAUTHORIZED;
    }

    @GetMapping(ADD)
    @Override
    public String add() {
        Subject subject = SHIRO.getSubject();
        if (subject.isPermitted(TO_ADD)) {
            return TO_ADD;
        }
        return FORWARD_UNAUTHORIZED;
    }
}
