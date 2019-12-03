package site.xiaokui.module.sys.blog.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.beetl.sql.core.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import site.xiaokui.XiaokuiCache;
import site.xiaokui.common.util.StringUtil;
import site.xiaokui.config.shiro.ShiroKit;
import site.xiaokui.module.base.BaseConstants;
import site.xiaokui.module.base.controller.BaseController;
import site.xiaokui.module.sys.blog.BlogConstants;
import site.xiaokui.module.sys.blog.entity.BlogDetailList;
import site.xiaokui.module.sys.blog.entity.BlogStatusEnum;
import site.xiaokui.module.sys.blog.entity.BlogUser;
import site.xiaokui.module.sys.blog.entity.SysBlog;
import site.xiaokui.module.sys.blog.service.BlogService;
import site.xiaokui.module.sys.blog.util.BlogFileHelper;
import site.xiaokui.module.sys.blog.util.BlogUtil;
import site.xiaokui.module.sys.user.entity.SysUser;
import site.xiaokui.module.sys.user.service.UserService;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * @author HK
 * @date 2018-06-25 14:16
 */
@Slf4j
@Controller("BLOG:INDEX")
@RequestMapping(BlogConstants.PREFIX)
public class IndexController extends BaseController {

    private static final String BLOG_PREFIX = BlogConstants.PREFIX;

    private static final String BLOG_INDEX = BLOG_PREFIX + INDEX;

    private static final String SHOW_BLOG = BLOG_PREFIX + BLOG_PREFIX;

    @Autowired
    private UserService userService;

    @Autowired
    private BlogService blogService;

    @Autowired
    private XiaokuiCache xiaokuiCache;

    /**
     * 默认首页为第一位注册的博客空间
     */
    @GetMapping({EMPTY, INDEX})
    public String index() {
        // 如果数据库设置了首页
        String index = xiaokuiCache.getBlogIndex();
        System.out.println(index);
        if (StringUtil.isNotBlank(index)) {
            return FORWARD + index;
        }
        SysUser user = userService.top();
        if (user == null) {
            return FORWARD_ERROR;
        }
        // 这里多了一次数据库查询
        if (this.isNotEmpty(user.getBlogSpace())) {
            return REDIRECT + BLOG_PREFIX + "/" + user.getBlogSpace() + "?layout=time";
        }
        return REDIRECT + BLOG_PREFIX + "/" + user.getId() + "?layout=time";
    }

    /**
     * 用户的个人博客空间
     *
     * @param blogSpace 如果用户没有指定个人博客空间名，那么默认为用户id
     * @param layout    值为null或time或dir
     * @param type      值为null或pub或pro或pri
     */
    @GetMapping("/{blogSpace}")
    public String blogSpace(@PathVariable String blogSpace, Model model, String layout, String type, String passwd) {
        if (this.isEmpty(blogSpace)) {
            return ERROR;
        }
        SysUser user = trueUser(blogSpace);
        if (user == null) {
            return ERROR;
        }
        boolean proCheckPass = false;
        if (passwd != null && ShiroKit.getInstance().md5(passwd, user.getSalt()).equals(user.getProPassword())) {
            log.info("受保护访问通过，userId为{}，passwd为{}", user.getId(), passwd);
            proCheckPass = true;
        }
        List<SysBlog> blogList = blogService.listBlogByUserId(user.getId());
        BlogDetailList details = BlogUtil.resolveBlogList(blogList, blogSpace, false);
        BlogUser blogUser = new BlogUser(user);

        commonConfig(model);
        // 如果指定了layout布局
        String result = dealLayout(layout, type, model, proCheckPass, details, blogUser);
        if (result != null) {
            return result;
        }

        // 没有指定layout布局
        if (BlogConstants.BLOG_TYPE_PRO.equals(type) && proCheckPass) {
            List<List<SysBlog>> protectedList = details.getProtectedList();
            blogUser.setBlogList(protectedList);
            blogUser.setPro(details.getPro());
            blogUser.setPub(details.getPub());
            blogUser.setPageTotal(details.getPro());
            blogUser.setDirCount(protectedList.size());
        } else if (BlogConstants.BLOG_TYPE_PRI.equals(type)) {
            // TODO
            return ERROR;
        } else {
            List<List<SysBlog>> dirLists = details.getPublicList();
            List<SysBlog> recentUpload = blogService.recentUpload(user.getId(), user.getBlogSpace());
            blogList.removeIf(new Predicate<SysBlog>() {
                @Override
                public boolean test(SysBlog blog) {
                    return !blog.getStatus().equals(BlogStatusEnum.PUBLIC.getCode());
                }
            });
            Map<String, Double> map = blogService.mostView(user.getId(), blogList);
            List<SysBlog> mostView = new ArrayList<>();
            // 从内存运算读取性能远远好过数据库读
            for (String i : map.keySet()) {
                for (SysBlog b : blogList) {
                    if (b.getId().toString().equals(i)) {
                        b.setViewCount(map.get(i).intValue());
                        mostView.add(b);
                    }
                }
            }
            blogUser.setBlogList(dirLists);
            blogUser.setPub(details.getPub());
            blogUser.setPro(details.getPro());
            blogUser.setPageTotal(details.getPub());
            blogUser.setDirCount(dirLists.size());
            model.addAttribute("recent", recentUpload);
            model.addAttribute("most", mostView);
        }
        model.addAttribute("user", blogUser);
        return BLOG_INDEX ;
    }

    private String dealLayout(String layout, String type, Model model, boolean proCheckPass, BlogDetailList details, BlogUser blogUser) {
        blogUser.setPub(details.getPub());
        blogUser.setPro(details.getPro());
        // 日期布局
        if (BlogConstants.BLOG_LAYOUT_TIME.equals(layout)) {
            if (BlogConstants.BLOG_TYPE_PRI.equals(type)) {
                model.addAttribute("titles", details.getPriCreateYears());
                model.addAttribute("lists", details.getPriCreateTimeList());
                blogUser.setPageTotal(details.getPri());
                blogUser.setDirCount(details.getPriCreateYears().size());
            } else if (BlogConstants.BLOG_TYPE_PRO.equals(type) && proCheckPass) {
                model.addAttribute("titles", details.getProCreateYears());
                model.addAttribute("lists", details.getProCreateTimeList());
                blogUser.setPageTotal(details.getPro());
                blogUser.setDirCount(details.getProCreateYears().size());
            } else {
                model.addAttribute("titles", details.getPubCreateYears());
                model.addAttribute("lists", details.getPubCreateTimeList());
                blogUser.setPageTotal(details.getPub());
                blogUser.setDirCount(details.getPubCreateYears().size());
            }
            model.addAttribute("user", blogUser);
            return BLOG_INDEX + "1";
        } else if (BlogConstants.BLOG_LAYOUT_DIR.equals(layout)) {
            // 目录布局
            if (BlogConstants.BLOG_TYPE_PRI.equals(type)) {
                model.addAttribute("titles", details.getPriDir());
                model.addAttribute("lists", details.getPrivateList());
                blogUser.setPageTotal(details.getPro());
                blogUser.setDirCount(details.getPriDir().size());
            } else if (BlogConstants.BLOG_TYPE_PRO.equals(type) && proCheckPass) {
                model.addAttribute("titles", details.getProDir());
                model.addAttribute("lists", details.getProtectedList());
                blogUser.setPageTotal(details.getPro());
                blogUser.setDirCount(details.getProDir().size());
            } else {
                model.addAttribute("titles", details.getPubDir());
                model.addAttribute("lists", details.getPublicList());
                blogUser.setPageTotal(details.getPub());
                blogUser.setDirCount(details.getPubDir().size());
            }
            model.addAttribute("user", blogUser);
            return BLOG_INDEX + "1";
        }
        return null;
    }

    /**
     * 根据id查找博客
     */
    @GetMapping("/{blogSpace}/{id}")
    public String showBlog(@PathVariable String blogSpace, @PathVariable Integer id, Model model) {
        SysUser user = trueUser(blogSpace);
        if (user == null) {
            return ERROR;
        }
        SysBlog blog = blogService.getById(id);
        return showBlog(blog, user, model);
    }

    /**
     * 根据博客目录和博客名称查找博客
     */
    @GetMapping({"/{blogSpace}/{blogDir}/{blogName}"})
    public String showBlog(@PathVariable String blogSpace, @PathVariable String blogDir, @PathVariable String blogName, Model model) {
        SysUser user = trueUser(blogSpace);
        if (user == null) {
            return ERROR;
        }
        SysBlog blog = blogService.findBlog(user.getId(), blogDir, blogName);
        return showBlog(blog, user, model);
    }

    private String showBlog(SysBlog blog, SysUser user, Model model) {
        if (blog == null) {
            return ERROR;
        }
        SysBlog preBlog = blogService.perBlog(blog);
        if (preBlog != null) {
            blog.setPreBlog(BlogUtil.getBlogPath(preBlog.getDir(), preBlog.getName(), user.getBlogSpace()));
            blog.setPreBlogTitle(preBlog.getTitle());
        }

        SysBlog nextBlog = blogService.nexBlog(blog);
        if (nextBlog != null) {
            blog.setNextBlog(BlogUtil.getBlogPath(nextBlog.getDir(), nextBlog.getName(), user.getBlogSpace()));
            blog.setNextBlogTitle(nextBlog.getTitle());
        }
        blog.setFilePath(BlogUtil.getFilePath(user.getId(), blog.getDir(), blog.getName()));

        blogService.addViewCount(this.getIP(), this.getUserId(), blog.getId(), blog.getUserId());
        BlogUser blogUser = new BlogUser(user);
        blogUser.setBlog(blog);
        model.addAttribute("user", blogUser);
        commonConfig(model);
        return SHOW_BLOG;
    }

    @GetMapping("/{blogSpace}/search")
    public String search(@PathVariable String blogSpace, String key, Model model) {
        if (this.isEmpty(blogSpace) || this.isEmpty(key)) {
            return ERROR;
        }
        SysUser user = trueUser(blogSpace);
        if (user == null) {
            return ERROR;
        }
        Query<SysBlog> query = blogService.createQuery();
        if (NumberUtil.isInteger(key)) {
            query.andEq("user_id", user.getId()).andLike("create_time", "%" + key + "%");
        } else {
            query.andEq("user_id", user.getId())
                    .and(query.condition().orLike("dir", "%" + key + "%").orLike("name", "%" + key + "%"));
        }
        List<SysBlog> blogList = blogService.query(query);
        BlogDetailList details = BlogUtil.resolveBlogList(blogList, blogSpace, false);
        BlogUser blogUser = new BlogUser(user);
        model.addAttribute("user", blogUser);
        model.addAttribute("titles", details.getPubDir());
        model.addAttribute("lists", details.getPublicList());
        commonConfig(model);
        return BLOG_INDEX + "1";
    }

    @RequiresAuthentication
    @GetMapping({"/{blogSpace}/preview"})
    public String preview(@PathVariable String blogSpace, String blogName, Model model) {
        SysUser user = trueUser(blogSpace);
        if (user == null) {
            return ERROR;
        }
        SysBlog blog = new SysBlog();
        blog.setTitle("这是预览文件，记得点击保存哟，亲^_^");
        blog.setFilePath(user.getId() + BaseConstants.TEMP_DIR + blogName);
        blog.setCreateTime(new Date());

        BlogUser blogUser = new BlogUser(user);
        blogUser.setBlog(blog);
        model.addAttribute("user", blogUser);
        commonConfig(model);
        return SHOW_BLOG;
    }

    /**
     * 用户自定义菜单界面，该html页面直接存于用户根目录下
     *
     * @param name html文件名
     */
    @GetMapping({"/{blogSpace}/user"})
    public String user(@PathVariable String blogSpace, String name, Model model) {
        SysUser user = trueUser(blogSpace);
        if (user == null || this.isEmpty(name)) {
            return ERROR;
        }
        File file = BlogFileHelper.getInstance().locateFile(user.getId(), name + ".html");
        if (file == null || !file.exists()) {
            return ERROR;
        }
        SysBlog blog = new SysBlog();
        blog.setTitle(name);
        blog.setCreateTime(DateUtil.date(file.lastModified()));
        blog.setFilePath(user.getId() + "/" + name);

        BlogUser blogUser = new BlogUser(user);
        blogUser.setBlog(blog);
        model.addAttribute("user", blogUser);
        commonConfig(model);
        return SHOW_BLOG;
    }

    private void commonConfig(Model model) {
        if (xiaokuiCache.showAbout()) {
            model.addAttribute("about", true);
        }
        if (xiaokuiCache.showResume()) {
            model.addAttribute("resume", true);
        }
    }

    private SysUser trueUser(String blogSpace) {
        SysUser user = userService.getUserByBlogSpace(blogSpace);
        if (user == null) {
            if (NumberUtil.isInteger(blogSpace)) {
                user = userService.getById(Integer.valueOf(blogSpace));
            }
        }
        return user;
    }
}
