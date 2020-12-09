package site.xiaokui.controller;

import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.utils.SpringContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import site.xiaokui.CacheCenter;
import site.xiaokui.config.XiaokuiProperties;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author HK
 * @date 2020-12-03 21:44
 */
@RequiredArgsConstructor
@Controller
@RequestMapping
public class RootController implements ErrorController {

    private final ErrorAttributes errorAttributes;

    private final XiaokuiProperties xiaokuiProperties;

    /**
     * 访问首页
     */
    @GetMapping({"/", "/index"})
    public String index() {
        String index = SpringContextHolder.getBean(CacheCenter.class).getSysConfigCache().getIndex();
        if (StrUtil.isNotBlank(index)) {
            return "forward:" + index;
        }
        return "forward:" + "/index";
    }

    /**
     * 统一处理错误
     */
    @RequestMapping("/error")
    @ResponseBody
    public ResponseEntity<Object> error(WebRequest webRequest) {
        Map<String, Object> body = this.errorAttributes.getErrorAttributes(webRequest, ErrorAttributeOptions.defaults());
        return ResponseEntity.status((Integer) body.get("status")).body(body);
    }

    @PostMapping("/blog/music/{userId}/{dir}")
    @ResponseBody
    public ResponseEntity<Object> list(@PathVariable Integer userId, @PathVariable String dir) {
        System.out.println(xiaokuiProperties.toString());
        File musicDir = new File( xiaokuiProperties.getBlogMusicPath() + userId + "/" + dir);
        List<String> list = new ArrayList<>(4);
        if (musicDir.isDirectory()) {
            File[] files = musicDir.listFiles();
            for (File f : files) {
                list.add(f.getName());
            }
        }
        return ResponseEntity.ok(list);
    }

    @GetMapping("/test")
    @ResponseBody
    public Date test(Model model, String id) {
        return new Date();
    }

    @Override
    public String getErrorPath() {
        return null;
    }
}
