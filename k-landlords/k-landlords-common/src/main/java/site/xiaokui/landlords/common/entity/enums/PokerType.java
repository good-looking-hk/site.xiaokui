package site.xiaokui.landlords.common.entity.enums;

/**
 * @author HK
 * @date 2020-08-03 17:12
 */
public enum  PokerType {

    BLANK("  "),

    DIAMOND("♦"),

    CLUB("♣"),

    SPADE("♠"),

    HEART("♥");

    private String name;

    private PokerType(String name) {
        this.name = name;
    }

    public final String getName() {
        return name;
    }
}
