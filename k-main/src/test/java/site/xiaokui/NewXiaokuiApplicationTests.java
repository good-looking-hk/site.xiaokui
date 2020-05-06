//package site.xiaokui;
//
//import org.beetl.sql.core.query.Query;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//import site.xiaokui.module.base.service.RedisService;
//import site.xiaokui.module.sys.blog.entity.BlogDetailList;
//import site.xiaokui.module.sys.blog.entity.SysBlog;
//import site.xiaokui.module.sys.blog.service.BlogService;
//import site.xiaokui.module.sys.blog.util.BlogUtil;
//import site.xiaokui.module.sys.user.entity.SysUser;
//import site.xiaokui.module.sys.user.service.UserService;
//
//import java.lang.reflect.Array;
//import java.util.ArrayList;
//import java.util.List;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest
//public class NewXiaokuiApplicationTests {
//
//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private RedisService redisService;
//
//    @Autowired
//    private BlogService blogService;
//
//    @Test
//    public void test2() {
//        Query<SysBlog> query = blogService.createQuery();
//        query.andEq("user_id", 1).desc("create_time").limit(1, 10);
//        List<SysBlog> blogs = blogService.query(query);
//        for (SysBlog s : blogs) {
//            System.out.println(s.toSimpleString());
//        }
//    }
//
//    @Test
//    public void test() {
//        String s = "werrfasdfds";
//        redisService.set("qqq", s, 60);
//        System.out.println(redisService.get("qqq", String.class));
//
//        List<SysBlog> list = blogService.all();
//        System.out.println(list.size() + "===========");
//
//        Query<SysBlog> query = blogService.createQuery();
//        query.andEq("user_id", 1).orderBy("create_time").limit(1, 10);
//        List<SysBlog> blogs = blogService.query(query);
//
//        redisService.set(1 + "_rs", blogs, 60);
//        ArrayList a;
//        List<SysBlog> blogs1 = redisService.get(1 + "_rs", ArrayList.class);
//        System.out.println("============================");
//        System.out.println(blogs.size());
//        System.out.println(blogs1.size());
//        System.out.println(blogs.toString());
//        System.out.println(blogs1.toString());
//        System.out.println(blogs.get(1));
//        System.out.println(blogs1.get(1));
//        System.out.println(blogs.get(9));
//        System.out.println(blogs1.get(9));
//
//    }
//
//    @Test
//    public void test1() {
////        blogService.recentUpload(1);
//        redisService.get(1 + "_rs", String.class);
//    }
//
//    @Test
//    public void contextLoads() {
//        System.out.println("====================");
//    }
//}
