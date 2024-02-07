package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.LoginAccount;
import org.example.service.InstagramService;
import org.example.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "Login controller", description = "IG登入相關API")
@RestController
@RequestMapping("user")
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

    @Operation(summary = "登入", description = "手動登入")
    @PostMapping
    public void loginIg4J(@RequestBody String account, @RequestBody String password) {
        instagramService.login(account, password);
    }
}