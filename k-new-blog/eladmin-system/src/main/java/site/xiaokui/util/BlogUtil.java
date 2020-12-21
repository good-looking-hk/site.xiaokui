package site.xiaokui.util;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.exception.ErrorRequestException;
import org.springframework.web.multipart.MultipartFile;
import site.xiaokui.domain.BlogDetailList;
import site.xiaokui.domain.SysBlog;
import site.xiaokui.domain.UploadBlog;
import site.xiaokui.domain.WordCounter;

import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author HK
 * @date 2018-06-25 00:01
 */
@Slf4j
public class BlogUtil {

    private static final String BLOG_PREFIX = "/blog" + "/";

    private static final Map<Long, BlogDetailList> BLOG_CACHE = new HashMap<>(4);

    public static final String HTML_SUFFIX = ".html";

    public static final String MD_SUFFIX = ".md";

    /**
     * 字节为单位，默认4M
     */
    public static final long MAX_BLOG_UPLOAD_FILE = 1024 * 1024 * 4;
    /**
     * 清除缓存
     */
    public static void clearBlogCache(Long userId) {
        if (userId == null) {
            BLOG_CACHE.clear();
        } else {
            BLOG_CACHE.remove(userId);
        }
    }

    /**
     * 获取博客实体对象的对应的访问url路径
     *
     * @param blogSpace 如果blogSpace为null，则可替换为对应的userId
     */
    public static String getBlogPath(String blogDir, String blogName, String blogSpace) {
        if (StrUtil.isEmpty(blogName) || StrUtil.isEmpty(blogSpace)) {
            throw new IllegalArgumentException("参数错误[" + blogName + "," + blogSpace + "]");
        }
        StringBuilder sb = new StringBuilder();
        if (StrUtil.isEmpty(blogDir)) {
            sb.append(BLOG_PREFIX).append(blogSpace).append("/").append(blogName);
            return sb.toString();
        }
        sb.append(BLOG_PREFIX).append(blogSpace).append("/").append(blogDir).append("/").append(blogName);
        return sb.toString();
    }

    /**
     * 获取博客实体对象对应的服务器本地html文件路径
     */
    public static String getFilePath(Long userId, String blogDir, String blogName) {
        if (userId <= 0 || StrUtil.isEmpty(blogName)) {
            throw new IllegalArgumentException("参数错误[" + userId + "," + blogDir + "," + blogName + "]");
        }
        StringBuilder sb = new StringBuilder();
        if (StrUtil.isEmpty(blogDir)) {
            sb.append(userId).append("/").append(blogName);
            return sb.toString();
        }
        sb.append(userId).append("/").append(blogDir).append("/").append(blogName);
        return sb.toString();
    }

    /**
     * 对博客实体按目录划分成多个子List，由前段模板渲染显示
     * 排序操作也可在数据库进行，在数据量大时可以做比较选择
     * 需要注意清空map缓存
     */
    public static BlogDetailList resolveBlogList(List<SysBlog> blogList, Long userId, String blogSpace, boolean useCache) {
        BlogDetailList list;
        if (useCache) {
            list = BLOG_CACHE.get(userId);
            if (list == null) {
                list = new BlogDetailList(blogList, blogSpace);
                BLOG_CACHE.put(userId, list);
            }
        } else {
            list = new BlogDetailList(blogList, blogSpace);
        }
        return list;
    }

    /**
     * 解析Typora生成的html/md文件
     */
    public static UploadBlog resolveUploadFile(MultipartFile upload, Long userId, boolean isBlog) {
        String fullName = upload.getOriginalFilename();
        // 解析文件名
        UploadBlog blog = resolveFileName(fullName, isBlog);
        if (blog.getErrorInfo() != null) {
            return blog;
        }
        boolean isSuccess = false;
        File targetFile = null, mdFile;
        try {
            // 创建html目标文件
            targetFile = BlogFileHelper.getInstance().createTempFile(userId, blog.getName() + HTML_SUFFIX);
            if (targetFile == null) {
                throw new RuntimeException("创建文件失败：" + fullName);
            }
            // 目标文件输入流
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(targetFile), "UTF-8");
            BufferedWriter writer = new BufferedWriter(outputStreamWriter);

            // 创建临时文件，将上传流保存
            mdFile = BlogFileHelper.getInstance().locateFile(userId, "$md", fullName);
            if (!mdFile.exists()) {
                if (!mdFile.createNewFile()) {
                    throw new RuntimeException("创建文件失败:" + mdFile.getAbsolutePath());
                }
            }
            // md文件备份
            upload.transferTo(mdFile);
            // 本地文件流，需要读两次，一次转换html，一次统计字数
            InputStreamReader inputFileReader = new InputStreamReader(new FileInputStream(mdFile), "UTF-8");
            if (MD_SUFFIX.equals(blog.getSuffix())) {
                MarkDownParser.ParseData data = MarkDownParser.PARSER.parse(inputFileReader);
                if (data.getHtmlStr() != null) {
                    writer.write(data.getHtmlStr());
                    writer.flush();
                    writer.close();
                    inputFileReader.close();
                    blog.setUploadFile(targetFile);

                }
                // 再读一次流
                inputFileReader = new InputStreamReader(new FileInputStream(mdFile), "UTF-8");
                BufferedReader reader = new BufferedReader(inputFileReader);
                String str;
                WordCounter total = new WordCounter(0, 0, 0, 0);
                while ((str = reader.readLine()) != null) {
                    WordCounter temp = MarkdownWordCounter.calcWordCount(str);
                    total.chineseCount += temp.chineseCount;
                    total.englishCount += temp.englishCount;
                    total.numberCount += temp.numberCount;
                    total.otherCount += temp.otherCount;
                }
                blog.setWordCounter(total);
                inputFileReader.close();
                isSuccess = true;
                blog.setUploadFile(targetFile);
            }
            log.info("上传目标文件地址为：" + targetFile);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("文件读取/写入失败，目标文件信息:{}，失败信息:{}, blog信息:{}", targetFile, e.getMessage(), blog);
        } finally {
            if (!isSuccess) {
                System.out.println("转换失败，删除上传转存的的文件");
            }
        }
        // finally代码块会在return执行之后，返回结果之前执行
        return blog;
    }

    /**
     * 对于名字的解析是很严格的，下面例子都是过关的
     * 1.Spring源码：bean的加载-6-20180808.html 或 md
     * 2.Spring源码：bean的加载-20180808-6.html 或 md
     * 3.Spring源码：bean的加载-6.html 或 md
     * 4.Spring源码：bean的加载-20180808.html 或 md
     * 完整的解析格式为 目录：标题-序号-日期.后缀
     * <p>
     * 如果是非博客格式，则下面例子是过关的
     * 20190929-实习周报.md
     *
     * @param fullName html文件全名 智能解析
     * @return 解析后上传博客对象
     */
    public static UploadBlog resolveFileName(String fullName, boolean isBlog) {
        UploadBlog blog = new UploadBlog();
        if (StrUtil.isEmpty(fullName)) {
            blog.setErrorInfo("不合法的文件：" + fullName);
            return blog;
        }
        boolean legal = fullName.endsWith(HTML_SUFFIX) || fullName.endsWith(MD_SUFFIX);
        if (!legal) {
            blog.setErrorInfo("文件格式只能为html或md:" + fullName);
            return blog;
        }

        int index = fullName.lastIndexOf(".");
        // 如.html或.md
        blog.setSuffix(fullName.substring(index));
        // 去除后缀
        fullName = fullName.substring(0, index);

        // 如果是不是博客，那么大致形如这个样子 20180808-我最帅.html/md
        if (!isBlog) {
            String[] arr = fullName.split("-");
            if (arr.length != 2 || arr[0].length() != 8 || !NumberUtil.isInteger(arr[0])) {
                throw new RuntimeException("不合法文件名：" + fullName + blog.getSuffix());
            }
            blog.setCreateDate(Integer.parseInt(arr[0]));
            blog.setName(arr[1]);
            return blog;
        }

        // 后续对于博客文件的处理：取出目录，建议使用中文分号，英文分号也行
        index = fullName.contains("：") ? fullName.indexOf("：") : fullName.indexOf(":");
        if (index < 0) {
            blog.setErrorInfo("您可能没有包含中文分号（：）");
            return blog;
        }
        blog.setDir(fullName.substring(0, index));

        // 去掉目录和分号
        fullName = fullName.substring(index + 1);
        index = fullName.lastIndexOf("-");
        if (index < 0) {
            blog.setErrorInfo("您没有为博客指定序号或日期");
            return blog;
        }
        setDateAndOrderNum(fullName, index, blog);
        if (blog.getErrorInfo() != null) {
            return blog;
        }
        // 去掉尾部日期或序号或者是取出标题
        fullName = fullName.substring(0, index);
        index = fullName.lastIndexOf("-");
        if (index > 0) {
            setDateAndOrderNum(fullName, index, blog);
            if (blog.getErrorInfo() != null) {
                return blog;
            }
            // 取出标题
            fullName = fullName.substring(0, index);
        }

        if (fullName.length() == 0) {
            blog.setErrorInfo("博客标题不能为空");
            return blog;
        }
        blog.setName(fullName);
        if (blog.getCreateDate() == null) {
            blog.setCreateDate(me.zhengjie.utils.DateUtil.parseIntDate(new Date()));
        }
        return blog;
    }

    /**
     * 取出日期或序号
     */
    private static void setDateAndOrderNum(String fullName, int index, UploadBlog blog) {
        // 取出日期或序号
        String str = fullName.substring(index + 1);
        // 默认日期格式为yyyyMMdd
        if (str.length() == 8 && str.startsWith("20")) {
            Date date;
            try {
                blog.setCreateDate(Integer.parseInt(str));
            } catch (Exception e) {
                blog.setErrorInfo("非法日期：" + str);
                return;
            }
        } else if ((str.length() < 3)) {
            // 规定最大序号不超过99
            int orderNumb;
            try {
                orderNumb = Integer.parseInt(str);
            } catch (NumberFormatException e) {
                blog.setErrorInfo("非法排序数字：" + str);
                return;
            }
            blog.setOrderNum(orderNumb);
        }
    }

    private static void closeStream(Reader reader, Writer writer) {
        try {
            if (writer != null) {
                writer.flush();
                writer.close();
            }
            if (reader != null) {
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String fullName = "Spring源码：bean的加载-6-20180808.html";
        System.out.println(resolveFileName(fullName, true));
        fullName = "Spring源码：bean的加载-20180808-6.html";
        System.out.println(resolveFileName(fullName, true));
        fullName = "Spring源码：bean的加载-6.html";
        System.out.println(resolveFileName(fullName, true));
        fullName = "Spring源码：bean的加载-20180808.html";
        System.out.println(resolveFileName(fullName, true));
        int index = fullName.lastIndexOf(".");
        System.out.println(fullName.substring(index));
        fullName = "20190929-实习周报.md";
        System.out.println(resolveFileName(fullName, false));
    }
}

