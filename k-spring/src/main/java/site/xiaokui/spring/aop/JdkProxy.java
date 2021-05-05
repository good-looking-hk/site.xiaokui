package site.xiaokui.spring.aop;

import site.xiaokui.spring.core.util.Assert;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * JDK代理类，代理机制为公共接口的实现子类，而为了更好的利用接口代理的特性，一般开启-parameters编译选项，以保证形参名保留在class文件中
 * 而CGLIG代理机制为代理类的派生子类
 * @author HK
 * @date 2019-06-17 19:49
 */
public abstract class JdkProxy implements InvocationHandler, IProxy {

    private Object object;

    private final Method[] objectMethods = Object.class.getMethods();

    public JdkProxy() {
    }

    public JdkProxy(Object obj) {
        setObject(obj);
    }

    @Override
    public IProxy setObject(Object object) {
        Assert.notNull(object);
        Class[] fathers = object.getClass().getInterfaces();
        if (fathers.length == 0) {
            throw new RuntimeException("JDK代理类必须实现某个接口，类型为：" + object.getClass());
        }
        this.object = object;
        return this;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 忽略Object类方法调用
        for (Method m : this.objectMethods) {
            if (m.getName().equals(method.getName())) {
                return method.invoke(object, args);
            }
        }
        preInvoke();
        method.setAccessible(true);
        Object result = method.invoke(object, args);
        postInvoke();
        return result;
    }

    @Override
    public Object getProxy() {
        return Proxy.newProxyInstance(object.getClass().getClassLoader(), object.getClass().getInterfaces(), this);
    }
}
