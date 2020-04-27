package site.xiaokui.common.hk.wx;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.HMac;
import cn.hutool.crypto.digest.HmacAlgorithm;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;


/**
 * @author HK
 * @date 2020-04-26 15:15
 */
public class CheckSign {

    public static void main(String[] args) {
        Map<String, Object> map = new TreeMap<>();
        map.put("appid", "wxd930ea5d5a258f4f");
        map.put("mch_id", 10000100);
        map.put("device_info", 1000);
        map.put("body", "test");
        // String randomStr = RandomUtil.randomString(5);
        // 官方示例为 ibuaiVcKdpRxkhJA
        map.put("nonce_str", "ibuaiVcKdpRxkhJA");

        StringBuilder sb = null;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (sb == null) {
                sb = new StringBuilder(entry.getKey() + "=" + entry.getValue());
            } else {
                sb.append("&").append(entry.getKey()).append("=").append(entry.getValue());
            }
        }
        String key = "192006250b4c09247ec02edce69f6a2d";
        sb.append("&key=").append(key);

        System.out.println(sb);
        // 预期md5签名 9A0A8659F005D6984697E2CA0A9CF3B7
        String sign = SecureUtil.md5(sb.toString()).toUpperCase();
        System.out.println(sign);
        // 预期hmac sha256签名 6A9AE1657590FD6257D693A078E1C3E4BB6BA4DC30B23E0EE2496E54170DACD6
        // 这里不是预期 7413C0B16EB07CCD8F78044956E41815A52E6E94BC037A17534EA867F813C5E2
        sign = SecureUtil.sha256(sb.toString()).toUpperCase();
        System.out.println(sign);

        HMac hMac = SecureUtil.hmac(HmacAlgorithm.HmacSHA256, key);
        // 得到预期值 6A9AE1657590FD6257D693A078E1C3E4BB6BA4DC30B23E0EE2496E54170DACD6
        sign = hMac.digestHex(sb.toString()).toUpperCase();
        System.out.println(sign);

         // 最后异步
        map.put("sign", sign);
    }
}
