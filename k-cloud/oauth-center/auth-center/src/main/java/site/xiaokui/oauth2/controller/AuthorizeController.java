package site.xiaokui.oauth2.controller;

import io.swagger.annotations.ApiOperation;
import org.apache.oltu.oauth2.as.issuer.MD5Generator;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuerImpl;
import org.apache.oltu.oauth2.as.request.OAuthAuthzRequest;
import org.apache.oltu.oauth2.as.response.OAuthASResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.error.OAuthError;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.apache.oltu.oauth2.common.message.types.ResponseType;
import org.apache.oltu.oauth2.common.utils.OAuthUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import site.xiaokui.base.aop.annotation.Log;
import site.xiaokui.oauth2.Constants;
import site.xiaokui.oauth2.entity.ShiroUser;
import site.xiaokui.oauth2.service.ClientService;
import site.xiaokui.oauth2.service.OAuthService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Map;

/**
 * 这个接口主要是以下步骤的体现，主要目的是获取凭证code
 *
 * 2.第三方应用发起微信登录授权请求，实例请求字符串如下
 *   https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE&state=STATE#wechat_redirect
 * 3.微信服务器拉起用户授权确认页面
 * 4.用户授权通过
 * 5.微信发送请求到第三方应用redirctUrl(第2步填写redirct_uri参数),返回凭证code与state(第2步自定义)，实例请求字符串如下
 *   http://host/redirct_uri?code=0217a07e9c194dbf539c45c266b2dcfZ&state=state
 * @author HK
 */
@Controller
public class AuthorizeController {

    @Autowired
    private OAuthService oAuthService;

    @Autowired
    private ClientService clientService;

    @RequestMapping("/authorize")
    @ApiOperation(value = "第三方客户端获取凭证code")
    @Log(module = "auth-center", remark="获取code")
    public Object authorize(Model model, HttpServletRequest request) throws URISyntaxException, OAuthSystemException {
        Map<String, String[]> map = request.getParameterMap();
        map.forEach((k, v) -> {
            System.out.println(k + Arrays.toString(v));
        });
        try {
            // 构建OAuth 授权请求
            OAuthAuthzRequest oauthRequest = new OAuthAuthzRequest(request);
            System.out.println("oauthRequest: " + oauthRequest);

            // 检查传入的客户端id是否正确，注意这里并不会包含秘钥
            if (!oAuthService.checkClientId(oauthRequest.getClientId())) {
                OAuthResponse response =
                        OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                                .setError(OAuthError.TokenResponse.INVALID_CLIENT)
                                .setErrorDescription(Constants.INVALID_CLIENT_DESCRIPTION)
                                .buildJSONMessage();
                return new ResponseEntity<>(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
            }

            Subject subject = SecurityUtils.getSubject();
            // 如果用户没有登录，跳转到登陆页面
            if(!subject.isAuthenticated()) {
                // 尝试登录操作
                if(!login(subject, request)) {
                    // 登录失败时跳转到登陆页面
                    return "/thirdLogin";
                }
            }

            ShiroUser shiroUser = (ShiroUser)subject.getPrincipal();
            // 生成授权码
            String authorizationCode = null;
            // responseType目前仅支持CODE，另外还有TOKEN
            String responseType = oauthRequest.getParam(OAuth.OAUTH_RESPONSE_TYPE);
            if (responseType.equals(ResponseType.CODE.toString())) {
                OAuthIssuerImpl oauthIssuerImpl = new OAuthIssuerImpl(new MD5Generator());
                authorizationCode = oauthIssuerImpl.authorizationCode();
                oAuthService.addAuthCode(authorizationCode, shiroUser.getUsername());
            }

            // 进行OAuth响应构建
            OAuthASResponse.OAuthAuthorizationResponseBuilder builder =
                    OAuthASResponse.authorizationResponse(request, HttpServletResponse.SC_FOUND);
            // 设置授权码
            builder.setCode(authorizationCode);
            // 得到到客户端重定向地址
            String redirectUrl = oauthRequest.getParam(OAuth.OAUTH_REDIRECT_URI);
            System.out.println("重定向：" + redirectUrl);

            // 构建响应
            final OAuthResponse response = builder.location(redirectUrl).buildQueryMessage();

            // 根据OAuthResponse返回ResponseEntity响应
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(new URI(response.getLocationUri()));
            return new ResponseEntity(headers, HttpStatus.valueOf(response.getResponseStatus()));
        } catch (OAuthProblemException e) {
            e.printStackTrace();
            // 出错处理
            String redirectUri = e.getRedirectUri();
            if (OAuthUtils.isEmpty(redirectUri)) {
                // 告诉客户端没有传入redirectUri直接报错
                return new ResponseEntity<>("OAuth callback url needs to be provided by client!", HttpStatus.NOT_FOUND);
            }

            // 返回错误消息（如?error=）
            final OAuthResponse response =
                    OAuthASResponse.errorResponse(HttpServletResponse.SC_FOUND)
                            .error(e).location(redirectUri).buildQueryMessage();
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(new URI(response.getLocationUri()));
            return new ResponseEntity(headers, HttpStatus.valueOf(response.getResponseStatus()));
        }
    }

    private boolean login(Subject subject, HttpServletRequest request) {
        // 一般来说，方法名都是大写，即都是GET、POST的形式
        if(HttpMethod.GET.name().equalsIgnoreCase(request.getMethod())) {
            return false;
        }
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        if(StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            return false;
        }

        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        try {
            subject.login(token);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}