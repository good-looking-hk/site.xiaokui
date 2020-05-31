package site.xiaokui.blog.sys.music;

import org.springframework.util.ClassUtils;

import java.io.*;

/**
 * @author HK
 * @date 2019-04-13 14:54
 */
public class FileUtil {

    private static final String STATIC_PATH;

    static {
        try {
            STATIC_PATH = ClassUtils.getDefaultClassLoader().getResource("static").getPath();
        } catch (Exception e) {
            throw e;
        }
    }

    public static boolean findFiles() {
        File file1 = new File(STATIC_PATH + "/" + "music" + "/" + "temp.mp3");
        File file2 = new File(STATIC_PATH + "/" + "lrc" + "/" + "temp.lrc");
        File file3 = new File(STATIC_PATH + "/" + "img" + "/" + "temp.jpg");
        File file4 = new File(STATIC_PATH + "/" + "img" + "/" + "temp.png");
        if (file1.exists() && file2.exists() && (file3.exists() || file4.exists())) {
            return true;
        }
        return false;
    }

    public static void delFiles(Integer id) {
        File file1 = new File(STATIC_PATH + "/" + "music" + "/" + id +  ".mp3");
        File file2 = new File(STATIC_PATH + "/" + "lrc" + "/" + id + ".lrc");
        File file3 = new File(STATIC_PATH + "/" + "img" + "/" + id + ".png");
        file1.delete();
        file2.delete();
        file3.delete();
    }

    public static boolean renameById(Integer id) {
        File file1 = new File(STATIC_PATH + "/" + "music" + "/" + "temp.mp3");
        File file2 = new File(STATIC_PATH + "/" + "lrc" + "/" + "temp.lrc");
        File file3 = new File(STATIC_PATH + "/" + "img" + "/" + "temp.jpg");
        File file4 = new File(STATIC_PATH + "/" + "img" + "/" + "temp.png");
        file1.renameTo(new File(file1.getParent() + "/" + id + ".mp3"));
        file2.renameTo(new File(file2.getParent() + "/" + id + ".lrc"));
        if (file3.exists()) {
            file3.renameTo(new File(file3.getParent() + "/" + id + ".jpg"));
        } else if (file4.exists()) {
            file4.renameTo(new File(file4.getParent() + "/" + id + ".png"));
        }
        return true;
    }

    public static void saveSong(String fileName, InputStream is) throws Exception {
        File file = new File(STATIC_PATH + "/" + "music" + "/" + fileName);
        FileOutputStream fos = new FileOutputStream(file);
        int i;
        while ((i = is.read()) != -1) {
            fos.write(i);
        }
        fos.flush();
        fos.close();
    }

    /**
     * 这一步需要好好注意,GBK需要转UTF-8
     */
    public static void saveLrc(String fileName, InputStream is) throws Exception {
        File file = new File(STATIC_PATH + "/" + "lrc" + "/" + fileName);
        FileOutputStream fos = new FileOutputStream(file);
        int i;
        while ((i = is.read()) != -1) {
            fos.write(i);
        }
        fos.flush();
        fos.close();
        String encodeType = EncodeUtil.getEncode(file.getAbsolutePath(), true);
        //
        if (encodeType.equals("GBK")) {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "GBK");
            File temp = new File(file.getParent() + "/" + "temp-" + file.getName());
            OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(temp), "UTF-8");
            int j;
            while ((j = isr.read()) != -1) {
                osw.write(j);
            }
            osw.flush();
            osw.close();
            file.delete();
            temp.renameTo(file);
        }
    }

    public static void saveImg(String fileName, InputStream is) throws Exception {
        File file = new File(STATIC_PATH + "/" + "img" + "/" + fileName);
        FileOutputStream fos = new FileOutputStream(file);
        int i;
        while ((i = is.read()) != -1) {
            fos.write(i);
        }
        fos.flush();
        fos.close();
    }

    private static void checkEncode(InputStream is) throws Exception {
        byte[] b = new byte[3];
        is.read(b);
        is.close();
        if (b[0] == -17 && b[1] == -69 && b[2] == -65) {
            System.out.println("编码为UTF-8");

        } else {
            System.out.println("可能是GBK，也可能是其他编码");
        }
    }

    public static void main(String[] args) throws Exception {
        String path = "/home/hk/Desktop/src/陈娟儿&六哲 - 错错错.lrc";
        File file = new File(path);
        System.out.println(file.exists());
        checkEncode(new FileInputStream(file));
        path = "/home/hk/Desktop/src/慕容晓晓-爱情买卖.lrc";
        file = new File(path);
        System.out.println(file.exists());
        checkEncode(new FileInputStream(file));
        path = "/home/hk/Desktop/src/7.lrc";
        file = new File(path);
        System.out.println(file.exists());
        checkEncode(new FileInputStream(file));
        path = "/home/hk/Desktop/src/8.lrc";
        file = new File(path);
        System.out.println(file.exists());
        checkEncode(new FileInputStream(file));

//        File file1 = new File(file.getParent() + "/" + "test-" + file.getName());
//        FileInputStream fis = new FileInputStream(file);
//        FileOutputStream fos = new FileOutputStream(file1);
//        int i;
//        while ((i = fis.read()) != -1) {
//            fos.write(i);
//        }
//        fos.flush();
//        fos.close();

//        File file2 = new File(file.getParent() + "/" + "test1-" + file.getName());
//        FileInputStream fis = new FileInputStream(file);
//        InputStreamReader isr = new InputStreamReader(fis, "GBK");
//        FileOutputStream fos = new FileOutputStream(file2);
//        OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
//        int i;
//        while ((i = isr.read()) != -1) {
//            osw.write(i);
//        }
//        osw.flush();
//        osw.close();
    }
}
