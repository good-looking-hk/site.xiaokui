package site.xiaokui.util;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.utils.FileUtil;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLReady;
import org.omg.CORBA.OBJ_ADAPTER;
import site.xiaokui.domain.SysBlog;
import site.xiaokui.domain.UploadBlog;
import site.xiaokui.domain.enums.BlogTypeEnum;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 批量更新博客工具
 * @author HK
 * @date 2021-01-11 14:25
 */
@Slf4j
public class BatchUpdateBlogUtil {

    private static final String LOCAL_MD_DIR = "/home/hk-pc/gitee/myBlog/md/";

    public static void main(String[] args) throws Exception {
        SQLManager sqlManager = LocalSqlManager.getSqlManager();
        List<SysBlog> list = sqlManager.execute(new SQLReady("select * from sys_blog order by dir,id"), SysBlog.class);
        AtomicInteger okRow = new AtomicInteger();
        AtomicInteger errorRow = new AtomicInteger();
        for (int i = 0; i < list.size(); i++) {
            SysBlog item = list.get(i);
            String dir = item.getDir();
            String fileName = item.getFileName();
            String title = item.getTitle();
            Integer orderNum = item.getOrderNum();
            Integer createDate = item.getCreateDate();
            String filePath = LOCAL_MD_DIR + dir + "/" + dir + "：" +  fileName + "-" + orderNum + "-" + createDate + ".md";
            filePath = filePath.replace("*", "@");
            if (item.getBlogType().equals(BlogTypeEnum.PUBLIC.getCode())) {
                File mdFile = new File(filePath);
                if (mdFile.exists()) {
                    System.out.println("dir=" + dir + " file_name=" + fileName + " title=" + title);
                    // 数据库为 * ，本地为 @
                    if (title.contains("@")) {
                        title = title.replace("@", "*");
                        sqlManager.executeUpdate(new SQLReady("update sys_blog set title = '" + title + "', file_name = '" + title + "' where id = " + item.getId()));
                    }
                    okRow.getAndIncrement();
                    Map<String, Object> map = new HashMap<>(4);
                    map.put("file", mdFile);
                    map.put("token", "a%f@4d");
                    JSONObject json = JSONObject.parseObject(HttpUtil.post("http://localhost:9090/api/blog/asyncBlog", map));
                    System.out.println(json);
                } else {
                    System.err.println(filePath);
                    errorRow.getAndIncrement();
                }
            } else if (item.getBlogType().equals(BlogTypeEnum.PROTECTED.getCode())) {
                filePath = LOCAL_MD_DIR + dir + "/" + createDate + "-" + title + ".md";
                File file = new File(filePath);
                if (!file.exists()) {
                    System.err.println(filePath);
                    errorRow.getAndIncrement();
                } else {
                    okRow.getAndIncrement();
                }
            }
        }
        System.out.println("正确条数:" + okRow + " 错误条数:" + errorRow);
    }
}
