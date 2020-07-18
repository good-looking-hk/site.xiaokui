package site.xiaokui.oauth2.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.WebSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import site.xiaokui.oauth2.entity.ShiroUser;
import site.xiaokui.oauth2.entity.SysUser;
import site.xiaokui.oauth2.service.UserService;

/**
 * @author hk
 */
@Slf4j
@Component
public class ShiroRealm extends AuthorizingRealm {

//    @Autowired
    private UserService userService;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        // 忽略权限信息
        return null;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String username = (String)token.getPrincipal();
        SysUser user = userService.getUserByName(username);
        if(user == null) {
            log.warn("用户登录失败：" + token);
            throw new UnknownAccountException();
        }
        log.info("用户登录成功：" + user);
        ShiroUser shiroUser = userService.wrapShiroUser(user);
        return new SimpleAuthenticationInfo(
                shiroUser,
                user.getPassword(),
                ByteSource.Util.bytes(user.getSalt()),
                this.getName()
        );
    }

    /**
     * 由于使用了自定义的加密方式，因此需要告诉shiro，以确保登录认证通过后的一次对于返回info和处理token的密码凭证断言能够通过
     * {@link org.apache.shiro.realm.AuthenticatingRealm#getAuthenticationInfo(AuthenticationToken token)}方法
     * {@code assertCredentialsMatch(token, info)}
     */
    @Override
    public void setCredentialsMatcher(CredentialsMatcher credentialsMatcher) {
        HashedCredentialsMatcher md5CredentialsMatcher = new HashedCredentialsMatcher() {
            // 暂时先这么做
            @Override
            public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info){
                return true;
            }
        };
        super.setCredentialsMatcher(md5CredentialsMatcher);
    }

    @Override
    public void clearCachedAuthorizationInfo(PrincipalCollection principals) {
        super.clearCachedAuthorizationInfo(principals);
    }

    @Override
    public void clearCachedAuthenticationInfo(PrincipalCollection principals) {
        super.clearCachedAuthenticationInfo(principals);
    }

    @Override
    public void clearCache(PrincipalCollection principals) {
        super.clearCache(principals);
    }

    public void clearAllCachedAuthorizationInfo() {
        getAuthorizationCache().clear();
    }

    public void clearAllCachedAuthenticationInfo() {
        getAuthenticationCache().clear();
    }

    public void clearAllCache() {
        clearAllCachedAuthenticationInfo();
        clearAllCachedAuthorizationInfo();
    }
}
