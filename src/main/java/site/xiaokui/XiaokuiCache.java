package site.xiaokui;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import site.xiaokui.module.base.entity.SysConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author HK
 * @date 2019-09-26 11:06
 */
@Component
public class XiaokuiCache implements ApplicationRunner {

    private Map<String, String> cacheMap = new HashMap<>(8);

    @Autowired
    private SQLManager sqlManager;

    @Override
    public void run(ApplicationArguments args) {
        prepareData();
    }

    private void prepareData() {
        Query<SysConfig> query = sqlManager.query(SysConfig.class);
        List<SysConfig> list = query.select();
        if (list == null) {
            throw new RuntimeException("读取系统配置失败");
        }
        for (SysConfig config : list) {
            this.cacheMap.put(config.getName(), config.getValue());
        }
    }

    public void refreshConfigCache() {
        if (this.cacheMap != null) {
            this.cacheMap.clear();
        }
        prepareData();
    }

    public String getKey(String key) {
        return this.cacheMap.get(key);
    }

    public String getIndex() {
        return this.cacheMap.get("index");
    }

    public String getCompany() {
        return this.cacheMap.get("company");
    }

    public boolean showResume() {
        return "true".equals(this.cacheMap.get("showResume"));
    }

    public boolean showAbout() {
        return "true".equals(this.cacheMap.get("showAbout"));
    }
}
