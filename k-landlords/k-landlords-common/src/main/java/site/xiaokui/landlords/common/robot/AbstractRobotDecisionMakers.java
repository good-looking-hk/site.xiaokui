package site.xiaokui.landlords.common.robot;

import site.xiaokui.landlords.common.entity.Poker;
import site.xiaokui.landlords.common.entity.PokerSell;

import java.util.List;

/**
 * @author HK
 * @date 2020-08-06 13:25
 */
public abstract class AbstractRobotDecisionMakers {

    /**
     * 怎样出牌
     * @param lastPokerSell 上一首牌
     * @param myPokers 当前手牌
     * @return 要出的牌，不出返回 null
     */
    public abstract PokerSell howToPlayPokers(PokerSell lastPokerSell, List<Poker> myPokers);

    /**
     * 是否叫地主
     * @param leftPokers 左手牌
     * @param rightPokers   右手牌
     * @param myPokers 自己手牌
     * @return  是否叫地主
     */
    public abstract boolean howToChooseLandlord(List<Poker> leftPokers, List<Poker> rightPokers, List<Poker> myPokers);
}
