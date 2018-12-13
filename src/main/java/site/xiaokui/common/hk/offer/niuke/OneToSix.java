package site.xiaokui.common.hk.offer.niuke;

import io.swagger.models.auth.In;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Stack;

/**
 * @author HK
 * @date 2018-11-06 07:39
 */
public class OneToSix {
    /**
     * 在一个二维数组中（每个一维数组的长度相同），每一行都按照从左到右递增的顺序排序，每一列都按照从上到下递增的顺序排序。
     * 请完成一个函数，输入这样的一个二维数组和一个整数，判断数组中是否含有该整数。
     */
    static class One {
        static int[][] array = new int[][]{
                {10, 20, 30, 40, 50},
                {20, 30, 40, 50, 60},
                {30, 40, 50, 60, 70},
                {40, 50, 60, 70, 80},
                {50, 60, 70, 80, 90}
        };
        static int[][] niuke1 = new int[][]{
                {1, 2, 4, 6, 8, 10, 11, 13, 15, 17, 20, 22, 24, 27, 30, 32, 35, 36, 38, 39, 42, 44, 46, 47, 48, 49, 51, 54, 55, 56},
                {3, 4, 6, 8, 11, 13, 15, 18, 19, 20, 23, 25, 27, 29, 33, 36, 38, 41, 42, 45, 48, 50, 53, 54, 57, 60, 63, 65, 66, 67},
                {5, 7, 8, 11, 14, 15, 17, 19, 21, 23, 24, 28, 31, 33, 36, 39, 41, 44, 47, 49, 52, 53, 56, 57, 60, 63, 66, 68, 71, 72},
                {7, 10, 13, 14, 17, 19, 22, 25, 26, 27, 30, 32, 34, 37, 39, 42, 44, 46, 50, 53, 55, 56, 59, 61, 64, 66, 69, 72, 74, 76},
                {8, 12, 16, 17, 20, 21, 23, 26, 29, 31, 33, 35, 37, 40, 42, 45, 48, 49, 52, 55, 58, 59, 61, 63, 67, 69, 70, 74, 76, 79},
                {10, 15, 18, 20, 22, 24, 27, 30, 32, 33, 36, 38, 39, 41, 45, 47, 50, 53, 56, 58, 61, 64, 65, 68, 69, 71, 73, 76, 79, 82},
                {13, 17, 20, 23, 24, 26, 28, 31, 34, 36, 38, 41, 42, 43, 48, 50, 53, 56, 58, 61, 64, 67, 68, 71, 73, 74, 75, 78, 81, 84},
                {15, 19, 22, 24, 26, 28, 31, 34, 35, 38, 41, 44, 45, 46, 49, 51, 56, 59, 61, 64, 67, 69, 71, 73, 76, 78, 79, 82, 85, 87},
                {17, 20, 23, 26, 29, 30, 32, 37, 40, 42, 43, 47, 50, 53, 55, 56, 58, 62, 64, 66, 69, 72, 75, 78, 80, 81, 83, 86, 88, 90},
                {18, 21, 25, 28, 30, 32, 35, 38, 42, 43, 44, 49, 51, 55, 58, 59, 61, 65, 68, 71, 73, 74, 77, 79, 82, 85, 88, 89, 91, 93},
                {20, 23, 28, 29, 33, 36, 37, 40, 43, 45, 47, 52, 55, 58, 60, 62, 63, 66, 70, 74, 76, 77, 79, 81, 85, 88, 89, 91, 93, 94},
                {23, 25, 31, 33, 35, 39, 42, 43, 44, 46, 50, 53, 56, 61, 62, 65, 68, 69, 72, 75, 78, 81, 82, 83, 88, 91, 92, 93, 96, 99},
                {26, 27, 33, 34, 38, 40, 45, 48, 51, 53, 55, 56, 58, 64, 66, 69, 72, 75, 77, 80, 82, 84, 87, 88, 90, 93, 94, 95, 99, 101},
                {29, 30, 36, 38, 40, 42, 47, 50, 53, 56, 57, 59, 62, 65, 68, 71, 73, 77, 79, 83, 84, 86, 88, 91, 93, 96, 99, 100, 102, 103},
                {32, 33, 39, 42, 44, 46, 49, 53, 56, 59, 62, 65, 68, 71, 72, 75, 77, 78, 80, 85, 87, 90, 92, 94, 96, 99, 101, 103, 105, 107},
                {35, 37, 42, 43, 46, 48, 51, 55, 59, 61, 65, 67, 71, 74, 76, 78, 81, 82, 84, 86, 90, 92, 95, 96, 99, 102, 103, 106, 107, 109},
                {36, 39, 43, 46, 49, 50, 53, 58, 62, 65, 67, 70, 73, 76, 77, 79, 84, 87, 88, 90, 93, 96, 99, 102, 103, 106, 108, 111, 112, 115},
                {38, 42, 45, 47, 52, 55, 57, 60, 64, 66, 69, 72, 75, 78, 80, 82, 87, 89, 91, 92, 94, 99, 100, 103, 105, 107, 111, 112, 115, 118},
                {39, 44, 48, 49, 55, 57, 60, 63, 66, 69, 72, 75, 78, 80, 82, 85, 89, 92, 94, 95, 98, 101, 102, 105, 108, 111, 112, 115, 116, 120},
                {40, 47, 49, 52, 56, 59, 63, 64, 68, 71, 75, 78, 80, 83, 85, 88, 91, 94, 97, 99, 101, 104, 105, 108, 110, 112, 115, 118, 120, 123},//19 99
                {42, 50, 53, 55, 59, 62, 66, 67, 71, 73, 78, 81, 82, 86, 87, 90, 94, 97, 100, 101, 104, 106, 107, 109, 111, 114, 117, 120, 123, 126},
                {43, 51, 55, 58, 62, 64, 69, 71, 74, 77, 81, 84, 86, 87, 89, 93, 96, 99, 102, 103, 105, 108, 111, 112, 113, 116, 120, 122, 125, 129},
                {45, 54, 56, 59, 65, 67, 72, 75, 76, 79, 84, 87, 90, 92, 94, 97, 99, 102, 104, 107, 108, 111, 114, 116, 119, 120, 122, 124, 127, 132},
                {47, 55, 59, 62, 66, 68, 74, 78, 80, 82, 85, 88, 93, 96, 99, 101, 102, 105, 106, 108, 110, 113, 115, 118, 120, 121, 124, 127, 130, 133},
                {49, 56, 61, 65, 68, 70, 76, 81, 83, 85, 87, 91, 95, 99, 102, 103, 104, 106, 108, 110, 113, 116, 118, 121, 124, 127, 130, 133, 134, 137},
                {51, 57, 62, 68, 71, 74, 78, 83, 86, 89, 92, 95, 97, 101, 104, 106, 108, 110, 113, 114, 117, 119, 121, 123, 126, 130, 132, 135, 137, 140},
                {53, 59, 65, 69, 73, 75, 81, 86, 88, 92, 95, 97, 100, 103, 107, 109, 111, 112, 115, 117, 120, 122, 125, 126, 129, 131, 133, 138, 141, 143},
                {55, 61, 67, 72, 75, 77, 82, 89, 92, 94, 97, 100, 102, 105, 108, 111, 114, 115, 116, 119, 123, 125, 126, 128, 131, 134, 137, 140, 144, 146},
                {57, 63, 68, 74, 78, 81, 85, 90, 95, 98, 100, 101, 103, 107, 110, 114, 117, 119, 120, 123, 126, 129, 130, 133, 136, 138, 141, 144, 146, 149},
                {58, 65, 70, 76, 81, 84, 86, 93, 98, 101, 104, 105, 107, 110, 112, 115, 120, 122, 124, 126, 129, 132, 135, 136, 138, 140, 142, 146, 149, 150}
        };

        public boolean Find(int target, int[][] array) {
            if (array == null || array.length == 0 || array[0].length == 0) {
                return false;
            }
            if (target < array[0][0]) {
                return false;
            }
            return find(0, 0, target, array);
        }

        public boolean Find1(int target, int[][] array) {
            int len = array.length - 1;
            int i = 0;
            while ((len >= 0) && (i < array[0].length)) {
                if (array[len][i] > target) {
                    len--;
                } else if (array[len][i] < target) {
                    i++;
                } else {
                    return true;
                }
            }
            return false;
        }

        public boolean find(int i, int j, int target, int[][] array) {
            // 比较当前值
            if (array[i][j] == target) {
                return true;
            }
            // 由于i = j，这里判断后续的操作是否越界
            if (i + 1 == array.length) {
                return false;
            }
            // 比较下一个对象，方向为左上到右下
            if (array[i + 1][j + 1] == target) {
                return true;
            }
            // 如果查找值大于下一个比较对象，则递归下一个
            if (array[i + 1][j + 1] < target) {
                return find(i + 1, j + 1, target, array);
            }
            // 如果小于，那就只能遍历右上角和左下角的那两块了
            for (int x = i + 1; x < array.length; x++) {
                for (int y = 0; y <= j; y++) {
                    if (array[x][y] == target) {
                        return true;
                    }
                }
            }
            for (int x = 0; x <= i; x++) {
                for (int y = j + 1; y < array.length; y++) {
                    if (array[x][y] == target) {
                        return true;
                    }
                }
            }
            // 如果还没找到，那就真的不在了
            return false;
        }

        private static void test() {
            boolean result = new One().Find(5, One.array);
            Assert.assertFalse(result);
            result = new One().Find(10, One.array);
            Assert.assertTrue(result);
            result = new One().Find(55, One.array);
            Assert.assertFalse(result);
            result = new One().Find(80, One.array);
            Assert.assertTrue(result);
            result = new One().Find(90, One.array);
            Assert.assertTrue(result);
            result = new One().Find(100, One.array);
            Assert.assertFalse(result);
            result = new One().Find(102, One.niuke1);
            Assert.assertTrue(result);

            result = new One().Find1(5, One.array);
            Assert.assertFalse(result);
            result = new One().Find1(10, One.array);
            Assert.assertTrue(result);
            result = new One().Find1(55, One.array);
            Assert.assertFalse(result);
            result = new One().Find1(80, One.array);
            Assert.assertTrue(result);
            result = new One().Find1(90, One.array);
            Assert.assertTrue(result);
            result = new One().Find1(100, One.array);
            Assert.assertFalse(result);
            result = new One().Find1(102, One.niuke1);
            Assert.assertTrue(result);
        }
    }

    /**
     * 请实现一个函数，将一个字符串中的每个空格替换成“%20”。例如，当字符串为We Are Happy.则经过替换之后的字符串为We%20Are%20Happy。
     */
    static class Two {
        static String str = "We Are Haapy";

        public String replaceSpace(StringBuffer str) {
            if (str == null || str.length() == 0) {
                return str == null ? null : str.toString();
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < str.length(); i++) {
                if (Character.isWhitespace(str.charAt(i))) {
                    sb.append("%20");
                    continue;
                }
                sb.append(str.charAt(i));
            }
            return sb.toString();
        }

        public String replaceSpace1(StringBuffer str) {
            int spaceNum = 0;
            for (int i = 0; i < str.length(); i++) {
                if (str.charAt(i) == ' ')
                    spaceNum++;
            }
            int indexOld = str.length() - 1;
            int newLength = str.length() + spaceNum * 2;
            int endIndex = newLength - 1;
            str.setLength(newLength);
            for (; indexOld >= 0 && indexOld < newLength; --indexOld) {
                if (str.charAt(indexOld) == ' ') {
                    str.setCharAt(endIndex--, '0');
                    str.setCharAt(endIndex--, '2');
                    str.setCharAt(endIndex--, '%');
                } else {
                    str.setCharAt(endIndex--, str.charAt(indexOld));
                }
            }
            return str.toString();
        }

        private static void test() {
            Two two = new Two();
            Assert.assertEquals(str.replaceAll(" ", "%20"), two.replaceSpace(new StringBuffer(str)));
            Assert.assertEquals(str.replaceAll(" ", "%20"), two.replaceSpace1(new StringBuffer(str)));
        }
    }

    /**
     * 输入一个链表，按链表值从尾到头的顺序返回一个ArrayList。
     */
    static class Three {
        static class ListNode {
            int val;
            ListNode next = null;

            ListNode(int val) {
                this.val = val;
            }
        }

        public ArrayList<Integer> printListFromTailToHead(ListNode node) {
            ArrayList<Integer> list = new ArrayList<>();
            if (node == null) {
                return list;
            }
            while (node != null) {
                list.add(node.val);
                node = node.next;
            }
            int size = list.size();
            int mid = size / 2;
            for (int i = 0; i < mid; i++) {
                int temp = list.get(i);
                list.set(i, list.get(size - 1 - i));
                list.set(size - 1 - i, temp);
            }
            return list;
        }

        public ArrayList<Integer> printListFromTailToHead1(ListNode node) {
            Deque<Integer> stack = new LinkedList<>();
            while (node != null) {
                stack.push(node.val);
                node = node.next;
            }
            ArrayList<Integer> list = new ArrayList<>();
            while (!stack.isEmpty()) {
                list.add(stack.pop());
            }
            return list;
        }

        private static void test() {
            ListNode node = new ListNode(1);
            ListNode node1 = new ListNode(2);
            ListNode node2 = new ListNode(3);
            ListNode node3 = new ListNode(4);
            node.next = node1;
            node1.next = node2;
            node2.next = node3;
            System.out.println(new Three().printListFromTailToHead(node));
            System.out.println(new Three().printListFromTailToHead1(node));
        }
    }

    /**
     * 输入某二叉树的前序遍历和中序遍历的结果，请重建出该二叉树。假设输入的前序遍历和中序遍历的结果中都不含重复的数字。
     * 例如输入前序遍历序列{1,2,4,7,3,5,6,8}和中序遍历序列{4,7,2,1,5,3,8,6}，则重建二叉树并返回。
     */
    static class Four {
        static class TreeNode {
            int val;
            TreeNode left;
            TreeNode right;

            TreeNode(int x) {
                val = x;
            }
        }

        public TreeNode reConstructBinaryTree(int[] pre, int[] in) {
            if (pre.length == 0) {
                return null;
            }
            TreeNode root = new TreeNode(pre[0]);
            int midIndex = index(pre[0], in);
            root.left = reConstructBinaryTree(left(midIndex, pre[0], pre), left(midIndex, pre[0], in));
            root.right = reConstructBinaryTree(right(midIndex, pre), right(midIndex, in));
            return root;
        }

        private int[] left(int size, int exclude, int[] arr) {
            int[] left = new int[size];
            int j = 0;
            for (int i = 0; i <= size; i++) {
                if (arr[i] != exclude) {
                    left[j++] = arr[i];
                }
            }
            return left;
        }

        private int[] right(int size, int[] arr) {
            int[] right = new int[arr.length - size - 1];
            int j = 0;
            for (int i = size + 1; i < arr.length; i++) {
                right[j++] = arr[i];
            }
            return right;
        }

        private int index(int value, int[] arr) {
            for (int i = 0; i < arr.length; i++) {
                if (arr[i] == value) {
                    return i;
                }
            }
            return -1;
        }

        private static void test() {
            int[] pre = {1, 2, 4, 7, 3, 5, 6, 8};
            int[] in = {4, 7, 2, 1, 5, 3, 8, 6};
            TreeNode root = new Four().reConstructBinaryTree(pre, in);
            pre(root);
        }

        private static void pre(TreeNode root) {
            System.out.print(root.val + " ");
            if (root.left != null) {
                System.out.println("节点" + root.val + "的左节点是" + root.left.val);
                pre(root.left);
            }
            if (root.right != null) {
                System.out.println("节点" + root.val + "的右节点是" + root.right.val);
                pre(root.right);
            }
        }
    }

    /**
     * 用两个栈来实现一个队列，完成队列的Push和Pop操作。 队列中的元素为int类型。
     */
    static class Five {
        Stack<Integer> stack1 = new Stack<>();
        Stack<Integer> stack2 = new Stack<>();

        public void push(int node) {
            stack1.push(node);
        }

        public int pop() {
            if (!stack2.isEmpty()) {
                return stack2.pop();
            }
            while (!stack1.isEmpty()) {
                stack2.push(stack1.pop());
            }
            return stack2.pop();
        }
    }

    /**
     * 把一个数组最开始的若干个元素搬到数组的末尾，我们称之为数组的旋转。 输入一个非减排序的数组的一个旋转，输出旋转数组的最小元素。
     * 例如数组{3,4,5,1,2}为{1,2,3,4,5}的一个旋转，该数组的最小值为1。 NOTE：给出的所有元素都大于0，若数组大小为0，请返回0。
     */
    static class Six {
        public int minNumberInRotateArray(int[] array) {
            if (array.length == 0) {
                return 0;
            }
            if (array.length == 1) {
                return array[0];
            }
            int i = 0;
            // 判断方向
            while (array[i] == array[i + 1] && i++ < array.length - 2) {
            }
            if (array[i] > array[i + 1]) {
                return findMin(i + 1, true, array);
            } else if (array[0] < array[array.length - 1]) {
                return array[0];
            } else {
                return findMin(array.length - 1, false, array);
            }
        }

        private int findMin(int i, boolean asc, int[] arr) {
            if (asc) {
                if (i == arr.length - 1) {
                    return arr[i];
                }
                if (arr[i] >= arr[i + 1]) {
                    return findMin(i + 1, true, arr);
                } else {
                    return arr[i];
                }
            } else {
                if (i == 2) {
                    return arr[i];
                }
                if (arr[i - 1] <= arr[i]) {
                    return findMin(i - 1, false, arr);
                } else {
                    return arr[i];
                }
            }
        }

        private static void test() {
            int min = new Six().minNumberInRotateArray(new int[]{3, 3, 4, 5, 1, 2});
            Assert.assertEquals(min, 1);
        }
    }

    public static void main(String[] args) {
//        One.test();
//        Two.test();
//        Three.test();
//        Four.test();
        Six.test();
    }
}
