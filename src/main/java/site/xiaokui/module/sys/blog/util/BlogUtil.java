package site.xiaokui.module.sys.blog.util;

import cn.hutool.core.date.DatePattern;
import org.springframework.web.multipart.MultipartFile;
import site.xiaokui.common.util.StringUtil;
import site.xiaokui.common.util.TimeUtil;
import site.xiaokui.module.sys.blog.entity.BlogStatusEnum;
import site.xiaokui.module.sys.blog.entity.SysBlog;
import site.xiaokui.module.sys.blog.entity.UploadBlog;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static site.xiaokui.module.sys.blog.BlogConstants.HTML_SUFFIX;
import static site.xiaokui.module.sys.blog.BlogConstants.PREFIX;

/**
 * @author HK
 * @date 2018-06-25 00:01
 */
public class BlogUtil {

    private static final String BLOG_PREFIX = PREFIX + "/";

    /**
     * 获取博客实体对象的对应的访问url路径
     */
    public static String getBlogPath(String blogDir, String blogName, String blogSpace) {
        if (StringUtil.isEmpty(blogName) || StringUtil.isEmpty(blogSpace)) {
            throw new IllegalArgumentException("参数错误[" + blogName + "," + blogSpace + "]");
        }
        StringBuilder sb = new StringBuilder();
        if (StringUtil.isEmpty(blogDir)) {
            sb.append(BLOG_PREFIX).append(blogSpace).append("/").append(blogName);
            return sb.toString();
        }
        sb.append(BLOG_PREFIX).append(blogSpace).append("/").append(blogDir).append("/").append(blogName);
        return sb.toString();
    }

    /**
     * 获取博客实体对象对应的服务器本地html文件路径
     */
    public static String getFilePath(Integer userId, String blogDir, String blogName) {
        if (userId <= 0 || StringUtil.isEmpty(blogName)) {
            throw new IllegalArgumentException("参数错误[" + userId + "," + blogDir + "," + blogName + "]");
        }
        StringBuilder sb = new StringBuilder();
        if (StringUtil.isEmpty(blogDir)) {
            sb.append(userId).append("/").append(blogName);
            return sb.toString();
        }
        sb.append(userId).append("/").append(blogDir).append("/").append(blogName);
        return sb.toString();
    }

    /**
     * 对博客实体按目录划分成多个子List，由前段模板渲染显示
     * 排序操作也可在数据库进行，在数据量大时可以做比较选择
     */
    public static List<List<SysBlog>> resolveBlogList(List<SysBlog> blogList, String blogSpace) {
        List<List<SysBlog>> list = null;
        if (blogList != null && blogList.size() != 0) {
            // 自定义比较器
            blogList.sort(new SysBlog.BlogComparator());
            String newDir = blogList.get(0).getDir();
            list = new LinkedList<>();
            List<SysBlog> temp = new LinkedList<>();
            Iterator<SysBlog> it = blogList.iterator();

            // 分解list成多个子list
            while (it.hasNext()) {
                SysBlog blog = it.next();
                // 如果设置是不公开的话，那么移除
                if (blog.getStatus() != BlogStatusEnum.PUBLIC.getCode()) {
                    it.remove();
                    continue;
                }
                blog.setBlogPath(getBlogPath(blog.getDir(), blog.getName(), blogSpace));
                // 如果与上一个是属于同目录，则添加进临时list
                if (blog.getDir().equals(newDir)) {
                    temp.add(blog);
                } else {
                    // 切换到下一个目录，把临时list加入根list然后清空，重新加入新的blog，重复
                    newDir = blog.getDir();
                    list.add(temp);
                    temp = new LinkedList<>();
                    temp.add(blog);
                }
            }
            // 添加最后一个临时list
            list.add(temp);
        }
        return list;
    }

    /**
     * 解析Typora生成的html文件
     */
    public static UploadBlog resolveUploadFile(MultipartFile upload, Integer userId) {
        String fullName = upload.getOriginalFilename();
        UploadBlog blog = resolveFileName(fullName);
        BufferedReader reader = null;
        BufferedWriter writer = null;
        boolean isSuccess = false;
        File targetFile = null;
        try {
            /// 留作测试用的InputStreamReader inputFileReader = new InputStreamReader(new FileInputStream(upload), "UTF-8");
            InputStreamReader inputFileReader = new InputStreamReader(upload.getInputStream(), "UTF-8");
            reader = new BufferedReader(inputFileReader);
            targetFile = FileUtil.createTempFile(userId, blog.getName() + HTML_SUFFIX);
            if (targetFile == null) {
                throw new RuntimeException("创建文件失败，" + fullName);
            }
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(targetFile), "UTF-8");
            writer = new BufferedWriter(outputStreamWriter);
            boolean startFlag = false;

            String str;
            while ((str = reader.readLine()) != null) {
                if (!startFlag) {
                    if (str.startsWith("<body")) {
                        startFlag = true;
                    }
                } else {
                    writer.write(str);
                }
            }
            isSuccess = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeStream(reader, writer);
            if (!isSuccess && targetFile != null && !targetFile.delete()) {
                System.out.println("转换失败，删除上传转存的的文件");
            }
        }
        // finally代码块会在return执行之后，返回结果之前执行
        return blog;
    }

    /**
     * 例如 Spring源码：bean的加载-6-20180808.html，解析格式为 目录：标题-序号-日期.后缀
     *
     * @param fullName html文件全名
     * @return 解析后上传博客对象
     */
    private static UploadBlog resolveFileName(String fullName) {
        UploadBlog blog = new UploadBlog();
        if (StringUtil.isEmpty(fullName) || !fullName.endsWith(HTML_SUFFIX)) {
            blog.setErrorInfo("不合法的文件：" + fullName);
            return blog;
        }
        // 去掉后缀
        int index = fullName.lastIndexOf(".");
        fullName = fullName.substring(0, index);

        // 取出目录，建议使用中文分号
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
            blog.setErrorInfo("您没有为博客指定序号");
            return blog;
        }

        // 取出日期或序号
        String str = fullName.substring(index + 1);
        // 默认日期格式为yyyyMMdd
        if (str.length() == 8 && str.startsWith("20")) {
            Date date = TimeUtil.parse(str, DatePattern.PURE_DATE_PATTERN);
            blog.setCreateTime(date);
            // 去掉日期
            fullName = fullName.substring(0, index);
            index = fullName.lastIndexOf("-");
            if (index < 0) {
                blog.setErrorInfo("博客必须指定序号");
                return blog;
            }
            str = fullName.substring(index + 1);
        }
        // 规定最大序号不超过99
        if (str.length() < 3) {
            int orderNumb;
            try {
                orderNumb = Integer.valueOf(str);
            } catch (NumberFormatException e) {
                blog.setErrorInfo("非法排序数字：" + str);
                return blog;
            }
            blog.setOrderNum(orderNumb);
        }

        // 取出标题
        fullName = fullName.substring(0, index);
        if (fullName.length() == 0) {
            blog.setErrorInfo("博客标题不能为空");
            return blog;
        }
        blog.setName(fullName);
        if (blog.getCreateTime() == null) {
            blog.setCreateTime(new Date());
        }
        return blog;
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
        String fullName = "Spring源码：bean的加载-6.html";
        System.out.println(resolveFileName(fullName));
        fullName = "Spring源码：bean的加载-6-20181122.html";
        System.out.println(resolveFileName(fullName));
    }
}

