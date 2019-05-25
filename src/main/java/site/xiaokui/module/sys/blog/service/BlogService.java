package site.xiaokui.module.sys.blog.service;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.cron.task.Task;
import lombok.extern.slf4j.Slf4j;
import org.beetl.sql.core.SQLReady;
import org.beetl.sql.core.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;
import site.xiaokui.ScheduleService;
import site.xiaokui.module.base.entity.RedisKey;
import site.xiaokui.module.base.entity.ResultEntity;
import site.xiaokui.module.base.service.BaseService;
import site.xiaokui.module.base.service.EmailService;
import site.xiaokui.module.base.service.RedisService;
import site.xiaokui.module.sys.blog.entity.SysBlog;
import site.xiaokui.module.sys.blog.entity.UploadBlog;
import site.xiaokui.module.sys.blog.util.BlogUtil;
import site.xiaokui.module.sys.blog.util.FileUtil;

import java.io.File;
import java.util.*;

import static site.xiaokui.module.sys.blog.BlogConstants.HTML_SUFFIX;

/**
 * 每个ip/userId贡献阅读量的记录、黑名单（不能贡献阅读量）的记录均在每晚23：59清空
 *
 * @author HK
 * @date 2018-06-24 22:33
 */
@Slf4j
@Service
public class BlogService extends BaseService<SysBlog> {

    @Autowired
    private RedisService redisService;

    /**
     * 最多访问-实时更新，需要处理数据库与Redis缓存之间的关系
     */
    @SuppressWarnings("unchecked")
    public Map<String, Double> mostView(Integer userId, List<SysBlog> blogs) {
        Jedis jedis = redisService.getRedis();
        try {
            // 不能用Double.MAX_VALUE或Double.MIN_VALUE
            Set<Tuple> sets = jedis.zrevrangeByScoreWithScores(userId + RedisKey.KEY_MOST_VIEW_SUFFIX,
                    "+inf", "-inf", 0, 10);
            Map<String, Double> map;
            if (sets == null || sets.size() == 0) {
                if (blogs == null || blogs.size() == 0) {
                    return Collections.emptyMap();
                }
                map = new HashMap<>(32);
                for (SysBlog b : blogs) {
                    if (b.getViewCount() == null) {
                        map.put(String.valueOf(b.getId()), 0.0);
                    } else {
                        map.put(String.valueOf(b.getId()), b.getViewCount().doubleValue());
                    }
                }
                log.debug("从数据库读取用户{}的博客id-访问量列表，并存至redis缓存，记录为{}条", userId, blogs.size());
                jedis.zadd(userId + RedisKey.KEY_MOST_VIEW_SUFFIX, map);
                jedis.zadd(RedisKey.HASH_BLOG_VIEW_COUNT, map);
                sets = jedis.zrevrangeByScoreWithScores(userId + RedisKey.KEY_MOST_VIEW_SUFFIX,
                        "+inf", "-inf", 1, 10);

            }
            map = new LinkedHashMap<>(10);
            for (Tuple p : sets) {
                map.put(p.getElement(), p.getScore());
            }
            log.debug("从redis缓存读取用户{}的最多访问map，记录为{}条", userId, map.size());
            return map;
        } catch (Exception e) {
            log.error("redis访问出错");
            throw e;
        } finally {
            jedis.close();
        }
    }

    /**
     * 最近上传-变动较小-过期失效
     */
    @SuppressWarnings("unchecked")
    public List<SysBlog> recentUpload(Integer userId, String blogSpace) {
        List<SysBlog> list = redisService.get(userId + RedisKey.KEY_RECENT_UPLOAD_SUFFIX, ArrayList.class);
        blogSpace = blogSpace == null ? String.valueOf(userId) : blogSpace;
        // 缓存过期
        if (list == null || list.size() == 0) {
            Query<SysBlog> query = this.createQuery();
            query.andEq("user_id", userId).desc("create_time").limit(1, 10);
            List<SysBlog> blogs = this.query(query);
            if (blogs != null && blogs.size() > 0) {
                // 缓存一天，如果不主动更新，一天后失效
                log.debug("从数据库读取用户{}最近上传的博客列表，并存至redis缓存，记录为{}条", userId, blogs.size());
                redisService.set(userId + RedisKey.KEY_RECENT_UPLOAD_SUFFIX, blogs, RedisService.ONE_DAY);
                for (SysBlog s : blogs) {
                    s.setBlogPath(BlogUtil.getBlogPath(s.getDir(), s.getName(), blogSpace));
                    s.setFilePath(BlogUtil.getFilePath(userId, s.getDir(), s.getName()));
                }
            }
            // ArrayList类型
            return blogs;
        }
        for (SysBlog s : list) {
            s.setBlogPath(BlogUtil.getBlogPath(s.getDir(), s.getName(), blogSpace));
            s.setFilePath(BlogUtil.getFilePath(userId, s.getDir(), s.getName()));
        }
        log.debug("从redis缓存读取用户{}的最近上传博客列表，记录为{}条", userId, list.size());
        return list;
    }

    public void reloadRecentUpload(Integer userId) {
        redisService.remove(userId + RedisKey.KEY_RECENT_UPLOAD_SUFFIX);
    }

    public void reloadMostView(Integer userId) {
        redisService.remove(userId + RedisKey.KEY_MOST_VIEW_SUFFIX);
    }

    /**
     * 一个ip对于一篇博客，不登录一天最多贡献2个访问量，登录最多贡献4个访问量，
     * 不登录每天至多贡献40个阅读量，登录后最多贡献80个阅读量
     * 每晚12点整清空缓存存入数据库，Redis设计如下
     * Hash键：blogId + 后缀，Field成员：ip，Value值：访问次数
     * Hash键：BLACK_VIEW_IP，Field成员：userId/ip，Value值：总贡献阅读量
     * 阅读量每天23：59更新至数据库
     */
    public void addViewCount(String ip, Integer userId, Integer blogId, Integer onwerId) {
        Jedis jedis = redisService.getRedis();
        try {
            if (userId == null) {
                // 是否还有贡献阅读量能力
                String sum = jedis.hget(RedisKey.KEY_BLACK_VIEW_IP, ip);
                if (NumberUtil.isInteger(sum)) {
                    int s = Integer.parseInt(sum);
                    if (s >= 40) {
                        return;
                    }
                }
                // 是否还能为此篇博客贡献阅读量
                sum = jedis.hget(blogId + RedisKey.HASH_IP_VIEWS_SUFFIX, ip);
                if (NumberUtil.isInteger(sum)) {
                    int s = Integer.parseInt(sum);
                    if (s >= 2) {
                        return;
                    }
                }
                // 读者有能力贡献阅读量，更新数据，没有设置缓存过期时间，需要依赖定时任务清除缓存，下同
                // 记录ip的阅读总贡献量
                jedis.hincrBy(RedisKey.KEY_BLACK_VIEW_IP, ip, 1);
                // 记录ip对该博客的的阅读贡献量
                jedis.hincrBy(blogId + RedisKey.HASH_IP_VIEWS_SUFFIX, ip, 1);
                // 记录博客的总阅读量
                jedis.zincrby(RedisKey.HASH_BLOG_VIEW_COUNT, 1.0, String.valueOf(blogId));
                // 记录用户博客的阅读量
                jedis.zincrby(onwerId + RedisKey.KEY_MOST_VIEW_SUFFIX, 1.0, String.valueOf(blogId));
                log.debug("ip({})为博客({})贡献一个阅读量", ip, blogId);
            } else {
                String id = String.valueOf(userId);
                String sum = jedis.hget(RedisKey.KEY_BLACK_VIEW_IP, id);
                if (NumberUtil.isInteger(sum)) {
                    int s = Integer.parseInt(sum);
                    if (s >= 80) {
                        return;
                    }
                }
                sum = jedis.hget(blogId + RedisKey.HASH_IP_VIEWS_SUFFIX, id);
                if (NumberUtil.isInteger(sum)) {
                    int s = Integer.parseInt(sum);
                    if (s >= 4) {
                        return;
                    }
                }
                // 记录ip的阅读总贡献量
                jedis.hincrBy(RedisKey.KEY_BLACK_VIEW_IP, id, 1);
                // 记录ip对该博客的的阅读贡献量
                jedis.hincrBy(blogId + RedisKey.HASH_IP_VIEWS_SUFFIX, id, 1);
                // 记录博客的总阅读量
                jedis.zincrby(RedisKey.HASH_BLOG_VIEW_COUNT, 1.0, String.valueOf(blogId));
                // 记录用户博客的阅读量
                jedis.zincrby(onwerId + RedisKey.KEY_MOST_VIEW_SUFFIX, 1.0, String.valueOf(blogId));
                log.debug("用户({})为博客({})贡献一个阅读量", id, blogId);
            }
        } catch (Exception e) {
            log.error("redis访问出错" + e.getMessage());
            throw e;
        } finally {
            jedis.close();
        }
    }

    /**
     * 开始任务时，会事先准备好缓存数据
     * 依赖于{@link ScheduleService}执行
     * 每天23：59执行
     */
    public Task redisTask() {
        return new Task() {
            @Override
            public void execute() {
                log.info("开始执行redis任务，更新博客相关记录");
                long start = System.currentTimeMillis();
                Jedis jedis = redisService.getRedis();
                try {
                    // 清除ip阅读贡献量黑名单
                    long r = jedis.del(RedisKey.KEY_BLACK_VIEW_IP);
                    log.info("清除ip阅读贡献量黑名单，共{}条", r);

                    // 清除ip对某个博客的阅读贡献量
                    Set<String> strs = jedis.keys("*" + RedisKey.HASH_IP_VIEWS_SUFFIX);
                    if (strs != null && strs.size() > 0) {
                        r = jedis.del(strs.toArray(new String[strs.size()]));
                    }
                    log.info("清除ip对某个博客的阅读贡献量，共{}条", r);

                    // redis记录更新至数据库，需要获取全局锁，待优化
                    // TODO
                    Set<Tuple> sets = jedis.zrangeByScoreWithScores(RedisKey.HASH_BLOG_VIEW_COUNT, "-inf", "+inf");
                    int count = 0;
                    for (Tuple t : sets) {
                        SysBlog blog = new SysBlog();
                        blog.setId(Integer.valueOf(t.getElement()));
                        Double d = t.getScore();
                        if (d < 1) {
                            continue;
                        }
                        count++;
                        int i = Double.valueOf(d).intValue();
                        blog.setViewCount(i);
                        BlogService.this.getSqlManager().executeUpdate(new SQLReady(
                                "update sys_blog set yesterday = ? - view_count and view_count = ? where id = ?", i, i, blog.getId()
                        ));
                    }
                    log.info("redis博客阅读量记录更新至数据库耗时{}ms，记录为{}条", System.currentTimeMillis() - start, count);
                    if (!log.isDebugEnabled()) {
                        EmailService.sendToMe("redis博客阅读量记录更新至数据库耗时" + (System.currentTimeMillis() - start) + "ms，记录为" + count + "条");
                    }
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
            SysBlog origin = findBlog(userId, blog.getDir(), blog.getName());
            // 如果博客信息已经存在，需要在数据库更新信息，即使源文件已存在
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
        // 插入失败则删除文件，控制层和前端可进行验证，以确保此步的正确性
        try {
            this.insert(blog);
        } catch (Exception e) {
            String str = targetFile.delete() ? "成功" : "失败";
            return ResultEntity.error(e.getMessage() + "(删除上传文件" + str + ")");
        }
        // 是最近上传缓存失效
        reloadRecentUpload(userId);
        return ResultEntity.ok("保存成功");
    }
}
