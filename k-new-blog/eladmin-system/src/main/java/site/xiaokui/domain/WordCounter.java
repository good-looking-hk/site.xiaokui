package site.xiaokui.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 有效字符为 汉字数 + 英文单词数
 *
 * @author HK
 * @date 2020-12-14 13:37
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class WordCounter implements Serializable {

    /**
     * 汉字数
     */
    public int chineseCount;

    /**
     * 英文单词数
     */
    public int englishCount;

    /**
     * 数字数
     */
    public int numberCount;

    /**
     * 其他字符数
     */
    public int otherCount;
}
