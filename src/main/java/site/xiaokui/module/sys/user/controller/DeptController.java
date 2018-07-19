package site.xiaokui.module.sys.user.controller;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import site.xiaokui.module.sys.user.UserConstants;
import site.xiaokui.module.base.controller.BaseController;
import site.xiaokui.module.sys.user.entity.SysDept;
import site.xiaokui.module.sys.user.entity.ZTreeNode;
import site.xiaokui.module.sys.user.service.DeptService;
import site.xiaokui.module.sys.user.util.ZTreeTool;

import java.util.List;

/**
 * @author HK
 * @date 2018-06-09 19:47
 */
@Controller
@RequestMapping(UserConstants.DEPT_PREFIX)
public class DeptController extends BaseController {

    /**
     * DEPT_PREFIX字段默认为 /sys/dept
     */
    private static final String DEPT_PREFIX = UserConstants.DEPT_PREFIX;

    @Autowired
    private DeptService deptService;


    @RequiresPermissions(DEPT_PREFIX)
    @GetMapping("")
    public String index() {
        return DEPT_PREFIX + "/index";
    }

    @RequiresPermissions(DEPT_PREFIX)
    @RequestMapping(value = "/list")
    @ResponseBody
    public List<SysDept> list() {
        return deptService.all();
    }

    @RequiresPermissions(DEPT_PREFIX + "/add")
    @RequestMapping("/tree")
    @ResponseBody
    public List<ZTreeNode> tree() {
        return ZTreeTool.toZTreeNodeList(deptService.all());
    }
}
