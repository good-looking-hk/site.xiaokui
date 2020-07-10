package test.extend;

/**
 * @author HK
 * @date 2018-06-25 19:09
 */
public abstract class AbstractFather {

    public AbstractFather() {
        System.out.println("3.AbstractFather初始化");
    }

    public String str = setStr();

    protected abstract String setStr();
}
