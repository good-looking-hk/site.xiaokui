package site.xiaokui.landlords.common.entity;

import site.xiaokui.landlords.common.entity.enums.PokerLevel;
import site.xiaokui.landlords.common.entity.enums.PokerType;

/**
 * @author HK
 * @date 2020-08-03 17:11
 */
public class Poker{

    private PokerLevel level;

    private PokerType type;

    public Poker() {
    }

    public Poker(PokerLevel level, PokerType type) {
        this.level = level;
        this.type = type;
    }

    public PokerLevel getLevel() {
        return level;
    }

    public void setLevel(PokerLevel level) {
        this.level = level;
    }

    public PokerType getType() {
        return type;
    }

    public void setType(PokerType type) {
        this.type = type;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((level == null) ? 0 : level.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Poker other = (Poker) obj;
        if (level != other.level) {
            return false;
        }
        if (type != other.type) {
            return false;
        }
        return true;
    }

}
