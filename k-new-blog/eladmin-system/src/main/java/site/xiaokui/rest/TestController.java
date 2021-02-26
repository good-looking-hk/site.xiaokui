package site.xiaokui.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import me.zhengjie.annotation.Log;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.xiaokui.service.dto.SysBlogQueryCriteria;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author HK
 * @date 2021-02-07 10:37
 */
@RestController
@RequiredArgsConstructor
@Api(tags = "接口测试")
@RequestMapping("/api/test")
public class TestController {

    /**
     * 用户统一下单 幂等性接口
     * @param response
     * @param criteria
     * @throws IOException
     */
    @Log("用户统一下单")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('blog:list')")
    public void download(String userId, String goodsId, String price, String asdfghjkl) throws IOException {
//        blogService.download(blogService.queryAll(criteria), response);
    }


    /**
     * 用户确认支付 幂等性接口
     * @param response
     * @param criteria
     * @throws IOException
     */
    @Log("用户统一下单")
    @ApiOperation("导出数据")
    @GetMapping(value = "/downlo1ad")
    @PreAuthorize("@el.check('blog:list')")
    public void dow1nload(HttpServletResponse response, SysBlogQueryCriteria criteria) throws IOException {
//        blogService.download(blogService.queryAll(criteria), response);
    }
}
