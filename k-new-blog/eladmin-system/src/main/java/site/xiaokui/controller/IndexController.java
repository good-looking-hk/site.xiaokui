package site.xiaokui.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.modules.system.service.UserService;
import me.zhengjie.modules.system.service.dto.UserDto;
import me.zhengjie.utils.IpUtil;
import org.beetl.sql.core.query.Query;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.unit.DataUnit;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import site.xiaokui.CacheCenter;
import site.xiaokui.Constants;
import site.xiaokui.domain.BlogDetailList;
import site.xiaokui.domain.BlogUser;
import site.xiaokui.domain.SysBlog;
import site.xiaokui.service.SysBlogService;
import site.xiaokui.service.dto.SysBlogDto;
import site.xiaokui.service.dto.SysBlogQueryCriteria;
import site.xiaokui.service.impl.SysBlogServiceImpl;
import site.xiaokui.util.BlogFileHelper;
import site.xiaokui.util.BlogUtil;

import java.io.File;
import java.util.*;

/**
 * 博客内容需要迎合SEO，故不可做成前后分离，只有类似管理端后台的需求才适合，并不是所有，请知悉
 *
 * @author HK
 * @date 2018-06-25 14:16
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/blog")
public class IndexController {

    private static final String BLOG_PREFIX = "/blog";

    private static final String SHOW_BLOG = BLOG_PREFIX + BLOG_PREFIX;

    /**
     * 时间线页面
     */
    private static final String BLOG_TIMELINE_PAGE = BLOG_PREFIX + "/timeline";

    /**
     * 按目录归档页面
     */
    private static final String BLOG_ARCHIVE_PAGE = BLOG_PREFIX + "/archive";

    private static final String ERROR = "/error", REDIRECT = "redirect:", FORWARD = "forward:";


    private final UserService userService;

    private final SysBlogServiceImpl sysBlogService;

    private final CacheCenter cacheCenter;

    @Value("${xiaokui.recent-upload}")
    private Integer recentUploadCount;

    @Value("${xiaokui.recommend-count}")
    private Integer recommendCount;

    @Value("${xiaokui.most-view}")
    private Integer mostView;

    /**
     * 默认首页为第一位注册的博客空间，如果
     */
    @GetMapping({"", "/index"})
    public String index() {
        // 如果数据库设置了首页
        String index = cacheCenter.getSysConfigCache().getBlogIndex();
        if (StrUtil.isNotBlank(index)) {
            return FORWARD + index;
        }
        // 默认取第一位用户
        UserDto userDto = userService.getFirstUser();
        if (userDto == null) {
            return FORWARD + ERROR;
        }
        if (StrUtil.isNotEmpty(userDto.getBlogSpace())) {
            return REDIRECT + BLOG_PREFIX + "/" + userDto.getBlogSpace();
        }
        return REDIRECT + BLOG_PREFIX + "/" + userDto.getId();
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
        if (StrUtil.isEmpty(blogSpace)) {
            return ERROR;
        }
        UserDto user = trueUser(blogSpace);
        if (user == null) {
            return ERROR;
        }
        boolean proCheckPass = false;
        if (passwd != null) {
            // 最好走第一个逻辑吧
            if (user.getBlogPwd() != null && user.getBlogPwd().equals(passwd)) {
                log.info("受保护访问通过，userId为{}，passwd为{}", user.getId(), passwd);
                proCheckPass = true;
            }
        }
        SysBlog sysBlog = new SysBlog();
        sysBlog.setUserId(user.getId());
        List<SysBlog> allBlogList = sysBlogService.match(sysBlog);
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
            List<SysBlog> recentUploadList = details.getUploadTopN(this.recentUploadCount);
            // 获取推荐阅读topN
            List<SysBlog> recommendList = details.getRecommendTopN(this.recommendCount);
            // 最多访问，这个需要查redis获取topN
            LinkedHashMap<Long, Integer> map = sysBlogService.getMostViewTopN(user.getId(), mostView);
            List<SysBlog> mostViewList = resolveMostViewList(details, map);

            model.addAttribute("upload", recentUploadList);
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
        UserDto user = trueUser(blogSpace);
        if (user == null) {
            return ERROR;
        }
        SysBlog blog = sysBlogService.getById(id);
        return showBlog(blog, user, model);
    }

    /**
     * 根据博客目录和博客名称查找博客
     */
    @GetMapping({"/{blogSpace}/{blogDir}/{blogName}"})
    public String showBlog(@PathVariable String blogSpace, @PathVariable String blogDir, @PathVariable String blogName, Model model) {
        UserDto user = trueUser(blogSpace);
        if (user == null) {
            return ERROR;
        }
        SysBlog query = new SysBlog();
        query.setUserId(user.getId());
        query.setDir(blogDir);
        query.setTitle(blogName);
        SysBlog blog = sysBlogService.matchOne(query);
        return showBlog(blog, user, model);
    }

    private String showBlog(SysBlog blog, UserDto user, Model model) {
        if (blog == null) {
            return ERROR;
        }
        SysBlog preBlog = sysBlogService.perBlog(blog);
        if (preBlog != null) {
            blog.setPreBlog(BlogUtil.getBlogPath(preBlog.getDir(), preBlog.getFileName(), user.getBlogSpace()));
            blog.setPreBlogTitle(preBlog.getTitle());
        }

        SysBlog nextBlog = sysBlogService.nexBlog(blog);
        if (nextBlog != null) {
            blog.setNextBlog(BlogUtil.getBlogPath(nextBlog.getDir(), nextBlog.getFileName(), user.getBlogSpace()));
            blog.setNextBlogTitle(nextBlog.getTitle());
        }
        blog.setFilePath(BlogUtil.getFilePath(user.getId(), blog.getDir(), blog.getFileName()));

        String str = blog.getCreateDate().toString();
        blog.setBlogDate(str.substring(0, 4) + "-" + str.substring(4, 6) + "-" + str.substring(6, 8));

        // 这里提前加了1
        blog.setViewCount(sysBlogService.getViewCountFromRedis(user.getId(), blog.getId()));
        // 实时增加访问量
        sysBlogService.addViewCountIntoRedis(IpUtil.getIp(), user.getId(), blog.getId(), blog.getUserId());

        BlogUser blogUser = new BlogUser(user);
        blogUser.setBlog(blog);
        model.addAttribute("user", blogUser);
        model.addAttribute("title", blog.getTitle());
        commonConfig(model);
        return SHOW_BLOG;
    }

    @GetMapping("/{blogSpace}/search")
    public String search(@PathVariable String blogSpace, String key, Model model) {
        if (StrUtil.isEmpty(blogSpace) || StrUtil.isEmpty(key)) {
            return ERROR;
        }
        UserDto user = trueUser(blogSpace);
        if (user == null) {
            return ERROR;
        }
        Query<SysBlog> query = sysBlogService.createQuery();
        if (NumberUtil.isInteger(key) && key.length() == 4) {
            query.andEq("user_id", user.getId()).andLike("create_time", "%" + key + "%").desc("create_time");
        } else {
            query.andEq("user_id", user.getId()).and(query.condition()
                    .orLike("dir", "%" + key + "%")
                    .orLike("title", "%" + key + "%"));
        }
        List<SysBlog> blogList = sysBlogService.query(query);
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

    @GetMapping({"/{blogSpace}/preview"})
    public String preview(@PathVariable String blogSpace, String blogName, Model model) {
        UserDto user = trueUser(blogSpace);
        if (user == null) {
            return ERROR;
        }
        SysBlog blog = new SysBlog();
        blog.setTitle("这是预览文件，记得点击保存哟，亲^_^");
        blog.setFilePath(user.getId() + BlogFileHelper.getTempDir() + blogName);
        blog.setCreateDate(me.zhengjie.utils.DateUtil.parseIntDate(new Date()));

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
        UserDto user = trueUser(blogSpace);
        if (user == null || StrUtil.isEmpty(name)) {
            return ERROR;
        }
        File file = BlogFileHelper.getInstance().locateFile(user.getId(), name + ".html");
        if (file == null || !file.exists()) {
            return ERROR;
        }
        SysBlog blog = new SysBlog();
        blog.setTitle(name);
        blog.setCreateDate(me.zhengjie.utils.DateUtil.parseIntDate(new Date(file.lastModified())));
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
        if (cacheCenter.getSysConfigCache().showProject()) {
            model.addAttribute("project", true);
        }
    }

    private UserDto trueUser(String blogSpace) {
        UserDto userDto = userService.getUserByBlogSpace(blogSpace);
        if (userDto == null) {
            if (NumberUtil.isInteger(blogSpace)) {
                userDto = userService.findById(Long.parseLong(blogSpace));
            }
        }
        return userDto;
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
