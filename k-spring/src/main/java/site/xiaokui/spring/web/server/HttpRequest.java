package site.xiaokui.spring.web.server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * GET /index HTTP/1.1
 * User-Agent: Opera/9.80 (Windows NT 6.1; U; Edition IBIS; zh-cn) Presto/2.6.30 Version/10.63
 * Host: localhost:81
 * Accept: text/html, application/xml;q=0.9, application/xhtml+xml, image/png, image/jpeg, image/gif, image/x-xbitmap,
 * Accept-Language:zh-CN,zh;q=0.9,en;q=0.8
 * Accept-Charset:iso-8859-1,utf-8,utf-16,*;q=0.1
 * Accept-Encoding:deflate,gzip,x-gzip,identity,*;q=0
 * Connection:Keep-Alive
 * //注意，此处两行空格也为HTTP请求的一部分
 * @author HK
 * @date 2020-04-01 16:16
 */
public class HttpRequest {

    private String method;

    private String url;

    private String reqBody;

    public HttpRequest(String reqMsg) {
        int index = reqMsg.indexOf("\r\n");
        String firstLine = reqMsg.substring(index + 1);
        this.method = firstLine.split(" ")[0];
        this.url = firstLine.split(" ")[1];
        index = reqMsg.lastIndexOf("\r\n");
        if (index != -1) {
            this.reqBody = reqMsg.substring(index);
        } else {
            this.reqBody = "";
        }
    }

    public String getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public String getReqBody() {
        return reqBody;
    }

    public static void main(String[] args) {
        new HttpRequest("GET / HTTP/1.1\n" +
                "Host: localhost:8080\n" +
                "Connection: keep-alive\n" +
                "Cache-Control: max-age=0\n" +
                "Upgrade-Insecure-Requests: 1\n" +
                "User-Agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.97 Safari/537.36\n" +
                "Sec-Fetch-User: ?1\n" +
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3\n" +
                "Sec-Fetch-Site: none\n" +
                "Sec-Fetch-Mode: navigate\n" +
                "Accept-Encoding: gzip, deflate, br\n" +
                "Accept-Language: zh-CN,zh;q=0.9\n" +
                "Cookie: csrftoken=0e8Os3QpxhyfVsHTpMrBfkmooGyOEev7eX7KRIid3c19Y33J2LNHYvDBl8MlmaEP; Idea-3474fc56=03b64bba-1854-4868-9502-b80a04b5c807; Webstorm-4159b9af=792e5e9c-13d7-46de-ae6b-abcda5678fb9; UM_distinctid=1710a75b23b177-02578177077877-14291003-1fa400-1710a75b23c191; CNZZDATA1264398488=202190574-1567761083-null%7C1585737046\n");
    }

}