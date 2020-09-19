package base;

/**
 * 第一句必须是this！！！
 * @author HK
 * @date 2020-09-09 17:29
 */
public class Son extends Father {

    public Son() {
//        this(1, "2");
//        String i = getSonInfo();
//        this(2, getSonInfo());
        getSonInfo();
    }

    public Son(int code, String msg) {
        super();
        getSonInfo();
    }

    public String getSonInfo() {
        return "sonInfo";
    }
}
