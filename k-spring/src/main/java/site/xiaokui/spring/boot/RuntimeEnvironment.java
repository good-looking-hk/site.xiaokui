package site.xiaokui.spring.boot;

import site.xiaokui.spring.core.util.StringUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author HK
 * @date 2020-04-01 13:26
 */
public class RuntimeEnvironment implements Environment {

    private Map<String, String> propertyMap = new HashMap<>(8);

    public RuntimeEnvironment() {
    }

    public RuntimeEnvironment(String[] args, String scanPackage) {
        // TODO to deal with args
        readProperties(scanPackage);
    }

    private void readProperties(String scanPackage) {
        URL url = StringUtil.checkPackage(scanPackage);

        File classpath = new File(url.getFile());
        File[] listFiles = classpath.listFiles();
        for (File file : listFiles) {
            if (file.isFile() && file.getName().endsWith(".properties")) {
                Properties properties = new Properties();
                try {
                    properties.load(new FileInputStream(file));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                for (Map.Entry entry : properties.entrySet()) {
                    propertyMap.put(entry.getKey().toString(), entry.getValue().toString());
                }
            }
        }
    }

    @Override
    public String getProperty(String key) {
        return propertyMap.get(key);
    }

    @Override
    public void setProperty(String key, String value) {
        propertyMap.put(key, value);
    }
}
