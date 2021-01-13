package site.xiaokui.util;

import org.beetl.ext.fn.StringUtil;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author HK
 * @date 2021-01-12 16:22
 */
public class Test {

    public static void main(String[] args) throws URISyntaxException {
        File file = new File("/home/hk-pc/gitee/myBlog/md/Java多线程/Java多线程：进阶必读*-1-20180721.md");

        URI uri = new URI("file:///home/hk-pc/gitee/myBlog/md/Java多线程/Java多线程：进阶必读-1-20180721.md");
        System.out.println(uri.getPath());
        System.out.println(new File(uri).exists());
        System.out.println(file.exists());
    }
}
