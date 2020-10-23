package study.hk.blog;

import java.util.LinkedList;
import java.util.Queue;

/**
 * @author HK
 * @date 2018-08-30 15:42
 */
public class MyStack<E> {

    private Queue<E> queue1;

    private Queue<E> queue2;

    public MyStack() {
        queue1 = new LinkedList<>();
        queue2 = new LinkedList<>();
    }

    public void push(E e) {
        if (queue2.size() != 0) {
            queue2.offer(e);
        } else {
            queue1.offer(e);
        }
    }

    public E pop() {
        if (queue1.size() > 0) {
            while (queue1.size() > 1) {
                E e = queue1.poll();
                queue2.offer(e);
            }
            return queue1.poll();
        }
        while (queue2.size() > 1) {
            E e = queue2.poll();
            queue1.offer(e);
        }
        return queue2.poll();
    }

    public void popAll() {
        System.out.print("栈输出顺序为：");
        while (queue1.size() != 0 || queue2.size() != 0) {
            System.out.print(pop() + " ");
        }
        System.out.println();
    }

    public static void main(String[] args) {
        MyStack<Integer> stack = new MyStack<>();
        stack.push(1);
        stack.push(2);
        stack.push(3);
        stack.push(4);
        stack.popAll();

        stack.push(4);
        stack.push(5);
        stack.push(6);
        stack.popAll();
    }
}