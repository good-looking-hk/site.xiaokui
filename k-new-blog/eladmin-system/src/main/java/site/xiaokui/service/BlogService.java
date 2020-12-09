//package site.xiaokui.service;
//
//import cn.hutool.core.date.DateUtil;
//import cn.hutool.core.exceptions.ExceptionUtil;
//import lombok.extern.slf4j.Slf4j;
//import org.beetl.sql.core.SQLReady;
//import org.beetl.sql.core.query.Query;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//import site.xiaokui.domain.ResultEntity;
//import site.xiaokui.domain.SysBlog;
//import site.xiaokui.domain.UploadBlog;
//import site.xiaokui.util.BlogFileHelper;
//import site.xiaokui.util.BlogUtil;
//
//import java.io.File;
//import java.util.*;
//import java.util.concurrent.TimeUnit;
//
//
///**
// * 每个ip/userId贡献阅读量的记录、黑名单（不能贡献阅读量）的记录均在每晚23：59清空
// *
// * @author HK
// * @date 2018-06-24 22:33
// */
//@Slf4j
//@Service
//public class BlogService extends BaseService<SysBlog> {
//
//    private static final String HTML_SUFFIX = ".html";
//    @Autowired
//    private BlogCacheService blogCacheService;
//
//    @Autowired
//    private RedisService redisService;
//
//    @Value("${spring.profiles.active}")
//    private String profile;
//
//    public int getViewCountFromRedis(Long userId, Long blogId) {
//        return blogCacheService.getViewCount(userId, blogId);
//    }
//
//    public void addViewCountIntoRedis(String ip, Long userId, Long blogId, Long owner) {
//        Date now = new Date();
//        int hours = DateUtil.hour(now, true);
//        int minute = DateUtil.minute(now);
//        if (userId == null) {
//            // 谷歌爬虫一般从23:20多一点开始，到1:00介绍
//            // 中午从12:10多一点开始，到12:30左右结束
//            if (hours > 23 && minute > 20) {
//                return;
//            }
//            if (hours < 6) {
//                return;
//            }
//            if (hours == 12 && 10 < minute && minute < 30) {
//                // 可能存在误杀
//                // 在不要求用户登录的情况下，想要不被谷歌爬虫刷一个访问量，只有两种办法
//                // 1.前端js判断是不是爬虫（可以判断是不是国外ip）,然后单独调增加访问量接口，但这样，接口会暴露
//                // 2.后端判断是不是爬虫，但可能需要访问远程ip库，白白增加了网络请求开销
//                // TODO
//            }
//        }
//        blogCacheService.addViewCount(ip, userId, blogId, owner);
//    }
//
//    public LinkedHashMap<Long, Integer> getMostViewTopN(Long userId, int n) {
//        return blogCacheService.getMostViewTopN(userId, n);
//    }
//
//    /**
//     * 一般由缓存调用
//     */
//    public void setMostViewCache(Long userId) {
//        blogCacheService.setMostView(userId, listBlogByUserId(userId));
//    }
//
//    public List<SysBlog> listBlogByUserId(Long userId) {
//        SysBlog sysBlog = new SysBlog();
//        sysBlog.setUserId(userId);
//        return match(sysBlog);
//    }
//
//    public SysBlog findBlog(Long userId, String dir, String name) {
//        SysBlog sysBlog = new SysBlog();
//        sysBlog.setUserId(userId);
//        sysBlog.setDir(dir);
//        sysBlog.setName(name);
//        return matchOne(sysBlog);
//    }
//
//    public SysBlog perBlog(SysBlog blog) {
//        Query<SysBlog> query = createQuery();
//        query.andEq("user_id", blog.getUserId()).andEq("dir", blog.getDir());
//        // 优先使用序号，其次是日期
//        if (blog.getOrderNum() != null && blog.getOrderNum() != 0) {
//            query.andLess("order_num", blog.getOrderNum());
//            query.desc("order_num").limit(1, 1);
//        } else {
//            query.andLess("create_time", blog.getCreateTime());
//            query.desc("create_time").limit(1, 1);
//        }
//        List<SysBlog> list = query.select();
//        if (list == null || list.size() == 0) {
//            return null;
//        }
//        return list.get(0);
//    }
//
//    public SysBlog nexBlog(SysBlog blog) {
//        Query<SysBlog> query = createQuery();
//        query.andEq("user_id", blog.getUserId()).andEq("dir", blog.getDir());
//        // 优先使用序号，其次是日期
//        if (blog.getOrderNum() != null && blog.getOrderNum() != 0) {
//            query.andGreat("order_num", blog.getOrderNum());
//            query.asc("order_num").limit(1, 1);
//        } else {
//            query.andGreat("create_time", blog.getCreateTime());
//            query.asc("create_time").limit(1, 1);
//        }
//        List<SysBlog> list = query.select();
//        if (list == null || list.size() == 0) {
//            return null;
//        }
//        return list.get(0);
//    }
//
//    public UploadBlog saveTemp(MultipartFile file, Long userId, boolean isBlog) {
//        return BlogUtil.resolveUploadFile(file, userId, isBlog);
//    }
//
//    /**
//     * 返回博客保存的结果信息
//     */
//    public ResultEntity saveBlog(SysBlog blog) {
//        Long userId = blog.getUserId();
//        File file = BlogFileHelper.getInstance().findTempFile(userId, blog.getName() + HTML_SUFFIX);
//        if (file == null) {
//            log.info("系统找不到文件指定文件（userId={}，SysBlog={}", userId, blog);
//            return ResultEntity.error("请先上传文件");
//        }
//        // 该文件地址是否已经已经存在，如果存在则替换
//        File targetFile = BlogFileHelper.getInstance().locateFile(userId, blog.getDir(), blog.getName() + HTML_SUFFIX);
//        boolean isUpdate = targetFile.exists();
//        // 数据库是否已存在记录
//        SysBlog origin = findBlog(userId, blog.getDir(), blog.getName());
//
//        if (isUpdate && !targetFile.delete()) {
//            throw new RuntimeException("删除原有文件失败");
//        }
//        // 如果文件地址未被占用，则移动文件
//        if (!targetFile.exists() && !file.renameTo(targetFile)) {
//            throw new RuntimeException("转存文件文件失败：" + targetFile.getName());
//        }
//        if (origin != null) {
//            // 如果博客信息已经存在，需要在数据库更新信息，即使源文件已存在
//            SysBlog temp = new SysBlog();
//            temp.setId(origin.getId());
//            temp.setCreateTime(blog.getCreateTime());
//            temp.setCharacterCount(blog.getCharacterCount());
//            temp.setUpdateTime(blog.getUpdateTime());
//            boolean success = this.updateByIdIgnoreNull(temp);
//            if (success) {
//                BlogUtil.clearBlogCache(userId);
//                return ResultEntity.ok("更新文件成功");
//            } else {
//                return ResultEntity.ok("更新文件失败");
//            }
//        }
//
//        // 插入失败则删除文件，控制层和前端可进行验证，以确保此步的正确性
//        try {
//            this.insert(blog);
//        } catch (Exception e) {
//            String str = targetFile.delete() ? "成功" : "失败";
//            return ResultEntity.error(e.getMessage() + "(删除上传文件" + str + ")");
//        }
//        BlogUtil.clearBlogCache(userId);
//        return ResultEntity.ok("保存成功");
//    }
//
//    /**
//     * 同步Redis博客访问量到数据库
//     */
//    public void syncRedisViewCountToDb() {
//        try (Jedis jedis = redisService.getRedis()) {
//            long start = System.nanoTime();
//            int count = 0;
//            // 某篇博客的阅读贡献量
//            Set<String> keySet = jedis.keys("*" + RedisKey.USER_BLOG_VIEW_COUNT_SORT_MAP_SUFFIX);
//            if (keySet == null || keySet.size() == 0) {
//                return;
//            }
//            for (String key : keySet) {
//                // redis记录更新至数据库
//                Set<Tuple> sets = jedis.zrangeByScoreWithScores(key, "-inf", "+inf");
//                for (Tuple t : sets) {
//                    SysBlog blog = new SysBlog();
//                    blog.setId(Long.valueOf(t.getElement()));
//                    double d = t.getScore();
//                    if (d < 1) {
//                        continue;
//                    }
//                    count++;
//                    blog.setViewCount((int) d);
//                    this.getSqlManager().executeUpdate(new SQLReady(
//                            "update sys_blog set yesterday = ? - view_count, view_count = ? where id = ?", d, d, blog.getId()
//                    ));
//                }
//            }
//            log.info("redis博客阅读量记录更新至数据库耗时{}ms，记录为{}条", TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start), count);
//        } catch (Exception e) {
//            if (Constants.PROFILE_REMOTE.equals(profile)) {
//                String msg = ExceptionUtil.stacktraceToString(e);
//                log.error("redis任务执行失败，异常信息如下：\n" + msg);
//                EmailService.sendError(msg);
//            } else {
//                log.error("redis任务执行失败，异常信息如下：\n" + ExceptionUtil.stacktraceToString(e));
//            }
//        }
//    }
//
//    public void clearContributeBlackList() {
//        log.info("执行redis任务:清空博客贡献黑名单");
//        long start = System.currentTimeMillis();
//        try (Jedis jedis = redisService.getRedis()) {
//            StringBuilder sb = new StringBuilder();
//            // 用户或IP总贡献访问量
//            Map<String, String> map = jedis.hgetAll(RedisKey.USER_OR_IP_CONTRIBUTE_VIEW_COUNT_MAP);
//            long total = 0;
//            for (Map.Entry<String, String> entry : map.entrySet()) {
//                sb.append("user_id/ip:").append(entry.getKey()).append(" contribute ").append(entry.getValue()).append(";\n");
//                total += Integer.parseInt(entry.getValue());
//            }
//            // 清除ip阅读贡献量黑名单，返回删除条数，默认为1
//            jedis.del(RedisKey.USER_OR_IP_CONTRIBUTE_VIEW_COUNT_MAP);
//            sb.append("清除用户/IP对所有博客的阅读贡献量").append(total).append("条;\n");
//
//            // 清除用户或IP对某篇博客的阅读贡献量
//            Set<String> strs = jedis.keys("*" + RedisKey.USER_OR_IP_TO_BLOG_CONTRIBUTE_VIEW_COUNT_SUFFIX);
//            if (strs != null && strs.size() > 0) {
//                total = jedis.del(strs.toArray(new String[0]));
//            }
//            sb.append("清除用户/IP对单篇博客的阅读贡献量").append(total).append("条;\n");
//            log.info(sb.toString());
//        } catch (Exception e) {
//            if (Constants.PROFILE_REMOTE.equals(profile)) {
//                String msg = ExceptionUtil.stacktraceToString(e);
//                log.error("redis任务执行失败，异常信息如下：\n" + msg);
//                EmailService.sendError(msg);
//            } else {
//                log.error("redis任务执行失败，异常信息如下：\n" + ExceptionUtil.stacktraceToString(e));
//            }
//        }
//    }
//}
