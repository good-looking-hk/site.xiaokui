package site.xiaokui.controller;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 阿尔萨斯 Arthas  http://arthas.gitee.io/
 * -Xmx1024m -Xms1024m -Xmn500M
 * @author HK
 * @date 2021-03-22 21:28
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/temp")
public class TempTestController {

    private final List<Object> list = new ArrayList<>();

    @GetMapping("/tempGetStr")
    public String test() {
        return "hello, I am OK";
    }

    @GetMapping("/tempTest")
    public String tempTest(int times) {
        for (int j = 0; j < times; j++) {
            String uuid = IdUtil.fastUUID();
            // 0 1 2
            int i = RandomUtil.randomInt(3);
            if (i == 0) {
                list.add(uuid);
            } else if (i == 1) {
                Map<String, Object> map = new HashMap<>(8);
                map.put(uuid, uuid);
                list.add(map);
            } else if (i == 2) {
                list.add(new Object());
            }
        }
        return "success run " + times + " times";
    }
}
