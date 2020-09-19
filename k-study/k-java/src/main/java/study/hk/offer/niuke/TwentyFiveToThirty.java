package study.hk.offer.niuke;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author HK
 * @date 2018-11-13 17:31
 */
public class TwentyFiveToThirty {

    /**
     * 输入一个复杂链表（每个节点中有节点值，以及两个指针，一个指向下一个节点，另一个特殊指针指向任意一个节点），返回结果为复制后复杂链表的head。
     * （注意，输出结果中请不要返回参数中的节点引用，否则判题程序会直接返回空）
     */
    static class TwentyFive {
        static class RandomListNode {
            int label;
            RandomListNode next = null;
            RandomListNode random = null;

            RandomListNode(int label) {
                this.label = label;
            }
        }

        public RandomListNode Clone(RandomListNode pHead) {
            if (pHead == null) {
                return null;
            }
            RandomListNode node = new RandomListNode(pHead.label);
            if (pHead.random != null) {
                node.random = new RandomListNode(pHead.random.label);
            }
            if (pHead.next != null) {
                node.next = Clone(pHead.next);
            }
            return node;
        }
    }

    /**
     * 输入一棵二叉搜索树，将该二叉搜索树转换成一个排序的双向链表。要求不能创建任何新的结点，只能调整树中结点指针的指向。
     */
    static class TwentySix {
        static class TreeNode {
            int val = 0;
            TreeNode left = null;
            TreeNode right = null;

            public TreeNode(int val) {
                this.val = val;
            }
        }

        TreeNode head = null;
        TreeNode realHead = null;

        public TreeNode Convert1(TreeNode pRootOfTree) {
            ConvertSub(pRootOfTree);
            return realHead;
        }

        private void ConvertSub(TreeNode pRootOfTree) {
            if (pRootOfTree == null) return;
            ConvertSub(pRootOfTree.left);
            if (head == null) {
                head = pRootOfTree;
                realHead = pRootOfTree;
            } else {
                head.right = pRootOfTree;
                pRootOfTree.left = head;
                head = pRootOfTree;
            }
            ConvertSub(pRootOfTree.right);
        }

        public TreeNode Convert(TreeNode pRootOfTree) {
            if (pRootOfTree == null) {
                return null;
            }
            TreeNode center = link(pRootOfTree);
            while (center.left != null) {
                center = center.left;
            }
            return center;
        }

        public TreeNode link(TreeNode pRootOfTree) {
            TreeNode begin = null, end = null;
            if (pRootOfTree.left != null) {
                begin = link(pRootOfTree.left);
                begin = begin.right == null ? begin : begin.right;
//                System.out.println(pRootOfTree.left.val + "通过begin返回值为：" + begin.val);
            }
            if (begin != null) {
                begin.right = pRootOfTree;
                pRootOfTree.left = begin;
//                System.out.println(begin.val + "连接" + pRootOfTree.val);
            }
            if (pRootOfTree.right != null) {
                end = link(pRootOfTree.right);
                end = end.left == null ? end : end.left;
//                System.out.println(pRootOfTree.right.val + "通过end返回值为：" + end.val);
            }
            if (end != null) {
                pRootOfTree.right = end;
                end.left = pRootOfTree;
//                System.out.println(pRootOfTree.val + "连接" + end.val);
            }
            return pRootOfTree;
        }

        private static void test() {
            TreeNode node1 = new TreeNode(6);
            TreeNode node2 = new TreeNode(4);
            TreeNode node3 = new TreeNode(8);
            TreeNode node4 = new TreeNode(3);
            TreeNode node5 = new TreeNode(5);
            TreeNode node6 = new TreeNode(7);
            TreeNode node7 = new TreeNode(9);
            node1.left = node2;
            node1.right = node3;
            node2.left = node4;
            node2.right = node5;
            node3.left = node6;
            node3.right = node7;

            TreeNode root = new TwentySix().Convert1(node1);
            TreeNode temp = null;
            while (root != null) {
                System.out.print(root.val + " ");
                temp = root;
                root = root.right;
            }
            System.out.println();
            while (temp != null) {
                System.out.print(temp.val + " ");
                temp = temp.left;
            }
        }
    }

    /**
     * 输入一个字符串,按字典序打印出该字符串中字符的所有排列。
     * 例如输入字符串abc,则打印出由字符a,b,c所能排列出来的所有字符串abc,acb,bac,bca,cab和cba。
     * 输入一个字符串,长度不超过9(可能有字符重复),字符只包括大小写字母。
     */
    static class TwentySeven {

        public ArrayList<String> Permutation(String str) {
            ArrayList<String> res = new ArrayList<>();
            if (str != null && str.length() > 0) {
                PermutationHelper(str.toCharArray(), 0, res);
                Collections.sort(res);
            }
            return res;
        }

        public void PermutationHelper(char[] cs, int i, List<String> list) {
            if (i == cs.length - 1) {
                String val = String.valueOf(cs);
                if (!list.contains(val)) {
                    list.add(val);
                }
            } else {
                for (int j = i; j < cs.length; j++) {
                    swap(cs, i, j);
                    PermutationHelper(cs, i + 1, list);
                    swap(cs, i, j);
                }
            }
        }

        public void swap(char[] cs, int i, int j) {
            char temp = cs[i];
            cs[i] = cs[j];
            cs[j] = temp;
        }

        private static void test() {
            String str = "abc";
            String str1 = "aabc";
            ArrayList<String> list = new TwentySeven().Permutation(str);
            System.out.println(list);
            list = new TwentySeven().Permutation(str1);
            System.out.println(list);
        }
    }

    /**
     * 数组中有一个数字出现的次数超过数组长度的一半，请找出这个数字。
     * 例如输入一个长度为9的数组{1,2,3,2,2,2,5,4,2}。由于数字2在数组中出现了5次，
     * 超过数组长度的一半，因此输出2。如果不存在则输出0。
     */
    static class TwentyEight {
        public int MoreThanHalfNum_Solution(int[] array) {
            int size = array.length / 2;
            int[] arr = new int[array.length];
            int index = 0;
            for (int i = 0; i < array.length; i++) {
                for (int j = 0; j <= index; j++) {
                    if (j == index) {
                        arr[index++]++;
                        break;
                    }
                    if (array[i] == array[j]) {
                        if (++arr[j] > size) {
                            return array[j];
                        }
                        break;
                    }
                }
            }
            return size != 1 ? 0 : array[0];
        }

        private static void test() {
            int[] arr = new int[]{1, 2, 3, 2, 4, 2, 5, 2, 3};
            int[] arr1 = new int[]{1, 3, 4, 5, 2, 2, 2, 2, 2};
            System.out.println(new TwentyEight().MoreThanHalfNum_Solution(arr1));
        }
    }

    /**
     * 输入n个整数，找出其中最小的K个数。例如输入4,5,1,6,2,7,3,8这8个数字，则最小的4个数字是1,2,3,4。
     */
    static class TwentyNine {
        public ArrayList<Integer> GetLeastNumbers_Solution(int[] input, int k) {
            ArrayList<Integer> list = new ArrayList<>();
            if (input == null || input.length == 0 || k == 0 || k > input.length) {
                return list;
            }
            quickSort(input, 0, input.length - 1);
            for (int i = 0; i < k; i++) {
                list.add(input[i]);
            }
            return list;
        }

        private void quickSort(int[] input, int start, int end) {
            if (start == end || start > end) {
                return;
            }
            int index = start;
            for (int i = start; i <= end; i++) {
                if (input[i] < input[index]) {
                    int temp = i;
                    int min = input[i];
                    while (temp != index) {
                        input[temp] = input[temp - 1];
                        temp--;
                    }
                    input[index++] = min;
                }
            }
            quickSort(input, start, index - 1);
            quickSort(input, index + 1, end);
        }

        private static void test() {
            int[] arr = new int[]{5, 8, 7, 6, 9, 4, 3, 2, 1};
            System.out.println(new TwentyNine().GetLeastNumbers_Solution(arr, arr.length));
        }
    }

    /**
     * 输入一个整形数组，数组里有正数也有负数。数组中的一个或连续多个整数组成一个子数组。
     * 求所有子数组的和的最大值。例如输入数组{1, -2, 3, 10, -4, 7, 2, -5}，和最大的子数组为{3, 10, -4, 7, 2}，因此输出为该子数组的和18。
     */
    static class Thirty {
        public int FindGreatestSumOfSubArray(int[] array) {
            if (array == null || array.length == 0) {
                return 0;
            }
            int max = array[0];
            int sum = array[0];
            for (int i = 1; i < array.length; i++) {
                int cur = array[i];
                // 当前元素大于连续子数组和加上元素本身 && 最大值比元素还小时
                // 形如1, -2, 3抛弃前面的连续子数组，重新开始计算连续数组和
                if (cur > (sum + cur) && max < cur) {
                    sum = cur;
                    max = cur;
                } else if (sum + cur > max) {
                    // 加上当前元素后，数组和比最大值还大，则连续该元素
                    max = sum + cur;
                    sum += cur;
                } else {
                    sum += cur;
                }
                System.out.println("经过第" + i + "个(" + cur + ")后，sum=" + sum + ",max=" + max);
            }
            return max;
        }

        public int FindGreatestSumOfSubArray1(int[] array) {
            if (array == null || array.length == 0) {
                return 0;
            }
            int curSum = array[0];
            int maxSum = curSum;
            for (int i = 1; i < array.length; i++) {
                if (curSum <= 0) {
                    curSum = array[i];
                } else {
                    curSum += array[i];
                }
                if (curSum > maxSum) {
                    maxSum = curSum;
                }
            }
            return maxSum;
        }

        /**
         * 动态规划解法
         * 返回f(i)={f(i-1)+array[i],array[i]},两者的最大值,如果f(i-1)<0,则选的是array[i]
         */
        public int FindGreatestSumOfSubArray2(int[] array) {
            if (array == null || array.length == 0) {
                return 0;
            }
            int result = array[0];
            int currentResult = array[0];
            for (int i = 1; i < array.length; i++) {
                currentResult = (currentResult + array[i]) > array[i] ? currentResult + array[i] : array[i];
                result = currentResult > result ? currentResult : result;
            }
            return result;
        }

        private static void test() {
            int[] arr = new int[]{1, -2, 3, 10, -4, 7, 2, -5};
            System.out.println(new Thirty().FindGreatestSumOfSubArray1(arr));
        }
    }

    public static void main(String[] args) {
//        TwentySix.test();
//        TwentySeven.test();
//        TwentyEight.test();
//        TwentyNine.test();
        Thirty.test();
    }
}
