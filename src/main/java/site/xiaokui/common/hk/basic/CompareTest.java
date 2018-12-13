package site.xiaokui.common.hk.basic;

/**
 * @author HK
 * @date 2018-10-30 16:32
 */
public class CompareTest {

    static class GrandFather<T> implements Comparable<T> {
        @Override
        public int compareTo(T o) {
            System.out.println("调用GrandFather比较");
            return 1;
        }
    }
    static class Father extends GrandFather<Father> {
        @Override
        public int compareTo(Father o) {
            System.out.println("调用Father比较");
            return 0;
        }
    }
    static class Son extends Father {
        @Override
        public int compareTo(Father o) {
            System.out.println("调用Son比较");
            return -1;
        }
    }

    public static void main(String[] args) {
        GrandFather grandFather = new GrandFather();
        Father father = new Father();
        Son son = new Son();
        Son son1 = new Son();
        Son son2 = new Son();
//        List list = Arrays.asList(grandFather, father, son);
//        List list = Arrays.asList(son, son1, son2);
//        list.sort(null);
//        System.out.println(list);

        Comparable c1 = (Comparable) grandFather;
        Comparable c2 = (Comparable) father;
        Comparable c3 = (Comparable) son2;
        c1.compareTo(c1);
        c1.compareTo(c2);
        c1.compareTo(c3);
        c2.compareTo(c1);
        c2.compareTo(c2);
        c2.compareTo(c3);
        c3.compareTo(c1);
        c3.compareTo(c2);
        c3.compareTo(c3);
    }

}
