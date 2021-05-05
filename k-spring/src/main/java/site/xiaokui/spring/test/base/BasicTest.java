package site.xiaokui.spring.test.base;

import site.xiaokui.spring.core.util.LogUtil;

import java.lang.reflect.Method;
import java.util.*;

/**
 * 测试基类
 * 子类必须遵循特定命名格式，如test0，test1
 * 调用时，将根据test后面的数值进行精确调用
 * @author HK
 * @date 2019-06-17 20:30
 */
public class BasicTest {

    protected LogUtil log = LogUtil.getLogger(this.getClass());

    private LinkedList<Method> list = new LinkedList<>();

    public BasicTest() {
        Method[]  methods = this.getClass().getDeclaredMethods();
        for (Method m : methods) {
            if (m.getReturnType() == void.class && m.getName().contains("test") && m.getName().length() > 4) {
                this.list.add(m);
            }
        }
    }

    public void callLastTestMethod() {
        this.list.sort((m1, m2) -> {
            Integer one = Integer.valueOf(m1.getName().substring(4));
            Integer two = Integer.valueOf(m2.getName().substring(4));
            if (one > two) {
                return 1;
            }
            return one.equals(two) ? 0 : -1;
        });
        Method m = list.getLast();
        log.debug("调用最后一个test方法" + m.getName() + ",第一个test方法是" + list.getFirst().getName());
        try {
            m.invoke(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void callAllTest() {
        callAllTestMethodByOrderByAsc(true);
    }

    public void callAllTestMethodByOrderByAsc(boolean asc) {
        this.list.sort(new Comparator<Method>() {
            @Override
            public int compare(Method o1, Method o2) {
                Integer one = Integer.valueOf(o1.getName().substring(4));
                Integer two = Integer.valueOf(o2.getName().substring(4));
                if (one > two) {
                    return 1;
                }
                return one < two ? -1 : 0;
            }
        });
        if (asc) {
            log.info("从小到大(test0 ~ test10)，依次调用方法");
        } else {
            log.info("从大到小(test10 ~ test0)，依次调用方法");
            Collections.reverse(list);
        }
        try {
            for (Method m : list) {
                log.info("【【【测试方法" + m.getName() + "调用开始");
                m.invoke(this);
                log.info("测试方法" + m.getName() + "调用结束】】】\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
