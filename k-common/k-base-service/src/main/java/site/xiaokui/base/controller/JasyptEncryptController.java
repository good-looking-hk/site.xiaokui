package site.xiaokui.base.controller;

import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import site.xiaokui.base.entity.ResultEntity;

/**
 * @author HK
 * @date 2020-10-18 15:52
 */
@Slf4j
@RestController
public class JasyptEncryptController implements ApplicationRunner {

    @Autowired
    private StringEncryptor stringEncryptor;

    @Override
    public void run(ApplicationArguments args) {
//        String result = stringEncryptor.encrypt("xxxxxxx!");
//        log.error("加密结果:" + result);
//        result = stringEncryptor.encrypt("xxxxxx~");
//        log.error("加密结果:" + result);
    }

    @PostMapping("/jencrypt")
    public ResultEntity encrypt(String input) {
        String result = stringEncryptor.encrypt(input);
        return ResultEntity.ok(result);
    }

    @PostMapping("/jdecrypt")
    public ResultEntity dencrypt(String output) {
        String result = stringEncryptor.encrypt(output);
        return ResultEntity.ok(result);
    }
}
