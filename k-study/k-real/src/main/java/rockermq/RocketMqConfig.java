package rockermq;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author HK
 * @date 2021-02-01 15:21
 */
public class RocketMqConfig {

    public static final String NAME_SERVER = "113.110.224.192:9876";

    public static final String LOCAL_NAME_SERVER = "10.10.19.51:9876";

    public static final String OTHER_NAME_SERVER = "10.10.19.51:10911";

    public static final String LOCALHOST = "localhost:9876";
}
