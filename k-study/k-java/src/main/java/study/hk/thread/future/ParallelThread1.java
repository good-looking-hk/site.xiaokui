package study.hk.thread.future;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author HK
 * @date 2020-03-29 16:03
 */
public class ParallelThread1 {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        CompletableFuture f1 = CompletableFuture.runAsync(()->{
            try {
                TimeUnit.MILLISECONDS.sleep(100);
                System.out.println("execute f1");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        CompletableFuture f2 = CompletableFuture.runAsync(()->{
            try {
                TimeUnit.MILLISECONDS.sleep(1000);
                System.out.println("execute f2");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        CompletableFuture all =  CompletableFuture.allOf(f1,f2);
        all.get();
        System.out.println("execute all");
    }
}
