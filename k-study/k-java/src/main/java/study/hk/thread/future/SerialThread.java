package study.hk.thread.future;

import lombok.SneakyThrows;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author HK
 * @date 2020-03-29 15:28
 */
public class SerialThread {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(new Supplier<Integer>() {
            @SneakyThrows
            @Override
            public Integer get() {
                Thread.sleep(1000);
                System.out.println("任务一执行完毕");
                return 1;
            }
        }).thenApplyAsync(new Function<Integer, Integer>() {
            @SneakyThrows
            @Override
            public Integer apply(Integer integer) {
                Thread.sleep(1000);
                System.out.println("任务二执行完毕");
                return integer + 2;
            }
        });
        System.out.println(future.get());
    }
}
