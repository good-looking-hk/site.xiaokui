package site.xiaokui.module.sys.blog.util;

import site.xiaokui.module.sys.blog.BlogConstants;

import java.io.File;
import java.io.IOException;

/**
 * 重写FileUtil，使之更加简单而强大
 * @author HK
 * @date 2018-06-26 16:05
 */
public class FileUtil {

    private static final String UPLOAD_PATH = BlogConstants.UPLOAD_PATH;

    private static final String TEMP_DIR = BlogConstants.TEMP_DIR;

    private static final String SLASH = BlogConstants.SLASH;

    static {
        File file = new File(UPLOAD_PATH);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                throw new RuntimeException("非法的上传路径：" + UPLOAD_PATH);
            }
        }
    }

    public static String getUserDir(Integer userId) {
        return UPLOAD_PATH + userId;
    }

    public static String getTempDir(Integer userId) {
        return getUserDir(userId) + TEMP_DIR;
    }

    public static String getTempFile(Integer userId, String fullName) {
        return getTempDir(userId) + fullName;
    }

    /**
     * 在用户的临时目录下创建指定文件，创建失败则返回null，否则返回创建成功的File
     */
    public static File createTempFile(Integer userId, String fullName) {
        String path = getTempFile(userId, fullName);
        File file = new File(path);
        if (file.exists()) {
            return file;
        }

        File parent = new File(file.getParent());
        // 如果父目录不存在，就递归创建目录
        if (!parent.exists()) {
            if (!parent.mkdirs()) {
                return null;
            }
        }
        try {
            if (!file.createNewFile()) {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * 没有找到则返回null
     */
    public static File findTempFile(Integer userId, String fullName) {
        String path = getTempFile(userId, fullName);
        File file = new File(path);
        return file.exists() ? file : null;
    }

    public static File locateFile(Integer userId, String fullName) {
        return locateFile(userId, "", fullName);
    }

    public static File locateFile(Integer userId, String dir, String fullName) {
        String path = getUserDir(userId) + SLASH + dir + SLASH + fullName;
        File file = new File(path);
        if (file.exists()) {
            return file;
        }
        // 创建必要的父目录
        File parent = new File(file.getParent());
        if (!parent.exists()) {
            if (!parent.mkdirs()) {
                throw new RuntimeException("创建文件夹失败：" + parent.getAbsolutePath());
            }
        }
        return file;
    }

    public static String toString(File file) {
        return "filename:" + file.getName() + ",exist:" + file.exists() + ",path:" + file.getAbsolutePath() + "\n";
    }

    public static void main(String[] args) {
        System.out.println(getTempDir(1));
        System.out.println(createTempFile(1, "test.html"));
        System.out.println(findTempFile(1, "test1.html"));
        System.out.println(locateFile(1, "test.html").getAbsolutePath());
        System.out.println(locateFile(1, "test", "test.html").getAbsolutePath());
        System.out.println(locateFile(2, "test", "test.html").getAbsolutePath());
    }
}
