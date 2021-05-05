package thread;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * @author HK
 * @date 2021-04-25 15:44
 */
public class CasTest {

    /**
     * 这里的Integer不能大于127若大于127则不会出现ABA情况，也就是没有CAS
     * true	128
     * false	128
     */
    private static AtomicReference<Integer> atomicReference = new AtomicReference<>(100);

    private static AtomicStampedReference<Integer> atomicStampedReference = new AtomicStampedReference<>(100, 1);

    public static void main(String[] args) {

        AtomicInteger atomicInteger = new AtomicInteger(10);
        atomicInteger.incrementAndGet();

        System.out.println("===以下是ABA问题的产生===");

        new Thread(() -> {
            System.out.println(atomicReference.compareAndSet(100, 127) + "\t" + atomicReference.get());

            System.out.println(atomicReference.compareAndSet(127, 100) + "\t" + atomicReference.get());

        }, "t1").start();


        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(atomicReference.compareAndSet(100, 2019) + "\t" + atomicReference.get());

        }, "t2").start();

        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("===以下是ABA问题的解决===");

        new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + "\t第一次版本号：" + atomicStampedReference.getStamp());
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(atomicStampedReference.compareAndSet(100, 127, atomicStampedReference.getStamp(), atomicStampedReference.getStamp() + 1) + "\t" + atomicStampedReference.getReference());

            System.out.println(Thread.currentThread().getName() + "\t第二次版本号：" + atomicStampedReference.getStamp());
            System.out.println(atomicStampedReference.compareAndSet(127, 100, atomicStampedReference.getStamp(), atomicStampedReference.getStamp() + 1) + "\t" + atomicStampedReference.getReference());

            System.out.println(Thread.currentThread().getName() + "\t第三次版本号：" + atomicStampedReference.getStamp());

        }, "t3").start();


        new Thread(() -> {
            int stamp = atomicStampedReference.getStamp();

            System.out.println(Thread.currentThread().getName() + "\t第一次版本号：" + atomicStampedReference.getStamp());
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println(atomicStampedReference.compareAndSet(100, 2019, stamp, stamp + 1) + "\t" + atomicStampedReference.getReference());
            System.out.println(Thread.currentThread().getName() + "\t最新的版本号：" + atomicStampedReference.getStamp());
        }, "t4").start();
    }
}
