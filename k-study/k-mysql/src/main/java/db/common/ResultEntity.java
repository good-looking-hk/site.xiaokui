package db.common;

import java.util.HashMap;

/**
 * @author HK
 * @date 2021-03-17 17:09
 */
public class ResultEntity extends HashMap<String, Object> {

    public ResultEntity() {
    }

    public ResultEntity(String code, String msg) {
        this.put("code", code);
        this.put("msg", msg);
    }

    public static ResultEntity ok() {
        ResultEntity result = new ResultEntity();
        result.put("code", "0");
        result.put("msg", "success");
        return result;
    }

    public static ResultEntity ok(Object data) {
        ResultEntity result = new ResultEntity();
        result.put("code", "0");
        result.put("msg", "success");
        result.put("data", data);
        return result;
    }

    public static ResultEntity error(String msg) {
        ResultEntity result = new ResultEntity();
        result.put("code", "-1");
        result.put("msg", msg);
        return result;
    }

    public static ResultEntity failed(String msg) {
        ResultEntity result = new ResultEntity();
        result.put("code", "-1");
        result.put("msg", msg);
        return result;
    }
}
