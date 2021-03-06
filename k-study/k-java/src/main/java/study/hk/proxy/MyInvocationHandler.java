package study.hk.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author HK
 * @date 2020-04-11 10:44
 */
public class MyInvocationHandler implements InvocationHandler {
    private Object target;
    public MyInvocationHandler(Object target) {
        super();
        this.target = target;
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("==== before invoke ====");
        Object result = method.invoke(target, args);
        System.out.println("==== after invoke ====");
        return result;
    }
    public Object getProxy() {
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), target.getClass().getInterfaces(), this);
    }

    public static void main(String[] args) {
        UserServiceImpl userService = new UserServiceImpl();
        MyInvocationHandler invocationHandler = new MyInvocationHandler(userService);
        Proxy proxy = (Proxy)invocationHandler.getProxy();
        proxy.toString();
        for (Method method : proxy.getClass().getDeclaredMethods()) {
            System.out.println(method);
        }
    }
}
