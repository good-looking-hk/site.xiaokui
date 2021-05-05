package site.xiaokui.spring.test.util;

import site.xiaokui.spring.core.util.Assert;
import site.xiaokui.spring.test.base.BasicTest;

import java.util.regex.Pattern;

/**
 * @author HK
 * @date 2020-04-02 13:55
 */
public class Regex extends BasicTest {

    public void test0() {
        String url = "/url";
        boolean result = Pattern.compile(url).matcher(url).matches();
        Assert.isTrue(result);

        result = Pattern.compile("/*").matcher(url).matches();
        Assert.isTrue(result);
    }

    public static void main(String[] args) {
        new Regex().callAllTest();
    }



}
