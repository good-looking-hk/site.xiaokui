package site.xiaokui.module.sys.music;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import site.xiaokui.blog.Constants;
import site.xiaokui.common.util.StringUtil;
import site.xiaokui.controller.AbstractController;
import site.xiaokui.entity.ResultEntity;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * TODO 有待后期完善
 * @author HK
 * @date 2019-06-08 20:41
 */
@Slf4j
@Controller("MusicController")
@RequestMapping(Constants.MUSIC_PREFIX)
public class MusicController extends AbstractController {

    @Autowired
    private MusicService musicService;

    @Autowired
    private MusicFileHelper musicFileHelper;

    private List<String> list;

    /**
     * 默认为 /sys/music
     */
    private static final String MUSIC_PREFIX = Constants.MUSIC_PREFIX;

    @Override
    protected String setPrefix() {
        return MUSIC_PREFIX;
    }

    @PostMapping("/{userId}/{dir}")
    @ResponseBody
    public ResultEntity list(@PathVariable Integer userId, @PathVariable String dir) {
        if (this.list != null) {
            return this.ok().put("data", list);
        }
        File musicDir = new File( musicFileHelper.getUserDir(userId) + "/" + dir);
        List<String> list = new ArrayList<>(4);
        if (musicDir.isDirectory()) {
            File[] files = musicDir.listFiles();
            for (File f : files) {
                list.add(f.getName());
            }
        }
        this.list = list;
        return this.ok().put("data", list);
    }

    /**
     * 如果成功保存进用户临时文件夹，那么返回上传的原始文件名称
     *
     * @param name 根据这个值保存不同的类型
     */
    @RequiresPermissions(MUSIC_PREFIX + ADD)
    @PostMapping("/upload")
    public ResultEntity upload(String name, MultipartFile file) {
        if (file == null || file.isEmpty() || file.getSize() > MusicConstant.MAX_MUSIC_UPLOAD_FILE) {
            return this.error("文件为空或过大");
        }
        if (this.isEmpty(name)) {
            return this.paramError(name);
        }
        String fullName = file.getOriginalFilename();
        String suffix = StringUtil.getSuffix(fullName);
        if (StringUtil.isEmpty(suffix)) {
            return this.error("不合法的文件名：" + file.getOriginalFilename());
        }
        if (MusicConstant.MP3_SUFFIX.equals(name)) {
            try {
                File mp3 = musicFileHelper.createTempFile(this.getUserId(), fullName);
                musicFileHelper.saveInputStream(file.getInputStream(), mp3);
            } catch (Exception e) {
                return this.error(e.getMessage());
            }
            return this.ok(fullName);
        } else if (MusicConstant.LRC_SUFFIX.equals(name)) {
            try {
                File lrc = musicFileHelper.createTempFile(this.getUserId(), fullName);
                musicFileHelper.saveInputStream(file.getInputStream(), lrc);
            } catch (Exception e) {
                return this.error(e.getMessage());
            }
            return this.ok(fullName);
        } else if (StringUtil.in(name, MusicConstant.IMGS_SUFFIX)) {
            try {
                File img = musicFileHelper.createTempFile(this.getUserId(), fullName);
                musicFileHelper.saveInputStream(file.getInputStream(), img);
            } catch (Exception e) {
                return this.error(e.getMessage());
            }
            return this.ok(fullName);
        } else {
            return this.paramError(name);
        }
    }

    /**
     * 根据之前上传的文件，进行规范保存
     * 其中mp3、lrc、img字段为服务器返回的字段，其他的为用户指定
     */
    @RequestMapping(MUSIC_PREFIX + ADD)
    @PostMapping(ADD)
    public ResultEntity add(String mp3, String lrc, String img, String author, Integer orderNum) {
        if (this.isEmpty(mp3)) {
            return this.error("mp3不能为空，mp3：" + mp3);
        }
        int userId = this.getUserId();
        File mp3File = musicFileHelper.findTempFile(userId, mp3);
        if (mp3File == null || !mp3File.exists()) {
            return this.error("mp3文件不存在，请上传");
        }
        SysMusic music = new SysMusic();
        music.setUserId(this.getUserId());
        music.setName(mp3);
        music.setAuthor(author);
        music.setOrderNum(orderNum);
        music.setCreateTime(new Date());
        musicService.insertIgnoreNullReturnKey(music);
        int id = music.getId();
        File target = musicFileHelper.locateFile(userId, String.valueOf(id), id + MusicConstant.MP3_SUFFIX);
        if (!mp3File.renameTo(target)) {
            throw new RuntimeException("转存文件失败：" + target.getName());
        }

        if (this.isNotEmpty(lrc)) {
            File lrcFile = musicFileHelper.findTempFile(userId, lrc);
            if (lrcFile == null || !lrcFile.exists()) {
                // 即使不存在，也没关系，下同
                return this.error("lrc文件不存在");
            } else {
                target = musicFileHelper.locateFile(userId, String.valueOf(id), id + MusicConstant.LRC_SUFFIX);
                if (!lrcFile.renameTo(target)) {
                    throw new RuntimeException("转存文件失败：" + target.getName());
                }
            }
        }

        if (this.isNotEmpty(img)) {
            File imgFile = musicFileHelper.findTempFile(userId, img);
            if (imgFile == null || !imgFile.exists()) {
                return this.error("图片文件不存在");
            } else {
                // 经测试，改变图片的后缀名，并不会影响显示（无论是浏览器，还是系统，类似魔数机制）
                target = musicFileHelper.locateFile(userId, String.valueOf(id), id + MusicConstant.PNG_SUFFIX);
                if (!imgFile.renameTo(target)) {
                    throw new RuntimeException("转存文件失败：" + target.getName());
                }
            }
        }
        return this.ok();
    }

    @Override
    public String edit(Integer id, Model model) {
        return null;
    }

    @Override
    public ResultEntity remove(Integer id) {
        return null;
    }
}
