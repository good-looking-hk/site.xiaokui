package site.xiaokui.spring.bean.config;

import site.xiaokui.spring.aop.IProxy;
import site.xiaokui.spring.aop.SimpleJdkProxy;
import site.xiaokui.spring.bean.factory.AbstractBeanFactory;
import site.xiaokui.spring.bean.annotation.Autowired;
import site.xiaokui.spring.core.util.Assert;
import site.xiaokui.spring.core.util.LogUtil;
import site.xiaokui.spring.core.util.StringUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author HK
 * @date 2019-06-15 10:53
 */
public class AnnotationBeanProcessor implements AfterBeanInitProcessor {

    private LogUtil log = LogUtil.getLogger(this.getClass());

    private AbstractBeanFactory factory;

    private IProxy proxy = new SimpleJdkProxy();

    public AnnotationBeanProcessor(AbstractBeanFactory factory) {
        Assert.notNull(factory);
        this.factory = factory;
    }

    @Override
    public void applyBeanProcessor(String beanName, Object obj) {
        Class cls;
        // 如果传入对象是类，则说明该类需要进行初始化
        if (obj instanceof Class) {
            cls = (Class) obj;
        } else {
            // 否则直接返回新的代理对象
            this.proxy = this.proxy.newProxy();
            obj = this.proxy.setObject(obj).getProxy();
            this.factory.getNameMap().put(beanName, obj);
            return;
        }
        try {
            Object temp = handleConstructor(beanName, cls);
            handleFiled(temp, cls);
            handleMethod(temp, cls);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Object handleConstructor(String beanName, Class cls) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor[] cons = cls.getDeclaredConstructors();
        log.debug("对于类型" + cls + "找到" + cons.length + "个构造方法，开始初始化");
        // 构造器数量肯定不为零
        for (Constructor c : cons) {
            Annotation annotation = c.getAnnotation(Autowired.class);
            if (annotation == null) {
                continue;
            }
            Parameter[] requiredParams = c.getParameters();
            Object[] autowireParams = new Object[requiredParams.length];
            log.trace("在构造器" + c + "中找到" + requiredParams.length + "个参数");
            if (requiredParams.length != 0) {
                for (int i = 0; i < requiredParams.length; i++) {
                    Parameter p = requiredParams[i];
                    String pName = p.getName();
                    log.trace("第" + i + "个参数名为" + pName + "，类型为" + p.getType());
                    Object o = factory.getBean(pName);
                    if (o == null) {
                        Class clszz = p.getType();
                        o = factory.getBean(clszz);
                        if (o == null) {
                            throw new RuntimeException("对于构造器" + c + "找不到指定参数bean，名称为" + pName + ",类型为" + p.getType());
                        }
                    }
                    autowireParams[i] = o;
                }
                try {
                    c.setAccessible(true);
                    Object obj = c.newInstance(autowireParams);
                    beanName = StringUtil.lowerFirst(beanName);
                    this.factory.getNameMap().put(beanName, obj);
                    log.debug("注入构造方法成功");
                    return obj;
                } catch (Exception e) {
                    log.error("构造注入失败，构造器为" + c + "，参数个数为" + requiredParams.length);
                    for (int i = 0; i < autowireParams.length; i++) {
                        log.error("第" + i + "个参数名为" + requiredParams[i].getName() + "，要求类型为" + requiredParams[i].getType() + "，实际类型为" + autowireParams[i].getClass());
                    }
                    e.printStackTrace();
                }
            }
        }
        // 这种情况发生在对象声明了一个public构造方法时
        if (cons.length == 1 && cons[0].getParameters().length == 0) {
            Constructor c = cons[0];
            c.setAccessible(true);
            Object obj = c.newInstance();
            beanName = StringUtil.lowerFirst(beanName);
            this.factory.getNameMap().put(beanName, obj);
            log.debug("注入构造方法成功");
            return obj;
        } else {
            throw new RuntimeException("对于" + cls + "找不到需要注入的构造方法");
        }
    }

    private void handleFiled(Object obj, Class cls) {
        // 遍历所有字段，包含私有和父类继承的
        Field[] own = cls.getDeclaredFields();
        Field[] parent = cls.getFields();
        Set<Field> set = new HashSet<>();
        set.addAll(Arrays.asList(own));
        set.addAll(Arrays.asList(parent));
        Iterator<Field> it = set.iterator();
        while (it.hasNext()) {
            Field field = it.next();
            Annotation annotation = field.getAnnotation(Autowired.class);
            if (annotation == null) {
                it.remove();
            }
        }
        log.debug("对于类型" + cls + "找到" + set.size() + "个字段");
        for (Field field : set) {
            Object v = factory.getBean(field.getName());
            if (v == null) {
                Class clszz = field.getType();
                v = factory.getBean(clszz);
                if (v == null) {
                    throw new RuntimeException("对于字段" + field + "找不到指定bean，名称为" + field.getName() + ",类型为" + field.getType());
                }
            }
            try {
                field.setAccessible(true);
                field.set(obj, v);
                log.debug("注入字段成功");
                return;
            } catch (Exception e) {
                log.error("注入字段失败，JDK代理要求目标类和注入类必须为同一接口类型，类为" + cls + "，字段为" + field);
                e.printStackTrace();
            }
        }
    }

    private void handleMethod(Object obj, Class cls) {
        // 遍历所有字段，包含私有和父类继承的
        Method[] own = cls.getDeclaredMethods();
        Method[] parent = cls.getMethods();
        Set<Method> set = new HashSet<>();
        set.addAll(Arrays.asList(own));
        set.addAll(Arrays.asList(parent));
        Iterator<Method> it = set.iterator();
        while (it.hasNext()) {
            Method method = it.next();
            Annotation annotation = method.getAnnotation(Autowired.class);
            if (annotation == null) {
                it.remove();
            }
        }
        log.debug("对于类型" + cls + "找到" + set.size() + "个方法");
        for (Method method : set) {
            // 如setName，则返回name
            String name = extractSetMethodName(method.getName());
            Object v = factory.getBean(name);
            if (v == null) {
                throw new RuntimeException("对于方法" + method.getName() + "找不到指定bean，名称为" + name);
            } else {
                Parameter[] requiredParams = method.getParameters();
                Object[] autowireParams = new Object[requiredParams.length];
                log.trace("在方法" + method + "中找到" + requiredParams.length + "个参数");
                if (requiredParams.length != 0) {
                    for (int i = 0; i < requiredParams.length; i++) {
                        Parameter p = requiredParams[i];
                        String pName = p.getName();
                        log.trace("第" + i + "个参数名为" + pName + "，类型为" + p.getType());
                        Object o = factory.getBean(pName);
                        if (o == null) {
                            Class clszz = p.getType();
                            o = factory.getBean(clszz);
                            if (o == null) {
                                throw new RuntimeException("对于构造方法" + method + "找不到指定参数bean，名称为" + pName + ",类型为" + p.getType());
                            }
                        }
                        autowireParams[i] = o;
                    }
                    try {
                        method.setAccessible(true);
                        method.invoke(obj, autowireParams);
                        log.debug("注入set方法成功");
                    } catch (Exception e) {
                        log.error("set注入失败，set方法为" + method + "，参数个数为" + requiredParams.length);
                        for (int i = 0; i < autowireParams.length; i++) {
                            log.error("第" + i + "个参数名为" + requiredParams[i].getName() + "，要求类型为" + requiredParams[i].getType() + "，实际类型为" + autowireParams[i].getClass());
                        }
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private String extractSetMethodName(String methodName) {
        if (methodName == null || !methodName.startsWith("set") || methodName.length() < 4) {
            throw new RuntimeException("无法识别的set方法:" + methodName);
        }
        int index = methodName.indexOf("set");
        String h = (methodName.substring(index + "set".length()).charAt(0) + "").toLowerCase();
        return h + methodName.substring(index + + "set".length() + 1);
    }
}
