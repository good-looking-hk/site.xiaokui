package site.xiaokui.module.base.entity;

import cn.hutool.json.JSONObject;

import java.util.HashMap;

/**
 * @author HK
 * @date 2018-05-20 20:29
 */
public class ResultEntity extends HashMap<String, Object> {

    public ResultEntity(Integer code, String msg) {
        super(4);
        put("code", code);
        put("msg", msg);
    }

    public static ResultEntity ok() {
        return new ResultEntity(200, "ok");
    }

    public static ResultEntity ok(String msg) {
        return new ResultEntity(200, msg);
    }


    public static ResultEntity failed() {
        return new ResultEntity(500, "failed");
    }

    public static ResultEntity failed(String msg) {
        return new ResultEntity(500, msg);
    }

    public static ResultEntity error() {
        return new ResultEntity(500, "异常错误");
    }

    public static ResultEntity error(String msg) {
        return new ResultEntity(500, msg);
    }

    public static ResultEntity paramError(Object... objects) {
        if (objects == null) {
            return paramError();
        }
        StringBuilder str = new StringBuilder("参数错误:[");
        for (Object o : objects) {
            str.append(o).append(",");
        }
        str.deleteCharAt(str.length() - 1);
        str.append("]");
        return error(str.toString());
    }

    public static ResultEntity paramError() {
        return error("参数错误:");
    }

    @Override
    public ResultEntity put(String key, Object value) {
        super.put(key, value);
        return this;
    }

    @Override
    public String toString() {
        return new JSONObject(this).toString();
    }
}
