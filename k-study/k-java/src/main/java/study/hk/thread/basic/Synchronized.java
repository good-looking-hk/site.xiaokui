package study.hk.thread.basic;

/**
 * @author HK
 * @date 2018-07-22 09:45
 */
public class Synchronized {
    public static void main(String[] args) {
        synchronized (Synchronized.class) {
        }
        m();
    }

    public static synchronized void m(){
    }
}
