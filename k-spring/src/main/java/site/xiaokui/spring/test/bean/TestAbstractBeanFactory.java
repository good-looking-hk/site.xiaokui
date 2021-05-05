package site.xiaokui.spring.test.bean;

import site.xiaokui.spring.aop.SimpleJdkProxy;
import site.xiaokui.spring.bean.factory.AbstractBeanFactory;
import site.xiaokui.spring.core.util.Assert;
import site.xiaokui.spring.core.util.LogType;
import site.xiaokui.spring.core.util.LogUtil;
import site.xiaokui.spring.test.base.BasicTest;


/**
 * 测试Spring中的AOP和IOC模块功能
 *
 * @author HK
 * @date 2019-06-12 22:58
 */
public class TestAbstractBeanFactory extends BasicTest {

    private LogUtil log = LogUtil.getLogger(this.getClass());

    /**
     * 测试动态代理，AOP
     */
    public void test0() {
        Teacher teacher = new Teacher("老师1", 30);
        Object o = new SimpleJdkProxy().setObject(teacher).getProxy();
        Assert.isFalse(o.getClass() == Teacher.class);
        Assert.isTrue(o.getClass().getInterfaces()[0] == Human.class);
        Human human = (Human) o;
        human.printInfo();
    }

    /**
     * 1.测试IOC/DI，以及@Autowired注解在对于字段、构造方法以及set方法的注入
     * 2.测试根据类名获取
     */
    public void test1() {
        AbstractBeanFactory factory = new AbstractBeanFactory();
        factory.addBean("englishTeacher", new Teacher("英语老师", 34));
        factory.addBean("chineseTeacher", new Teacher("语文老师", 33));
        factory.addBean("mathTeacher", new Teacher("数学老师", 32));
        factory.addBean("student", new Student("学生2", 22));
        factory.registerBeanDefinition(Room.class);
        Room room = (Room) factory.getBean(Room.class);
        Assert.isTrue(room != null);
        room.showTeacher();
    }

    /**
     * 测试注入内部类
     */
    public void test2() {
        AbstractBeanFactory factory = new AbstractBeanFactory();
        factory.registerBeanDefinition(Room.InnerRoom.class);
        factory.addBean("student", new Student("学生2", 22));
        Room.InnerRoom room = (Room.InnerRoom) factory.getBean(Room.InnerRoom.class);
        Assert.isTrue(room != null);
    }

    public static void main(String[] args) {
        LogUtil.setMode(LogType.DEBUG);
        new TestAbstractBeanFactory().callAllTest();
    }
}
