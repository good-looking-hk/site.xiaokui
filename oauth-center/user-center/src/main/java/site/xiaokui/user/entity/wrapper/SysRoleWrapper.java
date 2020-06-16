package site.xiaokui.user.entity.wrapper;

import cn.hutool.core.date.DateUtil;
import site.xiaokui.entity.wrapper.BaseEntityWrapper;
import site.xiaokui.user.entity.SysRole;
import site.xiaokui.user.service.ServiceFactory;

import java.util.Date;
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
        Object date = this.get("createTime");
        if (date instanceof Long) {
            Date temp = new Date((Long)date);
            this.put("createTime", DateUtil.format(temp, "yyyy-MM-dd HH:mm:ss"));
        }
    }

    @Override
    protected void wrap(SysRole sysRole) {
        SysRoleWrapper wrapper = new SysRoleWrapper(sysRole);
        jsonArray.add(wrapper.jsonObject);
    }
}