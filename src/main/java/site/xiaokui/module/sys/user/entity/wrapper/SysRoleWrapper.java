package site.xiaokui.module.sys.user.entity.wrapper;

import site.xiaokui.module.base.entity.wrapper.BaseEntityWrapper;
import site.xiaokui.module.sys.user.entity.SysRole;
import site.xiaokui.module.sys.user.service.ServiceFactory;

import java.util.List;

/**
 * @author HK
 * @date 2018-06-10 15:46
 */
public class SysRoleWrapper extends BaseEntityWrapper<SysRole> {

    public SysRoleWrapper(List<SysRole> list) {
        super(list);
    }

    public SysRoleWrapper(SysRole sysRole) {
        super(sysRole);
    }

    @Override
    protected void wrap() {
        this.put("parentName", ServiceFactory.me().getRoleName(this.getInt("parentId")));
    }

    @Override
    protected void wrap(SysRole sysRole) {
        SysRoleWrapper wrapper = new SysRoleWrapper(sysRole);
        jsonArray.add(wrapper.jsonObject);
    }
}
