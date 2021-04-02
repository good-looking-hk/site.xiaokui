package db.controller;

import db.common.InterCode;
import db.common.ResultEntity;
import db.entity.User;
import db.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;

/**
 * 测试 分库分表
 * @author HK
 * @date 2021-03-28 18:16
 */
@Profile("2-sub-db-table")
@Slf4j
@RequiredArgsConstructor
@RestController
public class TestSubDbTableController {

    private final UserService userService;

    /**
     * 我们期望如下，两个库，四张表，数据分布均匀
     */
    @PostConstruct
    private void initData() {
        int row = userService.deleteAll();
        log.info("成功删除{}行数据", row);
        for (int i = 1; i <= 10; i++) {
            User user = new User();
            user.setId((long) i);
            // 是数据分布均匀
            user.setAge(i + 1);
            user.setSex("1");
            user.setName("名字" + i);
            user.setPhone("12344321 " + i);
            user.setCreateTime(new Date());
            user.setModifiedTime(new Date());
            userService.insert(user);
        }
        for (int i = 11; i <= 20; i++) {
            User user = new User();
            user.setId((long) i);
            // 是数据分布均匀
            user.setAge(i);
            user.setSex("2");
            user.setName("名字" + i);
            user.setPhone("12344321 " + i);
            user.setCreateTime(new Date());
            user.setModifiedTime(new Date());
            userService.insert(user);
        }
        log.info("已成功初始化数据:" + userService.allUser().size());
    }

    @PostMapping(InterCode.INSERT_SINGLE_USER)
    public Object insert() {
        List<User> allUser = userService.allUser();
        User user = new User();
        user.setName("读写插入账号" + allUser.size());
        user.setPhone("123123123" + allUser.size());
        user.setCreateTime(new Date());
        user.setModifiedTime(new Date());
        user.setAge(allUser.size());
        userService.insert(user);
        return ResultEntity.ok();
    }

    @PostMapping(InterCode.PAGE_QUERY_USER)
    public Object page(int offset, int size) {
        List<User> list = userService.pageQuery(offset, size);
        return ResultEntity.ok(list);
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

    @PostMapping(InterCode.QUERY_SUM_USER)
    public Object groupByAndSum(String sum, String groupBy) {
        return ResultEntity.ok(userService.groupByAndSum(sum, groupBy));
    }
}


