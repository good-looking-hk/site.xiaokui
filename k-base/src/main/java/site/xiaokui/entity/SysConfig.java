package site.xiaokui.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author HK
 * @date 2019-09-26 11:09
 */
@ToString
public class SysConfig  extends BaseEntity{

    @Getter@Setter
    protected String key;

    @Getter@Setter
    protected String value;
}
