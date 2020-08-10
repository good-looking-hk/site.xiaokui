package site.xiaokui.landlords.common.entity;

import site.xiaokui.landlords.common.entity.enums.SellType;
import site.xiaokui.landlords.common.helper.PokerHelper;

import java.util.Arrays;
import java.util.List;

/**
 * 出牌实体类
 * @author HK
 * @date 2020-08-06 10:38
 */
public class PokerSell {

    /**
     * 分数
     */
    private int score;

    /**
     * 出牌类型，对子、三带一、炸弹
     */
    private SellType sellType;

    /**
     * 出的手牌
     */
    private List<Poker> sellPokers;

    /**
     * 分数级别
     */
    private int coreLevel;

    public PokerSell(SellType sellType, List<Poker> sellPokers, int coreLevel) {
        this.score = PokerHelper.parseScore(sellType, coreLevel);
        this.sellType = sellType;
        this.sellPokers = sellPokers;
        this.coreLevel = coreLevel;
    }

    public final int getCoreLevel() {
        return coreLevel;
    }

    public final void setCoreLevel(int coreLevel) {
        this.coreLevel = coreLevel;
    }

    public final int getScore() {
        return score;
    }

    public final void setScore(int score) {
        this.score = score;
    }

    public final SellType getSellType() {
        return sellType;
    }

    public final void setSellType(SellType sellType) {
        this.sellType = sellType;
    }

    public final List<Poker> getSellPokers() {
        return sellPokers;
    }

    public final void setSellPokers(List<Poker> sellPokers) {
        this.sellPokers = sellPokers;
    }

    @Override
    public String toString() {
        return sellType.name() + "\t| " + score + "分\t|" + sellPokers.toString();
    }
}
