package site.xiaokui.oauth2.service;

/**
 * 认证服务
 * @author hk
 */
public interface OAuthService {

    void addAuthCode(String authCode, String username);

    void addAccessToken(String accessToken, String username);

    boolean checkAuthCode(String authCode);

    boolean checkAccessToken(String accessToken);

    String getUsernameByAuthCode(String authCode);

    String getUsernameByAccessToken(String accessToken);

    long getExpireInSecond();

    boolean checkClientId(String clientId);

    boolean checkClientRequest(String clientId, String clientSecret);
}
