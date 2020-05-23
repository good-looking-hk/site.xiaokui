package site.xiaokui.config.beetl;

import org.beetl.core.Context;
import org.beetl.core.Function;
import org.beetl.core.Resource;

import java.io.IOException;
import java.io.Reader;

/**
 * 留作纪念，这个issue是我向作者提的，作者当晚就改好了，很赞
 * @author HK
 * @date 2018-06-26 23:54
 */
public class PrintFile implements Function {

    @Override
    public Object call(Object[] paras, Context ctx) {
        String path = (String) paras[0];
        Resource resource = ctx.gt.getResourceLoader().getResource(path);
        Reader reader = resource.openReader();
        char[] buff = new char[100];
        int rc;
        try {
            while ((rc = reader.read(buff)) != -1) {
                ctx.byteWriter.write(buff, rc);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "";
    }
}
