package site.xiaokui.blog.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.beetl.sql.core.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import site.xiaokui.base.controller.AbstractController;
import site.xiaokui.base.entity.ResultEntity;
import site.xiaokui.base.util.StringUtil;
import site.xiaokui.blog.Constants;
import site.xiaokui.blog.CacheCenter;
import site.xiaokui.blog.config.shiro.ShiroKit;
import site.xiaokui.blog.entity.BlogStatusEnum;
import site.xiaokui.blog.entity.SysBlog;
import site.xiaokui.blog.entity.SysUser;
import site.xiaokui.blog.entity.UploadBlog;
import site.xiaokui.blog.service.BlogService;
import site.xiaokui.blog.util.BlogUtil;

import java.io.File;
import java.util.Date;
import java.util.List;

import static site.xiaokui.blog.Constants.*;

/**
 * @author HK
 * @date 2018-06-24 21:33
 */
@Slf4j
@Controller
@RequestMapping(Constants.BLOG_PREFIX)
public class BlogController extends AbstractController {

    /**
     * 默认为 /sys/blog
     */
    private static final String BLOG_PREFIX = Constants.BLOG_PREFIX;

    @Autowired
    private BlogService blogService;

    @Autowired
    private CacheCenter cacheCenter;

    @Override
    protected String setPrefix() {
        return BLOG_PREFIX;
    }

    @Override
    protected SysUser getCurrentUser() {
        return (SysUser) ShiroKit.getInstance().getUser();
    }

    @RequiresPermissions(BLOG_PREFIX)
    @PostMapping(LIST)
    @ResponseBody
    public List<SysBlog> list(@RequestParam(required = false) String name, @RequestParam(required = false) String dir,
                              @RequestParam(required = false) String beginTime,
                              @RequestParam(required = false) String endTime) {
        if (StringUtil.isAllEmpty(name, dir, beginTime, endTime)) {
            SysBlog blog = new SysBlog();
            blog.setUserId(this.getCurrentUser().getId());
            return blogService.match(blog);
        }
        Query<SysBlog> query = blogService.createQuery();
        query.andEq("user_id", this.getCurrentUser().getId());
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
    @PostMapping("/blogTemp")
    @ResponseBody
    public ResultEntity blogTemp(MultipartFile file) {
        return temp(file, true);
    }

    @RequiresPermissions(BLOG_PREFIX + ADD)
    @PostMapping("/add")
    @ResponseBody
    public ResultEntity addBlog(String dir, String name, Integer orderNum, String createTime, Integer characterCount) {
        return addRecord(dir, name, orderNum, createTime, characterCount);
    }

    @RequiresPermissions(BLOG_PREFIX + ADD)
    @GetMapping("/work")
    public String work() {
        return BLOG_PREFIX + "/work";
    }

    @RequiresPermissions(BLOG_PREFIX + ADD)
    @PostMapping("/workTemp")
    @ResponseBody
    public ResultEntity workTemp(MultipartFile file) {
        return temp(file, false);
    }

    /**
     * @param isBlog 普通博客或周报
     */
    public ResultEntity temp(MultipartFile file, boolean isBlog) {
        ResultEntity checkResult = checkFile(file);
        if (checkResult != null) {
            return checkResult;
        }

        UploadBlog blog = blogService.saveTemp(file, this.getCurrentUser().getId(), isBlog);
        if (blog.getErrorInfo() != null) {
            return this.error(blog.getErrorInfo());
        }
        if (!isBlog) {
            blog.setDir(cacheCenter.getSysConfigCache().getCompany());
        }
        blog.setBlogSpace(this.getCurrentUser().getBlogSpace());
        return this.ok().put("upload", blog);
    }

    public ResultEntity addRecord(String dir, String name, Integer orderNum, String createTime, Integer characterCount) {
        if (StringUtil.hasEmpty(dir, name)) {
            return this.paramError(dir, name);
        }
        SysBlog blog = new SysBlog();
        blog.setUserId(this.getCurrentUser().getId());
        blog.setName(name);
        blog.setTitle(name);
        blog.setDir(dir);
        blog.setCreateTime(DateUtil.parseDate(createTime));
        blog.setUpdateTime(new Date());
        blog.setCharacterCount(characterCount);
        if (orderNum != null) {
            blog.setOrderNum(orderNum);
        }

        // 默认博客为公开，用户可以进一步修改
        String company = cacheCenter.getSysConfigCache().getCompany();
        if (dir.equals(company)) {
            blog.setStatus(BlogStatusEnum.PROTECTED.getCode());
        } else {
            blog.setStatus(BlogStatusEnum.PUBLIC.getCode());
        }
        return blogService.saveBlog(blog);
    }

    @RequiresPermissions(BLOG_PREFIX + EDIT)
    @GetMapping(EDIT + "/{id}")
    public String edit(@PathVariable Long id, Model model) {
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
    public ResultEntity edit(Long id, String title, Integer orderNum, String status, String createTime) {
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
        blog.setUpdateTime(new Date());
        boolean success = blogService.updateByIdIgnoreNull(blog);
        return returnResult(success);
    }

    /**
     * 仅删除数据库信息
     */
    @PostMapping(REMOVE)
    @ResponseBody
    public ResultEntity remove(Integer id) {
        boolean success = blogService.deleteById(id);
        return returnResult(success);
    }

    @GetMapping("/detail")
    public String detail() {
        return PREFIX + "/detail";
    }

    /**
     * TODO
     * 用户自定义扩展界面接口--后续有待完善
     * html、md文件名形如：关于-20171022.html/md
     * html、md文件名形如：20171022-关于.html/md
     */
    @RequiresPermissions(BLOG_PREFIX + ADD)
    @PostMapping("/user")
    @ResponseBody
    public ResultEntity user(MultipartFile file) {
        ResultEntity checkResult = checkFile(file);
        if (checkResult != null) {
            return checkResult;
        }
        String fileName = file.getOriginalFilename();
        int index = fileName.indexOf('-');
        if (index < 0) {
            return this.error("格式形如:20171022-关于.html");
        }

        UploadBlog blog = BlogUtil.resolveUploadFile(file, this.getCurrentUser().getId(), false);
        File upload = blog.getUploadFile();
        if (upload == null || !upload.exists()) {
            return this.error("文件上传失败:" + blog);
        }

        int i = fileName.lastIndexOf(".");
        // 去除后缀
        String noSuffixName = fileName.substring(0, i);
        String[] arr = noSuffixName.split("-");
        if (arr.length != 2) {
            return this.error("不支持的文件名:" + fileName);
        }

        String name, date;
        if (arr[0].length() == 8 && NumberUtil.isInteger(arr[0])) {
            name = arr[1];
            date = arr[0];
        } else if (arr[1].length() == 8 && NumberUtil.isInteger(arr[1])) {
            name = arr[0];
            date = arr[1];
        } else {
            return this.error("不支持的文件名:" + fileName);
        }
        File target = new File(upload.getParentFile().getParent() + "/" +  name + ".html");
        boolean result = upload.renameTo(target);
        if (result) {
            target.setLastModified(DateUtil.parse(date).getTime());
            log.info("用户{}存储了自定义key[name={},time={}]", this.getCurrentUser().getId(), fileName, date);
        }
        return this.returnResult(result, "上传失败",  "上传成功");
    }

    private ResultEntity checkFile(MultipartFile file) {
        if (file == null || file.isEmpty() || file.getSize() > MAX_BLOG_UPLOAD_FILE) {
            return this.error("文件为空或过大");
        }
        String fileName = file.getOriginalFilename();
        if (this.isEmpty(fileName)) {
            return this.error("文件名不能为空");
        }
        boolean legal = fileName.endsWith(HTML_SUFFIX) || fileName.endsWith(MD_SUFFIX);
        if (!legal) {
            return this.error("文件格式只能为html或md");
        }
        return null;
    }
}
