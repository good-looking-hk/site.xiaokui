package site.xiaokui.common.hk.offer;
import java.util.ArrayList;

/**
 * @author HK
 * @date 2018-09-04 18:45
 */
public class Main1 {
    static class Point {
        int x, y;
        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    static ArrayList<Point> list = new ArrayList<>();
    static boolean isFind = false;
    static int[][] arr = new int[][] {
            {0,1,0,0,0},
            {0,1,0,1,0},
            {0,0,0,0,0},
            {0,1,1,1,0},
            {0,0,0,1,0}
    };
    static boolean[][] map;
    public static void findPath(int[][] arr) {
        map = new boolean[arr.length][arr[0].length];
        findPath(0, 0, arr.length - 1, arr[0].length - 1, arr);
    }

    public static boolean findPath(int i, int j, int l, int c, int[][] arr) {
        if (i < 0 || i > l || j < 0 || j > c || arr[i][j] == 1 || map[i][j]) {
            return false;
        }
        System.out.println("机器人走到" + i + "," + j);
        map[i][j] = true;
        list.add(new Point(i, j));
        if (i == l && j == c) {
            isFind = true;
            System.out.println("机器人找到出口");
            return true;
        }
        boolean result = false;
        if (!isFind) {
            result = findPath(i + 1, j, l, c, arr) || findPath(i, j + 1, l, c, arr)
                    ||findPath(i - 1, j, l, c, arr) || findPath(i, j - 1, l, c, arr);
            if (!result) {
                list.remove(list.size() - 1);
                Point p = list.get(list.size() - 1);
                System.out.println("机器人退至" + p.x + "," + p.y);
            }
        }
        return result;
    }

    public static void main(String[] args) {
        findPath(arr);
        for (Point point : list) {
            System.out.println(point.x + "," + point.y);
        }
    }
}

