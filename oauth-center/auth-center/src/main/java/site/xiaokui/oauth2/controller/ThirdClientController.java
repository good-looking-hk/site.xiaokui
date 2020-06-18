package site.xiaokui.oauth2.controller;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import site.xiaokui.base.aop.annotation.Log;
import site.xiaokui.oauth2.entity.Client;
import site.xiaokui.oauth2.service.ClientService;

import java.util.List;

/**
 * @author HK
 * @date 2020-06-16 10:35
 */
@Slf4j
@Controller
@RequestMapping("/third")
public class ThirdClientController {

    @Autowired
    private ClientService clientService;

    @Autowired
    private StandardEnvironment environment;

    private Client clientInfo;

    /**
     * 获取code链接：http://localhost:8000/thirdLogin?client_id=c3d6777c2ced42b79d3e6e62bb41e4fe&redirect_uri=/third/user&response_type=code&scope=SCOPE&state=STATE
     */
    @GetMapping({"", "/index"})
    public String index(Model model) {
        Client client = getClientInfo(true);
        model.addAttribute("client", client);
        return "/third/index";
    }

    /**
     * 先用code换token，再用token换取用户信息
     * 为什么不用code直接换用户信息了，这是因为code获取的那一步是暴露在网络中的（clientId、code、redirectUrl）
     * 所以需要额外增加一步验证（clientID、clientSecret、code），以保证用户数据不被他人截取
     * 额外补充：
     * 1. redirect_uri在oltu.oauth2框架里面限制为必填的 {@link org.apache.oltu.oauth2.common.validators.AbstractValidator#validateRequiredParameters}
     * 2. 对于scope、state等字段暂不做特殊处理
     * 3. 可以使用post代替get
     *
     * code换token链接：http://localhost:8000/accessToken?client_id=11111&client_secret=22222&code=33333&grant_type=authorization_code&redirect_uri=/third/user
     * 微信示例：https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code
     *
     * token换用户信息链接：http://localhost:8000/userInfo?access_token=1111111&scope=SCOPE&state=STATE
     */
    @GetMapping({"/user"})
    @Log(module = "third-client-app", remark="获取用户授权信息")
    public String user(Model model, String code) {
        String port = environment.getProperty("server.port");
        String context = environment.getProperty("server.servlet.context-path");
        Client client = getClientInfo(false);

        String url = String.format("http://localhost:%s/%s/accessToken?client_id=%s&client_secret=%s&code=%s" +
                "&grant_type=authorization_code&redirect_uri=/third/user", port, context, client.getClientId(), client.getClientSecret(), code);
        // 远程调用，先用code换token，可以使用 post
        String resp = HttpUtil.post(url, "");

        String token = new JSONObject(resp).getStr("access_token");
        url = String.format("http://localhost:%s/%s/userInfo?access_token=%s&scope=SCOPE&state=STATE", port, context, token);
        resp = HttpUtil.post(url, "");
        model.addAttribute("user", resp);
        log.info("获取用户信息:code={}, token={}, userInfo={}", code, token, resp);
        return "/third/user";
    }

    private Client getClientInfo(boolean createNew) {
        if (this.clientInfo != null && !createNew) {
            return this.clientInfo;
        }
        List<Client> list = clientService.findAll();
        if (list == null || list.isEmpty()) {
            clientService.createClient("迷你自走棋");
            list = clientService.findAll();
        }
        Client client = list.get(0);
        client.setClientName("迷你自走棋");
        clientService.updateClient(client);
        return this.clientInfo = client;
    }
}
