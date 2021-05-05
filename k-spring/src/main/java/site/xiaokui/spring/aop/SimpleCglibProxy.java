package site.xiaokui.spring.aop;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.InvocationHandler;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import site.xiaokui.spring.core.util.Assert;

import java.lang.reflect.Method;

/**
 * @author HK
 * @date 2020-04-11 15:23
 */
public class SimpleCglibProxy implements IProxy {

    private Object object;

    @Override
    public IProxy setObject(Object object) {
        Assert.notNull(object);
        this.object = object;
        return this;
    }

    @Override
    public Object getProxy() {
        Assert.notNull(object);
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(this.object.getClass());
        enhancer.setCallback(new MethodInterceptor() {
            @Override
            public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                SimpleCglibProxy.this.preInvoke();
                Object result = proxy.invokeSuper(obj, args);
                SimpleCglibProxy.this.postInvoke();
                return result;
            }
        });
        return enhancer.create();
    }

    @Override
    public IProxy newProxy() {
        return new SimpleCglibProxy();
    }

    @Override
    public void preInvoke() {

    }

    @Override
    public void postInvoke() {

    }
}
