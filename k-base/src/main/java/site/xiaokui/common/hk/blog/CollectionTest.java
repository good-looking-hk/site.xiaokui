package site.xiaokui.common.hk.blog;

import java.util.*;

/**
 * @author HK
 * @date 2018-11-02 21:37
 */
public class CollectionTest {

    public static void main(String[] args) {
//        HashMap map;
//        ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap();
//        concurrentHashMap.get(null);
//        Queue queue;
        testQueue();
    }

    private static void testQueue() {
        Queue<Integer> queue = new LinkedList<>();
        Deque<Integer> deque = new LinkedList<>();
        Deque<Integer> stack = new LinkedList<>();
        for (int i = 0; i < 4; i++) {
            queue.add(i);
            deque.offer(i);
            stack.push(i);
        }
        System.out.println(queue.poll());
        System.out.println(deque.pollLast());
        System.out.println(stack.pop());
    }

    private static void testOther() {
        Vector<String> vector = new Vector<>();
    }

    private static void testAbstractCollection() {
        List<String> one = new ArrayList<>();
        List<String> two = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            one.add(String.valueOf(i));
            two.add(String.valueOf(i));
        }
        String[] str1 = (String[]) one.toArray();
        String[] str2 = two.toArray(new String[two.size()]);
        for (String t : str1) {
        }
        for (String t : str2) {
        }
    }

    private static void testSet() {
        Set<String> one = new HashSet<>();
        Set<String> two = new LinkedHashSet<>();
        for (int i = 0; i < 6; i++) {
            one.add(String.valueOf(i * 31));
            two.add(String.valueOf(i * 31));
        }
        for (String s : one) {
            System.out.print(s + " ");
        }
        System.out.println();
        for (String s : two) {
            System.out.print(s + " ");
        }
    }

    private static void testList() {
        List<String> one = new LinkedList<>();
        List<String> two = new ArrayList<>();
        for (int i = 100000; i > 0; i--) {
            one.add(String.valueOf(i));
            two.add(String.valueOf(i));
        }
        long startTime = System.currentTimeMillis();
        one.sort(null);
        System.out.println("LinkedList排序耗时" + (System.currentTimeMillis() - startTime) + "ms");
        two.sort(null);
        System.out.println("ArrayList排序耗时" + (System.currentTimeMillis() - startTime) + "ms");
        startTime = System.currentTimeMillis();
        for (String s : one) {
            if (s.equals("100")) {
                System.out.print(s);
            }
        }
        System.out.println("\nLinkedList使用foreach遍历耗时" + (System.currentTimeMillis() - startTime) + "ms");
        startTime = System.currentTimeMillis();
        for (String s : one) {
            if (s.equals("100")) {
                System.out.print(s);
            }
        }
        System.out.println("\nArrayList使用foreach遍历耗时" + (System.currentTimeMillis() - startTime) + "ms");

        startTime = System.currentTimeMillis();
        int size = one.size();
        for (int i = 0; i < size; i++) {
            String temp = one.get(i);
            if (temp.equals("100")) {
                System.out.print(temp);
            }
        }
        System.out.println("\nLinkedList使用for循环遍历耗时" + (System.currentTimeMillis() - startTime) + "ms");
        startTime = System.currentTimeMillis();
        for (int i = 0; i < size; i++) {
            String temp = two.get(i);
            if (temp.equals("100")) {
                System.out.print(temp);
            }
        }
        System.out.println("\nArrayList使用for循环遍历耗时" + (System.currentTimeMillis() - startTime) + "ms");
    }

    private static void testCollection() {
        Collection<Integer> collection = Arrays.asList(1, 2, 3, 4);
        Integer[] temp = collection.toArray(new Integer[collection.size()]);
        Iterator<Integer> it = collection.iterator();
        while (it.hasNext()) {
            Integer i = it.next();
            System.out.println(i + "(hashCode=" + i.hashCode() + ")");
        }
        // 此步会抛出UnsupportedOperationException
        collection.clear();
        System.out.println(collection.size());
    }
}
