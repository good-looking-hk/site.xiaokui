package site.xiaokui.landlords.common.robot;

import site.xiaokui.landlords.common.entity.Poker;
import site.xiaokui.landlords.common.entity.PokerSell;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author HK
 * @date 2020-08-06 13:25
 */
public class RobotDecisionMakers {

    private static Map<Integer, AbstractRobotDecisionMakers> decisionMakersMap = new HashMap<Integer, AbstractRobotDecisionMakers>() {
        private static final long serialVersionUID = 8541568961784067309L;
        {
            put(1, new SimpleRobotDecisionMakers());
        }
    };

    public static boolean contains(int difficultyCoefficient) {
        return decisionMakersMap.containsKey(difficultyCoefficient);
    }

    public static PokerSell howToPlayPokers(int difficultyCoefficient, PokerSell lastPokerSell, List<Poker> myPokers){
        return decisionMakersMap.get(difficultyCoefficient).howToPlayPokers(lastPokerSell, myPokers);
    }

    public static boolean howToChooseLandlord(int difficultyCoefficient, List<Poker> leftPokers, List<Poker> rightPokers, List<Poker> myPokers) {
        return decisionMakersMap.get(difficultyCoefficient).howToChooseLandlord(leftPokers, rightPokers, myPokers);
    }
}
