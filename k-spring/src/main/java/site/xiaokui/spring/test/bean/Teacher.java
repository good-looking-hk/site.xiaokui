package site.xiaokui.spring.test.bean;

/**
 * @author HK
 * @date 2019-09-01 11:38
 */
public class Teacher implements Human {

    private String name;

    private int age;

    public Teacher(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getAge() {
        return this.age;
    }

    @Override
    public String toString() {
        return this.getInfo() + "，我是一名老师;";
    }
}
