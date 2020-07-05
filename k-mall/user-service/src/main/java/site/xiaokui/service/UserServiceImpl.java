package site.xiaokui.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import site.xiaokui.dao.UserRepository;
import site.xiaokui.entity.MallUser;
import site.xiaokui.entity.ResultEntity;

import java.util.UUID;

/**
 * @author HK
 * @date 2020-06-29 16:10
 */
@RestController
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public ResultEntity login(String username, String password) {
        MallUser user = userRepository.findByUsernameAndPassword(username, password);
        if (user == null) {
            return ResultEntity.failed("登录失败");
        }
        return ResultEntity.ok("登录成功")
                .put("token", UUID.randomUUID().toString().replace("-", ""))
                .put("uid", user.getUid()).put("username", username);
    }
}
