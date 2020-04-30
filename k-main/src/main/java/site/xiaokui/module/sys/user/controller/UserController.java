package site.xiaokui.module.sys.user.controller;

import cn.hutool.json.JSON;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.beetl.sql.core.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import site.xiaokui.common.util.StringUtil;
import site.xiaokui.module.sys.user.UserConstants;
import site.xiaokui.config.shiro.ShiroUser;
import site.xiaokui.module.base.controller.AbstractController;
import site.xiaokui.entity.ResultEntity;
import site.xiaokui.module.sys.user.entity.SysRole;
import site.xiaokui.module.sys.user.entity.SysUser;
import site.xiaokui.module.sys.user.entity.enums.SexTypeEnum;
import site.xiaokui.module.sys.user.entity.wrapper.SysUserWrapper;
import site.xiaokui.module.sys.user.service.RoleService;
import site.xiaokui.module.sys.user.service.ServiceFactory;
import site.xiaokui.module.sys.user.service.UserService;

import java.util.List;

import static site.xiaokui.module.base.BaseConstants.SUPER_ADMIN;

/**
 * @author HK
 * @date 2018-05-24 22:00
 */
@Controller
@RequestMapping(UserConstants.USER_PREFIX)
public class UserController extends AbstractController {
    /**
     * USER_PREFIX字段默认为 /sys/user
     */
    private static final String USER_PREFIX = UserConstants.USER_PREFIX;

    @Override
    protected String setPrefix() {
        return USER_PREFIX;
    }

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    /**
     * 获取对应的菜单，含参数的调用均为客户端的异步查找
     *
     * @param key key代表username和email的模糊匹配
     */
    @RequiresPermissions(USER_PREFIX)
    @RequestMapping(LIST)
    @ResponseBody
    public JSON list(@RequestParam(required = false) String key, @RequestParam(required = false) String beginTime,
                     @RequestParam(required = false) String endTime) {
        if (StringUtil.isAllEmpty(key, beginTime, endTime)) {
            SysUserWrapper wrapper = new SysUserWrapper(userService.all());
            return wrapper.toJson();
        }
        Query<SysUser> userQuery = userService.createQuery();
        if (StringUtil.isNotEmpty(key)) {
            userQuery.andLike("name", "%" + key + "%").orLike("email", "%" + key + "%");
        }
        if (StringUtil.isNotEmpty(beginTime) && StringUtil.isNotEmpty(endTime)) {
            userQuery.andBetween("create_time", beginTime, endTime);
        }
        List<SysUser> list = userService.query(userQuery);
        SysUserWrapper wrapper = new SysUserWrapper(list);
        return wrapper.toJson();
    }

    @RequiresPermissions(USER_PREFIX + ADD)
    @PostMapping(ADD)
    @ResponseBody
    public ResultEntity add(String name, String email, String sex, String password, String repeat,
                            @RequestParam(required = false) String roleName, @RequestParam(required = false) Integer roleId) {
        if (StringUtil.hasEmpty(name, email, sex, password, repeat)) {
            return ResultEntity.paramError();
        }
        if (!password.equals(repeat)) {
            return ResultEntity.error("两次密码输入不匹配");
        }
        if (!StringUtil.checkEmailPass(email)) {
            return ResultEntity.error("邮箱格式不正确");
        }
        // 验证sex字段的合法性
        Integer sexCode = SexTypeEnum.codeOf(sex);
        if (sexCode == null) {
            return ResultEntity.paramError();
        }
        if (userService.getUserByName(name) != null) {
            return ResultEntity.error("该用户名已被注册");
        }
        if (userService.getUserByEmail(email) != null) {
            return ResultEntity.error("该邮箱已被注册");
        }

        SysUser user = initDefaultUser(name, email, password);
        user.setSex(sexCode);
        if (roleId != null && StringUtil.isNotEmpty(roleName)) {
            SysRole sysRole = roleService.getById(roleId);
            if (sysRole != null && sysRole.getName().equals(roleName)) {
                user.setRoleId(roleId);
            }
        }
        boolean success = userService.insertIgnoreNull(user);
        return returnResult(success, "添加用户失败");
    }

    @RequiresPermissions(USER_PREFIX + "/setRole")
    @PostMapping("/setRole")
    @ResponseBody
    public ResultEntity setRole(Integer id, Integer roleId) {
        SysUser user = new SysUser();
        user.setId(id);
        user.setRoleId(roleId);
        boolean success = userService.updateByIdIgnoreNull(user);
        return returnResult(success, "分配角色失败");
    }

    @RequiresPermissions(USER_PREFIX + REMOVE)
    @PostMapping(REMOVE)
    @ResponseBody
    @Override
    public ResultEntity remove(Integer id) {
        boolean success = userService.deleteById(id);
        return returnResult(success, "删除用户失败");
    }

    /**
     * 最大程度保护用户隐私，只有最高权限才能修改
     */
    @RequiresRoles(SUPER_ADMIN)
    @GetMapping(EDIT + "/{id}")
    @Override
    public String edit(@PathVariable Integer id, Model model) {
        SysUser user = userService.getById(id);
        if (user == null) {
            return ERROR;
        }
        user.setSsex(SexTypeEnum.valueOf(user.getSex()));
        model.addAttribute("user", user);
        model.addAttribute("roleName", ServiceFactory.me().getRoleName(user.getRoleId()));
        return TO_EDIT;
    }

    @RequiresRoles(SUPER_ADMIN)
    @PostMapping(EDIT)
    @ResponseBody
    public ResultEntity edit(Integer id, String name, String email, String sex, String password,
                             Integer roleId, @RequestParam(required = false) String roleName) {
        if (StringUtil.hasEmptyStrOrLessThanEqualsZeroNumber(id, name, email, sex, password)) {
            return this.paramError(id, name, email, sex, password);
        }
        Integer code = SexTypeEnum.codeOf(sex);
        if (code == null) {
            return this.paramError(sex);
        }

        SysRole role = roleService.getById(roleId);
        if (role == null) {
            return this.paramError(roleId);
        }
        if (roleName != null) {
            if (!role.getName().equals(roleName)) {
                return this.paramError(roleId, roleName);
            }
        }

        SysUser user = new SysUser();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        user.setSex(code);
        user.setSalt(SHIRO.fastSalt());
        user.setPassword(SHIRO.md5(password, user.getSalt()));
        user.setRoleId(roleId);

        //更新用户信息，忽略为的null的更新值
        boolean success = userService.updateByIdIgnoreNull(user);
        return returnResult(success, "修改用户信息失败");
    }

    @RequiresPermissions(USER_PREFIX + RESET_PASSWORD)
    @PostMapping(RESET_PASSWORD)
    @ResponseBody
    public ResultEntity resetPwd(Integer id) {
        boolean success = userService.resetPwd(id);
        return returnResult(success, "重置密码失败");
    }

    /**
     * 除超级管理员外，只有用户本人才能修改个人信息
     */
    @RequiresAuthentication
    @PostMapping(UPDATE)
    @ResponseBody
    public ResultEntity update(String username, String sex, String blogSpace, String password, String newPassword, String selfDescription) {
        if (StringUtil.isAllEmpty(username, sex, blogSpace, password, newPassword, selfDescription)) {
            return this.error("空参数");
        }
        SysUser user = new SysUser();
        ShiroUser cache = this.getUser();
        user.setId(this.getUserId());
        SysUser temp;
        if (this.isNotEmpty(username)) {
            temp = userService.getUserByName(username);
            if (temp != null) {
                return ResultEntity.error("该用户名已被使用");
            }
            user.setName(username);
            cache.setUsername(username);
        }

        if (this.isNotEmpty(blogSpace)) {
            temp = userService.getUserByBlogSpace(blogSpace);
            if (temp != null) {
                return ResultEntity.error("该博客空间已被使用");
            }
            user.setBlogSpace(blogSpace);
            cache.setBlogSpace(blogSpace);
        }

        if (this.isNotEmpty(sex)) {
            Integer code = SexTypeEnum.codeOf(sex);
            if (code != null) {
                user.setSex(code);
            }
        }

        if (this.isNotEmpty(selfDescription)) {
            user.setSelfDescription(selfDescription);
            cache.setSelfDescription(selfDescription);
        }

        // 密码逻辑判断
        if (this.isNotEmpty(password) && this.isNotEmpty(newPassword)) {
            SysUser temp1 = userService.getById(user.getId());
            String oldPassword = SHIRO.md5(password, temp1.getSalt());
            if (oldPassword.equals(temp1.getPassword())) {
                user.setSalt(SHIRO.fastSalt());
                user.setPassword(SHIRO.md5(newPassword, user.getSalt()));
            } else {
                return ResultEntity.error("原密码错误");
            }
            // 看具体业务咯，用户修改密码之后的操作
        }
        boolean success = userService.updateByIdIgnoreNull(user);
        return returnResult(success, "更新信息失败");
    }
}
