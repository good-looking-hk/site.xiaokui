package site.xiaokui.oauth2.controller;

import io.swagger.annotations.ApiOperation;
import org.apache.oltu.oauth2.as.issuer.MD5Generator;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuer;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuerImpl;
import org.apache.oltu.oauth2.as.request.OAuthTokenRequest;
import org.apache.oltu.oauth2.as.response.OAuthASResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.error.OAuthError;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.xiaokui.base.aop.annotation.Log;
import site.xiaokui.oauth2.Constants;
import site.xiaokui.oauth2.service.OAuthService;
import site.xiaokui.oauth2.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 这个接口主要是以下步骤的体现，主要目的是用凭证code换token
 *
 * 5.微信发送请求到第三方应用redirctUrl(第2步填写redirct_uri参数),返回凭证code与state(第2步自定义)，实例请求字符串如下
 *   http://host/redirct_uri?code=0217a07e9c194dbf539c45c266b2dcfZ&state=state
 * 6.第三方应用获取到code之后,根据code获取accessToken
 *   https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code
 * @author HK
 */
@RestController
public class AccessTokenController {

    @Autowired
    private OAuthService oAuthService;

    @Autowired
    private UserService userService;

    @RequestMapping("/accessToken")
    @ApiOperation(value = "第三方客户端以code凭证换token")
    @Log(module = "auth-center", remark="code换token")
    public HttpEntity token(HttpServletRequest request) throws OAuthSystemException {

        try {
            // 构建OAuth请求
            OAuthTokenRequest oauthRequest = new OAuthTokenRequest(request);

            // 检查提交的客户端id/secret是否正确
            if (!oAuthService.checkClientRequest(oauthRequest.getClientId(), oauthRequest.getClientSecret())) {
                OAuthResponse response =
                        OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                                .setError(OAuthError.TokenResponse.INVALID_CLIENT)
                                .setErrorDescription(Constants.INVALID_CLIENT_DESCRIPTION)
                                .buildJSONMessage();
                return new ResponseEntity<>(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
            }

            String authCode = oauthRequest.getParam(OAuth.OAUTH_CODE);
            // 检查验证类型，此处只检查AUTHORIZATION_CODE类型，其他的还有PASSWORD或REFRESH_TOKEN
            if (oauthRequest.getParam(OAuth.OAUTH_GRANT_TYPE).equals(GrantType.AUTHORIZATION_CODE.toString())) {
                if (!oAuthService.checkAuthCode(authCode)) {
                    OAuthResponse response = OAuthASResponse
                            .errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                            .setError(OAuthError.TokenResponse.INVALID_GRANT)
                            .setErrorDescription("错误的授权码")
                            .buildJSONMessage();
                    return new ResponseEntity<>(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
                }
            }

            // 生成Access Token
            OAuthIssuer oauthIssuerImpl = new OAuthIssuerImpl(new MD5Generator());
            final String accessToken = oauthIssuerImpl.accessToken();
            oAuthService.addAccessToken(accessToken, oAuthService.getUsernameByAuthCode(authCode));

            // 生成OAuth响应
            OAuthResponse response = OAuthASResponse
                    .tokenResponse(HttpServletResponse.SC_OK)
                    .setAccessToken(accessToken)
                    .setExpiresIn(String.valueOf(oAuthService.getExpireInSecond()))
                    .buildJSONMessage();
            // 根据OAuthResponse生成ResponseEntity
            return new ResponseEntity<>(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));

        } catch (OAuthProblemException e) {
            // 构建错误响应
            OAuthResponse res = OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST).error(e)
                    .buildJSONMessage();
            return new ResponseEntity<>(res.getBody(), HttpStatus.valueOf(res.getResponseStatus()));
        }
    }

}
