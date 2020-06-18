//package site.xiaokui.blog.config.shiro;
//
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.Setter;
//import lombok.extern.slf4j.Slf4j;
//import net.sf.ehcache.Cache;
//import net.sf.ehcache.CacheManager;
//import net.sf.ehcache.Element;
//import org.apache.shiro.authc.*;
//import org.apache.shiro.authc.credential.CredentialsMatcher;
//import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
//import org.apache.shiro.authz.AuthorizationInfo;
//import org.apache.shiro.authz.SimpleAuthorizationInfo;
//import org.apache.shiro.crypto.hash.Md5Hash;
//import org.apache.shiro.realm.AuthorizingRealm;
//import org.apache.shiro.subject.PrincipalCollection;
//import org.apache.shiro.util.ByteSource;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.util.Date;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//
//
///**
// * Shiro的主要逻辑实现，自定义的Realm可以有多个
// *
// * @author HK
// * @date 2018-05-21 21:01
// */
//@Slf4j
//public class ShiroDbRealm extends AuthorizingRealm {
//
//    @Autowired
//    private ShiroService shiroService;
//
//    /**
//     * 用户尝试密码的临界次数，超过则该IP及该账号被锁特定时间
//     */
//    private static final int MAX_PASSWORD_RETRY = 6;
//
//    /**
//     * 缓存可以依赖于Redis或Ehcache，默认为Ehcache
//     */
//    private Cache passwordRetryCache, rolePermissionCache;
//
//    @Autowired
//    public void setCacheManager(CacheManager cacheManager) {
//        passwordRetryCache = cacheManager.getCache("passwordRetryCache");
//        rolePermissionCache = cacheManager.getCache("rolePermissionCache");
//    }
//
//    /**
//     * 权限认证
//     */
//    @Override
//    @SuppressWarnings("unchecked")
//    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
//        ShiroUser shiroUser = (ShiroUser) principals.getPrimaryPrincipal();
//        Integer roleId = shiroUser.getRoleId();
//
//        Set<String> roleNameSet = new HashSet<>(4);
//
//        // 缓存处理
//        Element list = rolePermissionCache.get(roleId);
//        List<String> permissions;
//        if (list != null) {
//            permissions = (List<String>) list.getObjectValue();
//            log.debug("从缓存查找权限信息，权限数量为{}", permissions.size());
//        } else {
//            permissions = shiroService.findPermissionsByRoleId(roleId);
//            Element element = new Element(roleId, permissions);
//            rolePermissionCache.put(element);
//            log.debug("角色ID({})，从数据库获取权限信息，并加入缓存，权限数量为{}", roleId, permissions.size());
//        }
//
//        String roleName = ServiceFactory.me().getRoleName(roleId);
//        roleNameSet.add(roleName);
//        // 超级管理员是最大的，因此拥有所有角色
//        if (SUPER_ADMIN.equals(roleName)) {
//            roleNameSet.add(ADMIN);
//            roleNameSet.add(USER);
//        }
//
//        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
//        // shiro会去重
//        info.addStringPermissions(permissions);
//        info.addRoles(roleNameSet);
//        return info;
//    }
//
//    /**
//     * 登录认证
//     * 在ehcache中，一个Element闲置的时间若为0，则表示闲置时间为无穷大。当xml配置中指定特定值，且代码中Element也指定了
//     * 特定值，两者冲突时，以xml中指定的值为准
//     */
//    @Override
//    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) throws AuthenticationException {
//        UsernamePasswordToken token = (UsernamePasswordToken) authcToken;
//        // 依次根据用户名，邮箱，手机号进行用户查找
//        SysUser user = shiroService.findUser(token.getUsername());
//        if (user == null) {
//            throw new UnknownAccountException();
//        }
//
//        String userId = String.valueOf(user.getId());
//        String ip = token.getHost();
//        Element lock = passwordRetryCache.get(userId);
//
//        // 检查缓存，如果单个ip尝试单个账户密码失败次数过多，那么该ip+该账户将会被锁定
//        int i = 0;
//        if (lock != null) {
//            i = ((Lock) lock.getObjectValue()).getTimes();
//            String p = ((Lock) lock.getObjectValue()).getIp();
//            if (ip.equals(p) && i >= MAX_PASSWORD_RETRY) {
//                log.info("被锁账号[id：{}，ip：{}]", userId, ip);
//                throw new TooMuchPasswordRetryException("账号密码重试次数过多，请稍后再试");
//            }
//        }
//
//        // 密码校对
//        String password = ShiroKit.getInstance().md5(String.valueOf(token.getPassword()), user.getSalt());
//        if (!password.equals(user.getPassword())) {
//            if (lock == null || i < MAX_PASSWORD_RETRY) {
//                Lock lock1 = new Lock(i + 1, ip);
//                Element element = new Element(userId, lock1);
//                passwordRetryCache.put(element);
//            }
//            throw new IncorrectCredentialsException();
//        }
//        if (!user.getStatus().equals(UserStatusEnum.OK.getCode())) {
//            throw new LockedAccountException();
//        }
//
//        ByteSource credentialsSalt = new Md5Hash(user.getSalt());
//        // 包装成Shiro用户
//        ShiroUser shiroUser = shiroService.wrapUser(user);
//        shiroUser.setCurrentIp(ip);
//        shiroService.updateLoginTimeAndIP(shiroUser.getUserId(), new Date(), ip);
//
//        // 这里有待优化，目前实时权限的检查是先让用户重新登录下，重新缓存
//        Element list = rolePermissionCache.get(user.getRoleId());
//        if (list != null) {
//            log.info("删除缓存权限信息(角色id{})，重新查数据库", user.getRoleId());
//            rolePermissionCache.remove(user.getRoleId());
//        }
//
//        // 参数分别为登录后的subject,原始密码，原始密码盐，realm名字,shiro本身需要一次密码验证
//        return new SimpleAuthenticationInfo(shiroUser, password, credentialsSalt, getName());
//    }
//
//    /**
//     * 由于使用了自定义的加密方式，因此需要告诉shiro，以确保登录认证通过后的一次对于返回info和处理token的密码凭证断言能够通过
//     * {@link org.apache.shiro.realm.AuthenticatingRealm#getAuthenticationInfo(AuthenticationToken token)}方法
//     * {@code assertCredentialsMatch(token, info)}
//     */
//    @Override
//    public void setCredentialsMatcher(CredentialsMatcher credentialsMatcher) {
//        HashedCredentialsMatcher md5CredentialsMatcher = new HashedCredentialsMatcher();
//        md5CredentialsMatcher.setHashAlgorithmName(ShiroKit.ALGORITHM_NAME);
//        md5CredentialsMatcher.setHashIterations(ShiroKit.HASH_ITERATIONS);
//        // 必须是super
//        super.setCredentialsMatcher(md5CredentialsMatcher);
//    }
//
//    @Setter
//    @Getter
//    @AllArgsConstructor
//    private static class Lock {
//        Integer times;
//        String ip;
//    }
//}
