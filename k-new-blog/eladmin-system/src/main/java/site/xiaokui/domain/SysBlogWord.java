package site.xiaokui.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.beetl.sql.core.annotatoin.AssignID;

import java.io.Serializable;

/**
 * @author HK
 * @date 2020-12-14 13:36
 */
public class SysBlogWord extends WordCounter implements Serializable {

    /**
     * 博客ID
     */
    @AssignID
    private Long blogId;

    public SysBlogWord() {
    }

    public SysBlogWord(Long blogId, WordCounter wordCounter) {
        this.blogId = blogId;
        this.chineseCount = wordCounter.chineseCount;
        this.englishCount = wordCounter.englishCount;
        this.numberCount = wordCounter.numberCount;
        this.otherCount = wordCounter.otherCount;
    }

    public Long getBlogId() {
        return blogId;
    }

    public void setBlogId(Long blogId) {
        this.blogId = blogId;
    }
}
