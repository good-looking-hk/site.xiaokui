package site.xiaokui.module.sys.user.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import site.xiaokui.common.util.SpringContextHolder;

/**
 * @author HK
 * @date 2018-06-21 21:54
 */
@Component
@DependsOn("springContextHolder")
public class ServiceFactory {

    private static final String ROLE_MENU_DEPT_CACHE = "roleDeptCache";

    private MenuService menuService = SpringContextHolder.getBean(MenuService.class);
    private RoleService roleService = SpringContextHolder.getBean(RoleService.class);
    private DeptService deptService = SpringContextHolder.getBean(DeptService.class);

    public static ServiceFactory me() {
        return SpringContextHolder.getBean("serviceFactory");
    }

    @Cacheable(value = ROLE_MENU_DEPT_CACHE)
    public String getMenuName(Integer id) {
        return menuService.getName(id);
    }

    @Cacheable(value = ROLE_MENU_DEPT_CACHE)
    public String getRoleName(Integer id) {
        return roleService.getName(id);
    }
}
