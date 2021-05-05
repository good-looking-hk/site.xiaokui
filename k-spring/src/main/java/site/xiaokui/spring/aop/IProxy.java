package site.xiaokui.spring.aop;

/**
 * @author HK
 * @date 2019-09-01 15:12
 */
public interface IProxy {

    IProxy setObject(Object obj);

    Object getProxy();

    IProxy newProxy();

    default void preInvoke(){};

    default void postInvoke(){};
}
