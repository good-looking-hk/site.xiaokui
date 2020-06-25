package site.xiaokui.user.controller;

import org.apache.shiro.subject.Subject;
import site.xiaokui.base.entity.ResultEntity;
import site.xiaokui.base.util.StringUtil;
import site.xiaokui.user.config.shiro.ShiroKit;
import site.xiaokui.user.config.shiro.ShiroUser;
import site.xiaokui.user.entity.SysUser;
import site.xiaokui.user.entity.enums.RoleTypeEnum;
import site.xiaokui.user.entity.enums.SexTypeEnum;
import site.xiaokui.user.entity.enums.UserStatusEnum;

import java.util.Date;


/**
 * 封装对于request和response的一些基础的方法
 * @author HK
 * @date 2018-05-21 17:28
 */
public class BaseController implements ConstantController {

    /**
     * 是否开启严格模式，开启这个将启动对参数的严格检查，防止客户端做假(有待后续完善)
     */
    protected static final boolean STRICT_MODE = true;

    protected final static ShiroKit SHIRO = ShiroKit.getInstance();

    protected Subject getSubject() {
        return SHIRO.getSubject();
    }

    protected ShiroUser getUser() {
        return SHIRO.getUser();
    }

    protected Long getUserId() {
        if (getUser() == null) {
            return null;
        }
        return SHIRO.getUser().getUserId();
    }

    protected String getUsername() {
        if (getUser() == null) {
            return null;
        }
        return SHIRO.getUser().getUsername();
    }

    protected Integer getRoleId() {
        if (getUser() == null) {
            return null;
        }
        return SHIRO.getUser().getRoleId();
    }

    /**
     * 为用户分配一些默认的属性，如十位随机密码盐，默认头像，默认个性前面，状态，性别，状态，默认的用户角色
     */
    protected SysUser initDefaultUser(String username, String email, String password) {
        SysUser user = new SysUser();
        user.setName(username);
        user.setEmail(email);
        user.setSalt(SHIRO.fastSalt());
        user.setPassword(SHIRO.md5(password, user.getSalt()));
        user.setCreateTime(new Date());
        user.setStatus(UserStatusEnum.OK.getCode());
        user.setSex(SexTypeEnum.UNKNOWN.getCode());
        user.setRoleId(RoleTypeEnum.USER.getCode());
        return user;
    }

    protected ResultEntity ok() {
        return ResultEntity.ok();
    }

    protected ResultEntity ok(String msg) {
        return ResultEntity.ok(msg);
    }

    protected ResultEntity error(String msg) {
        return ResultEntity.error(msg);
    }

    protected ResultEntity paramError(Object... strs) {
        return ResultEntity.paramError(strs);
    }

    protected ResultEntity returnResult(boolean success) {
        if (!success) {
            return ResultEntity.failed();
        }
        return ResultEntity.ok();
    }

    protected ResultEntity returnResult(boolean success, String failedMsg) {
        if (!success) {
            return ResultEntity.failed(failedMsg);
        }
        return ResultEntity.ok();
    }

    protected ResultEntity returnResult(boolean success, String failedMsg, String successMsg) {
        if (!success) {
            return ResultEntity.failed(failedMsg);
        }
        return ResultEntity.ok(successMsg);
    }

    protected boolean isEmpty(final String str) {
        return StringUtil.isEmpty(str);
    }

    protected boolean isNotEmpty(final String str) {
        return !isEmpty(str);
    }
}
