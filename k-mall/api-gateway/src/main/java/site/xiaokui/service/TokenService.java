package site.xiaokui.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;

/**
 * @author HK
 * @date 2020-06-29 17:37
 */
@Service
public class TokenService {

    private final HashMap<String, Object> tokenMap = new HashMap<>();

    public boolean checkToken(String token) {
        return tokenMap.containsKey(token);
    }

    public void addToken(String token, Object object) {
        tokenMap.put(token, object);
    }

    public void removeToken(String token) {
        tokenMap.remove(token);
    }
}
