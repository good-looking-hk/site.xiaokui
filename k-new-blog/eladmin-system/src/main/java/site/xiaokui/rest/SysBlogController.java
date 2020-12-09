package site.xiaokui.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import me.zhengjie.annotation.Log;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import site.xiaokui.domain.SysBlog;
import site.xiaokui.service.SysBlogService;
import site.xiaokui.service.dto.SysBlogQueryCriteria;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 这里统一回答一下关于本类的几个疑问：
 * 1. 这里get用于查询、post用于新增、put用于修改、delete用于删除
 * 2. 实体查询信息统一封装进SysBlogQueryCriteria，分页相关信息统一封装进Pageable（具体实现类为 PageRequest）
 *
 * @author hk
 * @date 2020-12-01
 **/
@RestController
@RequiredArgsConstructor
@Api(tags = "博客管理")
@RequestMapping("/api/sysBlog")
public class SysBlogController {

    private final SysBlogService blogService;

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
    public ResponseEntity<Object> create(@Validated @RequestBody SysBlog resources) {
        return new ResponseEntity<>(blogService.create(resources), HttpStatus.CREATED);
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
}