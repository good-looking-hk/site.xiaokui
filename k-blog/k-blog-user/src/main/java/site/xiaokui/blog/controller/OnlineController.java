package site.xiaokui.blog.controller;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import site.xiaokui.blog.config.shiro.ShiroConstants;
import site.xiaokui.blog.config.shiro.ShiroUser;
import site.xiaokui.blog.constant.UserConstants;
import site.xiaokui.blog.entity.OnlineUser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * @author HK
 * @date 2018-06-19 23:08
 */
@Controller
@RequestMapping(UserConstants.ONLINE_PREFIX)
public class OnlineController {

    @Autowired
    private SessionDAO sessionDAO;

    /**
     * MENU_PREFIX字段默认为 /sys/online
     */
    private static final String ONLINE_PREFIX = UserConstants.ONLINE_PREFIX;

    /**
     * 会话管理界面
     */
    @RequiresPermissions(ONLINE_PREFIX)
    @GetMapping("/manage")
    public String manage() {
        return ONLINE_PREFIX + "/manage";
    }

    /**
     * 获取对应的菜单，含参数的调用均为客户端的异步查找
     */
    @GetMapping("")
    @ResponseBody
    public List<OnlineUser> list() {
        List<OnlineUser> list = new ArrayList<>();
        Collection<Session> sessions = sessionDAO.getActiveSessions();
        ShiroUser user;
        OnlineUser onlineUser;
        for (Session session : sessions) {
            SimplePrincipalCollection principalCollection = (SimplePrincipalCollection) session.getAttribute(ShiroConstants.SHIRO_USER_SESSION_KEY);
            user = (ShiroUser) principalCollection.getPrimaryPrincipal();
            System.out.println(user.toString());
            System.out.println(session.toString());

            onlineUser = new OnlineUser();
            onlineUser.setId((String)session.getId());
            onlineUser.setUsername(user.getUsername());
            onlineUser.setIp(user.getCurrentIp());
            System.out.println(session.getHost());
            System.out.println(session.getTimeout());

            onlineUser.setStartTimestamp(session.getStartTimestamp());
            onlineUser.setLastAccessTime(session.getLastAccessTime());

            list.add(onlineUser);
        }
        return list;
    }
}
