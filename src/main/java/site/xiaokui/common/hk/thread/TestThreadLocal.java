package site.xiaokui.common.hk.thread;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 测试 ThreadLocal的内存泄露
 * 限制java堆大小 -Xmx64m -verbose:gc -XX:+PrintGCDetails
 * @author HK
 * @date 2020-04-29 16:38
 */
public class TestThreadLocal {

    static class Session {
        Integer id;
        byte[] data;
        Session(Integer id, byte[] data) {
            this.id = id;
            this.data = data;
        }
    }

    static class Request {
        ThreadLocal<Session> threadLocal = new ThreadLocal() {
            @Override
            public Session initialValue () {
                // return new Session(id, data);
                return new Session(1, new byte[1024 * 1024 * 4]);
            }
        };
    }

    public static void main(String[] args) throws InterruptedException {
        Executor executor = Executors.newFixedThreadPool(20);
        List<Request> list = new LinkedList<>();
        for (int i = 0; i < 20; i++) {
            final int id = i;
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    // TODO
                    // fixme
                    // FIXME
                    NoSuchMethodError a;
//                    Session session = new Session(id, new byte[1024 * 1024 * 4]);
                    Request request = new Request();
                    list.add(request);
//                    list.add(session);
//                    session.threadLocal.remove();
                }
            });
        }
        System.out.println("内存分配完成");
        Thread.sleep(2000);
        for (int i = 0; i < list.size(); i++) {
            Request request = list.get(i);
            System.out.println(request);
//            System.out.println(request.id + " - " + request.data.length / 1024 / 1024);
        }
    }
}
