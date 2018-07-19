package site.xiaokui.common.util.hk;

import java.io.*;

/**
 * @author HK
 * @date 2017-09-06 20:14
 */
public class ShowMeCodeLineUtil {

    private static final String JAVA_SUFFIX = ".java";

    private static int javaFileTotal = 0;
    private static int javaCodeLine = 0;
    private static int whiteSpaceLine = 0;
    private static int javaCommentLine = 0;

    public static void main(String[] hk) {
        String path = "/home/hk/IdeaWorkSpace/newxiaokui/src/main/java";
//        String test = path + "/site/xiaokui/common/util/hk/CodeLineTest.java";
//        showMeCodeLine(test);
        showMeCodeLine(path);
    }

    private ShowMeCodeLineUtil(){}

    /**
     * 来，看看你写了多少行bug了
     */
    private static void showMeCodeLine(String path) {
        long beginTime = System.currentTimeMillis();
        File root = new File(path);
        if (!root.exists()) {
            System.out.println("非法路径");
            return;
        }
        try {
            readFile(root);
            System.out.println("目录" + path + "下共有：");
            System.out.println("java文件" + javaFileTotal + "个，有效java代码(不含注释和空行)" + javaCodeLine + "行，java注释" + javaCommentLine + "行" + "，空白行" + whiteSpaceLine + "行");
        } catch (IOException e) {
            throw new RuntimeException("统计代码行数时出现错误,错误信息：" + e.getMessage());
        }
        long endTime = System.currentTimeMillis();
        System.out.println("统计耗时：" + (endTime - beginTime) + "ms");
    }

    private static void readFile(File file) throws IOException {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null || files.length == 0) {
                return;
            }
            for (File temp : files) {
                readFile(temp);
            }
        } else {
            countJavaFile(file);
        }
    }

    private static void countJavaFile(File file) throws IOException {
        if (file.getName().endsWith(JAVA_SUFFIX)) {
            javaFileTotal++;
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line, str;
            while ((line = br.readLine()) != null) {
                str = line.trim();
                if (str.length() == 0) {
                    whiteSpaceLine++;
                } else if (isJavaComment(str)) {
                    javaCommentLine++;
                } else {
                    javaCodeLine++;
                }
            }
            br.close();
        }
    }

    private static boolean isJavaComment(String line) {
        return line.startsWith("//") || line.startsWith("*") || line.startsWith("/**");
    }
}
