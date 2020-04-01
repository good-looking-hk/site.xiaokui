package site.xiaokui.common.hk.thread.future;

import lombok.SneakyThrows;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author HK
 * @date 2020-03-29 15:41
 */
public class ParallelThread {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        CompletableFuture<String> server1 = CompletableFuture.supplyAsync(new Supplier<String>() {
            @SneakyThrows
            @Override
            public String get() {
                Thread.sleep(1000);
                System.out.println("服务器一返回消息");
                return "服务器一";
            }
        });
        System.out.println(11);
        CompletableFuture<String> server2 = CompletableFuture.supplyAsync(new Supplier<String>() {
            @SneakyThrows
            @Override
            public String get() {
                Thread.sleep(1000);
                System.out.println("服务器二返回消息");
                return "服务器二";
            }
        });
        System.out.println(22);
        CompletableFuture<String> result = server2.applyToEither(server1, new Function<String, String>() {
            @Override
            public String apply(String s) {
                return s + "返回消息更快";
            }
        });
        System.out.println(33);
        System.out.println(result.get());
        Thread.sleep(4000);
    }
}
