package site.xiaokui.common.hk.offer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * CVTE算法题，大概是这么个意思：给定几个不重复的随机数和一个正整数，求出所有和为目标数的随机数集合，数可以重复使用
 * 例如[2,3] 6，那么2 2 2， 3 3， 6都是所求集合
 *
 * @author HK
 * @date 2018-12-20 11:45
 */
public class Cvte {

    List<List<Integer>> lists = new ArrayList<>();
    List<Integer> temp = new ArrayList<>();

    /**
     * 假设给定1,2,3,4,5,6,7 6。那么结果应为
     * 6
     * 15  24  33
     * 114  123  222
     * 1113  1122
     * 11112
     * 111111
     */
    public List<List<Integer>> find(int[] sets, int target) {
        if (sets.length == 0 || target <= 0) {
            return lists;
        }
        // 排序
        Arrays.sort(sets);
        int newLength = 0;
        // 去掉不必要的值
        for (int i = sets.length - 1; i >= 0; i--) {
            if (sets[i] <= target) {
                newLength = i;
                break;
            }
        }
        // 开始递归求解
        find(0, newLength, sets, target);
        // 对子列表排序
        for (List<Integer> l : lists) {
            Collections.sort(l);
        }
        // 标记需要清除的项
        boolean[] delIndex = new boolean[lists.size()];
        for (int i = 0; i < lists.size() - 1; i++) {
            for (int j = i + 1; j < lists.size(); j++) {
                if (lists.get(i).size() == lists.get(j).size()) {
                    int size = lists.get(i).size();
                    boolean isSame = lists.get(i).get(0).equals(lists.get(j).get(0))
                            && lists.get(i).get(size - 1).equals(lists.get(j).get(size - 1));
                    if (isSame) {
                        delIndex[j] = true;
                    }
                }
            }
        }
        // 清除重复项
        int delCount = 0;
        for (int i = 0; i < delIndex.length; i++) {
            if (delIndex[i]) {
                lists.remove(i - delCount);
                delCount++;
            }
        }
        return lists;
    }

    private void find(int start, int end, int[] sets, int target) {
        // 当前temp已经满足
        if (target == 0) {
            lists.add(new ArrayList<>(temp));
            System.out.println("添加记录temp" + temp);
            return;
        }
        // 不合法验证
        if (start > end || target < 0 || sets[start] > target) {
            return;
        }
        System.out.println("在" + start + "和" + end + "之间寻找" + target + ",当前temp" + temp);
        // 找到目标
        if (sets[start] == target) {
            temp.add(sets[start]);
            System.out.println("在" + start + "和" + end + "之间发现" + target + "，添加记录" + temp);
            lists.add(new ArrayList<>(temp));
            temp.remove(temp.size() - 1);
            return;
        }
        // 关键代码，自己走一遍理解更佳
        for (int i = 0; i <= end; i++) {
            temp.add(sets[i]);
            System.out.println("-此时参数i=" + i + ",target=" + target + ",目标数为" + (target - sets[i]) + "," + "temp为" + temp);
            find(i, end, sets, target - sets[i]);
            temp.remove(temp.size() - 1);
        }
    }

    public static void main(String[] args) {
        int[] sets = new int[]{1, 2, 3, 4, 5, 6, 7};
        Cvte cvte = new Cvte();
        cvte.find(sets, 6);
        System.out.println(cvte.lists);

        sets = new int[] {2, 3, 6};
        cvte = new Cvte();
        cvte.find(sets, 6);
        System.out.println(cvte.lists);
    }
}
