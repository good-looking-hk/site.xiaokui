package real;

/**
 * 打印杨辉三角
 * 1
 * 1 1
 * 1 2 1
 * 1 3 3 1
 * 1 4 6 4 1
 * 1 5 10 10 5 1
 * 1 6 15 20 15 6 1
 *
 * @author HK
 * @date 2021-03-01 11:09
 */
public class Main5 {

    /**
     * 计算数组位置值
     * @param x 横坐标 从0开始
     * @param y 纵坐标 从0开始
     * @return 位置对应值
     */
    private static int calcValue(int x, int y, int[][] arr) {
        if (x < 0 || y < 0) {
            return 0;
        }
        if (x == 0 || x == 1 || x == y) {
            arr[x][y] = 1;
            return arr[x][y];
        }
        // 之前已完成计算
        if (arr[x][y] != 0) {
            return arr[x][y];
        }
        // arr[x][y] = arr[x - 1][y - 1] + arr[x - 1][y];
        int value =  calcValue(x - 1, y - 1, arr) + calcValue(x - 1, y, arr);
        arr[x][y] = value;
        return value;
    }

    public static void calc(int height) {
        if (height < 1) {
            throw new IllegalArgumentException("高度不能小于1:" + height);
        }
        int[][] arr = new int[height][height];
        for (int i = 0; i < arr[0].length; i++) {
            calcValue(height - 1, i, arr);
        }
        // 第一个无法使用之前的规律
        arr[0][0] = 1;

        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[0].length; j++) {
                if (arr[i][j] != 0) {
                    System.out.print(arr[i][j] + " ");
                }
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        calc(7);
    }
}
