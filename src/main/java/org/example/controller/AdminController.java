package org.example.controller;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.example.bean.dto.UploadAccountRequest;
import org.example.bean.enumtype.LoginAccountStatusEnum;
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
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Tag(name = "TEST controller", description = "開發測試用API")
@RestController
@RequestMapping("admin")
public class AdminController {

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
    public List<LoginAccount> handleFileUpload(@RequestParam(value = "file") MultipartFile file) {
        try {
            List<UploadAccountRequest> accountList = upload(file.getInputStream());
            List<LoginAccount> loginAccounts = accountList.stream().map(accountRequest -> LoginAccount.builder().account(accountRequest.getAccount()).password(accountRequest.getPassword()).status(LoginAccountStatusEnum.NORMAL).build()).toList();
            return loginService.saveAll(loginAccounts);
        } catch (Exception e) {
            log.error("上傳帳密清單失敗", e);
            throw new ApiException(SysCode.FILE_UPLOAD_FAILED);
        }
    }


    //private

    /**
     * 讀取帳密清單
     *
     * @param inputStream 帳密清單流
     * @return 帳密清單
     */
    private List<UploadAccountRequest> upload(InputStream inputStream) {
        List<UploadAccountRequest> cachedDataList = new ArrayList<>();
        EasyExcelFactory.read(inputStream, UploadAccountRequest.class, new ReadListener<UploadAccountRequest>() {
            @Override
            public void invoke(UploadAccountRequest data, AnalysisContext context) {
                cachedDataList.add(data);
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext context) {
                // do nothing
            }
        }).sheet().doRead();
        return cachedDataList;
    }
}