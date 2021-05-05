package jvm;

import java.lang.String;
import java.lang.Integer;

/**
 * @author HK
 * @date 2021-05-03 15:52
 */
public class TestLoad {

    public static void main(String[] args) throws Exception {

        ClassLoader classLoader;
        TestLoad.class.getClassLoader().loadClass("aa");

        Thread a = new Thread();
        a.setContextClassLoader(null);

        Integer integer = new Integer(1);
//        String str = new String(1);
    }
}
