package site.xiaokui.module.sys.music;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import site.xiaokui.module.base.service.BaseService;

import java.io.File;
import java.io.InputStream;

/**
 * @author HK
 * @date 2019-06-08 20:53
 */
@Slf4j
@Service
public class MusicService extends BaseService<SysMusic> {

    @Value("${xiaokui.blogMusicPath}")
    private String baseMusicPath;

    public void saveMp3(Integer userId, InputStream is) {

    }


    public String[] getMusicList() {
        File dir = new File(baseMusicPath);
        if (!dir.exists() || !dir.isDirectory()) {
            throw new RuntimeException("非法的音乐路径：" + baseMusicPath + "！ 请检查！");
        }
        String[] musicList = null;
        String[] temp = dir.list();
        if (temp == null) {
            musicList = new String[0];
        } else {
            musicList = temp;
//            MUSIC_LIST = new String[temp.length];
//            for (int i = 0; i < temp.length; i++) {
////                MUSIC_LIST[i] = temp[i].substring(2);
//            }
        }
        return musicList;
    }
}
