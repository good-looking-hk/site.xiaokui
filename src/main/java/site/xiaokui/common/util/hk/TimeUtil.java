package site.xiaokui.common.util.hk;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * @author HK
 * @date 2017/4/3
 * 本类默认规范日期显示格式为:2011-01-01 11:11:11
 */
public class TimeUtil {

    /**
     * 默认的具体日期时间显示格式
     */
    private static final DateTimeFormatter DTF_DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 默认的日期显示格式
     */
    private static final DateTimeFormatter DTF_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 默认的时间显示格式
     */
    private static final DateTimeFormatter DTF_TIME = DateTimeFormatter.ofPattern("HH:mm:ss");

    /**
     * 将现在的时间转换成2011-11-11形式的格式
     */
    public static String getCurrentDate() {
        LocalDateTime nowTime = LocalDateTime.now();
        return DTF_DATE.format(nowTime);
    }

    /**
     * 将现在的时间转换成2011-11-11 11:11:11形式的格式
     */
    public static String getCurrentDateTime() {
        LocalDateTime nowTime = LocalDateTime.now();
        return DTF_DATE_TIME.format(nowTime);
    }

    public static String getCurrentTime() {
        LocalDateTime nowTime = LocalDateTime.now();
        return DTF_TIME.format(nowTime);
    }

    /**
     * 将long型的时间值转换成2011-11-11 11:11:11形式的格式
     */
    public static String parseLongTime(long longTime) {
        Instant instant = Instant.ofEpochMilli(longTime);
        LocalDateTime nowTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        return DTF_DATE_TIME.format(nowTime);
    }

    /**
     * 将字符串型日期转换为long型
     */
    public static long parseStringDate(String date) {
        LocalDate temp = LocalDate.parse(date, DTF_DATE);
        return temp.atStartOfDay().atZone(ZoneId.systemDefault()).toEpochSecond();
    }

    /**
     * 计算若干天数后的日期
     */
    public static String afterDays(int days) {
        LocalDate date = LocalDate.now().plusDays(days);
        return DTF_DATE.format(date);
    }

    /**
     * 返回两个日期相差的天数,可以出现负数
     */
    public static int daysBetweenTwoDate(String startDate, String endDate) {
        long startTime = parseStringDate(startDate);
        long endTime = parseStringDate(endDate);
        return (int)((endTime - startTime) / 86400);
    }

    /**
     * 计算当前周数
     * @param startDate 开学日期
     */
    public static String showWeekInfo(String startDate) {
        int days = daysBetweenTwoDate(startDate, getCurrentDate());
        int week =  days / 7 + 1;
        LocalDate date = LocalDate.parse(getCurrentDate(), DTF_DATE);
        String weekName = Week.getValue(date.atStartOfDay().getDayOfWeek().getValue());
        return "当前日期为：第" + week + "周，" + weekName;
    }

    public static void main(String[] args) {
        System.out.println(getCurrentDate());
        System.out.println(getCurrentDateTime());
        System.out.println(getCurrentTime());
        System.out.println(parseLongTime(System.currentTimeMillis()));
        System.out.println(parseStringDate("2017-08-04"));
        System.out.println(afterDays(31));
        System.out.println(daysBetweenTwoDate("2017-09-04", "2017-10-25"));
        System.out.println(showWeekInfo("2017-09-04"));
    }

}
