package study.hk.offer.niuke;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author HK
 * @date 2018-11-21 21:31
 */
public class ThirtySevenToFortyTwo {

    /**
     * 统计一个数字在排序数组中出现的次数。
     */
    static class ThirtySeven {
        int times = 0;

        public int GetNumberOfK(int[] array, int k) {
            int t = 0;
            for (int i = 0; i < array.length; i++) {
                if (array[i] == k) {
                    ++t;
                    if (i + 1 != array.length && array[i + 1] != k) {
                        return t;
                    }
                }
            }
            return t;
        }

        public int getNumberOfK(int[] array, int k) {
            binarySearch(array, k, 0, array.length - 1);
            return times;
        }

        public int binarySearch(int[] array, int k, int start, int end) {
            if (start > end || start < 0 || end >= array.length) {
                return 0;
            }
            int mid = (start + end) / 2;
            if (array[mid] < k) {
                return binarySearch(array, k, mid + 1, end);
            }
            if (array[mid] > k) {
                return binarySearch(array, k, start, mid - 1);
            }
            times++;
            return binarySearch(array, k, start, mid - 1) + binarySearch(array, k, mid + 1, end);
        }
    }

    /**
     * 输入一棵二叉树，求该树的深度。从根结点到叶结点依次经过的结点（含根、叶结点）形成树的一条路径，最长路径的长度为树的深度。
     */
    static class ThirtyEight {
        static class TreeNode {
            int val = 0;
            TreeNode left = null;
            TreeNode right = null;

            public TreeNode(int val) {
                this.val = val;
            }
        }

        int max = 0;
        int depth = 0;

        public int TreeDepth(TreeNode root) {
            if (root == null) {
                return 0;
            }
            if (root.right == null && root.left == null) {
                if (depth > max) {
                    max = depth;
                }
                return max + 1;
            }
            if (root.left != null) {
                depth++;
                TreeDepth(root.left);
                depth--;
            }
            if (root.right != null) {
                depth++;
                TreeDepth(root.right);
                depth--;
            }
            return max + 1;
        }

        public int TreeDepth1(TreeNode root) {
            if (root == null) {
                return 0;
            }
            int nLelt = TreeDepth1(root.left);
            int nRight = TreeDepth1(root.right);
            return nLelt > nRight ? (nLelt + 1) : (nRight + 1);
        }

        private static void test() {
            TreeNode node1 = new TreeNode(1);
            TreeNode node2 = new TreeNode(2);
            TreeNode node3 = new TreeNode(4);
            TreeNode node4 = new TreeNode(5);
            node1.left = node2;
            node2.left = node3;
            node2.right = node4;
            System.out.println(new ThirtyEight().TreeDepth(node1));
        }
    }

    /**
     * 输入一棵二叉树，判断该二叉树是否是平衡二叉树。
     */
    static class ThirtyNine {
        static class TreeNode {
            int val = 0;
            TreeNode left = null;
            TreeNode right = null;

            public TreeNode(int val) {
                this.val = val;
            }
        }

        public boolean IsBalanced_Solution(TreeNode root) {
            if (root == null) {
                return false;
            }
            int left = 0, right = 0, depthLeft = 0, depthRight = 0;
            if (root.left != null) {
                left = root.left.val;
                depthLeft = TreeDepth1(root.left);
                if (left >= root.val) {
                    return false;
                }
            }
            if (root.right != null) {
                right = root.right.val;
                depthRight = TreeDepth1(root.right);
                if (right <= root.val) {
                    return false;
                }
            }
            if (depthLeft - depthRight > 1 || depthRight - depthLeft > 1) {
                return false;
            }
            boolean result = true;
            if (root.left != null) {
                result = IsBalanced_Solution(root.left);
            }
            if (root.right != null && result) {
                result = IsBalanced_Solution(root.right);
            }
            return result;
        }

        public boolean IsBalanced_Solution1(TreeNode root) {
            if (root == null) {
                return true;
            }
            int depthLeft = TreeDepth1(root.left);
            int depthRight = TreeDepth1(root.right);
            if (depthLeft - depthRight > 1 || depthRight - depthLeft > 1) {
                return false;
            }
            boolean result = true;
            if (root.left != null) {
                result = IsBalanced_Solution1(root.left);
            }
            if (root.right != null && result) {
                result = IsBalanced_Solution1(root.right);
            }
            return result;
        }

        public int TreeDepth1(TreeNode root) {
            if (root == null) {
                return 0;
            }
            int nLelt = TreeDepth1(root.left);
            int nRight = TreeDepth1(root.right);
            return nLelt > nRight ? (nLelt + 1) : (nRight + 1);
        }

        private static void test() {
            TreeNode node1 = new TreeNode(1);
            TreeNode node2 = new TreeNode(2);
            TreeNode node3 = new TreeNode(3);
            TreeNode node4 = new TreeNode(4);
            TreeNode node5 = new TreeNode(5);
            TreeNode node6 = new TreeNode(6);
            TreeNode node7 = new TreeNode(7);
            node4.left = node2;
            node4.right = node6;
            node2.left = node1;
            node2.right = node3;
            node6.left = node5;
            node6.right = node7;
            System.out.println(new ThirtyNine().IsBalanced_Solution(node1));
        }
    }

    /**
     * 一个整型数组里除了两个数字之外，其他的数字都出现了偶数次。请写程序找出这两个只出现一次的数字。
     */
    static class Forty {
        public void FindNumsAppearOnce(int[] array, int num1[], int num2[]) {
            HashMap<Integer, Integer> map = new HashMap(array.length);
            for (int i = 0; i < array.length; i++) {
                if (map.containsKey(array[i])) {
                    map.put(array[i], 0);
                } else {
                    map.put(array[i], 1);
                }
            }
            boolean findOne = false;
            Set<Map.Entry<Integer, Integer>> entry = map.entrySet();
            for (Map.Entry<Integer, Integer> t : entry) {
                if (t.getValue() == 1 && !findOne) {
                    num1[0] = t.getKey();
                    findOne = true;
                } else if (t.getValue() == 1) {
                    num2[0] = t.getKey();
                }
            }
        }
    }

    /**
     * 输出所有和为S的连续正数序列。序列内按照从小至大的顺序，序列间按照开始数字从小到大的顺序
     */
    static class FortyOne {
        public ArrayList<ArrayList<Integer>> FindContinuousSequence(int sum) {
            ArrayList<ArrayList<Integer>> lists = new ArrayList<>();
            int end = sum % 2 == 0 ? sum / 2 : sum / 2 + 1;
            for (int i = 1; i < end; i++) {
                int cur = 0;
                ArrayList<Integer> list = new ArrayList<>();
                for (int j = i; j <= end; j++) {
                    cur += j;
                    if (cur < sum) {
                        list.add(j);
                    } else if (cur == sum) {
                        list.add(j);
                        lists.add(list);
                    } else {
                        break;
                    }
                }
            }
            return lists;
        }
    }

    /**
     * 输入一个递增排序的数组和一个数字S，在数组中查找两个数，使得他们的和正好是S，如果有多对数字的和等于S，输出两个数的乘积最小的(小的在前)。
     */
    static class FortyTwo {
        public ArrayList<Integer> FindNumbersWithSum(int[] array, int sum) {
            ArrayList<Integer> list = new ArrayList<>();
            if (array.length == 0) {
                return list;
            }
            int a = 0, b = array.length - 1;
            while (a != b) {
                if (array[a] + array[b] > sum) {
                    b--;
                } else if (array[a] + array[b] < sum) {
                    a++;
                } else if (array[a] + array[b] == sum) {
                    list.add(array[a]);
                    list.add(array[b]);
                    break;
                }
            }
            return list;
        }
    }

    public static void main(String[] args) {
//        ThirtyEight.test();
        ThirtyNine.test();
    }
}
