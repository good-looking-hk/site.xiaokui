package study.hk.blog;

import java.util.Deque;
import java.util.LinkedList;

/**
 * 两栈实现队列
 * @author HK
 * @date 2018-08-30 15:28
 */
public class MyQueue<E> {

    private Deque<E> input;

    private Deque<E> output;

    public MyQueue() {
        input = new LinkedList<>();
        output = new LinkedList<>();
    }

    public void offer(E e) {
        input.push(e);
    }

    public E poll() {
        if (output.size() != 0) {
            return output.pop();
        }
        while (input.size() != 0) {
            E e = input.pop();
            output.push(e);
        }
        return output.pop();
    }


    public void pollAll() {
        System.out.print("队列输出顺序为：");
        while (input.size() != 0 || output.size() != 0) {
            System.out.print(poll() + " ");
        }
        System.out.println();
    }

    public static void main(String[] args) {
        MyQueue<Integer> queue = new MyQueue<>();
        queue.offer(1);
        queue.offer(2);
        queue.offer(3);
        System.out.println(queue.poll());
        queue.pollAll();

        queue.offer(4);
        queue.offer(5);
        queue.offer(6);
        queue.pollAll();
    }
}
