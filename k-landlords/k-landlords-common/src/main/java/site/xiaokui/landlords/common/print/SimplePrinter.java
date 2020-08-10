package site.xiaokui.landlords.common.print;

import cn.hutool.core.date.DateUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import site.xiaokui.landlords.common.entity.Poker;
import site.xiaokui.landlords.common.entity.enums.PokerLevel;
import site.xiaokui.landlords.common.entity.enums.PokerType;
import site.xiaokui.landlords.common.helper.PokerHelper;
import site.xiaokui.landlords.common.util.ChannelUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author HK
 * @date 2020-08-03 16:06
 */
public class SimplePrinter {

    private static final Log log = LogFactory.get(SimplePrinter.class);

    public static void printPokers(List<Poker> pokers) {
        System.out.println(PokerHelper.printPoker(pokers));
    }

    public static void printNotice(String msg) {
        System.out.println(msg);
    }

    public static void serverLog(String msg) {
//        System.out.println(DateUtil.now() + " -> " + msg);
        if (log.isInfoEnabled()) {
            log.info("服务器日志:" + msg);
        }
    }

    public static void main(String[] args) {
        List<Poker> list = new ArrayList<>(10);
        list.add(new Poker(PokerLevel.LEVEL_9, PokerType.HEART));
        list.add(new Poker(PokerLevel.LEVEL_8, PokerType.DIAMOND));
        list.add(new Poker(PokerLevel.LEVEL_7, PokerType.HEART));
        list.add(new Poker(PokerLevel.LEVEL_6, PokerType.CLUB));
        printPokers(list);
    }
}
