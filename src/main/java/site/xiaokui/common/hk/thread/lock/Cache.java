package site.xiaokui.common.hk.thread.lock;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static javafx.scene.input.KeyCode.R;

/**
 * @author HK
 * @date 2018-10-12 10:07
 */
public class Cache {

    static Map<String, Object> map = new HashMap<>();
    static ReentrantReadWriteLock rrwl = new ReentrantReadWriteLock();
    static Lock r = rrwl.readLock();
    static Lock w = rrwl.writeLock();

    public static final Object get(String key) {
        r.lock();
        try {
            return map.get(key);
        } finally {
            r.unlock();
        }
    }

    public static final Object put(String key, Object value) {
        w.lock();
        try {
            return map.put(key, value);
        } finally {
            w.unlock();
        }
    }

    public static final void clear() {
        w.lock();
        try {
            map.clear();
        } finally {
            w.unlock();
        }
    }
}
