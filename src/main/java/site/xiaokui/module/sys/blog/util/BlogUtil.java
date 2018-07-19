package site.xiaokui.module.sys.blog.util;

import org.springframework.web.multipart.MultipartFile;
import site.xiaokui.common.util.hk.StringUtil;
import site.xiaokui.module.sys.blog.entity.BlogStatusEnum;
import site.xiaokui.module.sys.blog.entity.SysBlog;
import site.xiaokui.module.sys.blog.entity.UploadBlog;

import java.io.*;
import java.util.*;

import static site.xiaokui.module.sys.blog.BlogConstants.HTML_SUFFIX;
import static site.xiaokui.module.sys.blog.BlogConstants.PREFIX;
import static site.xiaokui.module.sys.blog.BlogConstants.UPLOAD_PATH;

/**
 * @author HK
 * @date 2018-06-25 00:01
 */

public class BlogUtil {

    private static final String BLOG_PREFIX = PREFIX + "/";

    /**
     * 获取博客实体对象的对应的url路径
     */
    public static String getBlogPath(String blogDir, String blogName, String blogSpace) {
        if (StringUtil.isEmpty(blogName) || StringUtil.isEmpty(blogSpace)) {
            throw new RuntimeException("参数错误[" + blogName + "," + blogSpace + "]");
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
     * 获取博客实体对象对应的html文件路径
     */
    public static String getFilePath(Integer userId, String blogDir, String blogName) {
        if (userId <= 0 || StringUtil.isEmpty(blogName)) {
            throw new RuntimeException("参数错误[" + userId + "," + blogDir + "," + blogName + "]");
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
     */
    public static List<List<SysBlog>> resolveBlogList(List<SysBlog> blogList, String blogSpace) {
        List<List<SysBlog>> list = null;
        if (blogList != null && blogList.size() != 0) {
            Collections.sort(blogList);
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
        UploadBlog blog = new UploadBlog();
        String fullName = upload.getOriginalFilename();
        if (StringUtil.isEmpty(fullName) || !fullName.endsWith(HTML_SUFFIX)) {
            blog.setErrorInfo("不合法的文件：" + fullName);
            return blog;
        }
        String[] temp = StringUtil.split(fullName, ".");
        if (temp.length != 2) {
            blog.setErrorInfo("文件格式可能有点不正确：" + fullName);
        }
        // 获取文件前缀，去掉后缀，假设文件名fullName为 测试目录：测试博客-1
        fullName = temp[0];

        int index = fullName.indexOf("：");
        if (index < 0) {
            blog.setErrorInfo("您可能没有包含中文的分号（：）");
        }
        // 获取目录，此时dir为 测试目录
        blog.setDir(fullName.substring(0, index));

        // 此时fullNam为 测试博客-1
        fullName = fullName.substring(index + 1);
        index = fullName.indexOf("-");
        if (index > -1) {
            String str = fullName.substring(index + 1);
            int orderNumb;
            try {
                orderNumb = Integer.valueOf(str);
            } catch (NumberFormatException e) {
                blog.setErrorInfo("非法排序数字：" + str);
                return blog;
            }
            // 如果包括序号的化，orderNum为-后面的数字，此时fullName为测试博客
            fullName = fullName.substring(0, index);
            blog.setOrderNum(orderNumb);
        }
        blog.setCreateTime(new Date());
        blog.setName(fullName);

        BufferedReader reader = null;
        BufferedWriter writer = null;
        boolean isSuccess = false;
        File targetFile = null;
        try {
            /// 留作测试用的InputStreamReader inputFileReader = new InputStreamReader(new FileInputStream(upload), "UTF-8");
            InputStreamReader inputFileReader = new InputStreamReader(upload.getInputStream(), "UTF-8");
            reader = new BufferedReader(inputFileReader);
            targetFile = FileUtil.createTempFile(userId, fullName + ".html");
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
        String name = StringUtil.split(fullName, ".")[0];
        System.out.println(name);

        // 通过两个index
        int index1 = name.indexOf("：");
        String dir = name.substring(0, index1);
        System.out.println(dir);

        int index2 = fullName.indexOf("-");
        String orderNum = name.substring(index2 + 1);
        System.out.println(orderNum);

        name = name.substring(index1 + 1, index2);
        System.out.println(name);

        // 通过一个index
        fullName = "Spring源码：bean的加载-6.html";
        name = StringUtil.split(fullName, ".")[0];
        System.out.println(name);

        int index = name.indexOf("：");
        dir = name.substring(0, index);
        System.out.println(dir);
        name = name.substring(index + 1);
        System.out.println(name);

        index = name.indexOf("-");
        orderNum = name.substring(index + 1);
        System.out.println(orderNum);
        System.out.println(name.substring(0, index));
    }
}

