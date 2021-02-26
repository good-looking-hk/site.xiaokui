package site.xiaokui.service.impl;

import cn.hutool.core.date.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.exception.ErrorRequestException;
import me.zhengjie.utils.FileUtil;
import me.zhengjie.utils.PageUtil;
import me.zhengjie.utils.QueryHelp;
import me.zhengjie.utils.ValidationUtil;
import org.beetl.sql.core.query.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import site.xiaokui.domain.*;
import site.xiaokui.repository.SysBlogRepository;
import site.xiaokui.service.BaseService;
import site.xiaokui.service.BlogCacheService;
import site.xiaokui.service.SysBlogService;
import site.xiaokui.service.dto.SysBlogDto;
import site.xiaokui.service.dto.SysBlogQueryCriteria;
import site.xiaokui.service.mapstruct.SysBlogMapper;
import site.xiaokui.util.BlogFileHelper;
import site.xiaokui.util.BlogUtil;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.*;

import static site.xiaokui.Constants.HTML_SUFFIX;

/**
 * @author HK
 * @date 2020-12-01
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class SysBlogServiceImpl extends BaseService<SysBlog> implements SysBlogService {

    private final SysBlogRepository sysBlogRepository;
    private final SysBlogMapper sysBlogMapper;
    private final BlogCacheService blogCacheService;
    private final SysBlogWordServiceImpl blogWordService;

    public int getViewCountFromRedis(Long userId, Long blogId) {
        return blogCacheService.getViewCount(userId, blogId);
    }

    public void addViewCountIntoRedis(String ip, Long userId, Long blogId, Long owner) {
        Date now = new Date();
        int hours = DateUtil.hour(now, true);
        int minute = DateUtil.minute(now);
        if (userId == null) {
            // 谷歌爬虫一般从23:20多一点开始，到1:00介绍
            // 中午从12:10多一点开始，到12:30左右结束
            if (hours > 23 && minute > 20) {
                return;
            }
            if (hours < 6) {
                return;
            }
            if (hours == 12 && 10 < minute && minute < 30) {
                // 可能存在误杀
                // 在不要求用户登录的情况下，想要不被谷歌爬虫刷一个访问量，只有两种办法
                // 1.前端js判断是不是爬虫（可以判断是不是国外ip）,然后单独调增加访问量接口，但这样，接口会暴露
                // 2.后端判断是不是爬虫，但可能需要访问远程ip库，白白增加了网络请求开销
                // TODO
            }
        }
        blogCacheService.addViewCount(ip, userId, blogId, owner);
    }

    public LinkedHashMap<Long, Integer> getMostViewTopN(Long userId, int n) {
        return blogCacheService.getMostViewTopN(userId, n);
    }

    /**
     * 一般由缓存调用，将数据库中博客访问量读入redis缓存
     */
    public void setMostViewCache(Long userId) {
        blogCacheService.setMostView(userId, listBlogByUserId(userId));
    }

    public List<SysBlog> listBlogByUserId(Long userId) {
        SysBlog sysBlog = new SysBlog();
        sysBlog.setUserId(userId);
        return match(sysBlog);
    }

    public SysBlog perBlog(SysBlog blog) {
        Query<SysBlog> query = createQuery();
        query.andEq("user_id", blog.getUserId()).andEq("dir", blog.getDir());
        // 优先使用序号，其次是日期
        if (blog.getOrderNum() != null && blog.getOrderNum() != 0) {
            query.andLess("order_num", blog.getOrderNum());
            query.desc("order_num").limit(1, 1);
        } else {
            query.andLess("create_date", blog.getCreateDate());
            query.desc("create_date").limit(1, 1);
        }
        List<SysBlog> list = query.select();
        if (list == null || list.size() == 0) {
            return null;
        }
        return list.get(0);
    }

    public SysBlog nexBlog(SysBlog blog) {
        Query<SysBlog> query = createQuery();
        query.andEq("user_id", blog.getUserId()).andEq("dir", blog.getDir());
        // 优先使用序号，其次是日期
        if (blog.getOrderNum() != null && blog.getOrderNum() != 0) {
            query.andGreat("order_num", blog.getOrderNum());
            query.asc("order_num").limit(1, 1);
        } else {
            query.andGreat("create_date", blog.getCreateDate());
            query.asc("create_date").limit(1, 1);
        }
        List<SysBlog> list = query.select();
        if (list == null || list.size() == 0) {
            return null;
        }
        return list.get(0);
    }

    public UploadBlog saveTemp(MultipartFile file, Long userId) {
        return BlogUtil.resolveUploadFile(file, userId);
    }

    public SysBlog findBlog(Long userId, String dir, String fileName) {
        SysBlog sysBlog = new SysBlog();
        sysBlog.setUserId(userId);
        sysBlog.setDir(dir);
        sysBlog.setFileName(fileName);
        return matchOne(sysBlog);
    }

    /**
     * @return 是否为新增
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean saveBlog(SysBlog blog, SysBlogWord sysBlogWord) {
        Long userId = blog.getUserId();
        File file = BlogFileHelper.getInstance().findTempFile(userId, blog.getFileName() + HTML_SUFFIX);
        if (file == null) {
            log.error("系统找不到文件指定文件（userId={}，SysBlog={}", userId, blog);
            throw new ErrorRequestException("请先上传文件");
        }
        // 先处理文件的逻辑，该文件地址是否已经已经存在，如果存在则替换，反之则新建
        File targetFile = BlogFileHelper.getInstance().locateFile(userId, blog.getDir(), blog.getFileName() + HTML_SUFFIX);
        if (targetFile.exists() && !targetFile.delete()) {
            throw new RuntimeException("删除原有文件失败");
        }
        // 如果文件地址未被占用，则移动文件
        if (!targetFile.exists() && !file.renameTo(targetFile)) {
            throw new RuntimeException("转存文件文件失败：" + targetFile.getName());
        }

        boolean isInsert = false;
        // 再处理数据库的逻辑，数据库是否已存在记录，存在则更新，反之则新建
        SysBlog origin = findBlog(userId, blog.getDir(), blog.getFileName());
        if (origin != null) {
            // 如果博客信息已经存在，需要在数据库更新信息，即使源文件已存在
            SysBlog temp = new SysBlog();
            temp.setId(origin.getId());
            temp.setCreateDate(blog.getCreateDate());
            temp.setCharacterCount(blog.getCharacterCount());
            temp.setLastUploadTime(blog.getLastUploadTime());
            this.updateByIdIgnoreNull(temp);
        } else {
            this.insertIgnoreNullReturnKey(blog);
            origin = blog;
            isInsert = true;
        }

        sysBlogWord.setBlogId(origin.getId());
        // 最后再处理博客字数的逻辑
        if (blogWordService.getById(sysBlogWord.getBlogId()) == null) {
            blogWordService.insert(sysBlogWord);
        } else {
            blogWordService.updateById(sysBlogWord);
        }
        // 清除博客信息缓存
        BlogUtil.clearBlogCache(userId);
        return isInsert;
    }

    /**
     * 关于这里lambda具体含义，可参见本类的main方法代码示例
     */
    @Override
    public Map<String, Object> queryAll(SysBlogQueryCriteria criteria, Pageable pageable) {
        // 这里 page 的实现类为 PageImpl
        // 其中 root包含了表信息，criteriaQuery包含了查询字段及其排序信息，criteriaBuilder包含了字段间关系与具查询条件信息
        Page<SysBlog> page = sysBlogRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder), pageable);
        return PageUtil.toPage(page.map(sysBlogMapper::toDto));
    }

    @Override
    public List<SysBlogDto> queryAll(SysBlogQueryCriteria criteria) {
        return sysBlogMapper.toDto(sysBlogRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder)));
    }

    @Override
    @Transactional
    public SysBlogDto findById(Long id) {
        SysBlog sysBlog = sysBlogRepository.findById(id).orElseGet(SysBlog::new);
        ValidationUtil.isNull(sysBlog.getId(), "SysBlog", "id", id);
        return sysBlogMapper.toDto(sysBlog);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysBlogDto create(SysBlog resources) {
        // ID为默认为null，则直接进行插入操作
        // 若ID不为null，则先进行一次查询，再判断是插入或更新
        return sysBlogMapper.toDto(sysBlogRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(SysBlog resources) {
        // 如果根据id找不到则返回一个new实体对象
        SysBlog sysBlog = sysBlogRepository.findById(resources.getId()).orElseGet(SysBlog::new);
        ValidationUtil.isNull(sysBlog.getId(), "SysBlog", "id", resources.getId());
        sysBlog.copy(resources);
        // 再实体类ID不为null的前提先，不管这里是不是显式主动查一遍
        // save隐式含义都会先去根据ID线程内最多查一遍再去判断是否进行插入或更新操作
        sysBlogRepository.save(sysBlog);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            sysBlogRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<SysBlogDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (SysBlogDto sysBlog : all) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("博客目录", sysBlog.getDir());
            map.put("博客标题", sysBlog.getTitle());
            map.put("博客文件名称", sysBlog.getFileName());
            map.put("拥有者id", sysBlog.getUserId());
            map.put("博客类型", sysBlog.getBlogType());
            map.put("创建时间", sysBlog.getCreateTime());
            map.put("上次上传时间", sysBlog.getLastUploadTime());
            map.put("排序号", sysBlog.getOrderNum());
            map.put("总浏览次数", sysBlog.getViewCount());
            map.put("昨日访问", sysBlog.getYesterdayView());
            map.put("字符数", sysBlog.getCharacterCount());
            map.put("状态", sysBlog.getStatus());
            map.put("更新时间", sysBlog.getUpdateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    public static void main(String[] args) throws InterruptedException {
        // 线程lambda简写示例
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("kkkkkkkk");
            }
        }).start();
        new Thread(() -> {
            System.out.println("hhhhhhhh");
        }).start();
        // Specification使用lambda简写示例，说句老实话，我宁愿多写一点代码
        // 虽然习惯之后是挺简洁的，但第一次看，一脸懵逼
        SysBlogServiceImpl service = new SysBlogServiceImpl(null, null, null, null);
        service.sysBlogRepository.findAll(new Specification<SysBlog>() {
            @Override
            public Predicate toPredicate(Root<SysBlog> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return null;
            }
        });
        service.sysBlogRepository.findAll((root, query, criteriaBuilder) -> {
            return null;
        });
        Thread.sleep(1000);
    }
}