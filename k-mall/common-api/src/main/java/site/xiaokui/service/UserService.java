package site.xiaokui.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import site.xiaokui.entity.ResultEntity;

/**
 * @author HK
 * @date 2020-06-29 16:04
 */
@FeignClient(value = "user-service")
public interface UserService {

    /**
     * 用户登录接口
     */
    @RequestMapping("/login")
    @ResponseBody
    ResultEntity login(@RequestParam String username, @RequestParam String password);
}
