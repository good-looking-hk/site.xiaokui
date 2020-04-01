package site.xiaokui.module.sys.blog.service;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.cron.task.Task;
import lombok.extern.slf4j.Slf4j;
import org.beetl.sql.core.SQLReady;
import org.beetl.sql.core.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;
import site.xiaokui.ScheduleCenter;
import site.xiaokui.common.aop.annotation.Log;
import site.xiaokui.module.base.entity.ResultEntity;
import site.xiaokui.module.base.service.BaseService;
import site.xiaokui.module.base.service.EmailService;
import site.xiaokui.module.base.service.RedisService;
import site.xiaokui.module.sys.blog.RedisKey;
import site.xiaokui.module.sys.blog.entity.SysBlog;
import site.xiaokui.module.sys.blog.entity.UploadBlog;
import site.xiaokui.module.sys.blog.util.BlogFileHelper;
import site.xiaokui.module.sys.blog.util.BlogUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static site.xiaokui.module.sys.blog.BlogConstants.HTML_SUFFIX;

/**
 * 每个ip/userId贡献阅读量的记录、黑名单（不能贡献阅读量）的记录均在每晚23：59清空
 * @author HK
 * @date 2018-06-24 22:33
 */
@Slf4j
@Service
public class BlogService extends BaseService<SysBlog> {

    @Autowired
    private BlogCacheService blogCacheService;

    @Autowired
    private RedisService redisService;

    public int getViewCount(Integer blogId) {
        return blogCacheService.getViewCount(blogId);
    }

    /**
     * 获取最多缓存，这个缓存属于热点缓存
     * @param userId 用户id
     * @param allBlogList 所有博客列表
     * @return 做多访问博客列表，从大到小排序
     */
    public List<SysBlog> mostViewList(Integer userId, List<SysBlog> allBlogList) {
        Map<String, Double> map = blogCacheService.getMostView(userId);
        if (map == null || map.size() == 0) {
            map = blogCacheService.setMostView(userId, allBlogList);
        }
        List<SysBlog> mostView = new ArrayList<>();
        // 从内存运算读取性能远远好过数据库读，找到博客的具体信息
        for (String i : map.keySet()) {
            for (SysBlog b : allBlogList) {
                if (b.getId().toString().equals(i)) {
                    b.setViewCount(map.get(i).intValue());
                    mostView.add(b);
                }
            }
        }
        return mostView;
    }

    public void addViewCount(String ip, Integer userId, Integer blogId, Integer owner) {
        blogCacheService.addViewCount(ip, userId, blogId, owner);
    }

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

    public SysBlog perBlog(SysBlog blog) {
        Query<SysBlog> query = createQuery();
        query.andEq("user_id", blog.getUserId()).andEq("dir", blog.getDir());
        // 优先使用序号，其次是日期
        if (blog.getOrderNum() != null && blog.getOrderNum() != 0) {
            query.andLess("order_num", blog.getOrderNum());
            query.desc("order_num").limit(1, 1);
        } else {
            query.andLess("create_time", blog.getCreateTime());
            query.desc("create_time").limit(1, 1);
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
            query.andGreat("create_time", blog.getCreateTime());
            query.asc("create_time").limit(1, 1);
        }
        List<SysBlog> list = query.select();
        if (list == null || list.size() == 0) {
            return null;
        }
        return list.get(0);
    }

    public UploadBlog saveTemp(MultipartFile file, Integer userId, boolean isBlog) {
        return BlogUtil.resolveUploadFile(file, userId, isBlog);
    }

    /**
     * 返回博客保存的结果信息
     */
    public ResultEntity saveBlog(SysBlog blog) {
        Integer userId = blog.getUserId();
        File file = BlogFileHelper.getInstance().findTempFile(userId, blog.getName() + HTML_SUFFIX);
        if (file == null) {
            log.info("系统找不到文件指定文件（userId={}，SysBlog={}", userId, blog);
            return ResultEntity.error("请先上传文件");
        }
        // 该文件地址是否已经已经存在，如果存在则替换
        File targetFile = BlogFileHelper.getInstance().locateFile(userId, blog.getDir(), blog.getName() + HTML_SUFFIX);
        // 数据库是否已存在记录
        SysBlog origin = findBlog(userId, blog.getDir(), blog.getName());

        if (targetFile.exists() || origin != null) {
            if (!targetFile.delete()) {
                throw new RuntimeException("删除原有文件失败");
            } else if (!file.renameTo(targetFile)) {
                throw new RuntimeException("上传文件替换原有文件失败");
            }

            // 如果文件地址未被占用，则移动文件
            if (!targetFile.exists() && !file.renameTo(targetFile)) {
                throw new RuntimeException("转存文件文件失败：" + targetFile.getName());
            }

            // 如果博客信息已经存在，需要在数据库更新信息，即使源文件已存在
            SysBlog temp = new SysBlog();
            temp.setId(origin.getId());
            temp.setCreateTime(blog.getCreateTime());
            temp.setCharacterCount(blog.getCharacterCount());
            temp.setUpdateTime(blog.getUpdateTime());
            boolean success = this.updateByIdIgnoreNull(temp);
            if (success) {
                BlogUtil.clearBlogCache(userId);
                return ResultEntity.ok("更新文件成功");
            } else {
                return ResultEntity.ok("更新文件失败");
            }
        }

        // 插入失败则删除文件，控制层和前端可进行验证，以确保此步的正确性
        try {
            this.insert(blog);
        } catch (Exception e) {
            String str = targetFile.delete() ? "成功" : "失败";
            return ResultEntity.error(e.getMessage() + "(删除上传文件" + str + ")");
        }
        BlogUtil.clearBlogCache(userId);
        return ResultEntity.ok("保存成功");
    }

    /**
     * 开始任务时，会事先准备好缓存数据
     * 依赖于{@link ScheduleCenter}执行
     * 每天23：59执行
     */
    @Log(name = "redis数据同步至数据库", writeToDB = true)
    public Task redisTask() {
        return new Task() {
            @Override
            public void execute() {
                log.info("开始执行redis任务，更新博客相关记录");
                long start = System.currentTimeMillis();
                Jedis jedis = redisService.getRedis();
                StringBuilder sb = new StringBuilder();
                try {
                    Map<String, String> map = jedis.hgetAll(RedisKey.KEY_BLACK_VIEW_IP);
                    long total = 0;
                    for (Map.Entry<String, String> entry : map.entrySet()) {
                        sb.append("user_id/ip:").append(entry.getKey()).append(" contribute ").append(entry.getValue()).append(";");
                        total += Integer.parseInt(entry.getValue());
                    }
                    // 清除ip阅读贡献量黑名单，返回删除条数，默认为1
                    jedis.del(RedisKey.KEY_BLACK_VIEW_IP);
                    long addViewCount = total;
                    sb.append("清除ip阅读贡献量黑名单").append(total).append("条;");

                    // 清除ip对某个博客的阅读贡献量
                    Set<String> strs = jedis.keys("*" + RedisKey.HASH_IP_VIEWS_SUFFIX);
                    if (strs != null && strs.size() > 0) {
                        total = jedis.del(strs.toArray(new String[0]));
                    }
                    sb.append("清除ip对某个博客的阅读贡献量").append(total).append("条;");

                    // redis记录更新至数据库
                    Set<Tuple> sets = jedis.zrangeByScoreWithScores(RedisKey.ALL_HASH_BLOG_VIEW_COUNT, "-inf", "+inf");
                    int count = 0;
                    for (Tuple t : sets) {
                        SysBlog blog = new SysBlog();
                        blog.setId(Integer.valueOf(t.getElement()));
                        double d = t.getScore();
                        if (d < 1) {
                            continue;
                        }
                        count++;
                        int i = Double.valueOf(d).intValue();
                        blog.setViewCount(i);
                        BlogService.this.getSqlManager().executeUpdate(new SQLReady(
                                "update sys_blog set yesterday = ? - view_count, view_count = ? where id = ?", i, i, blog.getId()
                        ));
                    }
                    log.info("redis博客阅读量记录更新至数据库耗时{}ms，记录为{}条", System.currentTimeMillis() - start, count);
                    sb.append("\n同步耗时").append(System.currentTimeMillis() - start).append("ms，更新博客访问记录")
                            .append(count).append("条，共新增").append(addViewCount).append("个阅读量");
                    log.info(sb.toString());
//                        EmailService.sendToMe("redis博客阅读量记录更新至数据库耗时" + (System.currentTimeMillis() - start) + "ms，记录为" + count + "条");
                } catch (Exception e) {
                    if (log.isDebugEnabled()) {
                        log.info("redis任务执行失败，异常信息如下：\n" + ExceptionUtil.stacktraceToString(e));
                    } else {
                        String msg = ExceptionUtil.stacktraceToString(e);
                        log.info("redis任务执行失败，异常信息如下：\n" + msg);
                        EmailService.sendToMe(msg);
                    }
                } finally {
                    jedis.close();
                }
            }
        };
    }
}
