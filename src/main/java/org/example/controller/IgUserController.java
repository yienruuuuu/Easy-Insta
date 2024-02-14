package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.IgUser;
import org.example.service.IgUserService;
import org.example.service.InstagramService;
import org.example.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "USER controller", description = "IG 用戶相關API")
@RestController
@RequestMapping("iguser")
public class IgUserController extends BaseController {

    @Autowired
    LoginService loginService;
    @Autowired
    InstagramService instagramService;
    @Autowired
    IgUserService igUserService;

    @Operation(summary = "以用戶名查詢用戶", description = "查詢用戶資料")
    @GetMapping(value = "/{username}/{needToWriteToDb}")
    public IgUser getUserInfoByUserName(@PathVariable String username, @PathVariable boolean needToWriteToDb) {
        IgUser igUser = instagramService.searchUser(username);
        // 檢查是否需要寫入資料庫,保存或更新使用者訊息
        if (needToWriteToDb) {
            igUserService.save(igUser);
            log.info("用户信息已保存或更新 : " + igUser.getUserName());
        } else {
            log.info("不需要写入数据库 用戶名稱: {}", igUser.getUserName());
        }
        return igUser;
    }

    @Operation(summary = "提交排程>以用戶名紀錄所有追隨者", description = "提交排程>查詢某用戶所有追隨者")
    @GetMapping(value = "/followers/{username}")
    public void getFollowersByUserName(@PathVariable String username) {
        instagramService.searchUserFollowers(username);
    }

    @Operation(summary = "以用戶名查詢所有發文", description = "查詢用戶發文(簡化版)")
    @GetMapping(value = "/post/{username}")
    public void getPostsByUserName(@PathVariable String username) {
        instagramService.searchUserPosts(username);
    }

}