package site.xiaokui.common.util;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import lombok.AllArgsConstructor;
import lombok.ToString;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;

import java.io.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author HK
 * @date 2018-10-07 15:00
 */
public class SerializeUtil {

    /**
     * Schema缓存
     */
    private static Map<Class<?>, Schema<?>> cachedSchema = new ConcurrentHashMap<>(4);

    public static <T> T deserialize(byte[] data, Class<T> cls) {
        try {
            Schema<T> schema = getSchema(cls);
            T message = schema.newMessage();
            ProtostuffIOUtil.mergeFrom(data, message, schema);
            return message;
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> byte[] serialize(T obj) {
        Class<T> cls = (Class<T>) obj.getClass();
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try {
            Schema<T> schema = getSchema(cls);
            return ProtostuffIOUtil.toByteArray(obj, schema, buffer);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        } finally {
            buffer.clear();
        }
    }

    @SuppressWarnings("uncheck")
    private static <T> Schema<T> getSchema(Class<T> cls) {
        Schema<T> schema = (Schema<T>) cachedSchema.get(cls);
        if (schema == null) {
            schema = RuntimeSchema.createFrom(cls);
            if (schema != null) {
                cachedSchema.put(cls, schema);
            }
        }
        return schema;
    }

    /**
     * 一个简单的例子，便于理解序列化
     */
    @SuppressWarnings("uncheck")
    public static Object serialize1(Serializable o) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(o);
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
            return ois.readObject();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 手动序列化
     */
    public static void main(String[] args) throws Exception {
        Test test = new Test(1, "20");
        Object object = serialize1(test);
        System.out.println(object);

        ArrayList<Test> list = new ArrayList<>();
        list.add(test);
        list.add(new Test(2, "40"));
        Object object1 = serialize(list);
        System.out.println(object1);
        System.out.println(object1.getClass());

        ArrayList object2 = (ArrayList<Test>) serialize1(list);
        System.out.println(object2);
        System.out.println(object2.getClass());
    }

    @ToString
    @AllArgsConstructor
    static class Test implements Serializable {
        Integer id;
        String age;
    }
}
