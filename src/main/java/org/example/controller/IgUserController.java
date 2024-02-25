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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @Operation(summary = "提交排程，安排任務")
    @PostMapping(value = "/task/{taskEnum}/{username}")
    public Object sendTask(@PathVariable String username, @PathVariable TaskTypeEnum taskEnum) {
        // 檢查對於查詢對象的任務是否存在
        boolean taskExists = taskQueueService.checkGetFollowersTaskQueueExist(username, taskEnum);
        if (taskExists) {
            log.info("用戶: {} 的 {} 任務已存在", taskEnum, username);
            return new ApiResponse(SysCode.TASK_ALREADY_EXISTS.getCode(), SysCode.TASK_ALREADY_EXISTS.getMessage(), null);
        }
        // 保存任务並返回保存的任务
        Optional<TaskQueue> savedTask = taskQueueService.createAndSaveTaskQueue(username, taskEnum, TaskStatusEnum.PENDING);
        if (savedTask.isPresent()) {
            log.info("username: {} 的 {} 任務創建成功", taskEnum, username);
            return savedTask.get();
        } else {
            log.info("username: {}的 {} 任務建立失敗", taskEnum, username);
            return new ApiResponse(SysCode.TASK_CREATION_FAILED.getCode(), SysCode.TASK_CREATION_FAILED.getMessage(), null);
        }
    }
}