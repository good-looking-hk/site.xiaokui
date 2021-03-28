package db.controller;

import db.common.InterCode;
import db.common.ResultEntity;
import db.entity.User;
import db.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

/**
 * 测试读写分离
 *
 * @author HK
 * @date 2021-03-24 16:54
 */
@Slf4j
@RequiredArgsConstructor
@RestController
public class TestReadWriteController {

    private final UserService userService;

    @PostMapping(InterCode.INSERT_SINGLE_USER)
    public Object insert() {
        List<User> allUser = userService.allUser();
        User user = new User();
        user.setName("读写插入账号" + allUser.size());
        user.setPhone("123123123" + allUser.size());
        user.setCreateTime(new Date());
        user.setModifiedTime(new Date());
        userService.insert(user);
        return ResultEntity.ok();
    }

    @PostMapping(InterCode.UPDATE_SINGLE_USER)
    public Object update(Long id) {
        List<User> allUser = userService.allUser();
        User user = new User();
        user.setId(id);
        user.setName("读写修改账号" + allUser.size());
        user.setPhone("123123123" + allUser.size());
        user.setCreateTime(new Date());
        user.setModifiedTime(new Date());
        int row = userService.update(user);
        return ResultEntity.ok(row);
    }

    @PostMapping(InterCode.QUERY_SINGLE_USER)
    public Object query(Long id) {
        if (id == null) {
            List<User> allUser = userService.allUser();
            return ResultEntity.ok(allUser);
        }
        User user = userService.querySingle(id);
        return ResultEntity.ok(user);
    }
}
