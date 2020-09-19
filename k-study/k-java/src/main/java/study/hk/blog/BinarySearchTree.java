package study.hk.blog;

/**
 * @author HK
 * @date 2018-08-31 15:02
 */
public class BinarySearchTree<E extends Comparable<? super E>> {

    private BinaryNode<E> root;

    public BinarySearchTree() {
        root = null;
    }

    public void clearTree() {
        root = null;
    }

    public boolean isEmpty() {
        return root == null;
    }

    public void insert(E e) {
        root = insert(e, root);
    }

    public boolean contains(E e) {
        return contains(e, root);
    }

    public void remove(E e) {
        root = remove(e, root);
    }

    public void inOrderTraverse() {
        inOrderTraverse(root);
    }

    /**
     * 先序遍历：根节点->左子树->右子树
     */
    public void preOrderTraverse(BinaryNode<E> node) {
        System.out.print(node.element + " ");
        if (node.left != null) {
            preOrderTraverse(node.left);
        }
        if (node.right != null) {
            preOrderTraverse(node.right);
        }
    }

    /**
     * 中序遍历，输出有序序列：左子树->根节点->右子树
     */
    public void inOrderTraverse(BinaryNode<E> node) {
        if (node.left != null) {
            inOrderTraverse(node.left);
        }
        System.out.print(node.element + " ");
        if (node.right != null) {
            inOrderTraverse(node.right);
        }
    }

    /**
     * 后序遍历：左子树->右子树->根节点
     */
    public void postOrderTraverse(BinaryNode<E> node) {
        if (node.left != null) {
            postOrderTraverse(node.left);
        }
        if (node.right != null) {
            postOrderTraverse(node.right);
        }
        System.out.print(node.element + " ");
    }

    public E findMin() {
        if (isEmpty()) {
            throw new RuntimeException("空树");
        }
        return findMin(root).element;
    }

    public E findMax() {
        if (isEmpty()) {
            throw new RuntimeException("空树");
        }
        return findMax(root).element;
    }

    private BinaryNode<E> insert(E e, BinaryNode<E> node) {
        if (node == null) {
            return new BinaryNode<>(e);
        }
        int compareResult = e.compareTo(node.element);
        if (compareResult < 0) {
            node.left = insert(e, node.left);
        } else if (compareResult > 0) {
            node.right = insert(e, node.right);
        }
        return node;
    }

    private boolean contains(E e, BinaryNode<E> node) {
        if (node == null || e == null) {
            return false;
        }
        int compareResult = e.compareTo(node.element);
        //e小于，继续跟node的左子树比较
        if (compareResult < 0) {
            return contains(e, node.left);
        } else if (compareResult > 0) {
            return contains(e, node.right);
        }
        return true;
    }

    private BinaryNode<E> remove(E e, BinaryNode<E> node) {
        if (node == null) {
            return null;
        }
        int compareResult = e.compareTo(node.element);
        if (compareResult < 0) {
            node.left = remove(e, node.left);
        } else if (compareResult > 0) {
            node.right = remove(e, node.right);
        } else if (node.left != null && node.right != null) {
            // 把最小的节点与目标节点交换，再删除目标节点
            node.element = findMin(node.right).element;
            node.right = remove(node.element, node.right);
        } else {
            node = (node.left != null) ? node.left : node.right;
        }
        return node;
    }

    /**
     * 递归形式
     */
    private BinaryNode<E> findMin(BinaryNode<E> node) {
        if (node == null) {
            return null;
        } else if (node.left == null) {
            return node;
        }
        return findMin(node.left);
    }

    /**
     * 非递归形式
     */
    private BinaryNode<E> findMax(BinaryNode<E> node) {
        if (node != null) {
            while (node.right != null) {
                node = node.right;
            }
        }
        return node;
    }

    private static class BinaryNode<E> {
        E element;
        BinaryNode<E> left;
        BinaryNode<E> right;

        BinaryNode(E node) {
            this(node, null, null);
        }

        BinaryNode(E node, BinaryNode<E> lt, BinaryNode<E> rt) {
            element = node;
            left = lt;
            right = rt;
        }
    }

    public static void main(String[] args) {
        BinarySearchTree<Integer> bst = new BinarySearchTree<>();
        bst.insert(6);
        bst.insert(8);
        bst.insert(9);
        bst.insert(7);
        bst.insert(3);
        bst.insert(1);
        bst.insert(2);
        bst.insert(4);
        bst.insert(0);
        bst.insert(5);
        bst.insert(4);
        bst.insert(1);
        bst.preOrderTraverse(bst.root);//6 3 1 0 2 4 5 8 7 9
        System.out.println();
        bst.inOrderTraverse();//0 1 2 3 4 5 6 7 8 9
        System.out.println();
        bst.postOrderTraverse(bst.root);//0 2 1 5 4 3 7 9 8 6
        System.out.println("\n" + bst.findMax());//9
        System.out.println(bst.findMin());//0
        System.out.println(bst.contains(1));//true
        bst.remove(3);
        bst.inOrderTraverse();//0 1 2 4 5 6 7 8 9
    }
}