package site.xiaokui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 参考链接如下，项目原型参考开涛大神的脚手架，但对技术选型/代码逻辑进行大部分重写
 * https://www.iteye.com/blog/jinnianshilongnian-2038646
 * https://developers.weixin.qq.com/doc/oplatform/Mobile_App/WeChat_Login/Development_Guide.html
 * https://www.jianshu.com/p/4f5fcddb4106
 * 流程简述：
 * 0.首先oauth2是一套规范协议，在这个协议规范之下，各家可以有自己的不同实现，保证大致流程/逻辑相同即可
 * 1.用户访问第三方网站,第三方应用需要用户登录验证,用户选择微信授权登录
 * 2.第三方应用发起微信登录授权请求，实例请求字符串如下
 *   https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code
 * 3.微信服务器拉起用户授权确认页面
 * 4.用户授权通过
 * 5.微信发送请求到第三方应用redirctUrl(第2步填写redirct_uri参数),返回凭证code与state(第2步自定义)，实例请求字符串如下
 *   http://host/redirct_uri?code=0217a07e9c194dbf539c45c266b2dcfZ&state=state
 * 6.第三方应用获取到code之后,根据code获取accessToken
 *   https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code
 * 7.根据accessToken获取用户信息
 *   https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID
 * 8.对用户信息进行处理(用户是否第一次登录,保存用户信息,自定义token,session处理等)
 * 9.返回结果(步骤1对应url或者重定向到首页)
 *
 * 更多参考 {@link site.xiaokui.oauth2.controller.ThirdClientController}
 *
 * @author HK
 * @date 2020-06-12 15:38
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class Oauth2App {
    public static void main(String[] args) {
        SpringApplication.run(Oauth2App.class, args);
    }
}
