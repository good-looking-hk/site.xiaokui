package site.xiaokui.landlords.client.print;

import site.xiaokui.landlords.client.ClientContainer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author HK
 * @date 2020-08-04 16:04
 */
public class SimpleWriter {

    private final static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public static String write(String tip) {
        System.out.println();
        System.out.print("[" + ClientContainer.clientId + "-" + ClientContainer.nickname + "@" + tip + "]$ ");
        try {
            return reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println();
        }
        return null;
    }
}
