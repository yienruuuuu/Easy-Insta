package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.IgUser;
import org.example.entity.LoginAccount;
import org.example.entity.Media;
import org.example.exception.ApiException;
import org.example.exception.SysCode;
import org.example.service.IgUserService;
import org.example.service.LoginService;
import org.example.service.MediaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Tag(name = "TEST controller", description = "開發測試用API")
@RestController
@RequestMapping("admin")
public class LoginController extends BaseController {

    private final LoginService loginService;
    private final MediaService mediaService;
    private final IgUserService igUserService;

    public LoginController(LoginService loginService, MediaService mediaService, IgUserService igUserService) {
        this.loginService = loginService;
        this.mediaService = mediaService;
        this.igUserService = igUserService;
    }

    @Operation(summary = "查詢帳密", description = "查詢資料庫內，用於操作的IG帳密")
    @GetMapping(value = "getLoginAccount")
    public List<LoginAccount> getLoginAccount() {
        return loginService.findAll();
    }

    @Operation(summary = "查詢兩周內貼文", description = "查詢兩周內貼文")
    @GetMapping("getMediaInTwoWeeks/{userName}")
    public List<Media> getMediaInTwoWeeks(@PathVariable String userName) {
        IgUser targetUser = igUserService.findUserByIgUserName(userName).orElseThrow(() -> new ApiException(SysCode.IG_USER_NOT_FOUND_IN_DB));
        log.info("確認對象，用戶: {}存在", targetUser.getUserName());
        return mediaService.listMediaByIgUserIdAndDateRange(targetUser, null);
    }
}