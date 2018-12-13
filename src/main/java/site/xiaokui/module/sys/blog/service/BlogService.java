package site.xiaokui.module.sys.blog.service;

import lombok.extern.slf4j.Slf4j;
import org.beetl.sql.core.query.Query;
import org.omg.CORBA.SystemException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import site.xiaokui.module.base.entity.ResultEntity;
import site.xiaokui.module.base.service.BaseService;
import site.xiaokui.module.sys.blog.entity.SysBlog;
import site.xiaokui.module.sys.blog.entity.UploadBlog;
import site.xiaokui.module.sys.blog.util.BlogUtil;
import site.xiaokui.module.sys.blog.util.FileUtil;

import java.io.File;
import java.util.Date;
import java.util.List;

import static site.xiaokui.module.sys.blog.BlogConstants.HTML_SUFFIX;

/**
 * @author HK
 * @date 2018-06-24 22:33
 */
@Slf4j
@Service
public class BlogService extends BaseService<SysBlog> {

    public List<SysBlog> listBlogByUserId(Integer userId) {
        SysBlog sysBlog = new SysBlog();
        sysBlog.setUserId(userId);
        return match(sysBlog);
    }

    public SysBlog findBlog(Integer userId, String dir, String name) {
        SysBlog sysBlog = new SysBlog();
        sysBlog.setUserId(userId);
        sysBlog.setDir(dir);
        sysBlog.setName(name);
        return matchOne(sysBlog);
    }

    public SysBlog perBlog(Integer userId, String dir, Integer orderNum) {
        Query<SysBlog> query = createQuery();
        query.andEq("user_id", userId).andEq("dir", dir).andLess("order_num", orderNum)
                .desc("order_num").limit(1, 1);
        List<SysBlog> list = query.select();
        if (list == null || list.size() == 0) {
            return null;
        }
        return list.get(0);
    }

    public SysBlog nexBlog(Integer userId, String dir, Integer orderNum) {
        Query<SysBlog> query = createQuery();
        query.andEq("user_id", userId).andEq("dir", dir).andGreat("order_num", orderNum)
                .asc("order_num").limit(1, 1);
        List<SysBlog> list = query.select();
        if (list == null || list.size() == 0) {
            return null;
        }
        return list.get(0);
    }

    public UploadBlog saveTemp(MultipartFile file, Integer userId) {
        return BlogUtil.resolveUploadFile(file, userId);
    }

    /**
     * 返回博客保存的结果信息
     */
    public ResultEntity saveBlog(SysBlog blog) {
        Integer userId = blog.getUserId();
        File file = FileUtil.findTempFile(userId, blog.getName() + HTML_SUFFIX);
        if (file == null) {
            log.info("系统找不到文件指定文件（userId={}，SysBlog={}", userId, blog);
            return ResultEntity.error("请先上传文件");
        }
        // 该文件地址是否已经已经存在，如果存在则替换
        File targetFile = FileUtil.locateFile(userId, blog.getDir(), blog.getName() + HTML_SUFFIX);
        if (targetFile.exists()) {
            if (!targetFile.delete()) {
                throw new RuntimeException("删除原有文件失败");
            } else if (!file.renameTo(targetFile)) {
                throw new RuntimeException("上传文件替换原有文件失败");
            }
            SysBlog origin = this.findBlog(userId, blog.getDir(), blog.getName());
            // 如果博客信息已经存在，反之需要在数据库插入新信息，即使源文件已存在
            if (origin != null) {
                SysBlog temp = new SysBlog();
                temp.setId(origin.getId());
                temp.setCreateTime(blog.getCreateTime());
                temp.setModifiedTime(new Date());
                this.updateByIdIgnoreNull(temp);
                return ResultEntity.ok("更新文件成功");
            }
        }

        // 如果文件地址未被占用，则移动文件
        if (!targetFile.exists() && !file.renameTo(targetFile)) {
            throw new RuntimeException("转存文件文件失败：" + targetFile.getName());
        }
        // 插入失败则删除文件，控制层和前段可进行验证，以确保此部的正确性
        try {
            this.insert(blog);
        } catch (Exception e) {
            String str = targetFile.delete() ? "成功" : "失败";
            return ResultEntity.error(e.getMessage() + "(删除上传文件" + str + ")");
        }
        return ResultEntity.ok("保存成功");
    }
}
