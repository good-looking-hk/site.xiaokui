package site.xiaokui.spring.test.bean;

/**
 * @author HK
 * @date 2019-09-01 11:38
 */
public interface Human {

    String getName();

    int getAge();

    default void printInfo() {
        System.out.println(this);
    }

    default String getInfo() {
        return "我叫"+ getName() + "，今年" + getAge() + "岁";
    }
}
