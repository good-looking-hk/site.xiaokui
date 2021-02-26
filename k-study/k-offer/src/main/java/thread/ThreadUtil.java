package thread;

/**
 * @author HK
 * @date 2021-01-24 16:10
 */
public class ThreadUtil {

    public static void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
