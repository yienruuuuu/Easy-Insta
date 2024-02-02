package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.example.bean.enumtype.AccountEnum;
import org.example.entity.LoginAccount;
import org.example.service.InstagramService;
import org.example.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Tag(name = "Login controller", description = "IG登入相關API")
@RestController
@RequestMapping("login")
public class LoginController extends BaseController {

    @Autowired
    LoginService loginService;
    @Autowired
    InstagramService instagramService;

    @Operation(summary = "查詢帳密", description = "查詢資料庫內，用於操作的IG帳密資料")
    @GetMapping(value = "getLoginAccount")
    public List<LoginAccount> getLoginAccount() {
        return loginService.findAll();
    }

    @Operation(summary = "登入", description = "登入IG4J插件")
    @GetMapping(value = "login/ig4J/{accountEnum}")
    public void loginIg4J(@PathVariable AccountEnum accountEnum) {
        instagramService.login(accountEnum);
    }
}