package site.xiaokui.service.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * @author HK
 * @date 2020-12-14 15:56
 */
@Data
public class SaveBlogDto implements Serializable {

    private Long userId;

    @NotBlank
    private String dir;

    @NotBlank
    private String name;

    @Min(value = 1, message = "序号不能小于1")
    private Integer orderNum;

    @Size(min = 8, max = 8, message = "必须为有效的日期类型")
    private String createDate;

    @Min(value = 0)
    private int chineseCount;

    @Min(value = 0)
    private int englishCount;

    @Min(value = 0)
    private int numberCount;

    @Min(value = 0)
    private int otherCount;
}
