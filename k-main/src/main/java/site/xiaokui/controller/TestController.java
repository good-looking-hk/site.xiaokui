package site.xiaokui.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import redis.clients.jedis.Jedis;
import site.xiaokui.blog.service.RedisService;

import java.util.Set;

/**
 * @author HK
 * @date 2019-04-17 14:02
 */
@Profile("local")
@Controller("testController")
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
