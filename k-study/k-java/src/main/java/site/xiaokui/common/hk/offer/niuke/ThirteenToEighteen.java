package site.xiaokui.common.hk.offer.niuke;

import java.util.Deque;
import java.util.LinkedList;

/**
 * @author HK
 * @date 2018-11-07 21:37
 */
public class ThirteenToEighteen {

    /**
     * 输入一个整数数组，实现一个函数来调整该数组中数字的顺序，使得所有的奇数位于数组的前半部分，
     * 所有的偶数位于数组的后半部分，并保证奇数和奇数，偶数和偶数之间的相对位置不变。
     */
    static class Thirteen {
        public void reOrderArray(int[] array) {
            int[] one = new int[array.length];
            int[] two = new int[array.length];
            int index1 = 0, index2 = 0;
            for (int i = 0; i < array.length; i++) {
                if (array[i] % 2 == 0) {
                    two[index2++] = array[i];
                } else {
                    one[index1++] = array[i];
                }
            }
            for (int i = 0; i < index1; i++) {
                array[i] = one[i];
            }
            for (int i = 0; i < index2; i++) {
                array[index1 + i] = two[i];
            }
        }
    }

    /**
     * 输入一个链表，输出该链表中倒数第k个结点。
     */
    static class Fourteen {
        private class ListNode {
            int val;
            ListNode next = null;

            ListNode(int val) {
                this.val = val;
            }
        }

        public ListNode FindKthToTail(ListNode head, int k) {
            if (k <= 0 || head == null) {
                return null;
            }
            Deque<ListNode> stack = new LinkedList<>();
            while (head != null) {
                stack.push(head);
                head = head.next;
            }
            for (int i = 1; i < k; i++) {
                stack.poll();
            }
            return stack.poll();
        }
    }

    /**
     * 输入一个链表，反转链表后，输出新链表的表头。
     */
    static class Fifteen {
        private static class ListNode {
            int val;
            ListNode next = null;

            ListNode(int val) {
                this.val = val;
            }
        }

        public ListNode ReverseList(ListNode head) {
            ListNode pre = head;
            if (pre == null || pre.next == null) {
                return pre;
            }
            ListNode cur = pre.next;
            pre.next = null;
            ListNode nex = cur.next;
            while (nex != null) {
                cur.next = pre;
                pre = cur;
                cur = nex;
                nex = nex.next;
            }
            cur.next = pre;
            return cur;
        }

        private static void test() {
            ListNode node1 = new ListNode(1);
            ListNode node2 = new ListNode(2);
            ListNode node3 = new ListNode(3);
            ListNode node4 = new ListNode(4);
            node1.next = node2;
            node2.next = node3;
            node3.next = node4;
            ListNode root = new Fifteen().ReverseList(node1);
            while (root != null) {
                System.out.println(root.val);
                root = root.next;
            }
        }
    }

    /**
     * 输入两个单调递增的链表，输出两个链表合成后的链表，当然我们需要合成后的链表满足单调不减规则。
     */
    static class Sixteen {
        static class ListNode {
            int val;
            ListNode next = null;

            ListNode(int val) {
                this.val = val;
            }
        }

        public ListNode Merge(ListNode list1, ListNode list2) {
            if (list1 == null || list2 == null) {
                return list1 != null ? list1 : list2;
            }
            // 被插入链，头结点不应该被修改
            ListNode to = list1.val < list2.val ? list1 : list2;
            // 插入链
            ListNode from = to == list2 ? list1 : list2;
            ListNode head = to;
            // from没有遍历完，且to还没到最后一个
            while (from != null && to.next != null) {
                // 针对在1 2/1 3/2 2中插入2的情况
                if (to.val <= from.val && to.next.val >= from.val) {
                    ListNode temp = to.next;
                    to.next = from;
                    ListNode from1 = from.next;
                    from.next = temp;
                    from = from1;
                    to = to.next;
                } else {
                    // 针对在1 2 3中插入4的情况
                    to = to.next;
                }
            }
            if (from != null) {
                to.next = from;
            }
            return head;
        }

        private static void test() {
            ListNode node1 = new ListNode(1);
            ListNode node2 = new ListNode(3);
            ListNode node3 = new ListNode(5);
//            ListNode node4 = new ListNode(4);
            node1.next = node2;
            node2.next = node3;
//            node3.next = node4;

            ListNode node5 = new ListNode(1);
            ListNode node6 = new ListNode(3);
            ListNode node7 = new ListNode(5);
//            ListNode node8 = new ListNode(5);
            node5.next = node6;
            node6.next = node7;
//            node7.next = node8;

            ListNode root = new Sixteen().Merge(node1, node5);
            while (root != null) {
                System.out.println(root.val);
                root = root.next;
            }
        }
    }

    /**
     * 输入两棵二叉树A，B，判断B是不是A的子结构。（ps：我们约定空树不是任意一个树的子结构）
     */
    static class Seventeen {
        static class TreeNode {
            int val = 0;
            TreeNode left = null;
            TreeNode right = null;

            public TreeNode(int val) {
                this.val = val;
            }
        }

        public boolean HasSubtree(TreeNode root1, TreeNode root2) {
            if (root1 == null || root2 == null) {
                return false;
            }
            if (root1.val != root2.val) {
                return HasSubtree(root1.left, root2) || HasSubtree(root1.right, root2);
            }
            boolean yes = isSame(root1, root2);
            return yes ? yes : HasSubtree(root1.left, root2) || HasSubtree(root1.right, root2);
        }

        public boolean isSame(TreeNode node1, TreeNode node2) {
            if (node1 == null || node1.val != node2.val) {
                return false;
            }
            boolean result = true;
            if (node2.left != null) {
                result = isSame(node1.left, node2.left);
            }
            if (node2.right != null && result) {
                result = isSame(node1.right, node2.right);
            }
            return result;
        }

        private static void test() {
            TreeNode node1 = new TreeNode(1);
            TreeNode node2 = new TreeNode(2);
            TreeNode node3 = new TreeNode(3);
            TreeNode node4 = new TreeNode(4);
            TreeNode node5 = new TreeNode(5);
            node1.left = node2;
            node1.right = node3;
            node2.left = node4;
            node2.right = node5;

            TreeNode node6 = new TreeNode(2);
            TreeNode node7 = new TreeNode(4);
            TreeNode node8 = new TreeNode(5);
            node6.left = node7;
            node6.right = node8;

            System.out.println(new Seventeen().HasSubtree(node1, node6));
        }
    }

    /**
     * 操作给定的二叉树，将其变换为源二叉树的镜像。
     */
    static class Eighteen {
        static class TreeNode {
            int val = 0;
            TreeNode left = null;
            TreeNode right = null;

            public TreeNode(int val) {
                this.val = val;
            }
        }
        public void Mirror(TreeNode root) {
            if (root == null) {
                return;
            }
            if (root.right != null || root.left != null) {
                swap(root);
                if (root.left != null) {
                    Mirror(root.left);
                }
                if (root.right != null) {
                    Mirror(root.right);
                }
            }
        }
        private void swap(TreeNode target) {
            TreeNode left = target.left;
            TreeNode right = target.right;
            target.left = right;
            target.right = left;
        }
    }

    public static void main(String[] args) {
//            Fifteen.test();
//        Sixteen.test();
        Seventeen.test();
    }
}
