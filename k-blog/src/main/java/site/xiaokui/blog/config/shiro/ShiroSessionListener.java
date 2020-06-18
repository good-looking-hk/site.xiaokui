//package site.xiaokui.blog.config.shiro;
//
//import lombok.extern.slf4j.Slf4j;
//import org.apache.shiro.session.Session;
//import org.apache.shiro.session.SessionListenerAdapter;
//import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
//import org.apache.shiro.session.mgt.eis.MemorySessionDAO;
//import org.apache.shiro.session.mgt.eis.SessionDAO;
//import org.apache.shiro.subject.SimplePrincipalCollection;
//import site.xiaokui.blog.entity.SysUser;
//
//import java.util.Collection;
//
///**
// * @author HK
// * @date 2018-06-04 21:25
// */
//@Slf4j
//public class ShiroSessionListener extends SessionListenerAdapter {
//
//    private SessionDAO sessionDAO;
//
//    private boolean kickOutBeforeLogin = ShiroConstants.SHIRO_KICK_OUT_BEFORE_LOGIN;
//
//    public ShiroSessionListener(SessionDAO sessionDAO) {
//        if (sessionDAO == null) {
//            throw new NullPointerException("SessionDAO不能为空");
//        }
//        this.sessionDAO = sessionDAO;
//    }
//
//    /**
//     * 根据用户session以及用户信息，判断是否同一用户存在不同的会话，如果有，则根据相应规则，系统只保留一位用户的会话
//     * 对于需要踢出的session，表面上可以简单地通过sessionDAO.delete(session)来实现，但其实不然，因为尽管这个session删除了，
//     * 客户端还是会马上创建一个新的session，从而几乎没做任何操作（还是需要通过shiro的官方渠道，由logout来实现）
//     */
//    @Override
//    public void onStart(Session userSession) {
//        SysUser loginUser = ShiroKit.getInstance().getUser();
//        // 如果用户未登录
//        if (loginUser == null) {
//            log.debug("创建会话(id：{})", userSession.getId());
//            return;
//        }
//
//        // 在sessionDAO中,对于session的操作是直接映射到session实体上的（这里留个纪念）
//        // 针对这里可以做分布式session管理
//        if (sessionDAO instanceof MemorySessionDAO || sessionDAO instanceof EnterpriseCacheSessionDAO) {
//            Collection<Session> sessions = sessionDAO.getActiveSessions();
//            for (Session session : sessions) {
//                SimplePrincipalCollection principalCollection = (SimplePrincipalCollection) session.getAttribute(ShiroConstants.SHIRO_USER_SESSION_KEY);
//                ShiroUser user = (ShiroUser) principalCollection.getPrimaryPrincipal();
//                if (!userSession.getId().equals(session.getId()) && loginUser.getUserId().equals(user.getUserId())) {
//                    if (kickOutBeforeLogin) {
//                        log.info("开始清理原有会话（用户id：{}，会话id：{}）", loginUser.getUserId(), session.getId());
//                        session.setAttribute(ShiroConstants.SHIRO_KICK_OUT_KEY, true);
//                    } else {
//                        log.info("开始清理现有会话（用户id：{}，会话id：{}）", loginUser.getUserId(), session.getId());
//                        userSession.setAttribute(ShiroConstants.SHIRO_KICK_OUT_KEY, true);
//                    }
//                }
//            }
//        }
//    }
//
//    @Override
//    public void onExpiration(Session session) {
//        log.info("会话过期(id：{})" + session.getId());
//    }
//
//    @Override
//    public void onStop(Session session) {
//        log.info("会话停止(id：{})", session.getId());
//    }
//
//    public void setKickOutBeforeLogin(boolean kickOutBeforeLogin) {
//        this.kickOutBeforeLogin = kickOutBeforeLogin;
//    }
//}
