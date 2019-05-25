package site.xiaokui.module.sys.blog.controller;

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
import org.springframework.web.bind.annotation.ResponseBody;
import site.xiaokui.module.base.controller.BaseController;
import site.xiaokui.module.sys.blog.BlogConstants;
import site.xiaokui.module.sys.blog.entity.BlogDetailList;
import site.xiaokui.module.sys.blog.entity.BlogUser;
import site.xiaokui.module.sys.blog.entity.SysBlog;
import site.xiaokui.module.sys.blog.entity.UserLink;
import site.xiaokui.module.sys.blog.service.BlogService;
import site.xiaokui.module.sys.blog.util.BlogUtil;
import site.xiaokui.module.sys.blog.util.FileUtil;
import site.xiaokui.module.sys.user.entity.SysUser;
import site.xiaokui.module.sys.user.service.UserService;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static site.xiaokui.module.sys.blog.BlogConstants.TEMP_DIR;

/**
 * @author HK
 * @date 2018-06-25 14:16
 */
@Slf4j
@Controller("BLOG")
@RequestMapping(BlogConstants.PREFIX)
public class IndexController extends BaseController {

    private static final String BLOG_PREFIX = BlogConstants.PREFIX;

    private static final String BLOG_INDEX = BLOG_PREFIX + INDEX;

    private static final String SHOW_BLOG = BLOG_PREFIX + BLOG_PREFIX;

    @Autowired
    private UserService userService;

    @Autowired
    private BlogService blogService;

    /**
     * 默认首页为第一位注册的博客空间
     */
    @GetMapping({EMPTY, INDEX})
    public String index() {
        SysUser user = userService.top();
        if (user == null) {
            return FORWARD_ERROR;
        }
        // 这里多了一次数据库查询
        if (!this.isEmpty(user.getBlogSpace())) {
            return FORWARD + BLOG_PREFIX + "/" + user.getBlogSpace();
        }
        return FORWARD + BLOG_PREFIX + "/" + user.getId();
    }

    /**
     * 用户的个人博客空间
     * @param blogSpace 如果用户没有指定个人博客空间名，那么默认为用户id
     * @param layout 值为null或time
     */
    @GetMapping("/{blogSpace}")
    public String blogSpace(@PathVariable String blogSpace, Model model, String layout) {
        if (this.isEmpty(blogSpace)) {
            return ERROR;
        }
        SysUser user = trueUser(blogSpace);
        if (user == null) {
            return ERROR;
        }
        List<SysBlog> blogList = blogService.listBlogByUserId(user.getId());
        BlogDetailList details = BlogUtil.resolveBlogList(blogList, blogSpace);
        if ("time".equals(layout)) {
            BlogUser blogUser = new BlogUser(user);
            model.addAttribute("user", blogUser);
            model.addAttribute("titles", details.getCreateYears());
            model.addAttribute("lists", details.getCreateTimeList());
            userLink(model, user);
            return BLOG_INDEX + "1";
        } else if ("dir".equals(layout)) {
            BlogUser blogUser = new BlogUser(user);
            model.addAttribute("user", blogUser);
            model.addAttribute("titles", details.getDirs());
            model.addAttribute("lists", details.getNonProtectedList());
            userLink(model, user);
            return BLOG_INDEX + "1";
        }
        List<List<SysBlog>> dirLists = details.getNonProtectedList();
        List<SysBlog> recentUpload = blogService.recentUpload(user.getId(), user.getBlogSpace());
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

        BlogUser blogUser = new BlogUser(user);
        blogUser.setBlogList(dirLists);
        blogUser.setPro(details.getPro());
        blogUser.setPub(details.getPub());
        blogUser.setDirCount(details.getDirCount());
        model.addAttribute("user", blogUser);
        model.addAttribute("recent", recentUpload);
        model.addAttribute("most", mostView);
        userLink(model, user);
        return BLOG_INDEX;
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
        if (blog == null) {
            return ERROR;
        }
        SysBlog preBlog = blogService.perBlog(user.getId(), blog.getDir(), blog.getOrderNum());
        if (preBlog != null) {
            blog.setPreBlog(BlogUtil.getBlogPath(preBlog.getDir(), preBlog.getName(), blogSpace));
            blog.setPreBlogTitle(preBlog.getTitle());
        }

        SysBlog nextBlog = blogService.nexBlog(user.getId(), blog.getDir(), blog.getOrderNum());
        if (nextBlog != null) {
            blog.setNextBlog(BlogUtil.getBlogPath(nextBlog.getDir(), nextBlog.getName(), blogSpace));
            blog.setNextBlogTitle(nextBlog.getTitle());
        }
        blog.setFilePath(BlogUtil.getFilePath(user.getId(), blog.getDir(), blog.getName()));

        blogService.addViewCount(this.getIP(), this.getUserId(), blog.getId(), blog.getUserId());
        BlogUser blogUser = new BlogUser(user);
        blogUser.setBlog(blog);
        model.addAttribute("user", blogUser);
        userLink(model, user);
        return SHOW_BLOG;
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
        if (blog == null) {
            return ERROR;
        }
        SysBlog preBlog = blogService.perBlog(user.getId(), blog.getDir(), blog.getOrderNum());
        if (preBlog != null) {
            blog.setPreBlog(BlogUtil.getBlogPath(preBlog.getDir(), preBlog.getName(), blogSpace));
            blog.setPreBlogTitle(preBlog.getTitle());
        }

        SysBlog nextBlog = blogService.nexBlog(user.getId(), blog.getDir(), blog.getOrderNum());
        if (nextBlog != null) {
            blog.setNextBlog(BlogUtil.getBlogPath(nextBlog.getDir(), nextBlog.getName(), blogSpace));
            blog.setNextBlogTitle(nextBlog.getTitle());
        }
        blog.setFilePath(BlogUtil.getFilePath(user.getId(), blog.getDir(), blog.getName()));

        blogService.addViewCount(this.getIP(), this.getUserId(), blog.getId(), blog.getUserId());
        BlogUser blogUser = new BlogUser(user);
        blogUser.setBlog(blog);
        model.addAttribute("user", blogUser);
        userLink(model, user);
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
        BlogDetailList details = BlogUtil.resolveBlogList(blogList, blogSpace);
        BlogUser blogUser = new BlogUser(user);
        model.addAttribute("user", blogUser);
        model.addAttribute("titles", details.getDirs());
        model.addAttribute("lists", details.getNonProtectedList());
        userLink(model, user);
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
        blog.setFilePath(user.getId() + TEMP_DIR + blogName);
        blog.setCreateTime(new Date());

        BlogUser blogUser = new BlogUser(user);
        blogUser.setBlog(blog);
        model.addAttribute("user", blogUser);
        userLink(model, user);
        return SHOW_BLOG;
    }

    /**
     * 用户自定义菜单界面，该html页面直接存于用户根目录下
     * @param name html文件名
     */
    @GetMapping({"/{blogSpace}/user"})
    public String about(@PathVariable String blogSpace, String name,  Model model) {
        SysUser user = trueUser(blogSpace);
        if (user == null || this.isEmpty(name)) {
            return ERROR;
        }
        File file = FileUtil.locateFile(user.getId(),  name + ".html");
        if (file == null || !file.exists()) {
            return ERROR;
        }
        SysBlog blog = new SysBlog();
        for (UserLink s : BlogUtil.getUserMap(user.getId())) {
            if (s.getName().equals(name)) {
                blog.setTitle(s.getTitle());
                break;
            }
        }
        blog.setFilePath(user.getId() + "/" + name);

        BlogUser blogUser = new BlogUser(user);
        blogUser.setBlog(blog);
        model.addAttribute("user", blogUser);
        userLink(model, user);
        return SHOW_BLOG;
    }

    /**
     * 我推荐的音乐
     */
    @GetMapping("/music/july")
    @ResponseBody
    public String[] musicList() {
        return BlogUtil.getMusicList();
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

    private void userLink(Model model, SysUser user) {
        List<UserLink> userKey = BlogUtil.getUserMap(user.getId());
        if (userKey != null) {
            model.addAttribute("userLink", userKey);
        }
    }
}
