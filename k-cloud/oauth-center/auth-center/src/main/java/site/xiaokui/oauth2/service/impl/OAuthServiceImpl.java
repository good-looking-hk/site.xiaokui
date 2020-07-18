package site.xiaokui.oauth2.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import site.xiaokui.oauth2.Constants;
import site.xiaokui.oauth2.entity.Client;
import site.xiaokui.oauth2.service.ClientService;
import site.xiaokui.oauth2.service.OAuthService;

import java.util.Objects;

/**
 * @author hk
 */
@Service
public class OAuthServiceImpl implements OAuthService {

    private Cache cache;

    @Autowired
    private ClientService clientService;

    @Autowired
    public OAuthServiceImpl(CacheManager cacheManager) {
        this.cache = cacheManager.getCache(Constants.OAUTH_CACHE_NAME);
        if (this.cache == null) {
            throw new RuntimeException("未找到缓存bean: " + Constants.OAUTH_CACHE_NAME);
        }
    }

    @Override
    public void addAuthCode(String authCode, String username) {
        cache.put(authCode, username);
    }

    @Override
    public void addAccessToken(String accessToken, String username) {
        cache.put(accessToken, username);
    }

    @Override
    public String getUsernameByAuthCode(String authCode) {
        Cache.ValueWrapper wrapper = cache.get(authCode);
        return wrapper == null ? null : (String) wrapper.get();
    }

    @Override
    public String getUsernameByAccessToken(String accessToken) {
        Cache.ValueWrapper wrapper = cache.get(accessToken);
        return wrapper == null ? null : (String) wrapper.get();
    }

    @Override
    public boolean checkAuthCode(String authCode) {
        return cache.get(authCode) != null;
    }

    @Override
    public boolean checkAccessToken(String accessToken) {
        return cache.get(accessToken) != null;
    }

    @Override
    public long getExpireInSecond() {
        return Constants.OAUTH_CACHE_EXPIRED_TIME_IN_SECOND;
    }

    @Override
    public boolean checkClientId(String clientId) {
        return clientService.checkClientId(clientId);
    }

    @Override
    public boolean checkClientRequest(String clientId, String clientSecret) {
        return clientService.checkClientRequest(clientId, clientSecret);
    }
}
