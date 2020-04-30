package site.xiaokui.entity.wrapper;

import lombok.Getter;
import lombok.Setter;

/**
 * 用于解决Protostuff无法序列化List、Map等问题
 * @author HK
 * @date 2019-02-21 01:07
 */
public class SerializeDeserializeWrapper<T> {

    @Getter@Setter
    private T data;

    public static <T> SerializeDeserializeWrapper<T> builder(T data) {
        SerializeDeserializeWrapper<T> wrapper = new SerializeDeserializeWrapper<>();
        wrapper.setData(data);
        return wrapper;
    }
}
