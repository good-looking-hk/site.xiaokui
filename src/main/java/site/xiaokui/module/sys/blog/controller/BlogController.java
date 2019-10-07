package site.xiaokui.module.sys.blog.controller;

import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.beetl.sql.core.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import site.xiaokui.XiaokuiCache;
import site.xiaokui.common.util.StringUtil;
import site.xiaokui.common.util.TimeUtil;
import site.xiaokui.module.base.controller.AbstractController;
import site.xiaokui.module.base.entity.ResultEntity;
import site.xiaokui.module.sys.blog.BlogConstants;
import site.xiaokui.module.sys.blog.entity.BlogStatusEnum;
import site.xiaokui.module.sys.blog.entity.SysBlog;
import site.xiaokui.module.sys.blog.entity.UploadBlog;
import site.xiaokui.module.sys.blog.service.BlogService;
import site.xiaokui.module.sys.blog.util.BlogUtil;

import java.io.File;
import java.util.Date;
import java.util.List;

import static site.xiaokui.module.sys.blog.BlogConstants.*;

/**
 * @author HK
 * @date 2018-06-24 21:33
 */
@Slf4j
@Controller
@RequestMapping(BlogConstants.BLOG_PREFIX)
public class BlogController extends AbstractController {
    /**
     * 默认为 /sys/blog
     */
    private static final String BLOG_PREFIX = BlogConstants.BLOG_PREFIX;

    @Autowired
    private BlogService blogService;

    @Autowired
    private XiaokuiCache xiaokuiCache;

    @Override
    protected String setPrefix() {
        return BLOG_PREFIX;
    }

    @RequiresPermissions(BLOG_PREFIX)
    @PostMapping(LIST)
    @ResponseBody
    public List<SysBlog> list(@RequestParam(required = false) String name, @RequestParam(required = false) String dir,
                        @RequestParam(required = false) String beginTime,
                        @RequestParam(required = false) String endTime) {
        if (StringUtil.isAllEmpty(name, dir, beginTime, endTime)) {
            SysBlog blog = new SysBlog();
            blog.setUserId(this.getUserId());
            List<SysBlog> l = blogService.match(blog);
            System.out.println(l.get(0));
            return l;
        }
        Query<SysBlog> query = blogService.createQuery();
        query.andEq("user_id", this.getUserId());
        if (this.isNotEmpty(name)) {
            query.andLike("title", "%" + name + "%");
        }
        if (this.isNotEmpty(dir)) {
            query.andLike("dir", "%" + dir + "%");
        }
        if (this.isNotEmpty(beginTime) && this.isNotEmpty(endTime)) {
            query.andBetween("create_time", beginTime, endTime);
        }
        return blogService.query(query);
    }

    @RequiresPermissions(BLOG_PREFIX + ADD)
    @PostMapping("/temp")
    @ResponseBody
    public ResultEntity temp(MultipartFile file) {
        if (file == null || file.isEmpty() || file.getSize() > MAX_BLOG_UPLOAD_FILE) {
            return this.error("文件为空或过大");
        }
        String fileName = file.getOriginalFilename();
        if (this.isEmpty(fileName) || !fileName.endsWith(HTML_SUFFIX)) {
            return this.error("文件格式只能为html");
        }
        UploadBlog blog = blogService.saveTemp(file, this.getUserId());
        if (blog.getErrorInfo() != null) {
            return this.error(blog.getErrorInfo());
        }
        blog.setBlogSpace(this.getUser().getBlogSpace());
        return this.ok().put("upload", blog);
    }

    @RequiresPermissions(BLOG_PREFIX + ADD)
    @PostMapping(ADD)
    @ResponseBody
    public ResultEntity add(String dir, String name, Integer orderNum, String createTime) {
        if (StringUtil.hasEmpty(dir, name)) {
            return this.paramError(dir, name);
        }
        SysBlog blog = new SysBlog();
        blog.setUserId(this.getUserId());
        blog.setName(name);
        blog.setTitle(name);
        blog.setDir(dir);
        blog.setCreateTime(TimeUtil.parseDate(createTime));
        blog.setModifiedTime(blog.getCreateTime());
        if (orderNum == null) {
            blog.setOrderNum(0);
        } else {
            blog.setOrderNum(orderNum);
        }
        // 默认博客为公开，用户可以进一步修改
        String company = xiaokuiCache.getCompany();
        if (dir.equals(company)) {
            blog.setStatus(BlogStatusEnum.PROTECTED.getCode());
        } else {
            blog.setStatus(BlogStatusEnum.PUBLIC.getCode());
        }
        BlogUtil.clearBlogCache();
        return blogService.saveBlog(blog);
    }

    @RequiresPermissions(BLOG_PREFIX + EDIT)
    @GetMapping(EDIT + "/{id}")
    @Override
    public String edit(@PathVariable Integer id, Model model) {
        SysBlog blog = blogService.getById(id);
        if (blog == null) {
            return ERROR;
        }
        model.addAttribute("blog", blog);
        return TO_EDIT;
    }

    @RequiresPermissions(BLOG_PREFIX + EDIT)
    @PostMapping(EDIT)
    @ResponseBody
    public ResultEntity edit(Integer id, String title, Integer orderNum, String status, String createTime) {
        if (StringUtil.hasEmpty(title, status, createTime)) {
            return this.paramError(title, status);
        }
        Integer code = BlogStatusEnum.codeOf(status);
        if (code == null) {
            return this.paramError(status);
        }
        Date date = DateUtil.parse(createTime).toJdkDate();
        SysBlog blog = new SysBlog();
        blog.setId(id);
        blog.setTitle(title);
        blog.setOrderNum(orderNum);
        blog.setStatus(code);
        blog.setCreateTime(date);
        blog.setModifiedTime(new Date());
        boolean success = blogService.updateByIdIgnoreNull(blog);
        return returnResult(success);
    }

    /**
     * 仅删除数据库信息
     */
    @PostMapping(REMOVE)
    @ResponseBody
    @Override
    public ResultEntity remove(Integer id) {
        boolean success = blogService.deleteById(id);
        return returnResult(success);
    }

    @GetMapping("/detail")
    public String detail() {
        return PREFIX + "/detail";
    }

    /**
     * 用户自定义扩展界面接口--后续有待完善
     * html文件名形如：关于-20171022.html
     */
    @RequiresPermissions(BLOG_PREFIX + ADD)
    @PostMapping("/user")
    @ResponseBody
    public ResultEntity user(MultipartFile file, String name) {
        if (file == null || file.isEmpty() || file.getSize() > MAX_BLOG_UPLOAD_FILE) {
            return this.error("文件为空或过大");
        }
        String fileName = file.getOriginalFilename();
        if (this.isEmpty(name) || this.isEmpty(fileName)) {
            return this.error("必须自定义name");
        }
        int index = fileName.indexOf('-');
        if (index < 0) {
            return this.error("格式形如:关于-20171022.html");
        }
        File about = BlogUtil.resolveUploadFile(file, this.getUserId()).getUploadFile();
        if (about == null || !about.exists()) {
            return this.error("文件上传失败");
        }
        // 移动到用户根目录下
        boolean result = about.renameTo(new File(about.getParentFile().getParent() + "/" +  name + ".html"));
        if (result) {
            String title = fileName.substring(0, index);
            log.info("用户{}存储了自定义key[name={},title={}]", this.getUserId(), name, title);
        }
        return this.returnResult(result, "更新失败",  "更新成功");
    }
}
