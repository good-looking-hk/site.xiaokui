package study.hk.basic;

/**
 * @author HK
 * @date 2018-10-30 14:47
 */
public class Reference {

    static class Test {
        int value;
        Test(int value) {
            this.value = value;
        }
    }
    static final class Test1 {
        Integer value;
        public Test1(int value) {
            this.value = value;
        }
    }

    public static void main(String[] args) {
        int a = 1, b = 2, c = 3, d = 4;
        int[] test = {a, b, c, d};
        test[0] += 1;
        System.out.print(test[0] + " ");
        System.out.println(a);//2 1基本数据类型，说明是按值传递

        String aa = "1", bb = "2", cc = "3";
        String[] test1 = {aa, bb, cc};
        test1[0] = "11";//想当于new了一个对象
        System.out.print(test1[0] + " ");
        System.out.println(aa);//11 1非基本数据类型，说明是按址传递，test1[0]指向了一个新的地址

        Test aaa = new Test(1), bbb = new Test(2), ccc = new Test(3);
        Test[] test2 = {aaa, bbb, ccc};
        test2[0].value += 1;
        System.out.print(test2[0].value + " ");
        System.out.println(aaa.value);//2 2非基本数据类型，按址传递，但这里并没有new一个对象，两者指向的仍是同一地址

        Test1 aaaa = new Test1(1), bbbb = new Test1(2), cccc = new Test1(3);
        Test1[] test3 = {aaaa, bbbb, cccc};
        test3[0] = new Test1(11);
        System.out.print(test3[0].value + " ");
        System.out.println(aaaa.value);//11 1同String
        Integer temp1 = new Integer(10);
        Integer temp2 = new Integer(20);
        test3[1].value = temp1;
        temp1 = temp2;
        System.out.print(test3[1].value + " ");
        System.out.println(temp1);// 10 20这一步需要好好理解
    }
}
