package main;

import java.io.IOException;

/**
 * @author HK
 * @date 2020-10-02 17:43
 */
public class TestBash {

    public static void main(String[] args) throws IOException, InterruptedException {
        String cmd = "echo $USER";
        Process process = Runtime.getRuntime().exec(cmd);
        int code = process.waitFor();
        System.out.println(code);

        Thread thread = new Thread();
    }
}
