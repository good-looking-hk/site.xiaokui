package site.xiaokui.landlords.common.robot;

import site.xiaokui.landlords.common.entity.Poker;
import site.xiaokui.landlords.common.entity.PokerSell;
import site.xiaokui.landlords.common.helper.PokerHelper;

import java.util.List;

/**
 * @author HK
 * @date 2020-08-06 13:26
 */
public class MediumRobotDecisionMakers extends AbstractRobotDecisionMakers {

    @Override
    public PokerSell howToPlayPokers(PokerSell lastPokerSell, List<Poker> myPokers) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean howToChooseLandlord(List<Poker> leftPokers, List<Poker> rightPokers, List<Poker> myPokers) {
        int leftScore = PokerHelper.parsePokerColligationScore(leftPokers);
        int rightScore = PokerHelper.parsePokerColligationScore(rightPokers);
        int myScore = PokerHelper.parsePokerColligationScore(myPokers);
        return myScore >= (leftScore + rightScore) / 2;
    }

}
