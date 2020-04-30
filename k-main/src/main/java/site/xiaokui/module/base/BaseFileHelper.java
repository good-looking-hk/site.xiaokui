package site.xiaokui.module.base;

import java.io.*;

/**
 * 操作用户目录更加简单
 * @author HK
 * @date 2019-06-09 20:09
 */
public abstract class BaseFileHelper {

    protected String basePath;

    private static final String TEMP_DIR = BaseConstants.TEMP_DIR;

    private static final String SLASH = BaseConstants.SLASH;

    /**
     * 根据不同的basePath，对不同的用户分级目录进行操作
     * @param basePath 一般是通过配置文件配置
     */
    protected abstract void setBasePath(String basePath);

    /**
     * 获取用户目录的File构造字符串
     */
    public String getUserDir(Integer userId) {
        return basePath + userId;
    }

    /**
     * 获取用户临时目录的File字符串
     */
    protected String getTempDir(Integer userId) {
        return getUserDir(userId) + TEMP_DIR;
    }

    /**
     * 获取用户临时目录文件的File字符串
     */
    private String getTempFile(Integer userId, String fullName) {
        return getTempDir(userId) + fullName;
    }

    /**
     * 在用户的临时目录下创建指定文件，创建失败则返回null，否则返回创建成功的File
     */
    public File createTempFile(Integer userId, String fullName) {
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
            // 可能是权限不足
            e.printStackTrace();
        }
        return file;
    }

    /**
     * 没有找到则返回null
     */
    public File findTempFile(Integer userId, String fullName) {
        String path = getTempFile(userId, fullName);
        File file = new File(path);
        return file.exists() ? file : null;
    }

    /**
     * 会主动创建目录结构
     */
    public File locateFile(Integer userId, String fullName) {
        return locateFile(userId, "", fullName);
    }

    /**
     * 定位目标文件，这里只能依靠exist判断，会主动创建目录结构
     */
    public File locateFile(Integer userId, String dir, String fullName) {
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

    public void saveInputStream(InputStream is, File file) {
        BufferedOutputStream bos = null;
        try {
            if (is.available() != 0 && file.exists()) {
                BufferedInputStream bis = new BufferedInputStream(is);
                bos = new BufferedOutputStream(new FileOutputStream(file));
                byte[] arr = new byte[1024];
                while(bis.read(arr) != -1) {
                    bos.write(arr);
                }
                bos.flush();
            }
        } catch (IOException e) {
            throw new RuntimeException("保存流至文件出错,file=" + toString(file));
        } finally {
            try {
                if (bos != null) {
                    bos.close();
                }
            } catch (IOException e) {
                // 忽略
            }
        }
    }

    public static String toString(File file) {
        return "filename:" + file.getName() + ",exist:" + file.exists() + ",path:" + file.getAbsolutePath() + "\n";
    }

}
