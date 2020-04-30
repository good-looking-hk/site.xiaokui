package site.xiaokui.common.hk.jvm;

import java.util.Collections;
import java.util.Hashtable;
import java.util.Vector;

/**
 * @author HK
 * @date 2018-12-30 21:39
 */
public class VectorTest {

    private static Vector<Integer> vector = new Vector<>();

    Hashtable hashtable;

    Collections s;

    Thread removeThread = new Thread(new Runnable() {
        @Override
        public void run() {
            synchronized (vector) {
                for (int i = 0; i < vector.size(); i++) {
                    vector.remove(i);
                }
            }
        }
    });

    Thread printThread = new Thread(new Runnable() {
        @Override
        public void run() {
            synchronized (vector) {
                for (int i = 0; i < vector.size(); i++) {
                    System.out.println(vector.get(i));
                }
            }
        }
    });

    public static void main(String[] args) {
        while (true) {
            for (int i = 0; i < 10; i++) {
                vector.add(i);
            }
            Thread removeThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < vector.size(); i++) {
                        vector.remove(i);
                    }
                }
            });
            Thread printThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < vector.size(); i++) {
                        System.out.println(vector.get(i));
                    }
                }
            });
            removeThread.start();
            printThread.start();
//            if (Thread.activeCount() > 20) {
//                return;
//            }
            // 不要同时产生过多的线程，否则会导致操作系统假死
            while (Thread.activeCount() > 20) {
            }
        }
    }
}
