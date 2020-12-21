package site.xiaokui.util;

import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.Collections;

/**
 * @author HK
 * @date 2019-10-15 17:21
 */
public class MarkDownParser {

    public final static MarkDownParser PARSER = new MarkDownParser();

    private final Parser parser;

    private final HtmlRenderer renderer;

    private MarkDownParser() {
        MutableDataSet options = new MutableDataSet();
        options.set(Parser.EXTENSIONS, Collections.singletonList(TablesExtension.create()));
        parser = Parser.builder(options).build();
        renderer = HtmlRenderer.builder(options).build();
    }

    public ParseData parse(Reader reader) throws IOException {
        Node document = parser.parseReader(reader);
        return new ParseData(renderer.render(document), document.getTextLength());
    }

    @AllArgsConstructor
    @Getter
    static class ParseData {
        String htmlStr;
        int textLength;
    }
}
