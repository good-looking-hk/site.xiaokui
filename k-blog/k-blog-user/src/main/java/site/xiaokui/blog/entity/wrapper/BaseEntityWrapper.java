package site.xiaokui.blog.entity.wrapper;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;

import java.util.List;

/**
 * JSON是封装依赖于HuTool，原因是它的API简单上手，虽然性能没有fastjson那么好
 * 将实体转化为json对象，实现对于特定属性的添加处理(自动剔除null值)
 * @author HK
 * @date 2018-06-09 20:22
 */
public abstract class BaseEntityWrapper<T> {

    protected JSONObject jsonObject;

    protected JSONArray jsonArray;

    protected BaseEntityWrapper(List<T> list) {
        jsonArray = new JSONArray();
        for (T t : list) {
            wrap(t);
        }
    }

    protected BaseEntityWrapper(T t) {
        jsonObject = new JSONObject(t);
        wrap();
    }

    protected String getStr(String key) {
        return jsonObject.getStr(key);
    }

    protected Object get(Object key) {
        return jsonObject.get(key);
    }

    protected Integer getInt(String key) {
        return jsonObject.getInt(key);
    }

    protected JSONObject put(String key, Object value) {
        if (value == null) {
            value = "-";
        }
        return jsonObject.put(key, value);
    }

    protected JSONObject remove(String key) {
        jsonObject.remove(key);
        return this.jsonObject;
    }

    /**
     * 由子类调用重写，包装单个实体
     */
    protected abstract void wrap();

    /**
     * 由子类重写，包装实体列表
     * @param t 需要包装实体
     */
    protected abstract void wrap(T t);

    @Override
    public String toString() {
        if (jsonArray != null) {
            return jsonArray.toString();
        }
        return jsonObject.toString();
    }

    /**
     * 大部分情况下只需返回jsonArray
     */
    public JSON toJson() {
        return jsonArray != null ? jsonArray : jsonObject;
    }
}
