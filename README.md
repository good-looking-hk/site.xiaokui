##项目说明
个人成长历程的一点文字及代码，主要分为以下几个模块

- k-blog:

spring.mvc.throw-exception-if-no-handler-found=true


    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResponseData defaultErrorHandler(HttpServletRequest req, Exception e) throws Exception {
        logger.error("", e);
        ResponseData r = new ResponseData();
        r.setMessage(e.getMessage());
        if (e instanceof org.springframework.web.servlet.NoHandlerFoundException) {
             r.setCode(404);
        } else {
             r.setCode(500);
        }
        r.setData(null);
        r.setStatus(false);
        return r;
