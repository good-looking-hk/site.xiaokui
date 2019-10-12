package site.xiaokui.common.hk;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.extra.mail.MailAccount;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author HK
 * @date 2019-02-21 18:56
 */
public class Main {

    Main main = new Main();
    static Main main1 = new Main();
//        6 3
//            1 1
//            3 5
//            4 8
//            6 4
//            10 3
//            11 2
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        if (1 > n || n >= 200000) {
            System.out.println(0);
            return;
        }
        int d = sc.nextInt();
        if (1 > d || d >= 100000000) {
            System.out.println(0);
            return;
        }
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            int x = sc.nextInt();
            int g = sc.nextInt();
            points[i] = new Point(x, g);
        }
        Arrays.sort(points);
        int a = 0, b = 1, c = 2;
        int max = 0;
        while (c < n) {
            int curAB = 0, curAC = 0;
            if (Math.abs(points[a].x - points[b].x) > 3) {
                curAB = points[a].g + points[b].g;
            }
            if (Math.abs(points[a].x - points[c].x) > 3) {
                curAC = points[a].g + points[c].g;
            }
            if (curAB > curAC) {
                max = max > curAB ? max : curAB;
            } else {
                max = max > curAC ? max : curAB;
            }
            a = b;
            b = c;
            c = c + 1;
        }
        System.out.println(max);
    }

    static class Point implements Comparable<Point> {
        int x;
        int g;
        Point(int x, int g) {
            this.x = x;
            this.g = g;
        }
        @Override
        public int compareTo(Point o) {
            if (this.x > o.x) {
                return 1;
            }
            return -1;
        }
    }


//    private static void quickSort(int[] nums, int start, int end) {
//        if (start >= end || start < 0 || end > nums.length - 1) {
//            return;
//        }
//        int index = start;
//        for (int i = start; i <= end; i++) {
//            if (nums[i] < nums[index]) {
//                int temp = nums[i];
//                for (int j = i; j > start; j--) {
//                    nums[j] = nums[j - 1];
//                }
//                nums[start] = temp;
//                index++;
//            }
//        }
//        quickSort(nums, start, index - 1);
//        quickSort(nums, index + 1, end);
//    }

//    public static void main(String[] args) {
//        Scanner sc = new Scanner(System.in);
//        int n = sc.nextInt();
//        if (n < 1 || n > 50) {
//            System.out.println(0);
//            return ;
//        }
//        int[] a = new int[n];
//        int[] b = new int[n];
//        for (int i = 0; i < n; i++) {
//            a[i] = sc.nextInt();
//        }
//        for (int i = 0; i < n; i++) {
//            b[i] = sc.nextInt();
//        }
//        quickSort(a, 0, n - 1);
//        quickSort(b, 0, n - 1);
//        int sum = 0;
//        for (int i = 0; i < n; i++) {
//            sum += a[i] * b[n - 1 - i];
//        }
//        System.out.println(sum);
//    }
//
//    private static  void quickSort(int[] nums, int start, int end) {
//        if (start >= end || start < 0 || end > nums.length - 1) {
//            return;
//        }
//        int index = start;
//        for (int i = start; i <= end; i++) {
//            if (nums[i] < nums[index]) {
//                int temp = nums[i];
//                for (int j = i; j > start; j--) {
//                    nums[j] = nums[j - 1];
//                }
//                nums[start] = temp;
//                index++;
//            }
//        }
//        quickSort(nums, start, index - 1);
//        quickSort(nums, index + 1, end);
//    }


//    public static void main(String[] args) throws Exception {
//        callLastTestMethod();
//    }

    public void test4() {
        Scanner scanner = new Scanner(System.in);
//        while (scanner.hasNextInt()) {
//            System.out.println(scanner.nextInt());
//        }
        while (scanner.hasNextLine()) {
            System.out.println(scanner.nextLine());
        }
    }

    public void test3() {
        List<String> list = new ArrayList<>();
        list.add("1");
        list.add("2");
        List<String> list1 = test2(list);
        for (String s : list) {
            System.out.print(s);
        }
        System.out.println();
        for (String s : list1) {
            System.out.print(s);
        }
    }

    public List<String> test2(List<String> list) {
        list.add("4");
        list = new ArrayList<>();
        list.add("3");
        list.add("5");
        list.add("6");
        return list;
    }

    public void test1() {
        int d = 0;
        Integer i = null;
        System.out.println((double) d);
///        System.out.println((double) i);
        MailAccount account = new MailAccount();
        account.setHost("smtp.163.com");
        account.setPort(25);
        account.setAuth(true);
        account.setFrom("hk467914950@163.com");
        account.setUser("hk467914950");
        account.setPass("1q2w3er4m");
//        MailUtil.send(account, "467914950@qq.com", "测试", "邮件来自小葵博客的测试", false);
    }

    public static void callLastTestMethod() throws Exception {
        Object o = Main.class.newInstance();
        Method[] methosd = ReflectUtil.getMethodsDirectly(o.getClass(), false);
        LinkedList<Method> list = new LinkedList<>();
        for (Method m : methosd) {
            if (m.getReturnType() == void.class && m.getName().contains("test") && m.getName().length() > 4) {
                list.add(m);
            }
        }
        list.sort(new Comparator<Method>() {
            @Override
            public int compare(Method o1, Method o2) {
                Integer one = Integer.valueOf(o1.getName().substring(4));
                Integer two = Integer.valueOf(o2.getName().substring(4));
                if (one > two) {
                    return 1;
                }
                return -1;
            }
        });
        Method m = list.getLast();
        System.out.println("调用最后一个test方法" + m.getName() + ",第一个test方法是" + list.getFirst().getName());
        m.invoke(o);
    }
}
