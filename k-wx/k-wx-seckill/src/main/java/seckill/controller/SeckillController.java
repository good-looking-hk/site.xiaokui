package seckill.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import site.xiaokui.base.aop.annotation.Log;
import site.xiaokui.controller.BaseController;
import site.xiaokui.module.sys.seckill.dto.Exposer;
import site.xiaokui.module.sys.seckill.dto.SeckillResult;
import site.xiaokui.module.sys.seckill.entity.SeckillProduct;
import site.xiaokui.module.sys.seckill.service.SeckillService;

import java.util.List;

import static site.xiaokui.module.sys.seckill.SeckillConstants.SECKILL_PREFIX;

/**
 * 模拟秒杀
 * @author HK
 * @date 2018-10-03 19:33
 */
@Controller
@RequestMapping(SECKILL_PREFIX)
public class SeckillController extends BaseController {

    /**
     * 默认为/sys/seckill
     */
    private static final String PREFIX = SECKILL_PREFIX;

    @Autowired
    private SeckillService seckillService;

    @Log
    @GetMapping({EMPTY, INDEX, LIST})
    public String list(Model model) {
        List<SeckillProduct> list = seckillService.all();
        model.addAttribute("products", list);
        return PREFIX + INDEX;
    }

    @Log
    @GetMapping("reset")
    public String reset() {
        seckillService.resetDate();
        return REDIRECT + PREFIX + INDEX;
    }

    @Log
    @GetMapping("/{pid}/detail")
    public String detail(@PathVariable("pid") Integer id, Model model) {
        if (id == null || id < 0 || id > 3) {
            return REDIRECT + PREFIX + INDEX;
        }
        SeckillProduct product = seckillService.getById(id);
        if (product == null) {
            return REDIRECT + PREFIX + INDEX;
        }
        model.addAttribute("product", product);
        return PREFIX + "/detail";
    }

    /**
     * 以服务器时间为准
     */
    @Log
    @GetMapping("/time/now")
    @ResponseBody
    public Long time() {
        return System.currentTimeMillis();
    }

    /**
     * 加密秒杀连接
     */
    @Log
    @PostMapping("/{pid}/expose")
    @ResponseBody
    public Exposer expose(@PathVariable("pid") Integer pid) {
        return seckillService.exportSecKillUrl(pid);
    }

    /**
     * 执行秒杀
     */
    @Log
    @PostMapping("/{id}/{md5}/execute")
    @ResponseBody
    public SeckillResult executeSeckill(@PathVariable("pid") Integer pid, @PathVariable("md5") String md5, String phone) {
        return seckillService.executeSeckillProcedure(pid, md5, phone);
    }
}
