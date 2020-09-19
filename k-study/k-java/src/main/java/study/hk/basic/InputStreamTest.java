package study.hk.basic;

import java.io.*;

/**
 * @author HK
 * @date 2018-11-15 15:10
 */
public class InputStreamTest {

    public static void main(String[] args) throws Exception {
        byte[] bytes = new byte[]{65, 66, 67, 68};
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        int i;
        System.out.println("开始读取ByteArrayInputStream--原始方法读取字节流");
        while ((i = bais.read()) != -1) {
            System.out.print((char) i + " ");
        }

        bais = new ByteArrayInputStream(bytes);
        BufferedInputStream bis = new BufferedInputStream(bais);
        System.out.println("\n开始读取BufferedInputStream--带有缓存的读取");
        while ((i = bis.read()) != -1) {
            System.out.print((char) i + " ");
        }

        bais = new ByteArrayInputStream(bytes);
        bis = new BufferedInputStream(bais);
        InputStreamReader isr = new InputStreamReader(bis);
        BufferedReader br = new BufferedReader(isr);
        String str;
        System.out.println("\n开始读取BufferedReader--带有缓存的字符流");
        while ((str = br.readLine()) != null) {
            System.out.print(str);
        }
    }
}
