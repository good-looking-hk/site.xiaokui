package study.hk.offer;

import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * 本地运行环境deepin(linux) + idea + JDK8，题目比较简单，暂无更多测试用例
 *
 * @author HK
 * @date 2019-03-25 19:49
 */
public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int row, column;
        // 预防非int型输入
        try {
            row = scanner.nextInt();
            column = scanner.nextInt();
            if (row < 1 || column < 1) {
                System.out.print("Incorrect mesh size");
                return;
            }
        } catch (InputMismatchException e) {
            System.out.print("Incorrect mesh size");
            return;
        }
        String str = null;
        // linux系统会自动在输入后添加\n，需要读两次空才能换行读字符串
        int count = 2;
        while (count != 0) {
            str = scanner.nextLine();
            count--;
        }
        if (str.length() != row * column) {
            System.out.print("Data mismatch");
            return;
        }
        // 控制遍历方向
        boolean left = true;
        for (int i = 0; i < row; i++) {
            char[] temp = new char[column];
            for (int j = 0; j < column; j++) {
                int index = i * column + j;
                char target = str.charAt(index);
                if (target != 'G' && target != 'F' && target != 'R') {
                    System.out.print("Invalid cell type");
                    return;
                } else {
                    temp[j] = target;
                }
            }
            if (left) {
                for (char c : temp) {
                    System.out.print(c);
                }
                left = false;
            } else {
                for (int k = temp.length - 1; 0 <= k; k--) {
                    System.out.print(temp[k]);
                }
                left = true;
            }
            // 省略最后一个换行
            if (i != row - 1) {
                System.out.println();
            }
        }
    }
}
