package study.hk.offer.niuke;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * @author HK
 * @date 2018-12-15 05:54
 */
public class SixtyOneToSixtySix {

    /**
     * 请实现两个函数，分别用来序列化和反序列化二叉树
     */
    static class SixtyOne {
        public class TreeNode {
            int val = 0;
            TreeNode left = null;
            TreeNode right = null;

            public TreeNode(int val) {
                this.val = val;
            }
        }

        int index = -1;   //计数变量

        String Serialize(TreeNode root) {
            StringBuilder sb = new StringBuilder();
            if (root == null) {
                sb.append("#,");
                return sb.toString();
            }
            sb.append(root.val + ",");
            sb.append(Serialize(root.left));
            sb.append(Serialize(root.right));
            return sb.toString();
        }

        TreeNode Deserialize(String str) {
            index++;
            //int len = str.length();
            //if(index >= len){
            //    return null;
            // }
            String[] strr = str.split(",");
            TreeNode node = null;
            if (!strr[index].equals("#")) {
                node = new TreeNode(Integer.valueOf(strr[index]));
                node.left = Deserialize(str);
                node.right = Deserialize(str);
            }
            return node;
        }
    }

    /**
     * 给定一棵二叉搜索树，请找出其中的第k小的结点。例如， （5，3，7，2，4，6，8）    中，按结点数值大小顺序第三小结点的值为4。
     */
    static class SixtyTwo {
        public class TreeNode {
            int val = 0;
            TreeNode left = null;
            TreeNode right = null;

            public TreeNode(int val) {
                this.val = val;
            }
        }

        int index = 0; //计数器

        TreeNode KthNode(TreeNode root, int k) {
            if (root != null) { //中序遍历寻找第k个
                TreeNode node = KthNode(root.left, k);
                if (node != null)
                    return node;
                index++;
                if (index == k)
                    return root;
                node = KthNode(root.right, k);
                if (node != null)
                    return node;
            }
            return null;
        }

    }

    /**
     * 如何得到一个数据流中的中位数？如果从数据流中读出奇数个数值，那么中位数就是所有数值排序之后位于中间的数值。
     * 如果从数据流中读出偶数个数值，那么中位数就是所有数值排序之后中间两个数的平均值。我们使用Insert()方法读取数据流，
     * 使用GetMedian()方法获取当前读取数据的中位数。
     */
    static class SixtyThree {
        /***********方式一、用两个优先队列来模拟两个堆---主要思路************************
         1.先用java集合PriorityQueue来设置一个小顶堆和大顶堆，大顶堆需要先重写一下里面的比较器

         2.主要的思想是：因为要求的是中位数，那么这两个堆，大顶堆用来存较小的数，从大到小排列；
         小顶堆存较大的数，从小到大的顺序排序，
         显然中位数就是大顶堆的根节点与小顶堆的根节点和的平均数。

         保证：小顶堆中的元素都大于等于大顶堆中的元素，所以每次塞值，并不是直接塞进去，而是从另一个堆中poll出一个最大（最小）的塞值

         3.当数目为偶数的时候，将这个值插入大顶堆中，再将大顶堆中根节点（即最大值）插入到小顶堆中；
         当数目为奇数的时候，将这个值插入小顶堆中，再讲小顶堆中根节点（即最小值）插入到大顶堆中；

         这样就可以保证，每次插入新值时，都保证小顶堆中值大于大顶堆中的值，并且都是有序的。

         4.由于第一个数是插入到小顶堆中的，所以在最后取中位数的时候，若是奇数，就从小顶堆中取即可。
         这样，当count为奇数的时候，中位数就是小顶堆的根节点；当count为偶数的时候，中位数为大顶堆和小顶堆两个根节点之和的平均数

         5.例如，传入的数据为：[5,2,3,4,1,6,7,0,8],那么按照要求，输出是"5.00 3.50 3.00 3.50 3.00 3.50 4.00 3.50 4.00 "
         a.那么，第一个数为5，count=0,那么存到小顶堆中，
         步骤是：先存到大顶堆；然后弹出大顶堆root，就是最大值给小顶堆，第一次执行完，就是小顶堆为5，count+1=1；
         此时若要输出中位数，那么就是5.0，因为直接返回的是小顶堆最小值(第一次塞入到小顶堆中，是从大顶堆中找到最大的给他的)



         b.继续传入一个数为2，那么先存到小顶堆中，将小顶堆最小值弹出给大顶堆，即2，那么这次执行完，小顶堆为5，大顶堆为2，count+1=2
         此时若要输出中位数，因为是偶数，那么取两个头的平均值，即(5+2)/2=3.5(第二次塞入到大顶堆中，是从小顶堆中找到最小的给他的)

         c.继续传入一个数为3，那么此时count为偶数，那么执行第一个if，先存到大顶堆中，大顶堆弹出最大值，那么3>2，就是弹出3
         3存到小顶堆中，那么此时小顶堆为3,5，大顶堆为2，count+1=3(第三次塞入到小顶堆中，是从大顶堆中找到最大的给他的)
         此时若要输出中位数，因为是奇数，那么取小顶堆的最小值，即3.0

         d.继续传入一个数为4，先存到小顶堆中，小顶堆此时为3,4，5,弹出最小值为3，给大顶堆
         此时大顶堆为3,2,小顶堆为4,5，(第四次塞入到小顶堆中，是从大顶堆中找到最大的给他的)
         此时若要输出中位数，因为是偶数，那么取两个头的平均值,即(3+4)/2=3.5

         e.依次类推。。。


         ******************************************/

        /***************方式二、ArrayList***********************
         用ArrayList来存输入的数据流，然后每次用Collections.sort(list)来保证数据流有序，然后再取中位数
         思想非常简单，但是每次都要进行排序，时间复杂度可想而知
         ****************************************/

        /***************方式三、插入排序，插入到对应的位置***********************
         LinkedList<Integer> data = new LinkedList<Integer>();
         public void Insert(Integer num) {
         for (int i = data.size() - 1; i >= 0 ; i--) {
         if (num >= data.get(i)){
         data.add(i+1,num);
         return;
         }
         }
         data.addFirst(num);
         }
         ****************************************/

        int count = 0;
        private PriorityQueue<Integer> minHeap = new PriorityQueue<>();//默认是小根堆
        private PriorityQueue<Integer> maxHeap = new PriorityQueue<Integer>(15, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o2 - o1;
            }
        });

        public void Insert(Integer num) {
            if (count % 2 == 0) {
                //数目为偶数时，插入到小根堆中
                maxHeap.offer(num);
                int filteredMaxNum = maxHeap.poll();
                minHeap.offer(filteredMaxNum);
            } else {
                //数目为奇数时，插入到大根堆中
                minHeap.offer(num);
                int filteredMinNum = minHeap.poll();
                maxHeap.offer(filteredMinNum);
            }
            count++;
        }

        public Double GetMedian() {
            if (count % 2 == 0) {
                return new Double((minHeap.peek() + maxHeap.peek())) / 2;
            } else {
                return new Double(minHeap.peek());
            }
        }
    }

    /**
     * 给定一个数组和滑动窗口的大小，找出所有滑动窗口里数值的最大值。
     * 例如，如果输入数组{2,3,4,2,6,2,5,1}及滑动窗口的大小3，那么一共存在6个滑动窗口，
     * 他们的最大值分别为{4,4,6,6,6,5}； 针对数组{2,3,4,2,6,2,5,1}的滑动窗口有以下6个：
     * {[2,3,4],2,6,2,5,1}， {2,[3,4,2],6,2,5,1}， {2,3,[4,2,6],2,5,1}，
     * {2,3,4,[2,6,2],5,1}， {2,3,4,2,[6,2,5],1}， {2,3,4,2,6,[2,5,1]}。
     */
    static class SixtyFour {

        public ArrayList<Integer> maxInWindows(int[] num, int size) {
            ArrayList<Integer> res = new ArrayList<>();
            if (size == 0) return res;
            int begin;
            ArrayDeque<Integer> q = new ArrayDeque<>();
            for (int i = 0; i < num.length; i++) {
                begin = i - size + 1;
                if (q.isEmpty())
                    q.add(i);
                else if (begin > q.peekFirst())
                    q.pollFirst();

                while ((!q.isEmpty()) && num[q.peekLast()] <= num[i])
                    q.pollLast();
                q.add(i);
                if (begin >= 0)
                    res.add(num[q.peekFirst()]);
            }
            return res;
        }
    }

    public static void main(String[] args) {

    }
}
