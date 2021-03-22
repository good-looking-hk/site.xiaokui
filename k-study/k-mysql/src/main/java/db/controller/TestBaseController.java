package db.controller;

import db.common.InterCode;
import db.common.ResultEntity;
import db.entity.User;
import db.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 测试框架基本功能
 * @author HK
 * @date 2021-03-17 17:07
 */
@Slf4j
@RequiredArgsConstructor
@RestController
public class TestBaseController {

    private final UserService userService;

    @PostMapping(InterCode.IS_STARTED)
    @ResponseBody
    public Object test0(@RequestBody Map<String, Object> map) {
        log.info("前端传来数据:" + map);
        return ResultEntity.ok(map);
    }

    @PostMapping(InterCode.CAN_QUERY)
    @ResponseBody
    public Object test1() {
        Map<String, Object> map = new HashMap<>(8);
        List<User> allUser = userService.allUser();
        User user = userService.findFirst();
        map.put("all_user", allUser);
        map.put("first_user", user);
        return ResultEntity.ok(map);
    }

    @PostMapping(InterCode.INSERT_USER)
    @ResponseBody
    public Object test2() {
        List<User> allUser = userService.allUser();
        User user = new User();
        user.setName("测试账号" + allUser.size());
        user.setPhone("123123123" + allUser.size());
        user.setCreateTime(new Date());
        user.setModifiedTime(new Date());
        userService.insert(user);
        return ResultEntity.ok();
    }

    @PostMapping(InterCode.INIT_USER)
    @ResponseBody
    public Object test3() {
        List<User> allUser = userService.allUser();
        User user = new User();
        user.setName("测试账号" + allUser.size());
        user.setPhone("123123123" + allUser.size());
        user.setCreateTime(new Date());
        user.setModifiedTime(new Date());
        userService.initUser(user);
        return ResultEntity.ok();
    }

    @PostMapping(InterCode.INIT_USER1)
    @ResponseBody
    public Object test4() {
        List<User> allUser = userService.allUser();
        User user = new User();
        user.setName("测试账号" + allUser.size());
        user.setPhone("123123123" + allUser.size());
        user.setCreateTime(new Date());
        user.setModifiedTime(new Date());
        userService.initUser1(user);
        return ResultEntity.ok();
    }

    @PostMapping(InterCode.INIT_USER_FAIL1)
    @ResponseBody
    public Object test5() {
        List<User> allUser = userService.allUser();
        User user = new User();
        user.setName("测试账号" + allUser.size());
        user.setPhone("123123123" + allUser.size());
        user.setCreateTime(new Date());
        user.setModifiedTime(new Date());
        userService.initUser2(user);
        return ResultEntity.ok();
    }

    @PostMapping(InterCode.INIT_USER_FAIL2)
    @ResponseBody
    public Object test6() {
        List<User> allUser = userService.allUser();
        User user = new User();
        user.setName("测试账号" + allUser.size());
        user.setPhone("123123123" + allUser.size());
        user.setCreateTime(new Date());
        user.setModifiedTime(new Date());
        userService.initUser3(user);
        return ResultEntity.ok();
    }
}
