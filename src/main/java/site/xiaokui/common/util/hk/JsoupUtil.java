package site.xiaokui.common.util.hk;

import site.xiaokui.common.util.StringUtil;

import java.io.*;

/**
 * @author HK
 * @date 2018-06-30 19:49
 */
public class JsoupUtil {

    // <span class="hljs-comment line-number">1.</span>
//    public static void main(String[] args) throws IOException {
//        File file = new File("logs/test.html");
//        Document doc = Jsoup.parse(file, "UTF-8");
//        Elements elements = doc.getElementsByClass("hljs-comment line-number");
//        for (Element element : elements) {
//            element.remove();
//        }
//        FileWriter fileWriter = new FileWriter(new File("logs/temp1.html"));
//        fileWriter.write(doc.html());
//        fileWriter.flush();
//        fileWriter.close();
//    }

    public static void main(String[] args) throws IOException {
        File root = new File("/home/hk/NewBlog");
        File[] dirs = root.listFiles();
        for (File dir : dirs) {
            if (dir.listFiles() == null || dir.listFiles().length == 0) {
                continue;
            }
            for (File son : dir.listFiles()) {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(son));
                File old = new File("/home/hk/OldBlog/" + dir.getName() + "/" + son.getName());
                File parent = new File(old.getParent());
                if (!parent.exists()) {
                    parent.mkdirs();
                }
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(old));

                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    if (line.contains("<span class=\"hljs-comment line-number\">")) {
                        for (int i = 0; i < 500; i++) {
                            line = StringUtil.replace(line, "<span class=\"hljs-comment line-number\">" + i + ".</span>", "");
                        }
                        bufferedWriter.write(line);
                    } else {
                        bufferedWriter.write(line);
                    }
                }
                bufferedWriter.flush();
                bufferedWriter.close();
                bufferedReader.close();
            }
        }
    }


//            if (line.startsWith("</div><div class=\"hljs-line\"><span class=\"hljs-comment line-number\">")) {
//                int start = line.indexOf("<span");
//                int end = line.indexOf("</span>");
//                if (start > -1 && end > -1) {
//                    String new1 = line.substring(0, start);
//                    String new2 = line.substring(end + 7, line.length());
//                    bufferedWriter.write(new1 + new2);
//                    bufferedWriter.newLine();
//                }
//            } else if (line.startsWith("<pre class=\"prettyprint with-line-number hljs-dark\">")) {
//                String sss = "<span class=\"hljs-comment line-number\">";
//                int start = line.indexOf(sss);
//                int end = start + 1;
//                String new1 = line.substring(0, start);
//                String new2 = line.substring(end + sss.length() + 8, line.length());
//                bufferedWriter.write(new1 + new2);
//                bufferedWriter.newLine();
//            } else {
//                bufferedWriter.write(line);
//                bufferedWriter.newLine();
//            }
}