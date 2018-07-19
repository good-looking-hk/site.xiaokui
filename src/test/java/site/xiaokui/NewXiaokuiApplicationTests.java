package site.xiaokui;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import site.xiaokui.module.sys.user.entity.SysUser;
import site.xiaokui.module.sys.user.service.UserService;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class NewXiaokuiApplicationTests {

    @Autowired
    private UserService userService;

    @Test
    public void contextLoads() {
        System.out.println("====================");
    }
}
