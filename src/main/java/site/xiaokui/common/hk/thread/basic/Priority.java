package site.xiaokui.common.hk.thread.basic;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author HK
 * @date 2018-07-21 14:52
 */
public class Priority {

    private static volatile boolean notStart = true;

    private static volatile boolean notEnd = true;

    public static void main(String[] args) throws Exception {
        List<Job> jobs = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            int priority = i < 5 ? Thread.MIN_PRIORITY : Thread.MAX_PRIORITY;
            Job job = new Job(priority);
            jobs.add(job);
            Thread thread = new Thread(job, "Thread:" + i);
            thread.setPriority(priority);
            thread.start();
        }
        notStart = false;
        TimeUnit.SECONDS.sleep(5);
        notEnd = false;
        for (Job job : jobs) {
            System.out.println("Job Priority :" + job.priority + ", Count :" + job.jobCount);
        }
    }

    static class Job implements Runnable {

        private int priority;

        private long jobCount;

        public Job(int priority) {
            this.priority = priority;
        }

        @Override
        public void run() {
            while (notStart) {
                // 线程让步
                Thread.yield();
            }
            while (notEnd) {
                // 让一步，记录加1
                Thread.yield();
                jobCount++;
            }
        }
    }
}
