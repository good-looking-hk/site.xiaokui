package site.xiaokui.base.controller;

import site.xiaokui.base.entity.ResultEntity;
import site.xiaokui.base.util.HttpUtil;
import site.xiaokui.base.util.StringUtil;


/**
 * 封装对于request和response的一些基础的方法
 * @author HK
 * @date 2018-05-21 17:28
 */
public class BaseController implements ConstantController {

    protected String getIP() {
        return HttpUtil.getRequestIP();
    }

    protected ResultEntity ok() {
        return ResultEntity.ok();
    }

    protected ResultEntity ok(String msg) {
        return ResultEntity.ok(msg);
    }

    protected ResultEntity error(String msg) {
        return ResultEntity.error(msg);
    }

    protected ResultEntity paramError(Object... strs) {
        return ResultEntity.paramError(strs);
    }

    protected ResultEntity returnResult(boolean success) {
        if (!success) {
            return ResultEntity.failed();
        }
        return ResultEntity.ok();
    }

    protected ResultEntity returnResult(boolean success, String failedMsg) {
        if (!success) {
            return ResultEntity.failed(failedMsg);
        }
        return ResultEntity.ok();
    }

    protected ResultEntity returnResult(boolean success, String failedMsg, String successMsg) {
        if (!success) {
            return ResultEntity.failed(failedMsg);
        }
        return ResultEntity.ok(successMsg);
    }

    protected boolean isEmpty(final String str) {
        return StringUtil.isEmpty(str);
    }

    protected boolean isNotEmpty(final String str) {
        return !isEmpty(str);
    }
}
