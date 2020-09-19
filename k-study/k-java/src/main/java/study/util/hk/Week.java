package study.util.hk;

/**
 * @author HK
 * @date 2017/10/26 8:28
 */
public enum Week {

    MONDAY("星期一", 1),

    TUESDAY("星期二", 2),

    WEDNESDAY("星期三", 3),

    THURSDAY("星期四", 4),

    FRIDAY("星期五", 5),

    SATURDAY("星期六", 6),

    SUNDAY("星期日", 7);

    private String name;

    private int value;

    Week(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public static String getValue(int week) {
        for(Week temp : values()) {
            if (temp.getValue() == week) {
                return temp.getName();
            }
        }
        return "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

}
