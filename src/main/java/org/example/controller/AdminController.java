package org.example.controller;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.example.bean.dto.UploadAccountRequest;
import org.example.entity.IgUser;
import org.example.entity.LoginAccount;
import org.example.entity.Media;
import org.example.exception.ApiException;
import org.example.exception.SysCode;
import org.example.service.IgUserService;
import org.example.service.LoginService;
import org.example.service.MediaService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

@Slf4j
@Tag(name = "TEST controller", description = "開發測試用API")
@RestController
@RequestMapping("admin")
public class AdminController extends BaseController {

    private final LoginService loginService;
    private final MediaService mediaService;
    private final IgUserService igUserService;

    public AdminController(LoginService loginService, MediaService mediaService, IgUserService igUserService) {
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

    @PostMapping(value = "/uploadAccountListByExcel", consumes = "multipart/form-data")
    @Operation(summary = "上傳帳密清單", description = "上傳帳密清單")
    public void handleFileUpload(@RequestParam("file") MultipartFile file) {
        try {
            InputStream input = file.getInputStream();
            List<UploadAccountRequest> accountList = ExcelImportUtil.importExcel(input, UploadAccountRequest.class, new ImportParams());
            log.info("上傳帳密清單: {}", accountList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}