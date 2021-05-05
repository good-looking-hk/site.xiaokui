package site.xiaokui.spring.web.server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 简单实现
 * @author HK
 * @date 2020-04-01 16:16
 */
public class HttpResponse {

    private String msg;

    public HttpResponse() {
    }

    public HttpResponse(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                // 做点什么
            }
        });
    }
}
