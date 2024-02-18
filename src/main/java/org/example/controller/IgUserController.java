package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.example.bean.dto.ApiResponse;
import org.example.bean.enumtype.TaskStatusEnum;
import org.example.bean.enumtype.TaskTypeEnum;
import org.example.entity.IgUser;
import org.example.entity.TaskQueue;
import org.example.exception.SysCode;
import org.example.service.IgUserService;
import org.example.service.InstagramService;
import org.example.service.LoginService;
import org.example.service.TaskQueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * @author Eric.Lee
 */
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
    @Autowired
    TaskQueueService taskQueueService;

    @Operation(summary = "以用戶名查詢用戶，並可控是否紀錄到資料庫")
    @PostMapping(value = "/search/{username}/{needToWriteToDb}")
    public IgUser getUserInfoByUserName(@PathVariable String username, @PathVariable boolean needToWriteToDb) {
        IgUser igUser = instagramService.searchUser(username);
        // 檢查是否需要寫入資料庫,保存或更新使用者訊息
        if (needToWriteToDb) {
            igUserService.save(igUser);
            log.info("用户信息已保存或更新 : {}", igUser.getUserName());
        } else {
            log.info("不需要寫入資料庫 使用者名稱: {}", igUser.getUserName());
        }
        return igUser;
    }

    @Operation(summary = "提交排程，安排查詢某用戶下所有追隨者")
    @PostMapping(value = "/task/followers/{username}")
    public Object getFollowersByUserName(@PathVariable String username) {
        // 檢查對於查詢對象的任務是否存在
        boolean taskExists = taskQueueService.checkGetFollowersTaskQueueExist(username, TaskTypeEnum.GET_FOLLOWERS);
        if (taskExists) {
            log.info("用戶: {} 的追隨者任務已存在", username);
            return new ApiResponse(SysCode.TASK_ALREADY_EXISTS.getCode(), SysCode.TASK_ALREADY_EXISTS.getMessage(), null);
        }
        // 保存任务並返回保存的任务
        Optional<TaskQueue> savedTask = taskQueueService.createAndSaveTaskQueue(username, TaskTypeEnum.GET_FOLLOWERS, TaskStatusEnum.PENDING);
        if (savedTask.isPresent()) {
            log.info("username: {} 的追隨者查詢任務創建成功", username);
            return savedTask.get();
        } else {
            log.info("username: {}的追隨者查詢任務建立失敗", username);
            return new ApiResponse(SysCode.TASK_CREATION_FAILED.getCode(), SysCode.TASK_CREATION_FAILED.getMessage(), null);
        }
    }

    //TODO: 這個方法應該是用來查詢用戶的所有貼文，但是實際上是空的
    @Operation(summary = "以用戶名查詢所有發文", description = "查詢用戶發文(簡化版)")
    @GetMapping(value = "/post/{username}")
    public void getPostsByUserName(@PathVariable String username) {
        instagramService.searchUserPosts(username);
    }

}