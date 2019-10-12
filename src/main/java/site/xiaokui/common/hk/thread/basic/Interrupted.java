package site.xiaokui.common.hk.thread.basic;

import java.util.concurrent.TimeUnit;

/**
 * @author HK
 * @date 2018-07-22 00:20
 */
public class Interrupted {

    public static void main(String[] args) throws Exception {
        // sleepThread不停地尝试睡眠
        Thread sleepThread = new Thread(new SleepRunner(), "SleepThread");
        sleepThread.setDaemon(true);
        // busyThread不停地运行
        Thread busyThread = new Thread(new BusyRunner(), "BusyThread");
        busyThread.setDaemon(true);
        sleepThread.start();
        busyThread.start();
        // 休眠5秒，让sleepThread和busyThread充分运行
        TimeUnit.SECONDS.sleep(5);
        sleepThread.interrupt();
        busyThread.interrupt();
        System.out.println("SleepThread interrupted is " + sleepThread.isInterrupted());
        System.out.println("BusyThread interrupted is " + busyThread.isInterrupted());
        TimeUnit.SECONDS.sleep(2);
    }

    static class SleepRunner implements Runnable {
        @Override
        public void run() {
            while (true) {
                // 已经中断的不能再次中断，会抛出java.lang.InterruptedException: sleep interrupted
                ThreadState.SleepUtils.second(10);
            }
        }
    }

    static class BusyRunner implements Runnable {
        @Override
        public void run() {
            while (true) {
            }
        }
    }
}
