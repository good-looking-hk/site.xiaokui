package site.xiaokui.common.hk.offer.niuke;

import javax.swing.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

/**
 * @author HK
 * @date 2018-12-13 23:46
 */
public class FiftyFiveToSixty {

    /**
     * 给一个链表，若其中包含环，请找出该链表的环的入口结点，否则，输出null。
     */
    static class FiftyFive {
        public class ListNode {
            int val;
            ListNode next = null;

            ListNode(int val) {
                this.val = val;
            }
        }

        public ListNode EntryNodeOfLoop(ListNode pHead) {
            ArrayList<ListNode> list = new ArrayList<>();
            while (pHead != null) {
                if (list.contains(pHead)) {
                    return pHead;
                }
                list.add(pHead);
                pHead = pHead.next;
            }
            return null;
        }

        ListNode EntryNodeOfLoop1(ListNode pHead) {
            if (pHead == null || pHead.next == null)
                return null;
            ListNode p1 = pHead;
            ListNode p2 = pHead;
            while (p2 != null && p2.next != null) {
                p1 = p1.next;
                p2 = p2.next.next;
                if (p1 == p2) {
                    p2 = pHead;
                    while (p1 != p2) {
                        p1 = p1.next;
                        p2 = p2.next;
                    }
                    if (p1 == p2)
                        return p1;
                }
            }
            return null;
        }
    }

    /**
     * 在一个排序的链表中，存在重复的结点，请删除该链表中重复的结点，重复的结点不保留，返回链表头指针。
     * 例如，链表1->2->3->3->4->4->5 处理后为 1->2->5。
     */
    static class FiftySix {
        public static class ListNode {
            int val;
            ListNode next = null;

            ListNode(int val) {
                this.val = val;
            }
        }

        public ListNode deleteDuplication(ListNode pHead) {
            if (pHead == null) {
                return null;
            }
            ListNode head = new ListNode(0);
            ListNode root = head;
            ListNode cur = pHead;
            ListNode nex = cur.next;
            boolean isInCheck = false;
            while (nex != null) {
                if (cur.val != nex.val) {
                    if (!isInCheck) {
                        System.out.println(head.val + "正常添加" + cur.val);
                        head.next = cur;
                        head = head.next;
                        cur = cur.next;
                        nex = nex.next;
                        head.next = null;
                    } else {
                        cur = nex;
                        nex = nex.next;
                        isInCheck = false;
                    }
                } else {
                    isInCheck = true;
                    nex = nex.next;
                }
            }
            if (cur.next == null && !isInCheck) {
                head.next = cur;
            }
            return root.next;
        }

        private static void test() {
            ListNode node1 = new ListNode(1);
            ListNode node2 = new ListNode(2);
            ListNode node3 = new ListNode(3);
            ListNode node4 = new ListNode(3);
            ListNode node5 = new ListNode(4);
            ListNode node6 = new ListNode(4);
            ListNode node7 = new ListNode(5);
            node1.next = node2;
            node2.next = node3;
            node3.next = node4;
            node4.next = node5;
            node5.next = node6;
            node6.next = node7;
            ListNode root = new FiftySix().deleteDuplication(node1);
            while (root != null) {
                System.out.println(root.val);
                root = root.next;
            }
        }
    }

    /**
     * 给定一个二叉树和其中的一个结点，请找出中序遍历顺序的下一个结点并且返回。注意，树中的结点不仅包含左右子结点，同时包含指向父结点的指针。
     */
    static class FiftySeven {
        public static class TreeLinkNode {
            int val;
            TreeLinkNode left = null;
            TreeLinkNode right = null;
            TreeLinkNode next = null;

            TreeLinkNode(int val) {
                this.val = val;
            }
        }

        boolean findTarget = false;
        TreeLinkNode target = null;

        public TreeLinkNode GetNext(TreeLinkNode pNode) {
            if (pNode == null) {
                return null;
            }
            TreeLinkNode node = pNode;
            while (pNode.next != null) {
                pNode = pNode.next;
            }
            TreeLinkNode root = pNode;
            traverse(root, node);
            return target;
        }

        private void traverse(TreeLinkNode root, TreeLinkNode node) {
            if (root == null || target != null) {
                return;
            }
            traverse(root.left, node);
            if (findTarget) {
                target = root;
                findTarget = false;
            }
            if (root == node) {
                findTarget = true;
            }
            traverse(root.right, node);
        }

        private static void test() {
            TreeLinkNode node1 = new TreeLinkNode(8);
            TreeLinkNode node2 = new TreeLinkNode(6);
            TreeLinkNode node3 = new TreeLinkNode(10);
            TreeLinkNode node4 = new TreeLinkNode(5);
            TreeLinkNode node5 = new TreeLinkNode(7);
            TreeLinkNode node6 = new TreeLinkNode(9);
            TreeLinkNode node7 = new TreeLinkNode(11);
            node1.left = node2;
            node1.right = node3;
            node2.left = node4;
            node2.right = node5;
            node3.left = node6;
            node3.right = node7;
            System.out.println(new FiftySeven().GetNext(node1).val);
        }
    }

    /**
     * 请实现一个函数，用来判断一颗二叉树是不是对称的。注意，如果一个二叉树同此二叉树的镜像是同样的，定义其为对称的。
     */
    static class FiftyEight {
        public static class TreeNode {
            int val = 0;
            TreeNode left = null;
            TreeNode right = null;

            public TreeNode(int val) {
                this.val = val;
            }
        }

        boolean isSymmetrical(TreeNode pRoot) {
            if (pRoot == null) {
                return true;
            }
            ArrayList<TreeNode> list = new ArrayList<>();
            traverse(pRoot, list);
            if (list.size() % 2 == 0 || list.get(list.size() / 2) != pRoot) {
                return false;
            }
            while (list.size() != 1) {
                if (list.remove(0).val != (list.remove(list.size() - 1).val)) {
                    return false;
                }
            }
            return true;
        }

        private void traverse(TreeNode node, ArrayList<TreeNode> list) {
            if (node.left != null) {
                traverse(node.left, list);
            }
            System.out.println(node.val);
            list.add(node);
            if (node.right != null) {
                traverse(node.right, list);
            }
        }

        boolean isSymmetrical1(TreeNode pRoot) {
            if (pRoot == null) {
                return true;
            }
            return isSymmetrical1(pRoot.left, pRoot.right);
        }

        boolean isSymmetrical1(TreeNode t1, TreeNode t2) {
            if (t1 == null && t2 == null) {
                return true;
            }
            if (t1 != null && t2 != null) {
                return t1.val == t2.val && isSymmetrical1(t1.left, t2.right) && isSymmetrical1(t1.right, t2.left);
            }
            return false;
        }

        boolean isSymmetricalDFS(TreeNode pRoot) {
            if (pRoot == null) return true;
            Stack<TreeNode> s = new Stack<>();
            s.push(pRoot.left);
            s.push(pRoot.right);
            while (!s.empty()) {
                TreeNode right = s.pop();//成对取出
                TreeNode left = s.pop();
                if (left == null && right == null) continue;
                if (left == null || right == null) return false;
                if (left.val != right.val) return false;
                //成对插入
                s.push(left.left);
                s.push(right.right);
                s.push(left.right);
                s.push(right.left);
            }
            return true;
        }

        boolean isSymmetricalBFS(TreeNode pRoot) {
            if (pRoot == null) return true;
            Queue<TreeNode> s = new LinkedList<>();
            s.offer(pRoot.left);
            s.offer(pRoot.right);
            while (!s.isEmpty()) {
                TreeNode right = s.poll();//成对取出
                TreeNode left = s.poll();
                if (left == null && right == null) continue;
                if (left == null || right == null) return false;
                if (left.val != right.val) return false;
                //成对插入
                s.offer(left.left);
                s.offer(right.right);
                s.offer(left.right);
                s.offer(right.left);
            }
            return true;
        }

        private static void test() {
            TreeNode node1 = new TreeNode(1);
            TreeNode node2 = new TreeNode(2);
            TreeNode node3 = new TreeNode(3);
            TreeNode node4 = new TreeNode(2);
            TreeNode node5 = new TreeNode(3);
            node1.left = node2;
            node2.left = node3;
            node1.right = node4;
            node4.right = node5;
        }
    }

    /**
     * 请实现一个函数按照之字形打印二叉树，即第一行按照从左到右的顺序打印，
     * 第二层按照从右至左的顺序打印，第三行按照从左到右的顺序打印，其他行以此类推。
     */
    static class FiftyNine {
        public class TreeNode {
            int val = 0;
            TreeNode left = null;
            TreeNode right = null;
            public TreeNode(int val) {
                this.val = val;
            }
        }
        public static ArrayList<ArrayList<Integer>> Print(TreeNode pRoot) {
            int layer = 1;
            //s1存奇数层节点
            Stack<TreeNode> s1 = new Stack<TreeNode>();
            s1.push(pRoot);
            //s2存偶数层节点
            Stack<TreeNode> s2 = new Stack<TreeNode>();

            ArrayList<ArrayList<Integer>> list = new ArrayList<ArrayList<Integer>>();

            while (!s1.empty() || !s2.empty()) {
                if (layer%2 != 0) {
                    ArrayList<Integer> temp = new ArrayList<Integer>();
                    while (!s1.empty()) {
                        TreeNode node = s1.pop();
                        if(node != null) {
                            temp.add(node.val);
                            System.out.print(node.val + " ");
                            s2.push(node.left);
                            s2.push(node.right);
                        }
                    }
                    if (!temp.isEmpty()) {
                        list.add(temp);
                        layer++;
                        System.out.println();
                    }
                } else {
                    ArrayList<Integer> temp = new ArrayList<Integer>();
                    while (!s2.empty()) {
                        TreeNode node = s2.pop();
                        if(node != null) {
                            temp.add(node.val);
                            System.out.print(node.val + " ");
                            s1.push(node.right);
                            s1.push(node.left);
                        }
                    }
                    if (!temp.isEmpty()) {
                        list.add(temp);
                        layer++;
                        System.out.println();
                    }
                }
            }
            return list;
        }
    }

    /**
     * 从上到下按层打印二叉树，同一层结点从左至右输出。每一层输出一行。
     */
    static class Sixty {
        public class TreeNode {
            int val = 0;
            TreeNode left = null;
            TreeNode right = null;
            public TreeNode(int val) {
                this.val = val;
            }
        }
        ArrayList<ArrayList<Integer> > Print(TreeNode pRoot) {
            ArrayList<ArrayList<Integer>> list = new ArrayList<>();
            depth(pRoot, 1, list);
            return list;
        }

        private void depth(TreeNode root, int depth, ArrayList<ArrayList<Integer>> list){
            if(root == null) return;
            if(depth > list.size())
                list.add(new ArrayList<Integer>());
            list.get(depth -1).add(root.val);

            depth(root.left, depth + 1, list);
            depth(root.right, depth + 1, list);
        }
    }

    public static void main(String[] args) {
//        FiftySix.test();
//        FiftySeven.test();
        FiftyEight.test();
    }
}
