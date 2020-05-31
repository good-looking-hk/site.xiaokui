package site.xiaokui.blog.config.shiro;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.apache.shiro.session.mgt.eis.MemorySessionDAO;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Map;

/**
 * 在线会话过滤器，可通过此过滤器限制同一账号的不同登录行为，如是否运行同时登录多账号、前后两次登录以哪次登录为准等情况
 * shiro默认使用的sessionDao是MemorySessionDAO，默认的session类型为SimpleSession，且默认情况下，session存储着两个值，
 * 分别为{@link org.apache.shiro.subject.support.DefaultSubjectContext#AUTHENTICATED_SESSION_KEY}
 * 和{@link org.apache.shiro.subject.support.DefaultSubjectContext#PRINCIPALS_SESSION_KEY}，两者分别代表用户
 * 是否认证(Boolean)以及认证用户的个人信息({@link SimplePrincipalCollection})
 * </p>
 * 这里的Subject类型默认为{@link org.apache.shiro.web.subject.support.WebDelegatingSubject}
 * subject.getSession类型默认为{@link org.apache.shiro.subject.support.DelegatingSubject.StoppingAwareProxiedSession}
 * sessionDao中session默认类型{@link org.apache.shiro.session.mgt.SimpleSession}
 * request默认类型{@link org.apache.shiro.web.servlet.ShiroHttpServletRequest}
 *
 * @author HK
 * @date 2018-06-02 13:41
 */
@Slf4j
public class OnlineSessionFilter extends AccessControlFilter {

    private SessionDAO sessionDAO;

    private String kickOutUrl = ShiroConstants.SHIRO_KICK_OUT_URL;
    
    private final static String kickOutKey = ShiroConstants.SHIRO_KICK_OUT_KEY;

    public OnlineSessionFilter(SessionDAO sessionDAO) {
        if (sessionDAO == null) {
            throw new NullPointerException("SessionDAO不能为空");
        }
        this.sessionDAO = sessionDAO;
    }

    /**
     * @return true：如果请求允许正常通过该过滤器,反之进入{@link #onAccessDenied}方法
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        Subject subject = getSubject(request, response);
        // 没有登录授权，且没有记住我，那么拒绝通过
        if (!subject.isAuthenticated() && !subject.isRemembered()) {
            return false;
        }
        Session userSession = subject.getSession();
        // 如果该用户被标记为挤出状态
        if (userSession.getAttribute(kickOutKey) != null && (Boolean) userSession.getAttribute(kickOutKey)) {
            return false;
        }
        return true;
    }

    /**
     * 具体逻辑见{@link AccessControlFilter#onPreHandle}方法
     *
     * @return true:还需进一步处理(被其他过滤器),反之子类直接处理请求结果
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        Subject subject = getSubject(request, response);
        Session userSession = subject.getSession();
        if (userSession.getAttribute(kickOutKey) != null && (Boolean) userSession.getAttribute(kickOutKey)) {
            ShiroUser loginUser = (ShiroUser) subject.getPrincipals().getPrimaryPrincipal();
            log.warn("用户被踢出，清理会话（用户id：{}，会话id：{}）", loginUser.getUserId(), userSession.getId());
            subject.logout();
            saveRequest(request);
            WebUtils.issueRedirect(request, response, kickOutUrl);
            return false;
        }
        return true;
    }

    public final String getFilterName() {
        return OnlineSessionFilter.class.getName();
    }

    public void setKickOutUrl(String kickOutUrl) {
        this.kickOutUrl = kickOutUrl;
    }

}
