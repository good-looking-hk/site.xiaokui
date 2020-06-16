package site.xiaokui.oauth2.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import site.xiaokui.oauth2.entity.Client;
import site.xiaokui.oauth2.service.ClientService;

import java.util.List;

/**
 * @author hk
 */
@Controller
@RequestMapping("/client")
public class ClientController {

    @Autowired
    private ClientService clientService;

    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation(value = "查询客户端列表")
    public String list(Model model) {
        model.addAttribute("list", clientService.findAll());
        List<Client> list = clientService.findAll();
        System.out.println(list.get(0));
        System.out.println(list.get(0).getId());
        return "/client/index";
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ApiOperation(value = "新增客户端")
    public String create(Client client, RedirectAttributes redirectAttributes) {
        clientService.createClient(client);
        redirectAttributes.addFlashAttribute("msg", "新增成功");
        return "redirect:/client";
    }

    @RequestMapping(value = "/{id}/delete", method = RequestMethod.POST)
    @ApiOperation(value = "删除客户端")
    public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        clientService.deleteClient(id);
        redirectAttributes.addFlashAttribute("msg", "删除成功");
        return "redirect:/client";
    }
}
