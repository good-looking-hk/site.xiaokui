package site.xiaokui.base.service;

import lombok.Getter;
import lombok.Setter;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLReady;
import org.beetl.sql.core.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import site.xiaokui.common.util.StringUtil;
import site.xiaokui.entity.BaseEntity;

import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 通过BeetlSql的SqlManager来封装一些通用功能的操作
 * 按道理，这一个类基本上就可以完成所有的数据库操作了，但为了后续的扩展起见，还是分层（虽然，我觉得这个分层真的有点啰嗦）
 *
 * @author HK
 * @date 2018-06-09 21:11
 */
public class BaseService<T extends BaseEntity> {

    /**
     * 需要子类注入，否则不能正常使用
     */
    @Getter
    @Setter
    @Autowired
    protected SQLManager sqlManager;

    /**
     * 通用插入，插入一个实体对象到数据库，所有字段将参与操作，除非你使用ColumnIgnore注解
     *
     * @param t 插入对象
     * @return 影响行数
     */
    public boolean insert(T t) {
        return sqlManager.insert(t) == 1;
    }

    /**
     * 通用插入，获取自增主键
     *
     * @param t 插入对象
     * @return 影响行数
     */
    public boolean insertReturnKey(T t) {
        return sqlManager.insert(t, true) == 1;
    }

    /**
     * 等同于insertTemplate(t, false)
     *
     * @param t 插入对象
     * @return 影响行数
     */
    public boolean insertIgnoreNull(T t) {
        return sqlManager.insertTemplate(t) == 1;
    }

    /**
     * 根据类 T，插入数据，忽略null值，回去自增主键
     *
     * @param t 插入对象
     * @return 影响行数
     */
    public boolean insertIgnoreNullReturnKey(T t) {
        return sqlManager.insert(t, true) == 1;
    }

    /**
     * 根据id删除数据
     *
     * @param t 传入对象
     * @return 影响行数
     */
    public boolean deleteById(T t) {
        return sqlManager.deleteObject(t) == 1;
    }

    /**
     * 根据id删除数据
     *
     * @param id 删除id
     * @return 影响行数
     */
    public boolean deleteById(Integer id) {
        Class<T> cls = getCurrentEntityClass();
        return sqlManager.deleteById(cls, id) == 1;
    }

    /**
     * 根据id更新对象
     *
     * @param t 插入对象
     * @return 影响行数
     */
    public boolean updateById(T t) {
        return sqlManager.updateById(t) == 1;
    }


    /**
     * 根据id更新对象，忽略null值
     *
     * @param t 插入对象
     * @return 影响行数
     */
    public boolean updateByIdIgnoreNull(T t) {
        return sqlManager.updateTemplateById(t) == 1;
    }

    /**
     * 根据类 T，进行map的精确匹配查找
     *
     * @param params 查找键值对
     * @return 返回模糊查找的匹配结果
     */
    public List<T> termQuery(Map<String, Object> params) {
        Class<T> cls = getCurrentEntityClass();
        Query<T> query = sqlManager.query(cls);
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            query.andLike(entry.getKey(), String.valueOf(entry.getValue()));
        }
        return query.select();
    }

    /**
     * 创建Query对象
     *
     * @return Query对象
     */
    public Query<T> createQuery() {
        Class<T> cls = getCurrentEntityClass();
        return sqlManager.query(cls);
    }

    /**
     * 使用query完成复杂的查询
     *
     * @param query query对象
     * @return 结果集
     */
    public List<T> query(Query<T> query) {
        return query.select();
    }

    /**
     * 根据类 T，进行map的模糊匹配查找
     *
     * @param params 查找键值对
     * @return 返回模糊查找的匹配结果
     */
    public List<T> fuzzyQuery(Map<String, Object> params) {
        Class<T> cls = getCurrentEntityClass();
        Query<T> query = sqlManager.query(cls);
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            query.andLike(entry.getKey(), "%" + String.valueOf(entry.getValue()) + "%");
        }
        return query.select();
    }

    /**
     * 根据主键查找对象
     *
     * @param id 主键id
     * @return 没有则返回null
     */
    public T getById(Integer id) {
        Class<T> cls = getCurrentEntityClass();
        return sqlManager.single(cls, id);
    }

    public List<T> inIds(String column, Collection<?> c) {
        return query(createQuery().andIn(column, c));
    }

    /**
     * 匹配查找对象t里面的非空属性
     *
     * @param t 传入对象
     * @return 匹配结果集
     */
    public List<T> match(T t) {
        return sqlManager.template(t);
    }

    /**
     * 匹配单列数据
     *
     * @param t 传入参数
     * @return 没有则返回null
     */
    public T matchOne(T t) {
        return sqlManager.templateOne(t);
    }

    public <K> List<K> matchOneColumn(Class<K> cls, String column) {
        String sql = getSelectSql(column) + getFromSql();
        return sqlManager.execute(sql, cls, null);
    }

    public T preEntity(int id) {
        Query<T> query = createQuery();
        List<T> list = query.andLess("id", id).limit(1, 1).select();
        if (list == null || list.size() == 0) {
            return null;
        }
        return list.get(0);
    }

    public T nextEntity(int id) {
        Query<T> query = createQuery();
        List<T> list = query.andGreat("id", id).limit(1, 1).select();
        if (list == null || list.size() == 0) {
            return null;
        }
        return list.get(0);
    }

    public T bottom() {
        return bottom(1).get(0);
    }

    public List<T> bottom(int n) {
        Query<T> query = createQuery();
        return query.desc("id").limit(1, n).select();
    }

    public T top() {
        return top(1).get(0);
    }

    public List<T> top(int n) {
        return all(1, n);
    }

    /**
     * start默认从1开始，beetlsql会处理好数据库之间的差异性，假设传入值为2和4，那么在mysql种形成的语句大致如下
     * select * from `sys_user` limit 1 , 4(mysql中默认可以从0开始)
     */
    public List<T> all(int start, int size) {
        Class<T> cls = getCurrentEntityClass();
        return sqlManager.all(cls, start, size);
    }

    private String getSelectSql(String column) {
        return " SELECT " + column + " ";
    }

    private String getFromSql() {
        String tableName = StringUtil.toUnderlineCase(getClass().getSimpleName());
        return " FROM " + tableName + " ";
    }

    /**
     * 获取所有实体数据
     *
     * @return 所有数据
     */
    public List<T> all() {
        Class<T> cls = getCurrentEntityClass();
        return sqlManager.all(cls);
    }

    /**
     * 根据id寻找对应的name，这个方法的实现大致有两种，一是只查单个字段，二是查找所有字段
     *
     * @param id id
     * @return 返回所对应的名称，没有则返回null
     */
    public String getName(Integer id) {
        T t = getById(id);
        return t == null ? null : t.getName();
    }

    /**
     * 最好传入args，预防sql注入
     */
    public int executeDelSql(String sql, Object... args) {
        return sqlManager.executeUpdate(new SQLReady(sql, args));
    }

    /**
     * 获取当前注入泛型T的类型
     *
     * @return 具体类型
     */
    @SuppressWarnings("unchecked")
    private Class<T> getCurrentEntityClass() {
        return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }
}
