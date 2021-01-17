package site.xiaokui.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import site.xiaokui.service.dto.SaveBlogDto;
import site.xiaokui.util.MarkdownWordCounter;

import java.io.File;
import java.util.Date;

/**
 * @author HK
 * @date 2018-06-26 20:38
 */
@ToString
@Getter@Setter
public class UploadBlog {
    private Long userId;
    private String blogSpace;
    private String name;
    private String dir;
    private Integer orderNum;
    private String suffix;
    private Integer createDate;
    private Long lastModified;
    private String errorInfo;
    private File uploadFile;
    private WordCounter wordCounter;

    public SaveBlogDto toSaveBlogDto() {
        SaveBlogDto dto = new SaveBlogDto();
        dto.setUserId(userId);
        dto.setDir(dir);
        dto.setName(name);
        dto.setOrderNum(orderNum);
        dto.setCreateDate(createDate.toString());
        dto.setChineseCount(wordCounter.chineseCount);
        dto.setEnglishCount(wordCounter.englishCount);
        dto.setNumberCount(wordCounter.numberCount);
        dto.setOtherCount(wordCounter.otherCount);
        return dto;
    }
}