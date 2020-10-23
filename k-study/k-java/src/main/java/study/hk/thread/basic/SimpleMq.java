package study.hk.thread.basic;

import java.util.LinkedList;
import java.util.Queue;

/**
 * @author HK
 * @date 2019-10-09 17:10
 */
public class SimpleMq {

    private static Queue<String> queue = new LinkedList<>();

    private static Object readLock = new Object(), writeLock = new Object();

    private volatile static boolean end = false;

    static class Producer {
        public void send(String str) throws InterruptedException {
            synchronized (writeLock) {
                queue.add(str);
                System.out.println("生产者发送：" + str);
                synchronized (readLock) {
                    readLock.notifyAll();
                }
                writeLock.wait();
            }
        }
    }

    static class Consumer extends Thread {
        @Override
        public void run() {
            System.out.println("消费者启动");
            // 不依赖于轮询，而依赖Java内置的通知等待机制
            while (!end) {
                synchronized (readLock) {
                    try {
                        // 必须事先在这里等待，否则无法保证等待通知机制正常运行
                        readLock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("消费者接收：" + queue.poll());
                synchronized (writeLock) {
                    writeLock.notifyAll();
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        // 消费者必须先启动，必要时可以设置sleep时间
        Consumer consumer = new Consumer();
        consumer.start();

        Producer producer = new Producer();
        producer.send("Hello");
        producer.send("World");
    }
}
