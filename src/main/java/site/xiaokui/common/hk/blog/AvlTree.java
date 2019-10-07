package site.xiaokui.common.hk.blog;

/**
 * @author HK
 * @date 2018-09-05 14:19
 */
public class AvlTree<T extends Comparable<? super T>> {

    private boolean isTrace = true;

    private static final int ALLOWED_IMBALANCE = 1;

    private AvlNode<T> root;

    public AvlNode<T> insert(T x) {
        if (root == null) {
            if (isTrace) {
                System.out.println("节点" + x + "直接作为根节点 ");
            }
            return root = new AvlNode<>(x, null, null);
        }
        return root = insert(root, x);
    }

    /**
     * 将T插入到AVL树中，并返回目标根节点
     */
    private AvlNode<T> insert(AvlNode<T> tree, T x) {
        if (tree == null) {
            if (isTrace) {
                System.out.println("返回新节点：" + x);
            }
            return new AvlNode<>(x, null, null);
        }
        int compareResult = x.compareTo(tree.key);
        if (compareResult < 0) {
            // x小于tree节点值，插入到tree的左子树
            if (isTrace) {
                System.out.println(x + "插入到" + tree + "的left");
            }
            tree.left = insert(tree.left, x);
        } else if (compareResult > 0) {
            // 将x插入到tree的右子树
            if (isTrace) {
                System.out.println(x + "插入到" + tree + "的right");
            }
            tree.right = insert(tree.right, x);
        } else {
            System.out.println("添加失败：不允许添加相同的节点！");
            return tree;
        }
        return balance(tree);
    }

    private AvlNode<T> balance(AvlNode<T> tree) {
        if (tree == null) {
            return null;
        }
        boolean hasRotated = false;
        if (height(tree.left) - height(tree.right) > ALLOWED_IMBALANCE) {
            hasRotated = true;
            System.out.print("节点" + tree + "的left高度比right高2 ");
            if (height(tree.left.left) >= height(tree.left.right)) {
                if (isTrace) {
                    System.out.println("节点" + tree + "需要左左单旋转,left" + tree.left + ",right" + tree.right);
                }
                tree = leftLeftRotation(tree);
            } else {
                if (isTrace) {
                    System.out.println("节点" + tree + "需要左右双旋转,left" + tree.left + ",right" + tree.right);
                }
                tree = leftRightRotation(tree);
            }
        } else if (height(tree.right) - height(tree.left) > ALLOWED_IMBALANCE) {
            hasRotated = true;
            System.out.print("节点" + tree + "的right高度比left高2 ");
            if (height(tree.right.right) >= height(tree.right.left)) {
                tree = rightRightRotation(tree);
            } else {
                tree = rightLeftRotation(tree);
            }
        }
        if (isTrace) {
            int newHeight = Math.max(height(tree.left), height(tree.right)) + 1;
            System.out.println((hasRotated ? "旋转后" : "无需旋转") +"，" + tree + "高度变为" + newHeight);
        }
        tree.height = Math.max(height(tree.left), height(tree.right)) + 1;
        return tree;
    }

    public AvlNode<T> remove(T x) {
        return remove(x, root);
    }

    private AvlNode<T> remove(T x, AvlNode<T> tree) {
        if (tree == null) {
            return null;
        }
        int compareResult = x.compareTo(tree.key);
        // 待删除的节点在"tree的左子树"中
        if (compareResult < 0) {
            tree.left = remove(x, tree.left);
        } else if (compareResult > 0) {
            tree.right = remove(x, tree.right);
        } else if (tree.left != null && tree.right != null) {
            tree.key = findMin(tree.right).key;
            tree.right = remove(tree.key, tree.right);
        } else {
            tree = (tree.left != null) ? tree.left : tree.right;
        }
        return balance(tree);
    }

    /**
     * LL：左左对应的情况(左单旋转)。
     */
    private AvlNode<T> leftLeftRotation(AvlNode<T> k2) {
        AvlNode<T> k1;
        k1 = k2.left;
        k2.left = k1.right;
        k1.right = k2;
        k2.height = Math.max(height(k2.left), height(k2.right)) + 1;
        k1.height = Math.max(height(k1.left), k2.height) + 1;
        return k1;
    }

    /**
     * RR：右右对应的情况(右单旋转)。
     */
    private AvlNode<T> rightRightRotation(AvlNode<T> k1) {
        AvlNode<T> k2;
        k2 = k1.right;
        k1.right = k2.left;
        k2.left = k1;
        k1.height = Math.max(height(k1.left), height(k1.right)) + 1;
        k2.height = Math.max(height(k2.right), k1.height) + 1;
        return k2;
    }

    /**
     * LR：左右对应的情况(左双旋转)。
     */
    private AvlNode<T> leftRightRotation(AvlNode<T> k3) {
        k3.left = rightRightRotation(k3.left);
        return leftLeftRotation(k3);
    }

    /**
     * RL：右左对应的情况(右双旋转)。
     */
    private AvlNode<T> rightLeftRotation(AvlNode<T> k1) {
        k1.right = leftLeftRotation(k1.right);
        return rightRightRotation(k1);
    }

    public AvlNode<T> findMin() {
        return findMin(root);
    }

    private AvlNode<T> findMin(AvlNode<T> node) {
        if (node == null) {
            return null;
        } else if (node.left == null) {
            return node;
        }
        return findMin(node.left);
    }

    private int height(AvlNode<T> tree) {
        return tree == null ? -1 : tree.height;
    }

    public int height() {
        return height(root);
    }

    private void print(AvlNode<T> tree, T key, int direction) {
        if (tree != null) {
            if (direction == 0) {
                System.out.println(tree.key + " is root");
            } else {
                // tree是分支节点
                String dire = direction == 1 ? "right" : "left";
                System.out.println(tree.key + " is " + key + "'s " + dire + " child");
            }
            print(tree.left, tree.key, -1);
            print(tree.right, tree.key, 1);
        }
    }

    public void print() {
        if (root != null) {
            print(root, root.key, 0);
        }
    }

    private static class AvlNode<T> {
        T key;
        int height;
        AvlNode<T> left;
        AvlNode<T> right;

        public AvlNode(T key, AvlNode<T> left, AvlNode<T> right) {
            this.key = key;
            this.left = left;
            this.right = right;
            this.height = 0;
        }

        @Override
        public String toString() {
            return "[key=" + key + ",height=" + height + "]";
        }
    }

    public static void main(String[] args) {
        int[] arr = {3, 2, 1, 4, 5, 6, 7, 16, 15, 14, 13, 12, 11, 10, 8, 9};
        AvlTree<Integer> tree = new AvlTree<>();

        for (int i = 0; i < arr.length; i++) {
            System.out.print("添加" + arr[i] + " ");
            // 插入数据通过测试
            tree.insert(arr[i]);
        }
        System.out.println("高度:" + tree.height());
        System.out.println("最小值:" + tree.findMin());
        System.out.println("树的详细信息:");
        tree.print();
        // 由于删除涉及的原理比较复杂，这里就跳过了，只给出代码实现
    }
}
