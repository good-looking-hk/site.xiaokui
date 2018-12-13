package site.xiaokui.module.sys.seckill.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import site.xiaokui.common.aop.annotation.Log;
import site.xiaokui.module.base.controller.BaseController;
import site.xiaokui.module.sys.seckill.dto.Execution;
import site.xiaokui.module.sys.seckill.dto.Exposer;
import site.xiaokui.module.sys.seckill.entity.SeckillProduct;
import site.xiaokui.module.sys.seckill.service.SeckillService;

import java.util.List;

import static site.xiaokui.module.sys.seckill.SeckillConstants.SECKILL_PREFIX;

/**
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
    @GetMapping("/{id}/detail")
    public String detail(@PathVariable("id") Integer id, Model model) {
        if (id == null) {
            return REDIRECT + PREFIX + INDEX;
        }
        SeckillProduct product = seckillService.getById(id);
        if (product == null) {
            return REDIRECT + PREFIX + INDEX;
        }
        model.addAttribute("product", product);
        return PREFIX + "/detail";
    }

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
    @PostMapping("/{id}/expose")
    @ResponseBody
    public Exposer expose(@PathVariable("id") Integer id) {
        return seckillService.exportSecKillUrl(id);
    }

    /**
     * 执行秒杀
     */
    @Log
    @PostMapping("/{id}/{md5}/execute")
    @ResponseBody
    public Execution executeSeckill(@PathVariable("id") Integer id, @PathVariable("md5") String md5) {
        return seckillService.executeSeckillProcedure(id, md5, getUser().getPhone());
    }
}
