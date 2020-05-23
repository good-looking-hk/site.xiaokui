package site.xiaokui.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import site.xiaokui.base.aop.annotation.Log;
import site.xiaokui.blog.CacheCenter;
import site.xiaokui.common.util.StringUtil;

/**
 * 项目根控制器，匹配 /*
 * @author HK
 * @date 2018-05-23 23:20
 */
@Controller("rootController")
public class IndexController extends BaseController {

    @Autowired
    private CacheCenter cacheCenter;

    @Value("${xiaokui.logsPath}" + "${xiaokui.logName}")
    private String appLogPath;

    @Log(remark = "访问主页", recordIp = true)
    @GetMapping({"/", "/index"})
    public String index() {
        String index = cacheCenter.getSysConfigCache().getIndex();
        if (StringUtil.isNotBlank(index)) {
            return index;
        }
        return FORWARD + "/index";
    }

    @Log(remark = "查看nginx日志", recordMethodParams = true)
    @GetMapping("/nginx")
    public String nginx(Model model, String type) {
        String accessLogPath = cacheCenter.getSysConfigCache().getNginxAccessLogPath();
        String errorLogPath = cacheCenter.getSysConfigCache().getNginxErrorLogPath();
        if ("error".equals(type)) {
            model.addAttribute("error", errorLogPath);
        } else {
            model.addAttribute("access", accessLogPath);
        }
        return "root/nginx";
    }

    @Log(remark = "查看系统日志", recordMethodParams = true)
    @GetMapping("/web/log")
    public String log(Model model, String key) {
        if ("199710".equals(key)) {
            model.addAttribute("log", appLogPath);
            System.out.println(appLogPath);
            return "root/log";
        }
        return ERROR;
    }

    @Log(remark = "清除缓存", writeToDB = true)
    @RequestMapping(value = "/clearCache", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String clearCache() {
        cacheCenter.clearSysConfigCache();
        return "重新载入配置成功";
    }
}
