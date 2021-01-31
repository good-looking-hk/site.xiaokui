package real;
/// 1.将head链表以m为组反转链表(不足m则不反转)：
/// 例子：假设m=3. 链表 1 -> 2 -> 3 -> 4 -> 5 -> 6 -> 7 ->8
/// output: 3 -> 2 -> 1 ->6 ->5 ->4 -> 7 ->8

/**
 * 校招的时候面过一些大厂（如 阿里、腾讯、拼多多、京东、网易、字节跳动、招商等，都没过，哈哈哈），被在线算法笔试给搞怕了
 * 有些不能用idea，要在线调试；有些还要一直开着视频，预防你作弊，偷窥
 * 关键是题目还难，作为一个对算法只能算熟悉的人来说（还不是特别熟悉），在那个时间、那个场景下，我是做不出来的
 * 所以产生恐惧心理了
 *
 * @author HK
 * @date 2021-01-28 14:01
 */
public class Main2 {

    static class Node {
        String value;
        Node next;

        public Node(String value, Node next) {
            this.value = value;
            this.next = next;
        }
    }

    /**
     * 将字符串数组转为Node节点链表
     */
    private static Node arrToNode(String[] str) {
        if (str == null) {
            throw new RuntimeException("入参不能为空");
        }
        Node head;
        if (str.length > 0) {
            head = new Node(str[0], null);
        } else {
            return null;
        }
        Node cur = head;
        for (int i = 1; i < str.length; i++) {
            cur.next = new Node(str[i], null);
            cur = cur.next;
        }
        return head;
    }

    /**
     * 打印节点信息
     */
    private static void printNode(Node node) {
        while (node != null) {
            System.out.print(node.value + " ");
            node = node.next;
        }
        System.out.println();
    }

    private static void printNode(Node head, String msg) {
        System.out.print(msg + ":  ");
        printNode(head);
    }

    /**
     * 假设节点为 1 -> 2 -> 3 -> 4，应该返回 4 -> 3 -> 2-> 1
     * 步骤是先把 1 和 2 反转(反转后为 2 -> 1 -> 3 -> 4)，然后把 2 -> 1 当作一个整体，将 3 放回 2 后面
     * 以此类推
     */
    private static Node reverseNode(Node head) {
        Node newHead = head;
        Node cur = head;
        while (cur.next != null) {
            // 第一次为 1 | 第二次为 1
            Node pre = cur;
            // 第一次 为 2 | 第二次 3
            Node next = cur.next;
            // 使得 1 -> 3 | 使得 1 -> 4
            pre.next = next.next;
            // 使得 2 -> 1 | 使得 3 -> 2
            next.next = newHead;
            // 将当前要处理node变为1
            cur = pre;
            // 保存新的头指针
            newHead = next;
            /// printNode(newHead);
        }
        return newHead;
    }


    /**
     * 以n为一组，反转数组
     * 例子：假设m=3. 链表 1 -> 2 -> 3 -> 4 -> 5 -> 6 -> 7 ->8
     * output: 3 -> 2 -> 1 ->6 ->5 ->4 -> 7 ->8
     * 思路如下：
     * 1. 先读取 n 个，形成一个新链表，如 1 -> 2 -> 3 -> null
     * 2. 然后反转成为 3 -> 2 -> 1 -> null。保存头节点和尾节点信息
     * 3. 进行下一轮
     *
     * @param head      待反转头节点
     * @param groupSize 一组大小
     * @return 反转后的数组
     */
    private static Node reverseNode(Node head, int groupSize) {
        Node node = head;
        printNode(head, "输入反转链表（分组为" + groupSize + "）");
        Node rootHead = null, rootTail = null;
        while (node != null) {
            int dealCount = groupSize - 1;
            Node newTail = new Node(node.value, null);
            Node newHead = newTail;
            while (dealCount > 0 && node.next != null) {
                dealCount--;
                node = node.next;
                newTail.next = new Node(node.value, null);
                newTail = newTail.next;
            }
            node = node.next;
            newTail = newHead;
            // 是否满足一个分组，满足则反序，否则不作处理
            if (dealCount == 0) {
                newHead = reverseNode(newHead);
            }
            /// printNode(newHead, "新链表");
            /// printNode(newHead, "根节点");
            /// printNode(newTail, "尾节点");
            if (rootHead == null) {
                rootHead = newHead;
            } else {
                rootTail.next = newHead;
            }
            rootTail = newTail;
            /// printNode(rootHead, "最终链表");
        }
        return rootHead;
    }

    public static void main(String[] args) {
        String[] strArr1 = new String[]{"1", "2", "3", "4", "5", "6", "7", "8"};
        String[] strArr2 = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
        String[] strArr3 = new String[]{};

        System.out.println("单纯的测试反转算法");
        printNode(arrToNode(strArr1));
        printNode(reverseNode(arrToNode(strArr1)));
        System.out.println("===============");

        // 官方给定例子
        printNode(reverseNode(arrToNode(strArr1), 3), "成功反转后输出");
        System.out.println("===============");
        printNode(reverseNode(arrToNode(strArr2), 3), "成功反转后输出");
        System.out.println("===============");
        printNode(reverseNode(arrToNode(strArr3), 3), "成功反转后输出");
        System.out.println("===============");

        ///  printNode(reverseNode(arrToNode(null), 3), "成功反转后输出");

        // 补充例子
        printNode(reverseNode(arrToNode(strArr1), 4), "成功反转后输出");
        System.out.println("===============");
        printNode(reverseNode(arrToNode(strArr2), 4), "成功反转后输出");
        System.out.println("===============");
        printNode(reverseNode(arrToNode(strArr3), 4), "成功反转后输出");

        // 两种极端情况
        printNode(reverseNode(arrToNode(strArr1), 10000000), "成功反转后输出");
        System.out.println("===============");
        printNode(reverseNode(arrToNode(strArr2), -111111), "成功反转后输出");
        System.out.println("===============");
    }
}
