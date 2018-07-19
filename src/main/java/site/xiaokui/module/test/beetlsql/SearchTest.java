package site.xiaokui.module.test.beetlsql;

import cn.hutool.json.JSONObject;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.query.Query;
import site.xiaokui.module.sys.csms.GoodsService;
import site.xiaokui.module.sys.user.entity.SysMenu;
import site.xiaokui.module.sys.user.entity.SysUser;
import site.xiaokui.module.base.service.BaseService;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author HK
 * @date 2018-06-19 15:56
 */
public class SearchTest {

    static SQLManager sqlManager = Sql.getSqlManager();

    public static void main(String[] args) {
//        selectAll();
//        query();
//        date();
//        limit();
        fruit();
    }

    private static void selectAll() {
        List<SysMenu> list = sqlManager.all(SysMenu.class);
        Sql.print(list);
    }

    private static void query() {
        List<SysMenu> list = sqlManager.query(SysMenu.class).andLike("code", "%test%").select();
        Sql.print(list);

        List<SysMenu> list1 = sqlManager.query(SysMenu.class).andLike("name", "%菜单%").andNotEq("id", 2).select();
        Sql.print(list1);

        List<SysMenu> list2 = sqlManager.query(SysMenu.class).andLike("code", "menu").andNotEq("id", 2).select();
        Sql.print(list2);

        List<SysMenu> list3 = sqlManager.query(SysMenu.class).andLike("code", "%%").select();
        Sql.print(list3);

        List<SysUser> list4 = sqlManager.query(SysUser.class).andBetween("create_time", "2016-01-29", "2019-01-29 08:49:53").select();
        Sql.print(list4);

        String key = "";
        Query<SysUser> userQuery = sqlManager.query(SysUser.class);
        List<SysUser> list5 = userQuery.andLike("name", "%" + key + "%").orLike("email", "%" + key + "%").andBetween(
                "create_time", "2018-05-24", "2018-06-01 08:49:53").select();
        Sql.print(list5);

        Query<SysUser> userQuery1 = sqlManager.query(SysUser.class);
        List<SysUser> list6 = userQuery1.andBetween("create_time", "2018-05-24", "2018-06-01")
                .andLike("name", "%%").orLike("name", "%%")
                .select();
        Sql.print(list6);
    }

    private static void date() {
        SysUser user = sqlManager.query(SysUser.class).select().get(0);
        System.out.println(user.getCreateTime());
        Date date = new Date();
        System.out.println(date);

        Map<String, Object > map = new HashMap<>(4);
        map.put("1", date);
        Object o = map.get("1");
        System.out.println(o);
        System.out.println(o.getClass().getName());

        JSONObject object = new JSONObject(user);
        System.out.println(object);
    }

    private static void limit() {
        Base baseService = new Base();
        baseService.setSqlManager(sqlManager);
        List<SysUser> list = baseService.all(2, 4);
        Sql.print(list);

        List<SysUser> list1 = baseService.top(4);
        Sql.print(list1);

        SysUser user = baseService.top(1).get(0);
        System.out.println(user);

        List<SysUser> list2 = baseService.bottom(3);
        Sql.print(list2);
        System.out.println(baseService.bottom(1).get(0));

        SysUser user1 = baseService.preEntity(1);
        System.out.println(user1);
        System.out.println(baseService.preEntity(2));
        System.out.println(baseService.nextEntity(1));
        System.out.println(baseService.nextEntity(100));
    }

    private static void fruit() {
        GoodsService fruitService = new GoodsService();
        fruitService.setSqlManager(sqlManager);
    }

    private static class Base extends BaseService<SysUser> {}
}
