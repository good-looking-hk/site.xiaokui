package site.xiaokui.module.sys.blog.controller;

import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.beetl.sql.core.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import site.xiaokui.module.base.controller.BaseController;
import site.xiaokui.module.sys.blog.BlogConstants;
import site.xiaokui.module.sys.blog.entity.BlogUser;
import site.xiaokui.module.sys.blog.entity.SysBlog;
import site.xiaokui.module.sys.blog.service.BlogService;
import site.xiaokui.module.sys.blog.util.BlogUtil;
import site.xiaokui.module.sys.user.entity.SysUser;
import site.xiaokui.module.sys.user.service.UserService;

import java.util.Date;
import java.util.List;

import static site.xiaokui.module.sys.blog.BlogConstants.TEMP_DIR;

/**
 * @author HK
 * @date 2018-06-25 14:16
 */
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
        if (user == null || this.isEmpty(user.getBlogSpace())) {
            return FORWARD_ERROR;
        }
        return FORWARD + BLOG_PREFIX + "/" + user.getBlogSpace();
    }

    @GetMapping("/{blogSpace}/search")
    public String search(@PathVariable String blogSpace, String key, Model model) {
        if (this.isEmpty(blogSpace) || this.isEmpty(key)) {
            return ERROR;
        }
        SysUser user = userService.getUserByBlogSpace(blogSpace);
        if (user == null) {
            return ERROR;
        }
        Query<SysBlog> query = blogService.createQuery();
        query.andEq("user_id", user.getId()).andLike("title", "%" + key + "%");
        List<SysBlog> blogList = this.blogService.query(query);
        List<List<SysBlog>> lists = BlogUtil.resolveBlogList(blogList, blogSpace);

        BlogUser blogUser = new BlogUser(user);
        blogUser.setBlogList(lists);
        model.addAttribute("user", blogUser);
        return BLOG_INDEX;
    }

    @GetMapping("/{blogSpace}")
    public String blogSpace(@PathVariable String blogSpace, Model model) {
        if (this.isEmpty(blogSpace)) {
            return ERROR;
        }
        SysUser user = userService.getUserByBlogSpace(blogSpace);
        if (user == null) {
            return ERROR;
        }
        List<SysBlog> blogList = this.blogService.listBlogByUserId(user.getId());
        List<List<SysBlog>> lists = BlogUtil.resolveBlogList(blogList, blogSpace);

        BlogUser blogUser = new BlogUser(user);
        blogUser.setBlogList(lists);
        model.addAttribute("user", blogUser);
        return BLOG_INDEX;
    }

    /**
     * 根据id查找博客
     */
    @GetMapping("/{blogSpace}/{id}")
    public String showBlog(@PathVariable String blogSpace, @PathVariable Integer id, Model model) {
        SysUser user = userService.getUserByBlogSpace(blogSpace);
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

        BlogUser blogUser = new BlogUser(user);
        blogUser.setBlog(blog);
        model.addAttribute("user", blogUser);
        return SHOW_BLOG;
    }

    /**
     * 根据博客目录和博客名称查找博客
     */
    @GetMapping({"/{blogSpace}/{blogDir}/{blogName}"})
    public String showBlog(@PathVariable String blogSpace, @PathVariable String blogDir, @PathVariable String blogName, Model model) {
        SysUser user = userService.getUserByBlogSpace(blogSpace);
        if (user == null) {
            return ERROR;
        }
        SysBlog blog = blogService.getBlog(user.getId(), blogDir, blogName);
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

        BlogUser blogUser = new BlogUser(user);
        blogUser.setBlog(blog);
        model.addAttribute("user", blogUser);
        return SHOW_BLOG;
    }

    @GetMapping({"/{blogSpace}/about"})
    public String about(@PathVariable String blogSpace, Model model) {
        SysUser user = userService.getUserByBlogSpace(blogSpace);
        if (user == null) {
            return ERROR;
        }
        SysBlog blog = blogService.getBlog(user.getId(), "about", "about");
        if (blog == null) {
            return ERROR;
        }
        blog.setFilePath("/upload/" + user.getId() + "/about/about");

        BlogUser blogUser = new BlogUser(user);
        blogUser.setBlog(blog);
        model.addAttribute("user", blogUser);
        return SHOW_BLOG;
    }

    @RequiresAuthentication
    @GetMapping({"/{blogSpace}/preview"})
    public String preview(@PathVariable String blogSpace, String blogName, Model model) {
        SysUser user = userService.getUserByBlogSpace(blogSpace);
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
        return SHOW_BLOG;
    }
}
