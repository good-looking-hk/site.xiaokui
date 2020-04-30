package site.xiaokui.common.hk.offer.niuke;

import java.util.*;

/**
 * @author HK
 * @date 2018-11-09 08:17
 */
public class NineteenToTwentyFour {

    /**
     * 输入一个矩阵，按照从外向里以顺时针的顺序依次打印出每一个数字，例如，
     * 如果输入如下4 X 4矩阵： 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16
     * 则依次打印出数字1,2,3,4,8,12,16,15,14,13,9,5,6,7,11,10.
     */
    static class Nineteen {
        public ArrayList<Integer> printMatrix(int[][] matrix) {
            ArrayList<Integer> list = new ArrayList<>();
            if (matrix == null || matrix.length == 0) {
                return list;
            }
            int i = 0;
            while (list.size() != matrix.length * matrix[0].length) {
                dealOne(i++, matrix, list);
            }
            return list;
        }

        private void dealOne(int index, int[][] arr, ArrayList<Integer> list) {
            int i = index;
            int j = i;
            int iEnd = arr.length - 1 - i;
            int jEnd = arr[0].length - 1 - i;
            if (i == iEnd) {
                for (; j <= jEnd; j++) {
                    list.add(arr[i][j]);
                }
                return;
            } else if (j == jEnd) {
                for (; i <= iEnd; i++) {
                    list.add(arr[i][j]);
                }
                return;
            }
            do {
                list.add(arr[i][j]);
                if (i < iEnd && j < jEnd) {
                    j++;
                } else if (i < iEnd) {
                    i++;
                }
            } while (i != iEnd || j != jEnd);
            do {
                list.add(arr[i][j]);
                if (i != index && j != index) {
                    j--;
                } else if (i != index) {
                    i--;
                }
            } while (i != index);
        }

        private static void test() {
            int[][] arr = new int[][]{
                    {1, 2, 3, 4},
                    {2, 3, 4, 5},
                    {3, 4, 5, 6},
                    {4, 5, 6, 7},
                    {5, 6, 7, 8}


            };
            int[][] arr1 = new int[][]{
                    {1}, {2}, {3}, {4}, {5}
            };
            List<Integer> list = new Nineteen().printMatrix(arr1);
            System.out.println(list);
        }
    }

    /**
     * 定义栈的数据结构，请在该类型中实现一个能够得到栈中所含最小元素的min函数（时间复杂度应为O（1））。
     */
    static class Twenty {
        ArrayList<Integer> list1 = new ArrayList<>();
        ArrayList<Integer> list2 = new ArrayList<>();

        public void push(int node) {
            list1.add(node);
            if (list2.size() == 0) {
                list2.add(node);
            } else {
                int min = min();
                if (min > node) {
                    list2.add(node);
                } else {
                    list2.add(min);
                }
            }
        }

        public void pop() {
            if (list1.size() != 0) {
                list1.remove(list1.size() - 1);
                list2.remove(list2.size() - 1);
            }
        }

        public int top() {
            if (list1.size() != 0) {
                return list1.get(list1.size() - 1);
            } else {
                throw new RuntimeException("空栈");
            }
        }

        public int min() {
            if (list2.size() != 0) {
                return list2.get(list2.size() - 1);
            } else {
                throw new RuntimeException("空栈");
            }
        }
    }

    /**
     * 输入两个整数序列，第一个序列表示栈的压入顺序，请判断第二个序列是否可能为该栈的弹出顺序。
     * 假设压入栈的所有数字均不相等。例如序列1,2,3,4,5是某栈的压入顺序，序列4,5,3,2,1是该压栈序列对应的一个弹出序列，
     * 但4,3,5,1,2就不可能是该压栈序列的弹出序列。（注意：这两个序列的长度是相等的）
     */
    static class TwentyOne {
        public boolean IsPopOrder(int[] pushA, int[] popA) {
            Stack<Integer> stack = new Stack<>();
            int i = 0;
            int j = i;
            while (i != pushA.length) {
                if (pushA[i] != popA[j]) {
                    stack.push(pushA[i]);
                    i++;
                } else {
                    i++;
                    j++;
                }
            }
            while (!stack.isEmpty()) {
                if (j == pushA.length || popA[j++] != stack.pop()) {
                    return false;
                }
            }
            return true;
        }

        private static void test() {
            int[] arr = new int[]{1, 2, 3, 4, 5};
            int[] arr1 = new int[]{1, 2, 5, 4, 3}; // 栈高度为3
            int[] arr2 = new int[]{2, 1, 5, 3, 4}; //
            int[] arr3 = new int[]{1, 2, 3, 4, 5}; //
            int[] arr4 = new int[]{3, 2, 1, 4, 5};
            boolean result = new TwentyOne().IsPopOrder(arr, arr4);
            System.out.println(result);
        }
    }

    /**
     * 从上往下打印出二叉树的每个节点，同层节点从左至右打印。
     */
    static class TwentyTwo {
        static class TreeNode {
            int val = 0;
            TreeNode left = null;
            TreeNode right = null;

            public TreeNode(int val) {
                this.val = val;
            }
        }

        public ArrayList<Integer> PrintFromTopToBottom(TreeNode root) {
            ArrayList<Integer> list = new ArrayList<>();
            if (root == null) {
                return list;
            }
            Deque<TreeNode> queue = new LinkedList<>();
            queue.offer(root);
            while (!queue.isEmpty()) {
                TreeNode node = queue.poll();
                list.add(node.val);
                if (node.left != null) {
                    queue.offer(node.left);
                }
                if (node.right != null) {
                    queue.offer(node.right);
                }
            }
            return list;
        }
    }

    /**
     * 输入一个整数数组，判断该数组是不是某二叉搜索树的后序遍历的结果。
     * 如果是则输出Yes,否则输出No。假设输入的数组的任意两个数字都互不相同。
     */
    static class TwentyThree {
        public boolean VerifySquenceOfBST(int[] sequence) {
            if (sequence == null || sequence.length == 0) {
                return false;
            }
            int index = index(sequence);
            // 顺序不符合遍历规则
            if (index == -1) {
                return false;
            } else if (index == sequence.length - 1 || index == 0) {
                // 针对两种极端情况
                return true;
            }
            int[] left = left(index, sequence);
            int[] right = right(index, sequence);
            return VerifySquenceOfBST(left) && VerifySquenceOfBST(right);
        }

        private int index(int[] arr) {
            int index = -1;
            // 找到分界点，这里偏向后半部分（也可以偏向前部分），前半部分都小于最后值，后半部分都大于最后值
            for (int i = 0; i < arr.length; i++) {
                if (arr[i] >= arr[arr.length - 1]) {
                    index = i;
                    break;
                }
            }
            // 处理54321极端情况
            if (index == 0) {
                for (int i = 0; i < arr.length - 1; i++) {
                    if (arr[i] > arr[i + 1]) {
                        continue;
                    }
                    return -1;
                }
                return 0;
            }
            // 判断后半部分是否存在小于分界点的值，最后值除外
            for (int i = index + 1; i < arr.length - 1; i++) {
                if (arr[i] < arr[index]) {
                    return -1;
                }
            }
            // 注意这里会存在12345的极端情况
            if (index == arr.length - 1) {
                for (int i = 0; i < arr.length - 1; i++) {
                    if (arr[i] < arr[i + 1]) {
                        continue;
                    }
                    return -1;
                }
                return arr.length - 1;
            }
            return index;
        }

        private int[] left(int index, int[] arr) {
            int[] left = new int[index];
            for (int i = 0; i < left.length; i++) {
                left[i] = arr[i];
            }
            return left;
        }

        private int[] right(int index, int[] arr) {
            int[] right = new int[arr.length - index - 1];
            for (int i = 0; i < right.length; i++) {
                right[i] = arr[index + i];
            }
            return right;
        }

        private static void test() {
            int[] arr = new int[]{3, 5};
            int[] arr1 = new int[]{2, 4, 3, 6, 8, 7, 5};
            int[] arr2 = new int[]{1, 2, 3, 4, 5};
            int[] arr3 = new int[]{5, 4, 3, 2, 1};
            boolean result = new TwentyThree().VerifySquenceOfBST(arr);
            System.out.println(result);
            result = new TwentyThree().VerifySquenceOfBST(arr1);
            System.out.println(result);
            result = new TwentyThree().VerifySquenceOfBST(arr2);
            System.out.println(result);
            result = new TwentyThree().VerifySquenceOfBST(arr3);
            System.out.println(result);
        }
    }

    /**
     * 输入一颗二叉树的跟节点和一个整数，打印出二叉树中结点值的和为输入整数的所有路径。
     * 路径定义为从树的根结点开始往下一直到叶结点所经过的结点形成一条路径。(注意: 在返回值的list中，数组长度大的数组靠前)
     */
    static class TwentyFour {
        static class TreeNode {
            int val = 0;
            TreeNode left = null;
            TreeNode right = null;

            public TreeNode(int val) {
                this.val = val;
            }
        }
        ArrayList<Integer> list = new ArrayList<>();
        ArrayList<ArrayList<Integer>> lists = new ArrayList<>();

        public ArrayList<ArrayList<Integer>> FindPath1(TreeNode root, int target) {
            if (root == null) {
                return lists;
            }
            list.add(root.val);
            int needValue = target - root.val;
            if (needValue == 0 && root.left == null && root.right == null) {
                lists.add(new ArrayList<>(list));
            }
            FindPath1(root.left, needValue);
            FindPath1(root.right, needValue);
            list.remove(list.size() - 1);
            return lists;
        }

        ArrayList<ArrayList<Integer>> res=new ArrayList<>();

        public void Path(TreeNode node,List<Integer> list,int target,int sum){
            if(node.left==null&&node.right==null){
                if(sum==target){
                    ArrayList<Integer> temp=new ArrayList<>(list);
                    res.add(temp);
                }
            }

            if(node.left!=null){
                sum+=node.left.val;
                list.add(node.left.val);
                Path(node.left,list,target,sum);
                sum-=list.remove(list.size()-1);
            }

            if(node.right!=null){
                sum+=node.right.val;
                list.add(node.right.val);
                Path(node.right,list,target,sum);
                sum-=list.remove(list.size()-1);
            }

        }

        public ArrayList<ArrayList<Integer>> FindPath(TreeNode root,int target) {
            if(root==null){
                return new ArrayList<>();
            }
            ArrayList<Integer> list=new ArrayList<>();
            list.add(root.val);
            Path(root,list,target,root.val);
            return res;
        }

        public static void test() {
            TreeNode node1 = new TreeNode(10);
            TreeNode node2 = new TreeNode(5);
            TreeNode node3 = new TreeNode(12);
            TreeNode node4 = new TreeNode(3);
            TreeNode node5 = new TreeNode(7);
//            TreeNode node5 = new TreeNode(1);
//            TreeNode node6 = new TreeNode(1);
//            TreeNode node7 = new TreeNode(3);
            node1.left = node2;
            node1.right = node3;
            node2.left = node4;
            node2.right = node5;
            System.out.println(new TwentyFour().FindPath(node1, 22));
        }
    }

    public static void main(String[] args) {
//        Nineteen.test();
//        Twenty.test();;
        TwentyOne.test();
//        TwentyThree.test();
//        TwentyFour.test();
    }
}
