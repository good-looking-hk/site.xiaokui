package site.xiaokui.spring.boot;

/**
 * @author HK
 * @date 2020-04-01 13:26
 */
public interface Environment {

    String getProperty(String key);

    void setProperty(String key, String value);
}
