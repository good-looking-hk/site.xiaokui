package site.xiaokui.landlords.common.print;

/**
 * @author HK
 * @date 2020-08-06 14:24
 */
public class FormatPrinter {

    private FormatPrinter() {
    }

    public static void printNotice(String format, Object... args) {
        System.out.printf(format, args);
    }
}

