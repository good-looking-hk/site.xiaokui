package site.xiaokui.module.sys.blog.util;

import org.beetl.sql.core.SQLManager;
import site.xiaokui.module.sys.blog.entity.BlogStatusEnum;
import site.xiaokui.module.sys.blog.entity.SysBlog;
import site.xiaokui.module.sys.blog.entity.UploadBlog;
import site.xiaokui.module.test.beetlsql.Sql;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 此工具不建议使用，有待完善
 * TODO
 * @author HK
 * @date 2019-10-16 11:20
 */
public class BatchMd2HtmlUtil {

    private static String MD_DIR = "/home/hk/我的博客/md";

    private static String HTML_DIR = "/home/hk/我的博客/html";

    private static int successTotal = 0;

    public static void parse() throws IOException {
        File mdDir = new File(MD_DIR);
        if (!mdDir.exists() && !mdDir.isDirectory()) {
            throw new RuntimeException("md文件夹不存在：" + mdDir);
        }
        File htmlDir = new File(HTML_DIR);
        if (!htmlDir.exists()) {
            boolean success = htmlDir.mkdir();
            if (!success) {
                throw new RuntimeException("创建html文件夹失败：" + htmlDir);
            }
        }
        if (!htmlDir.isDirectory()) {
            throw new RuntimeException("html文件夹名已被占用：" + htmlDir);
        }
        parseDir(mdDir);
    }

    public static void parseDir(File file) throws IOException {
        MarkDownParser parser = MarkDownParser.PARSER;
        if (file.isDirectory()) {
            for (File temp : file.listFiles()) {
                parseDir(temp);
            }
        } else {
            if (file.getName().endsWith(".md")) {
                FileReader reader = new FileReader(file);
                UploadBlog blog = BlogUtil.resolveFileName(file.getName());
                if (blog.getDir() != null) {
                    File target = new File(HTML_DIR + "/" + blog.getDir() + "/" + blog.getName() + ".html");
                    if (!target.exists()) {
                        target.getParentFile().mkdirs();
                    }
                    FileWriter writer = new FileWriter(target);
                    MarkDownParser.ParseData data = parser.parse(reader);
                    if (data.getHtmlStr() != null) {
                        writer.write(data.getHtmlStr());
                    }
                    blog.setCharacterCount(data.getTextLength());

                    SQLManager sqlManager = Sql.getSqlManager();
                    SysBlog sysBlog = new SysBlog();
                    sysBlog.setUserId(1);
                    sysBlog.setName(blog.getName());
                    sysBlog.setTitle(blog.getName());
                    sysBlog.setDir(blog.getDir());
                    sysBlog.setCreateTime(blog.getCreateTime());
                    sysBlog.setCharacterCount(blog.getCharacterCount());
                    if (blog.getOrderNum() == null) {
                        sysBlog.setOrderNum(0);
                    } else {
                        sysBlog.setOrderNum(blog.getOrderNum());
                    }
                    // 默认博客为公开，用户可以进一步修改
                    String company = "金微蓝";
                    if (blog.getDir().equals(company)) {
                        sysBlog.setStatus(BlogStatusEnum.PROTECTED.getCode());
                    } else {
                        sysBlog.setStatus(BlogStatusEnum.PUBLIC.getCode());
                    }

                    SysBlog origin = findBlog(1, blog.getDir(), blog.getName(), sqlManager);
                    // 如果博客信息已经存在，需要在数据库更新信息，即使源文件已存在
                    if (origin != null) {
                        SysBlog temp = new SysBlog();
                        temp.setId(origin.getId());
                        temp.setCreateTime(blog.getCreateTime());
                        temp.setCharacterCount(blog.getCharacterCount());
                        sqlManager.updateTemplateById(temp);
                    } else {
                        sqlManager.insert(sysBlog);
                    }
                    successTotal++;
                }
            }
        }
    }

    public static SysBlog findBlog(Integer userId, String dir, String name, SQLManager sqlManager) {
        SysBlog sysBlog = new SysBlog();
        sysBlog.setUserId(userId);
        sysBlog.setDir(dir);
        sysBlog.setName(name);
        return sqlManager.templateOne(sysBlog);
    }

    public static void main1(String[] args) throws IOException {
        parse();
        System.out.println("成功转换" + successTotal + "个md文件");
    }

}
