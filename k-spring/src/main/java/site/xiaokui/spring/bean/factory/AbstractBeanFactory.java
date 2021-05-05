package site.xiaokui.spring.bean.factory;

import site.xiaokui.spring.bean.BeanFactory;
import site.xiaokui.spring.bean.SingletonFactory;
import site.xiaokui.spring.bean.config.AnnotationBeanProcessor;
import site.xiaokui.spring.bean.config.BeanProcessor;
import site.xiaokui.spring.core.util.Assert;
import site.xiaokui.spring.core.util.LogUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author HK
 * @date 2019-06-12 22:57
 */
public class AbstractBeanFactory implements BeanFactory, SingletonFactory {

    protected LogUtil log = LogUtil.getLogger(this.getClass());

    /**
     * 保存简单的String-Object关系
     */
    private final ConcurrentHashMap<String, Object> nameMap = new ConcurrentHashMap<>(16);

    /**
     * 保存相同类型的Class-Object对象，注意这里的typeMap中的对象与nameMap中对象是一致的
     */
    private final ConcurrentHashMap<Class, List<Object>> typeMap = new ConcurrentHashMap<>(16);

    /**
     * 保存尚未初始化的Class对象
     */
    private final ConcurrentHashMap<String, Class> registerClassMap = new ConcurrentHashMap<>(8);

    private static final HashSet<Class> EXCLUDE_SET = new HashSet<>(10);

    private final List<BeanProcessor> beanProcessors = new ArrayList<>(4);

    static {
        EXCLUDE_SET.add(Byte.class);
        EXCLUDE_SET.add(Character.class);
        EXCLUDE_SET.add(Short.class);
        EXCLUDE_SET.add(Integer.class);
        EXCLUDE_SET.add(Long.class);
        EXCLUDE_SET.add(Float.class);
        EXCLUDE_SET.add(Double.class);
        EXCLUDE_SET.add(String.class);
    }

    public AbstractBeanFactory() {
        beanProcessors.add(new AnnotationBeanProcessor(this));
    }

    @Override
    public void addBean(Object obj) {
        addBean(obj.getClass().getSimpleName(), obj);
    }

    @Override
    public void addBean(String name, Object obj) {
        Assert.notNull(name, obj);
        checkType(obj);
        nameMap.put(name, obj);
        List<Object> types = typeMap.get(obj.getClass());
        if (types != null) {
            types.add(obj);
        } else {
            types = new ArrayList<>();
            types.add(obj);
            typeMap.put(obj.getClass(), types);
        }
    }

    @Override
    public void registerBeanDefinition(Class cls) {
        registerClassMap.put(cls.getCanonicalName(), cls);
    }

    public void registerBeanDefinition(String name, Class cls) {
        registerClassMap.put(name, cls);
    }

    @Override
    public void remove(String name) {
        nameMap.remove(name);
    }

    @Override
    public Object getBean(String name) {
        // 已经包含了对typeMap的搜寻
        Object obj = nameMap.get(name);
        if (obj == null) {
            // Class是否尚未初始化
            obj = registerClassMap.get(name);
            if (obj == null) {
                // 找不到则返回null
                return null;
            }
        }
        return processObj(name, obj);
    }

    @Override
    public Object getBean(Class cls) {
        List<Object> types = typeMap.get(cls);
        if (types == null) {
            Class temp = this.registerClassMap.get(cls.getCanonicalName());
            if (temp == null) {
                throw new RuntimeException("找不到任何有关于" + cls + "的bean信息");
            }
            return processObj(cls.getCanonicalName(), temp);
        }
        if (types.size() == 1) {
            return types.get(0);
        } else {
            throw new RuntimeException("找到一个以上的相同类型的bean，类型为" + cls + "，个数为" + types.size());
        }
    }

    protected Object processObj(String name, Object obj) {
        // 应用处理器
        synchronized (this.beanProcessors) {
            if (this.beanProcessors.size() != 0) {
                for (BeanProcessor processor : beanProcessors) {
                    log.trace("对对象" + obj + "应用处理器" + processor);
                    processor.applyBeanProcessor(name, obj);
                }
                return nameMap.get(name);
            }
        }
        return obj;
    }

    private void checkType(Object obj) {
        if (EXCLUDE_SET.contains(obj.getClass())) {
            throw new RuntimeException("基本类型不能作为bean对象：" + obj);
        }
    }

    public void addBeanProcessor(BeanProcessor beanProcessor) {
        Assert.notNull(beanProcessor);
        this.beanProcessors.add(beanProcessor);
    }

    public ConcurrentHashMap<String, Object> getNameMap() {
        return this.nameMap;
    }

    public ConcurrentHashMap<String, Class> getRegisterClassMap() {
        return registerClassMap;
    }

}
