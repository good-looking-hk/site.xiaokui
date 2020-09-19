package study.hk.blog;

/**
 * @author HK
 * @date 2018-09-06 19:15
 */
public class RedBlackTree<T extends Comparable<? super T>> {

    private RBTNode<T> header, nullNode;

    private RBTNode<T> current, parent, grand, great;

    private static final boolean isTrace = true;

    private static final int BLACK = 1;

    private static final int RED = 0;

    public RedBlackTree() {
        nullNode = new RBTNode<>(null);
        nullNode.left = nullNode.right = nullNode;
        header = new RBTNode<>(null);
        header.left = header.right = nullNode;
    }

    public void insert(T item) {
//        if (header.element == null) {
//            header.element = item;
//            return;
//        }
        current = parent = grand = header;
        nullNode.element = item;
        while (compare(item, current) != 0) {
            if (isTrace) {
                System.out.println("进行递归比较，当前current为" + current + ",item为" + item);
            }
            great = grand; grand = parent; parent = current;
            current = compare(item, current) < 0 ? current.left : current.right;

            // 是否有两个红孩子
            if (current.left.color == RED && current.right.color == RED) {
                if (isTrace) {
                    System.out.println("插入节点" + item + "后，需要进行平衡调整");
                }
                handleReorient(item);
            }
        }

        if (current != nullNode) {
            return ;
        }
        current = new RBTNode<>(item, nullNode, nullNode);

        if (compare(item, parent) < 0) {
            parent.left = current;
        } else {
            parent.left = current;
        }
        handleReorient(item);
    }

    private void handleReorient(T item) {
        current.color = RED;
        current.left.color = BLACK;
        current.right.color = BLACK;
        if (parent.color == RED) {
            grand.color = RED;
            if ((compare(item, grand) < 0) != compare(item, parent) < 0 ) {
                parent = rotate(item, grand);
            }
            current = rotate(item, great);
            current.color = BLACK;
        }
        header.right.color = BLACK;
    }

    private RBTNode<T> rotate(T item, RBTNode<T> parent) {
        if (compare(item, parent) < 0) {
            return parent.left = compare(item, parent.left) < 0 ? leftLeftRotation(parent.left) : leftRightRotation(parent.left);
        } else {
            return parent.right = compare(item, parent.right) < 0 ? rightLeftRotation(parent.right) : rightRightRotation(parent.right);
        }
    }

    private final int compare(T item, RBTNode<T> t) {
        if (t == header) {
            return 1;
        }
        return item.compareTo(t.element);
    }

    /**
     * LL：左左对应的情况(左单旋转)。
     */
    private RBTNode<T> leftLeftRotation(RBTNode<T> k2) {
        RBTNode<T> k1;
        k1 = k2.left;
        k2.left = k1.right;
        k1.right = k2;
        return k1;
    }

    /**
     * RR：右右对应的情况(右单旋转)。
     */
    private RBTNode<T> rightRightRotation(RBTNode<T> k1) {
        RBTNode<T> k2;
        k2 = k1.right;
        k1.right = k2.left;
        k2.left = k1;
        return k2;
    }

    /**
     * LR：左右对应的情况(左双旋转)。
     */
    private RBTNode<T> leftRightRotation(RBTNode<T> k3) {
        k3.left = rightRightRotation(k3.left);
        return leftLeftRotation(k3);
    }

    /**
     * RL：右左对应的情况(右双旋转)。
     */
    private RBTNode<T> rightLeftRotation(RBTNode<T> k1) {
        k1.right = leftLeftRotation(k1.right);
        return rightRightRotation(k1);
    }

    private void print(RBTNode<T> tree, T key, int direction) {
        if (tree != null) {
            if (direction == 0) {
                System.out.println(tree.element + " is root");
            } else {
                // tree是分支节点
                String dire = direction == 1 ? "right" : "left";
                System.out.println(tree.element + " is " + key + "'s " + dire + " child");
            }
            print(tree.left, tree.element, -1);
            print(tree.right, tree.element, 1);
        }
    }

    public void print() {
        if (header != null) {
            print(header, header.element, 0);
        }
    }

    private static class RBTNode<T> {
        T element;
        RBTNode<T> left;
        RBTNode<T> right;
        int color;
        RBTNode(T element) {
            this(element, null, null);
        }

        RBTNode(T element, RBTNode<T> left, RBTNode<T> right) {
            this.element = element;
            this.left = left;
            this.right = right;
            color = RedBlackTree.BLACK;
        }

        @Override
        public String toString() {
            String c = color == RED ? "红" : "黑";
            return "[key=" + element + ",color=" + c + "]";
        }
    }

    public static void main(String[] args) {
//        System.out.println(new Integer(-1).compareTo(null));
//        System.out.println(new Integer(0).compareTo(null));
//        System.out.println(new Integer(1).compareTo(null));

        int[] arr = {10, 85, 15, 70};
//        int[] arr = {10, 85, 15, 70, 20, 60, 30, 50, 65, 80, 90, 40, 5, 55};
        RedBlackTree<Integer> tree = new RedBlackTree<>();
        for (int i = 0; i < arr.length; i++) {
            System.out.print("添加" + arr[i] + " ");
            // 插入数据通过测试
            tree.insert(arr[i]);
        }
//        System.out.println("树的详细信息:");
//        tree.print();

    }

}
