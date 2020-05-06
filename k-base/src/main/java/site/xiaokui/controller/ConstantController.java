package site.xiaokui.controller;

/**
 * @author HK
 * @date 2018-06-25 14:20
 */
public interface ConstantController {

    String EMPTY = "", INDEX = "/index", LIST = "/list",  ADD = "/add", REMOVE = "/remove", EDIT = "/edit";

    String TREE = "/tree", ERROR = "/error", UNAUTHORIZED = "/unauthorized";

    String REDIRECT = "redirect:", FORWARD = "forward:";

    String FORWARD_ERROR = FORWARD + ERROR, FORWARD_UNAUTHORIZED = FORWARD + UNAUTHORIZED;

    String RESET_PASSWORD = "/resetPwd", UPDATE = "/update";
}
