package site.xiaokui.landlords.common.robot;

import site.xiaokui.landlords.common.entity.Poker;
import site.xiaokui.landlords.common.entity.PokerSell;
import site.xiaokui.landlords.common.entity.enums.SellType;
import site.xiaokui.landlords.common.helper.PokerHelper;

import java.util.List;
import java.util.Random;

/**
 * @author HK
 * @date 2020-08-06 13:26
 */
public class SimpleRobotDecisionMakers extends AbstractRobotDecisionMakers{

    private static Random random = new Random();

    /**
     * 怎么出牌
     */
    @Override
    public PokerSell howToPlayPokers(PokerSell lastPokerSell, List<Poker> myPokers) {

        if(lastPokerSell != null && lastPokerSell.getSellType() == SellType.KING_BOMB) {
            return null;
        }

        List<PokerSell> sells = PokerHelper.parsePokerSells(myPokers);
        if(lastPokerSell == null) {
            return sells.get(random.nextInt(sells.size()));
        }

        for(PokerSell sell: sells) {
            if(sell.getSellType() == lastPokerSell.getSellType()) {
                if(sell.getScore() > lastPokerSell.getScore() && sell.getSellPokers().size() == lastPokerSell.getSellPokers().size()) {
                    return sell;
                }
            }
        }
        if(lastPokerSell.getSellType() != SellType.BOMB) {
            for(PokerSell sell: sells) {
                if(sell.getSellType() == SellType.BOMB) {
                    return sell;
                }
            }
        }
        for(PokerSell sell: sells) {
            if(sell.getSellType() == SellType.KING_BOMB) {
                return sell;
            }
        }
        return null;
    }

    /**
     * 是否叫地主
     * @param leftPokers
     * @param rightPokers
     * @param myPokers
     * @return
     */
    @Override
    public boolean howToChooseLandlord(List<Poker> leftPokers, List<Poker> rightPokers, List<Poker> myPokers) {
        List<PokerSell> leftSells = PokerHelper.parsePokerSells(leftPokers);
        List<PokerSell> mySells = PokerHelper.parsePokerSells(myPokers);
        List<PokerSell> rightSells = PokerHelper.parsePokerSells(rightPokers);
        return mySells.size() > leftSells.size() && mySells.size() > rightSells.size();
    }
}
