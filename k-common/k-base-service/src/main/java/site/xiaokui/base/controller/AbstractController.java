package site.xiaokui.base.controller;

/**
 * 通过继承这个类，子类可以拥有跟多特性，本类除了继承父类和父接口的某些属性外，还对字段和方法做了更多的补充，大多情况下，这是您的首选
 * @author HK
 * @date 2018-06-24 22:50
 */
public abstract class AbstractController extends BaseController {

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
     * 获取当前登录用户信息
     * @return 登录用户信息，没有则返回null
     */
    protected abstract Object getCurrentUser();
}
