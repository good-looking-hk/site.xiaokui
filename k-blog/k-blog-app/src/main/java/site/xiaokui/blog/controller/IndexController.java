package site.xiaokui.blog.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.beetl.sql.core.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import site.xiaokui.base.aop.annotation.Log;
import site.xiaokui.base.controller.BaseController;
import site.xiaokui.base.util.StringUtil;
import site.xiaokui.blog.CacheCenter;
import site.xiaokui.blog.Constants;
import site.xiaokui.blog.config.shiro.ShiroKit;
import site.xiaokui.blog.entity.BlogDetailList;
import site.xiaokui.blog.entity.BlogUser;
import site.xiaokui.blog.entity.SysBlog;
import site.xiaokui.blog.entity.SysUser;
import site.xiaokui.blog.service.BlogService;
import site.xiaokui.blog.service.UserService;
import site.xiaokui.blog.util.BlogFileHelper;
import site.xiaokui.blog.util.BlogUtil;

import java.io.File;
import java.util.*;

/**
 * @author HK
 * @date 2018-06-25 14:16
 */
@Slf4j
@Controller
@RequestMapping(Constants.PREFIX)
public class IndexController extends BaseController {

    private static final String BLOG_PREFIX = Constants.PREFIX;

    private static final String SHOW_BLOG = BLOG_PREFIX + BLOG_PREFIX;

    /**
     * 时间线页面
     */
    private static final String BLOG_TIMELINE_PAGE = BLOG_PREFIX + "/timeline";

    /**
     * 按目录归档页面
     */
    private static final String BLOG_ARCHIVE_PAGE = BLOG_PREFIX + "/archive";
    
    @Autowired
    private UserService userService;

    @Autowired
    private BlogService blogService;

    @Autowired
    private CacheCenter cacheCenter;

    @Value("${xiaokui.recent-update}")
    private Integer recentUpdateCount;

    @Value("${xiaokui.recommend-count}")
    private Integer recommendCount;

    @Value("${xiaokui.most-view}")
    private Integer mostView;

    /**
     * 默认首页为第一位注册的博客空间
     */
    @Log(remark = "访问博客首页", recordReturn = true, recordIp = true)
    @GetMapping({EMPTY, INDEX})
    public String index() {
        // 如果数据库设置了首页
        String index = cacheCenter.getSysConfigCache().getBlogIndex();
        if (StringUtil.isNotBlank(index)) {
            return FORWARD + index;
        }
        // 默认取第一位用户
        SysUser user = userService.top();
        if (user == null) {
            return FORWARD_ERROR;
        }
        if (this.isNotEmpty(user.getBlogSpace())) {
            return REDIRECT + BLOG_PREFIX + "/" + user.getBlogSpace();
        }
        return REDIRECT + BLOG_PREFIX + "/" + user.getId();
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
        if (passwd != null) {
            // 最好走第一个逻辑吧
            if (user.getProPassword().equals(passwd) || StringUtil.isNotEmpty(user.getPassword()) && ShiroKit.getInstance().md5(passwd, user.getSalt()).equals(user.getProPassword())) {
                log.info("受保护访问通过，userId为{}，passwd为{}", user.getId(), passwd);
                proCheckPass = true;
            }
        }
        List<SysBlog> allBlogList = blogService.listBlogByUserId(user.getId());
        // 解析所有公开、受保护的博客，这一步很关键，一般会用缓存
        BlogDetailList details = BlogUtil.resolveBlogList(allBlogList, user.getId(), blogSpace, true);
        BlogUser blogUser = new BlogUser(user);
        // 设置关于 和 简历 等一些公共属性
        commonConfig(model);
        // 如果指定了layout布局
        String result = dealLayout(layout, type, model, proCheckPass, details, blogUser);
        if (result != null) {
            return result;
        }

        // 没有指定layout布局
        if (Constants.BLOG_TYPE_PRO.equals(type) && proCheckPass) {
            List<List<SysBlog>> protectedList = details.getProtectedList();
            blogUser.setBlogList(protectedList);
            blogUser.setPro(details.getPro());
            blogUser.setPub(details.getPub());
            blogUser.setPageTotal(details.getPro());
            blogUser.setDirCount(protectedList.size());
            model.addAttribute("titles", details.getProCreateYears());
            model.addAttribute("lists", details.getProCreateTimeList());
            model.addAttribute("user", blogUser);
            model.addAttribute("title", blogUser.getName());
            return BLOG_ARCHIVE_PAGE;
        } else if (Constants.BLOG_TYPE_PRI.equals(type)) {
            // TODO 有待开发
            List<List<SysBlog>> dirLists = details.getPrivateList();
            blogUser.setBlogList(dirLists);
            blogUser.setPub(details.getPub());
            blogUser.setPro(details.getPro());
            blogUser.setPageTotal(details.getPub());
            blogUser.setDirCount(dirLists.size());
            return ERROR;
        } else {
            // 获取最近更新博客topN
            List<SysBlog> recentUpdateList = details.getModifyTopN(this.recentUpdateCount);
            // 获取推荐阅读topN
            List<SysBlog>  recommendList = details.getRecommendTopN(this.recommendCount);
            // 最多访问，这个需要查redis获取topN
            LinkedHashMap<Long, Integer> map = blogService.getMostViewTopN(user.getId(), mostView);
            List<SysBlog> mostViewList = resolveMostViewList(details, map);

            model.addAttribute("update", recentUpdateList);
            model.addAttribute("recommend", recommendList);
            model.addAttribute("view", mostViewList);

            model.addAttribute("titles", details.getPubCreateYears());
            model.addAttribute("lists", details.getPubCreateTimeList());
            blogUser.setPageTotal(details.getPub());
            blogUser.setDirCount(details.getPubCreateYears().size());
        }
        model.addAttribute("user", blogUser);
        model.addAttribute("title", blogUser.getName());
        return BLOG_TIMELINE_PAGE;
    }

    private String dealLayout(String layout, String type, Model model, boolean proCheckPass, BlogDetailList details, BlogUser blogUser) {
        blogUser.setPub(details.getPub());
        blogUser.setPro(details.getPro());
        // 日期布局
        if (Constants.BLOG_LAYOUT_TIME.equals(layout)) {
            if (Constants.BLOG_TYPE_PRI.equals(type)) {
                model.addAttribute("titles", details.getPriCreateYears());
                model.addAttribute("lists", details.getPriCreateTimeList());
                blogUser.setPageTotal(details.getPri());
                blogUser.setDirCount(details.getPriCreateYears().size());
            } else if (Constants.BLOG_TYPE_PRO.equals(type) && proCheckPass) {
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
            return BLOG_ARCHIVE_PAGE;
        } else if (Constants.BLOG_LAYOUT_DIR.equals(layout)) {
            // 目录布局
            if (Constants.BLOG_TYPE_PRI.equals(type)) {
                model.addAttribute("titles", details.getPriDir());
                model.addAttribute("lists", details.getPrivateList());
                blogUser.setPageTotal(details.getPro());
                blogUser.setDirCount(details.getPriDir().size());
            } else if (Constants.BLOG_TYPE_PRO.equals(type) && proCheckPass) {
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
            model.addAttribute("title", blogUser.getName());
            return BLOG_ARCHIVE_PAGE;
        }
        return null;
    }

    /**
     * 根据id查找博客
     */
    @GetMapping("/{blogSpace}/{id}")
    public String showBlog(@PathVariable String blogSpace, @PathVariable Long id, Model model) {
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

        // 这里提前加了1
        blog.setViewCount(blogService.getViewCountFromRedis(user.getId(), blog.getId()));
        // 实时增加访问量
        blogService.addViewCountIntoRedis(this.getIP(), user.getId(), blog.getId(), blog.getUserId());

        BlogUser blogUser = new BlogUser(user);
        blogUser.setBlog(blog);
        model.addAttribute("user", blogUser);
        model.addAttribute("title", blog.getTitle());
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
        if (NumberUtil.isInteger(key) && key.length() == 4) {
            query.andEq("user_id", user.getId()).andLike("create_time", "%" + key + "%").desc("create_time");
        } else {
            query.andEq("user_id", user.getId()).and(query.condition()
                    .orLike("dir", "%" + key + "%")
                    .orLike("name", "%" + key + "%"));
        }
        List<SysBlog> blogList = blogService.query(query);
        BlogDetailList details = BlogUtil.resolveBlogList(blogList, user.getId(), blogSpace, false);
        BlogUser blogUser = new BlogUser(user);

        blogUser.setPub(details.getPub());
        blogUser.setPro(details.getPro());
        blogUser.setPageTotal(details.getPub());
        blogUser.setDirCount(details.getPubDir().size());

        model.addAttribute("user", blogUser);
        model.addAttribute("titles", details.getPubDir());
        model.addAttribute("lists", details.getPublicList());
        commonConfig(model);
        return BLOG_ARCHIVE_PAGE;
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
        blog.setFilePath(user.getId() + BlogFileHelper.getTempDir() + blogName);
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
        if (cacheCenter.getSysConfigCache().showAbout()) {
            model.addAttribute("about", true);
        }
        if (cacheCenter.getSysConfigCache().showResume()) {
            model.addAttribute("resume", true);
        }
    }

    private SysUser trueUser(String blogSpace) {
        SysUser user = userService.getUserByBlogSpace(blogSpace);
        if (user == null) {
            if (NumberUtil.isInteger(blogSpace)) {
                user = userService.getById(Long.valueOf(blogSpace));
            }
        }
        return user;
    }

    private List<SysBlog> resolveMostViewList(BlogDetailList details, LinkedHashMap<Long, Integer> map) {
        List<SysBlog> list = new ArrayList<>(map.size());
        List<SysBlog> allBlogList = details.getAllBlogList();
        for (Map.Entry<Long, Integer> entry : map.entrySet()) {
            for (SysBlog blog : allBlogList) {
                // 如果博客id相同
                if (entry.getKey().equals(blog.getId())) {
                    // 设置访问量
                    blog.setViewCount(entry.getValue());
                    list.add(blog);
                }
            }
        }
        return list;
    }
}
