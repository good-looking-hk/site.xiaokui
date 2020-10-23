package study.hk.mianshi;

import java.util.concurrent.*;

/**
 * @author HK
 * @date 2020-05-24 20:18
 */
public class BlockQueueTest {

    public static void main(String[] args) throws InterruptedException {
        // 虽然这里是 new ArrayBlockingQueue，但下面操作是有通用性
        BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<>(4);

        // 入队失败则抛异常，成功返回true，否则抛IllegalStateException
        blockingQueue.add("1");
        // 立马返回结果，成功true，失败false
        boolean success = blockingQueue.offer("2");
        System.out.println(success);
        // 阻塞于插入操作
        blockingQueue.put("3");
        // offer的超时返回
        success = blockingQueue.offer("4", 4, TimeUnit.SECONDS);
        System.out.println(success);

        /// blockingQueue.clear();
        // 对应add，成功返回移除对象，否则抛NoSuchElementException
        String obj = blockingQueue.remove();
        System.out.println(obj);
        // 对应offer
        blockingQueue.poll();
        // 对应put
        blockingQueue.take();
        // 对应offer的超时返回
        blockingQueue.poll(4, TimeUnit.SECONDS);

        // 获取头部元素，但不移除，成功返回头部元素，否则抛NoSuchElementException
        obj = blockingQueue.element();
        System.out.println(obj);
        // 获取头部原始，但不移除，存在返回头部元素，否则返回null
        // 这里又有个疑问了，是否任务可以为null呢
        // 为了不引发歧义，所以这里的值同ConcurrentHashMap，一样不允许为null
        obj = blockingQueue.peek();
        System.out.println(obj);
    }
}
