package study.hk.jvm;

import java.util.ArrayList;
import java.util.List;

/**
 * VM:-Xms20M -Xmx20m -XX:+HeapDumpOnOutOfMemoryError
 * 在Java语言中，可作为GC Roots的对象包括下面几种：
 * 1.虚拟机栈（栈帧中的本地变量表）中引用的对象
 * 2.方法区中静态属性引用的对象
 * 3.方法区中常量引用的对象
 * 4.本地方法栈中JNI（即一般说的Native方法）引用的对象
 * @author HK
 * @date 2018-10-12 21:39
 */
public class HeapOOM {

    public static void main(String[] args) {
        List<Object> list = new ArrayList<>();
        while (true) {
            list.add(new Object());
        }
    }
}
