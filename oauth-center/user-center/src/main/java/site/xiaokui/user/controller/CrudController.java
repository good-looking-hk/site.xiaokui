package site.xiaokui.user.controller;

import org.springframework.ui.Model;
import site.xiaokui.base.entity.ResultEntity;

/**
 * @author HK
 * @date 2018-06-23 11:40
 */
public interface CrudController {

    /**
     * 查找
     * @return get首页界面
     */
    String index();

    /**
     * 添加
     * @return get添加界面
     */
    String add();

    /**
     * 编辑
     * @param id 编辑实体的id
     * @param model request的载体
     * @return get请求界面
     */
    String edit(Integer id, Model model);

    /**
     * 删除
     * @param id 需要删除实体的id
     * @return post删除结果
     */
    ResultEntity remove(Integer id);
}
