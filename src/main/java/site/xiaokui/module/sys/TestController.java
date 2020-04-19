package site.xiaokui.module.sys;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import redis.clients.jedis.Jedis;
import site.xiaokui.module.base.service.RedisService;

import java.util.Set;

/**
 * @author HK
 * @date 2020-04-17 14:02
 */
@Controller("TEST")
public class TestController {

    @Autowired
    private RedisService redisService;

    @RequestMapping("/test")
    @ResponseBody
    public String test() {
        Jedis redis = redisService.getRedis();
        Set<String> sets = redis.keys("*");
        String str = sets.toString();
        return str;
    }
}
