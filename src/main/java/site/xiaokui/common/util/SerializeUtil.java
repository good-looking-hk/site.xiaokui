package site.xiaokui.common.util;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import site.xiaokui.module.base.entity.wrapper.SerializeDeserializeWrapper;
import site.xiaokui.module.sys.blog.entity.SysBlog;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 这个相对于传统序列化而言，占空间小，且做了缓存
 * @author HK
 * @date 2018-10-07 15:00
 */
@Slf4j
public class SerializeUtil {

    private static final Set<Class<?>> WRAPPER_SET = new HashSet<>();

    private static final Class<SerializeDeserializeWrapper> WRAPPER_CLASS = SerializeDeserializeWrapper.class;

    /**
     * 序列化/反序列化包装类 Schema 对象
     */
    private static final Schema<SerializeDeserializeWrapper> WRAPPER_SCHEMA = RuntimeSchema.createFrom(WRAPPER_CLASS);

    /**
     * Schema缓存
     */
    private static Map<Class<?>, Schema<?>> cachedSchema = new ConcurrentHashMap<>(4);

    static {
        cachedSchema.put(SysBlog.class, RuntimeSchema.createFrom(SysBlog.class));

        WRAPPER_SET.add(List.class);
        WRAPPER_SET.add(ArrayList.class);
        WRAPPER_SET.add(CopyOnWriteArrayList.class);
        WRAPPER_SET.add(LinkedList.class);
        WRAPPER_SET.add(Stack.class);
        WRAPPER_SET.add(Vector.class);

        WRAPPER_SET.add(Map.class);
        WRAPPER_SET.add(HashMap.class);
        WRAPPER_SET.add(TreeMap.class);
        WRAPPER_SET.add(Hashtable.class);
        WRAPPER_SET.add(SortedMap.class);

        WRAPPER_SET.add(Object.class);
    }

    @SuppressWarnings("unchecked")
    public static <T> byte[] serialize(T obj) {
        Class<T> cls = (Class<T>) obj.getClass();
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try {
            Object o = obj;
            Schema schema = WRAPPER_SCHEMA;
            if (!WRAPPER_SET.contains(cls)) {
                schema = getSchema(cls);
            } else {
                o = SerializeDeserializeWrapper.builder(obj);
            }
            return ProtostuffIOUtil.toByteArray(o, schema, buffer);
        } catch (Exception e) {
            log.error("无法序列化对象" + obj);
            throw new IllegalStateException(e.getMessage(), e);
        } finally {
            buffer.clear();
        }
    }

    public static <T> T deserialize(byte[] data, Class<T> cls) {
        try {
            if (!WRAPPER_SET.contains(cls)) {
                T message = cls.newInstance();
                Schema<T> schema = getSchema(cls);
                ProtostuffIOUtil.mergeFrom(data, message, schema);
                return message;
            } else {
                SerializeDeserializeWrapper<T> wrapper = new SerializeDeserializeWrapper<>();
                ProtostuffIOUtil.mergeFrom(data, wrapper, WRAPPER_SCHEMA);
                return wrapper.getData();
            }
        } catch (Exception e) {
            log.error("无法反序列化对象[byte:{},cls:{}]", data, cls);
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> Schema<T> getSchema(Class<T> cls) {
        Schema<T> schema = (Schema<T>) cachedSchema.get(cls);
        if (schema == null) {
            schema = RuntimeSchema.createFrom(cls);
            cachedSchema.put(cls, schema);
        }
        return schema;
    }

    /**
     * 一个简单的例子，便于理解序列化
     */
    @SuppressWarnings("unchecked")
    private static Object serialize1(Serializable o) {
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
