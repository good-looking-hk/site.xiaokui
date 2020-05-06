package site.xiaokui.module.user.dao;

import org.beetl.sql.core.annotatoin.Param;
import org.beetl.sql.core.annotatoin.SqlResource;
import org.beetl.sql.core.mapper.BaseMapper;
import site.xiaokui.module.user.entity.SysDept;

/**
 * @author HK
 * @date 2018-06-09 19:49
 */
@SqlResource("sys.dept")
public interface DeptDao extends BaseMapper<SysDept> {
    /**
     * 根据部门id返回部门名称
     * @param deptId 部门id
     * @return 部门名称
     */
    String getDeptNameByDeptId(@Param("deptId") Integer deptId);
}
