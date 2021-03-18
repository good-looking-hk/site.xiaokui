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
    public Object test1(@RequestBody Map<String, Object> map) {
        List<User> allUser = userService.allUser();
        return ResultEntity.ok(allUser);
    }
}
