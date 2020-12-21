package site.xiaokui.rest;

import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import me.zhengjie.annotation.Log;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.utils.DateUtil;
import me.zhengjie.utils.SecurityUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import site.xiaokui.domain.*;
import site.xiaokui.domain.enums.BlogTypeEnum;
import site.xiaokui.service.dto.SaveBlogDto;
import site.xiaokui.service.dto.SysBlogQueryCriteria;
import site.xiaokui.service.impl.SysBlogServiceImpl;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

import static site.xiaokui.Constants.*;

/**
 * 这里统一回答一下关于本类的几个疑问：
 * 1. 这里get用于查询、post用于新增、put用于修改、delete用于删除
 * 2. 实体查询信息统一封装进SysBlogQueryCriteria，分页相关信息统一封装进Pageable（具体实现类为 PageRequest）
 *
 * @author HK
 * @date 2020-12-01
 **/
@RestController
@RequiredArgsConstructor
@Api(tags = "博客管理")
@RequestMapping("/api/blog")
public class SysBlogController {

    private final SysBlogServiceImpl blogService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('blog:list')")
    public void download(HttpServletResponse response, SysBlogQueryCriteria criteria) throws IOException {
        blogService.download(blogService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询博客")
    @ApiOperation("查询博客")
    @PreAuthorize("@el.check('blog:list')")
    public ResponseEntity<Object> query(SysBlogQueryCriteria criteria, Pageable pageable) {
        return new ResponseEntity<>(blogService.queryAll(criteria, pageable), HttpStatus.OK);
    }

    @PostMapping
    @Log("新增博客")
    @ApiOperation("新增博客")
    @PreAuthorize("@el.check('blog:add')")
    public ResultEntity create(@Validated @RequestBody SaveBlogDto saveBlogDto) {
        SysBlog blog = new SysBlog();
        blog.setUserId(SecurityUtils.getCurrentUserId());
        blog.setDir(saveBlogDto.getDir());
        blog.setTitle(saveBlogDto.getName());
        blog.setFileName(saveBlogDto.getName());
        blog.setOrderNum(saveBlogDto.getOrderNum());
        blog.setCreateTime(DateUtil.formatIntDate(saveBlogDto.getCreateDate()));
        blog.setUpdateTime(new Date());
        blog.setBlogType(BlogTypeEnum.PUBLIC.getCode());

        WordCounter wordCounter = new WordCounter(saveBlogDto.getChineseCount(), saveBlogDto.getEnglishCount(), saveBlogDto.getNumberCount(), saveBlogDto.getOtherCount());
        SysBlogWord sysBlogWord = new SysBlogWord(blog.getId(), wordCounter);
        boolean isInsert = blogService.saveBlog(blog, sysBlogWord);
        if (isInsert) {
            return ResultEntity.ok("新增成功");
        }
        return ResultEntity.ok("更新成功");
    }

    @PutMapping
    @Log("修改博客")
    @ApiOperation("修改博客")
    @PreAuthorize("@el.check('blog:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody SysBlog resources) {
        blogService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除博客")
    @ApiOperation("删除博客")
    @PreAuthorize("@el.check('blog:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        blogService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * @param isBlog 普通博客或
     */
    @Log("上传博客")
    @ApiOperation("上传博客")
    @PreAuthorize("@el.check('blog:add')")
    @PostMapping("/upload")
    public ResponseEntity<Object> temp(MultipartFile file, boolean isBlog) {
        checkFile(file);
        UploadBlog blog = blogService.saveTemp(file, SecurityUtils.getCurrentUserId(), true);
        if (blog.getErrorInfo() != null) {
            throw new BadRequestException(blog.getErrorInfo());
        }
        blog.setBlogSpace(SecurityUtils.getCurrentBlogSpace());
        return ResponseEntity.ok(blog);
    }

    private void checkFile(MultipartFile file) {
        if (file == null || file.isEmpty() || file.getSize() > MAX_BLOG_UPLOAD_FILE) {
            throw new BadRequestException("文件为空或过大");
        }
        String fileName = file.getOriginalFilename();
        if (StrUtil.isEmpty(fileName)) {
            throw new BadRequestException("文件名不能为空");
        }
        boolean legal = fileName.endsWith(HTML_SUFFIX) || fileName.endsWith(MD_SUFFIX);
        if (!legal) {
            throw new BadRequestException("文件格式只能为html或md");
        }
    }
}