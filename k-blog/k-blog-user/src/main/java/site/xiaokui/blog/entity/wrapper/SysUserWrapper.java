package site.xiaokui.blog.entity.wrapper;

import cn.hutool.core.date.DateUtil;
import site.xiaokui.blog.entity.SysUser;
import site.xiaokui.blog.service.ServiceFactory;

import java.util.Date;
import java.util.List;

/**
 * SysUser的包装类，添加更多的信息
 * @author HK
 * @date 2018-06-09 20:21
 */
public class SysUserWrapper extends BaseEntityWrapper<SysUser> {

    public SysUserWrapper(List<SysUser> list) {
        super(list);
    }

    public SysUserWrapper(SysUser sysUser) {
        super(sysUser);
    }

    /**
     * Date属性会被转换为Long型，故需要手动转换
     */
    @Override
    protected void wrap() {
        this.put("roleName", ServiceFactory.me().getRoleName(this.getInt("roleId")));
        Object date = this.get("createTime");
        if (date instanceof Long) {
            Date temp = new Date((Long)date);
            this.put("createTime", DateUtil.format(temp, "yyyy-MM-dd HH:mm:ss"));
        }
        this.remove("password").remove("salt");
    }

    @Override
    protected void wrap(SysUser sysUser) {
        SysUserWrapper wrapper = new SysUserWrapper(sysUser);
        jsonArray.add(wrapper.jsonObject);
    }
}
