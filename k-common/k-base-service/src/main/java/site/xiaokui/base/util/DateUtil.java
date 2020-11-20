package site.xiaokui.base.util;

import java.util.Date;

/**
 * @author HK
 * @date 2020-11-16 11:12
 */
public class DateUtil extends cn.hutool.core.date.DateUtil {

    public static Integer parseIntDate(Date date) {
        return Integer.valueOf(DateUtil.format(date, "yyyyMMdd"));
    }

    public static Integer getToday() {
        return Integer.valueOf(DateUtil.format(new Date(), "yyyyMMdd"));
    }

    public static void main(String[] args) {
        System.out.println(getToday());
    }
}
