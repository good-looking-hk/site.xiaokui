package db.common;

/**
 * 测试接口编号总览
 * @author HK
 * @date 2021-03-17 17:05
 */
public class InterCode {

    /**
     * 测试服务器是否成功启动
     */
    public static final String IS_STARTED = "/100";

    /**
     * 测试mybatis是否正常运行
     */
    public static final String CAN_QUERY = "/200";

    /**
     * 增加单个用户
     */
    public static final String INSERT_USER = "/300";

    /**
     * 增加单个用户，同时新增余额信息，注解声明事务
     */
    public static final String INIT_USER = "/400";

    /**
     * 增加单个用户，同时新增余额信息，代码声明事务
     */
    public static final String INIT_USER1 = "/401";

    /**
     * 测试开启事务失败，代理非 public 方法
     */
    public static final String INIT_USER_FAIL1 = "/500";

    /**
     * 测试开启事务失败，自调用导致事务切面失效
     */
    public static final String INIT_USER_FAIL2 = "/501";
}
