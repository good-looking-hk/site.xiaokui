package site.xiaokui.service;

import org.springframework.web.bind.annotation.RestController;
import site.xiaokui.entity.ResultEntity;

/**
 * @author HK
 * @date 2020-06-29 16:10
 */
@RestController
public class UserServiceImpl implements UserService {

    @Override
    public ResultEntity login(String username, String password) {
        if ("admin".equals(username) && "admin".equals(password)) {
            return ResultEntity.ok("登录成功").put("token", "1q2w3e4r")
                    .put("id", "1").put("user", username);
        }
        return ResultEntity.failed("登录失败");
    }
}
