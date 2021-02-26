package me.zhengjie.utils;

import javax.servlet.http.HttpServletRequest;

/**
 * @author HK
 * @date 2021-02-26 14:00
 */
public class IpUtil {

    public static String getIp() {
        HttpServletRequest request = RequestHolder.getHttpServletRequest();
        return StringUtils.getIp(request);
    }
}
