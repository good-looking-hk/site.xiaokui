package site.xiaokui.config.shiro;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author HK
 * @date 2018-06-04 21:25
 */
@Slf4j
public class ShiroSessionListener extends SessionListenerAdapter {

    @Override
    public void onStart(Session session) {
        log.debug("创建会话(id：{})", session.getId());
    }

    @Override
    public void onExpiration(Session session) {
        log.debug("会话过期(id：{})" + session.getId());

    }

    @Override
    public void onStop(Session session) {
        log.debug("会话停止(id：{})", session.getId());
    }

}
