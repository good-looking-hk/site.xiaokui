package site.xiaokui.common.hk;

import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

/**
 * @author HK
 * @date 2019-10-14 17:17
 */
public class Markdown {

    public static void main(String[] args) throws IOException {
        File file = new File("test.md");
        if (!file.exists()) {
            throw new RuntimeException("文件不存在");
        }
        FileReader fileReader = new FileReader(file);
        MutableDataSet options = new MutableDataSet();
        options.set(Parser.EXTENSIONS, Arrays.asList(TablesExtension.create()));
        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();
        Node document = parser.parseReader(fileReader);
        String html = renderer.render(document);
        File target = new File("target.html");
        if (!target.exists()) {
            target.createNewFile();
        }
        FileWriter fileWriter = new FileWriter(target);
        if (html != null) {
            fileWriter.write(html);
            fileWriter.flush();
        }
        fileWriter.close();
        System.out.println(file.length());
        System.out.println(html.length());
        System.out.println(document.getTextLength());
        System.out.println(target.length());
    }
}
