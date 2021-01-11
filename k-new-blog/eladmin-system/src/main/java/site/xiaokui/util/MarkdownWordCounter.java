package site.xiaokui.util;

import site.xiaokui.domain.WordCounter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 * markdown文件字数统计工具
 * 在网上实在找不到好的统计工具/统计代码/统计方式，干脆自己动手造个轮子
 * 统计规则如下：
 * 1.要求文件为.md文件
 * 2.只统计汉字、英文字母、数字
 * 3.标点符号、空格、换行均不统计
 * 4.一汉字为一有效字符、一连串英文字符为一有效字符、一连串数字为一有效字符
 * 5.log4j也为一有效字符
 * <p>
 * 各种字符的unicode编码的范围：
 * 　 汉字：[0x4e00,0x9fa5] 或  十进制[19968,40869]
 * 数字：[0x30,0x39] 或   十进制[48, 57]
 * 小写字母：[0x61,0x7a] 或  十进制[97, 122]
 * 大写字母：[0x41,0x5a] 或  十进制[65, 90]
 *
 * @author HK
 * @date 2020-12-11 17:25
 */
public class MarkdownWordCounter {

    public static boolean isChinese(char c) {
        return c >= 0x4E00 && c <= 0x9FA5;
    }

    public static boolean isEnglish(char c) {
        return (c >= 0x61 && c <= 0x7a) || (c >= 0x41 && c <= 0x5a);
    }

    public static boolean isNumber(char c) {
        return (c >= 0x30 && c <= 0x39);
    }

    public static boolean isWhiteStr(char c) {
        return Character.isWhitespace(c) || Character.isSpaceChar(c) || c == '\ufeff' || c == '\u202a';
    }

    public static WordCounter calcWordCount(String line) {
        int chineseCount = 0, englishCount = 0, numberCount = 0, otherCount = 0;
        boolean preIsEnglish = false, preIsNumber = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (isChinese(c)) {
                if (preIsEnglish) {
                    englishCount++;
                    preIsEnglish = false;
                }
                if (preIsNumber) {
                    numberCount++;
                    preIsNumber = false;
                }
                chineseCount++;
            } else if (isEnglish(c)) {
                preIsEnglish = true;
            } else if (isNumber(c)) {
                preIsNumber = true;
            } else {
                if (preIsEnglish) {
                    englishCount++;
                    preIsEnglish = false;
                } else if (preIsNumber) {
                    numberCount++;
                    preIsNumber = false;
                }
                otherCount++;
            }
        }
        // WordCounter(chineseCount=8056, englishCount=21128, numberCount=3961, otherCount=55921)
        // WordCounter(chineseCount=8056, englishCount=21228, numberCount=4056, otherCount=55921)
        return new WordCounter(chineseCount, englishCount, numberCount, otherCount);
    }

    public static WordCounter readFile(File mdFile) {
        WordCounter total = new WordCounter(0, 0, 0, 0);
        try {
            InputStreamReader inputFileReader = new InputStreamReader(new FileInputStream(mdFile), "UTF-8");
            BufferedReader reader = new BufferedReader(inputFileReader);
            String str;
            while ((str = reader.readLine()) != null) {
                WordCounter temp = MarkdownWordCounter.calcWordCount(str);
                total.chineseCount += temp.chineseCount;
                total.englishCount += temp.englishCount;
                total.numberCount += temp.numberCount;
                total.otherCount += temp.otherCount;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return total;
    }

    public static void main(String[] args) {
        String str = "```abcd 123 我是一个中国工人aabb12Cdd332ac2a3f,hh 是";
        String ss = "一二三四五六七八九十。\n" +
                "\n" +
                "1234567890。\n" +
                "\n" +
                "abcdefghij.\n" +
                "\n" +
                "- abcdefg。\n" +
                "- 我们都是好孩子啊啊啊。\n" +
                "\n" +
                "```java\n" +
                "public static void main() {\n" +
                "\tSystem.out.println(\"我们都是好孩子啊啊啊\");\n" +
                "\tSystem.out.println(\"abcdefg\");\n" +
                "}\n" +
                "```\n" +
                "\n" +
                "有: 10 + 1 + 1 + 1 + 10 = 23。\n" +
                "\n" +
                "有: 1 + 1 + 1 + 1 + 3 + 10 + 3 + 10 = 30。\n" +
                "\n" +
                "共 23 + 30 + 7 + 10 + 本段字数 = 67。";
        System.out.println(calcWordCount(str));
        System.out.println(calcWordCount(ss));
        File file = new File("/xiaokui/product/upload/1/$md/Spring源码：Spring MVC之Web请求处理流程-13-20200402.md");
        System.out.println(readFile(file));
    }
}
