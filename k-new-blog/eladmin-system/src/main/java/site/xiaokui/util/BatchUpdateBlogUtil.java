package site.xiaokui.util;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLReady;
import site.xiaokui.domain.SysBlog;

import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 批量更新博客工具
 * @author HK
 * @date 2021-01-11 14:25
 */
public class BatchUpdateBlogUtil {

    private static final String base_dir = "/xiaokui/1/1";

    private static final String localMdIdr = "/home/hk-pc/gitee/myBlog/md/";


    public static void main(String[] args) {
        SQLManager sqlManager = LocalSqlManager.getSqlManager();
        List<SysBlog> list = sqlManager.execute(new SQLReady("select * from sys_blog order by dir,id"), SysBlog.class);
        AtomicInteger okRow = new AtomicInteger();
        AtomicInteger errorRow = new AtomicInteger();
        list.forEach(item -> {
            String dir = item.getDir();
            String fileName = item.getFileName();
            String title = item.getTitle();
            Integer orderNum = item.getOrderNum();
            Integer createDate = item.getCreateDate();
            String filePath = localMdIdr + dir + "/" + dir + "：" +  fileName + "-" + orderNum + "-" + createDate + ".md";
            File mdFile = new File(filePath);
            if (mdFile.exists()) {
                System.out.println("dir=" + dir + " file_name=" + fileName + " title=" + title);
                okRow.getAndIncrement();
            } else {
                System.err.println(filePath);
//                System.err.println("dir=" + dir + " file_name=" + fileName + " title=" + title);
                errorRow.getAndIncrement();
//                sqlManager.executeUpdate(new SQLReady("update sys_blog set create_date = create_date - 2 where id = " + item.getId()));
            }
        });
        System.out.println("正确条数:" + okRow + "错误条数:" + errorRow);
    }
}
