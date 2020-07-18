package site.xiaokui.user.entity.wrapper;

import site.xiaokui.user.entity.SysMenu;
import site.xiaokui.user.service.ServiceFactory;

import java.util.List;

/**
 * @author HK
 * @date 2018-06-11 18:03
 */
public class SysMenuWrapper extends BaseEntityWrapper<SysMenu> {

    public SysMenuWrapper(List<SysMenu> list) {
        super(list);
    }

    public SysMenuWrapper(SysMenu sysMenu) {
        super(sysMenu);
    }

    @Override
    protected void wrap() {
        this.put("parentName", ServiceFactory.me().getMenuName(this.getInt("parentId")));
    }

    @Override
    protected void wrap(SysMenu sysMenu) {
        SysMenuWrapper wrapper = new SysMenuWrapper(sysMenu);
        jsonArray.add(wrapper.jsonObject);
    }
}
