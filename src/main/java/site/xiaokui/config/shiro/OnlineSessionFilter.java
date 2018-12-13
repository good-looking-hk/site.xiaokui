package site.xiaokui.config.shiro;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.apache.shiro.session.mgt.eis.MemorySessionDAO;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.util.WebUtils;
import site.xiaokui.module.sys.user.UserConstants;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Map;

/**
 * shiro默认使用的sessionDao是MemorySessionDAO，默认的session类型为SimpleSession，且默认情况下，session存储着两个值，
 * 分别为{@link org.apache.shiro.subject.support.DefaultSubjectContext#AUTHENTICATED_SESSION_KEY}
 * 和{@link org.apache.shiro.subject.support.DefaultSubjectContext#PRINCIPALS_SESSION_KEY}，两者分别代表用户
 * 是否认证(Boolean)以及认证用户的个人信息({@link org.apache.shiro.subject.SimplePrincipalCollection})
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

    private static final String USER_KEY = ShiroConstants.SHIRO_USER_SESSION_KEY;

    private static final String KICK_OUT_KEY = "forceOut";

    /**
     * 挤出后到的地址
     */
    private String forceOutUrl = "/login?forceOut=true";

    /**
     * 默认false为挤出之前登录的用户
     */
    private boolean forceOutAfter = false;

    private SessionDAO sessionDAO;

    public final String getFilterName() {
        return "online";
    }

    /**
     * @return true：如果请求允许正常通过该过滤器,反之进入{@link #onAccessDenied}方法
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        Subject subject = getSubject(request, response);
        // 没有登录授权 且没有记住我
        if (!subject.isAuthenticated() && !subject.isRemembered()) {
            return false;
        }
        Session userSession = subject.getSession();
        if (userSession.getAttribute(KICK_OUT_KEY) != null && (Boolean) userSession.getAttribute(KICK_OUT_KEY)) {
            return false;
        }
        ShiroUser loginUser = (ShiroUser) subject.getPrincipals().getPrimaryPrincipal();

        // 在sessionDAO中,对于session的操作是直接映射到session实体上的（这里留个纪念）
        if (sessionDAO instanceof MemorySessionDAO || sessionDAO instanceof EnterpriseCacheSessionDAO) {
            Collection<Session> sessions = sessionDAO.getActiveSessions();
            for (Session session : sessions) {
                handleSession(userSession, loginUser, session);
            }
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
        if (userSession.getAttribute(KICK_OUT_KEY) != null && (Boolean) userSession.getAttribute(KICK_OUT_KEY)) {
            ShiroUser loginUser = (ShiroUser) subject.getPrincipals().getPrimaryPrincipal();
            log.debug("清理会话（用户id：{}，会话id：{}）", loginUser.getUserId(), userSession.getId());
            subject.logout();
            saveRequest(request);
            WebUtils.issueRedirect(request, response, forceOutUrl);
            return false;
        }
        return true;
    }

    /**
     * 根据用户session以及用户信息，判断是否同一用户存在不同的会话，如果有，则根据相应规则，系统只保留一位用户的会话
     * 对于需要踢出的session，表面上可以简单地通过sessionDAO.delete(session)来实现，但其实不然，因为尽管这个session删除了，
     * 客户端还是会马上创建一个新的session，从而几乎没做任何操作（还是需要通过shiro的官方渠道，由logout来实现）
     */
    private void handleSession(Session userSession, ShiroUser loginUser, Session session) {
        // 对于已经标记为踢出的session，不再比较，该session会在下次操作时自动失效
        if (session.getAttribute(USER_KEY) != null && session.getAttribute(KICK_OUT_KEY) == null) {
            SimplePrincipalCollection principalCollection = (SimplePrincipalCollection) session.getAttribute(USER_KEY);
            ShiroUser user = (ShiroUser) principalCollection.getPrimaryPrincipal();
            if (!userSession.getId().equals(session.getId()) && loginUser.getUserId().equals(user.getUserId())) {
                if (!forceOutAfter) {
                    log.debug("开始清理原有会话（用户id：{}，会话id：{}）", loginUser.getUserId(), session.getId());
                    session.setAttribute(KICK_OUT_KEY, true);
                } else {
                    log.debug("开始清理现有会话（用户id：{}，会话id：{}）", loginUser.getUserId(), session.getId());
                    userSession.setAttribute(KICK_OUT_KEY, true);
                }
            }
        }
    }

    private void out(ServletResponse hresponse, Map<String, String> resultMap) {
        try {
            hresponse.setCharacterEncoding("UTF-8");
            PrintWriter out = hresponse.getWriter();
            out.println(JSON.toJSONString(resultMap));
            out.flush();
            out.close();
        } catch (Exception e) {
            System.err.println("KickoutSessionFilter.class 输出JSON异常，可以忽略。");
        }
    }

    public void setForceOutUrl(String forceOutUrl) {
        this.forceOutUrl = forceOutUrl;
    }

    public void setForceOutAfter(boolean forceOutAfter) {
        this.forceOutAfter = forceOutAfter;
    }

    public void setSessionDAO(SessionDAO sessionDAO) {
        this.sessionDAO = sessionDAO;
    }
}
