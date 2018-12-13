package site.xiaokui.common.hk.offer.one_five;

import io.swagger.models.auth.In;

import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

/**
 * @author HK
 * @date 2018-08-04 20:46
 */
public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int count = 0;
        try {
            count = Integer.valueOf(scanner.nextLine());
            if (count <= 0) {
                throw new RuntimeException("数字不能小于等于0:" + count);
            }
        } catch (NumberFormatException e) {
            throw new RuntimeException("非法数字:" + count);
        }
        Point[] points = new Point[count];
        int i = 0;
        while (i != count - 1) {
            points[i++] = getInputPoint(scanner.nextLine());
        }
        Arrays.sort(points);

        /**
         * 时间来不及了，大致是排序后火车依次送货，最后统计所走路程
         */
    }

    private static int findMinDistantce(Point from, Point to) {
        return Math.abs(from.x - to.x) + Math.abs(from.y - to.y);
    }

    private static Point getInputPoint(String str) {
        String[] strs = str.split(",");
        if (strs.length != 2) {
            throw new RuntimeException("非法输入点坐标");
        }
        int x = 0, y =0;
        try {
            x = Integer.valueOf(strs[1]);
            y = Integer.valueOf(strs[2]);
        } catch (NumberFormatException e) {
            throw new RuntimeException("非法数字:" + strs[1] + "," + strs[2]);
        }
        return new Point(x, y);
    }

    static class Point implements Comparable <Point>{
        int x, y;
        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public int compareTo(Point o) {
            Point zero = new Point(0, 0);
            int distance1 = findMinDistantce(o, zero);
            int distance2 = findMinDistantce(this, zero);
            if (distance1 == distance2) {
                return 0;
            }
            return findMinDistantce(o, zero) > findMinDistantce(this, zero) ? 1 : -1;
        }
    }
}
