package site.xiaokui.util;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLReady;
import site.xiaokui.domain.SysBlog;
import site.xiaokui.domain.enums.BlogTypeEnum;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 批量更新博客工具
 *
 * @author HK
 * @date 2021-01-11 14:25
 */
@Slf4j
public class BatchUpdateBlogUtil {

    private static int okRow = 0, errorRow = 0, updateRow = 0, updateFail = 0, notUpdateRow = 0;

    private static final String LOCAL_MD_DIR = "/home/hk-pc/gitee/myBlog/md/";

    public static void main(String[] args) throws Exception {
        SQLManager sqlManager = LocalSqlManager.getSqlManager();
        List<SysBlog> list = sqlManager.execute(new SQLReady("select * from sys_blog order by dir,id"), SysBlog.class);
        for (int i = 0; i < list.size(); i++) {
            SysBlog item = list.get(i);
            String dir = item.getDir();
            String fileName = item.getFileName();
            String title = item.getTitle();
            String filePath = LOCAL_MD_DIR + dir + "/" + dir + "：" + fileName + "-" + item.getOrderNum() + "-" + item.getCreateDate() + ".md";
            filePath = filePath.replace("*", "@");
            if (item.getBlogType().equals(BlogTypeEnum.PUBLIC.getCode())) {
                File mdFile = new File(filePath);
                // 数据库可以匹配到本地md文件
                if (mdFile.exists()) {
                    okRow++;
                    // 数据库为 * ，本地为 @，需要替换
                    if (title.contains("@")) {
                        title = title.replace("@", "*");
                        sqlManager.executeUpdate(new SQLReady("update sys_blog set title = '" + title + "', file_name = '" + title + "' where id = " + item.getId()));
                    }
                    uploadFileIfNecessary(item, mdFile);
                } else {
                    System.err.println(filePath);
                    errorRow++;
                }
            } else if (item.getBlogType().equals(BlogTypeEnum.PROTECTED.getCode())) {
                filePath = LOCAL_MD_DIR + dir + "/" + item.getCreateDate() + "-" + title + ".md";
                File mdFile = new File(filePath);
                if (!mdFile.exists()) {
                    System.err.println(filePath);
                    errorRow++;
                } else {
                    okRow++;
                    uploadFileIfNecessary(item, mdFile);
                }
            }
        }
        System.out.println("正确条数:" + okRow + " 错误条数:" + errorRow + " 成功更新文件数:" + updateRow + " 失败更新文件数:" + updateFail + "未更新文件数:" + notUpdateRow);
    }

    private static void uploadFileIfNecessary(SysBlog item, File mdFile) {
        // 如果文件内容发生了变化，需要更新
        if (item.getLastUploadTime().getTime() != mdFile.lastModified()) {
            Map<String, Object> map = new HashMap<>(4);
            map.put("file", mdFile);
            map.put("token", "a%f@4d");
            map.put("lastModified", mdFile.lastModified());
            JSONObject json = JSONObject.parseObject(HttpUtil.post("http://localhost:9090/api/blog/asyncBlog", map));
            if (json != null && (json.getInteger("code") == 200 || json.getInteger("code") == 0)) {
                updateRow++;
            } else {
                System.err.println(json);
                updateFail++;
            }
        } else {
            notUpdateRow++;
        }
    }
}
