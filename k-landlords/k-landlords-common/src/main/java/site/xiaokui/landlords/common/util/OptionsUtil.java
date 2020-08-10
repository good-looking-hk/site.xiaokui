package site.xiaokui.landlords.common.util;

import cn.hutool.json.JSONObject;

/**
 * @author HK
 * @date 2020-08-06 10:26
 */
public class OptionsUtil {

    public static int getOptions(String line) {
        int option = -1;
        try {
            option = Integer.parseInt(line);
        } catch (Exception e) {
            return option;
        }
        return option;
    }

    public static void main(String[] args) {
        JSONObject jsonObject = new JSONObject("1111", "111");
        System.out.println(jsonObject.get("1111"));
    }
}
