package org.example.service.impl;


import com.github.instagram4j.instagram4j.IGClient;
import com.github.instagram4j.instagram4j.actions.users.UserAction;
import com.github.instagram4j.instagram4j.models.user.Profile;
import com.github.instagram4j.instagram4j.models.user.User;
import com.github.instagram4j.instagram4j.requests.friendships.FriendshipsFeedsRequest;
import com.github.instagram4j.instagram4j.responses.feed.FeedUsersResponse;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.example.bean.dto.FollowersAndMaxIdDTO;
import org.example.bean.enumtype.TaskStatusEnum;
import org.example.entity.Followers;
import org.example.entity.IgUser;
import org.example.entity.TaskQueue;
import org.example.exception.ApiException;
import org.example.exception.SysCode;
import org.example.service.FollowersService;
import org.example.service.InstagramService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
@Service("instagramService")
public class Instagram4jServiceImpl implements InstagramService {
    @Autowired
    LoginServiceImpl loginService;
    @Autowired
    IgUserServiceImpl igUserService;
    @Autowired
    FollowersService followersService;

    private IGClient client;

    @Override
    public boolean login(String account, String password) {
        try {
            client = IGClient.builder()
                    .username(account)
                    .password(password)
                    .login();
            log.info("登入成功, 帳號:{}", account);
            return true;
        } catch (Exception e) {
            log.error("登入異常", e);
        }
        return false;
    }

    @Override
    public IgUser searchUser(String username) {
        UserAction searchResult = null;
        try {
            searchResult = client.actions().users().findByUsername(username).join();
        } catch (CompletionException e) {
            log.error("IG查詢用戶異常", e);
            throw new ApiException(SysCode.IG_USER_NOT_FOUND, "查詢用戶異常");
        }
        User igUser = searchResult.getUser();
        log.info("IG查詢結果,用戶名稱: {} ,查詢用戶PK: {}", igUser.getUsername(), igUser.getPk());
        // 查詢資料庫中是否已存在該用戶
        Optional<IgUser> userOptional = igUserService.findUserByIgPk(igUser.getPk());
        // 建立新的User實體，或是從資料庫中獲取已存在的實體
        return getIgUser(userOptional, igUser);
    }

    @Override
    public boolean searchTargetUserFollowersAndSave(TaskQueue task, String maxId) {
        try {
            // 取得追蹤者
            FollowersAndMaxIdDTO followersObjFromIg = getFollowersByUserNameAndMaxId(client, task.getUserId(), maxId);
            // 將 Profile 物件轉換為 Followers 實體
            List<Followers> followersList = convertProfilesToFollowerEntities(task.getUserId(), followersObjFromIg.getFollowers());
            // 保存追蹤者
            followersService.batchInsertFollowers(followersList);
            task.setNextIdForSearch(followersObjFromIg.getMaxId());
            return true;
        } catch (ApiException e) {
            log.error("取得追蹤者失敗ApiException", e);
            task.setStatus(TaskStatusEnum.FAILED);
            task.setErrorMessage(e.getMessage());
        } catch (Exception e) {
            log.error("取得追蹤者失敗Exception", e);
            task.setStatus(TaskStatusEnum.FAILED);
            task.setErrorMessage(e.getMessage());
        }
        return false;
    }

    @Override
    public void searchUserPosts(String username) {

    }

    //private

    //資料實體處理
    @NotNull
    private static IgUser getIgUser(Optional<IgUser> userOptional, User igUser) {
        IgUser userEntity = userOptional.orElse(new IgUser());

        // 设置或更新用户信息
        userEntity.setIgPk(igUser.getPk());
        userEntity.setUserName(igUser.getUsername());
        userEntity.setFullName(igUser.getFull_name());
        userEntity.setMediaCount(igUser.getMedia_count());
        userEntity.setFollowerCount(igUser.getFollower_count());
        userEntity.setFollowingCount(igUser.getFollowing_count());
        return userEntity;
    }

    /**
     * 將 Profile 物件轉換為 Followers 實體
     *
     * @param igUserName         IG用戶名
     * @param followersObjFromIg IG追蹤者物件
     * @return Followers 實體列表
     */
    private static List<Followers> convertProfilesToFollowerEntities(String igUserName, List<Profile> followersObjFromIg) {
        return followersObjFromIg.stream().map(
                        profile -> Followers.builder()
                                .igUserName(igUserName)
                                .followerPk(profile.getPk())
                                .followerUserName(profile.getUsername())
                                .followerFullName(profile.getFull_name())
                                .build())
                .collect(Collectors.toList());
    }

    /**
     * 获取指定用户的所有追踪者。
     *
     * @param client   已登入的 IGClient 對象
     * @param username 要取得追蹤者的用戶名
     * @param maxId    用於分頁的最大 ID
     * @return 一個包含所有追蹤者 Profile 物件的列表
     */
    public static FollowersAndMaxIdDTO getFollowersByUserNameAndMaxId(IGClient client, String username, String maxId) {
        List<Profile> followers = Lists.newArrayList();
        AtomicReference<String> maxIdRef = new AtomicReference<>(maxId);
        // 計數器，用於追蹤請求到的資料數量
        int count = 0;

        try {
            UserAction userAction = client.actions().users().findByUsername(username).join();
            Long userId = userAction.getUser().getPk();

            while (true) {
                // 每次循環使用最新的max Id建立請求
                FeedUsersResponse response = client.sendRequest(
                        new FriendshipsFeedsRequest(userId, FriendshipsFeedsRequest.FriendshipsFeeds.FOLLOWERS, maxIdRef.get())
                ).join();

                List<Profile> users = response.getUsers();
                followers.addAll(users);
                count += users.size();
                log.info("目前查詢累計用戶數: " + count);

                // 更新maxId以供下一次请求使用
                String nextMaxId = response.getNext_max_id();
                if (nextMaxId == null || nextMaxId.isEmpty()) {
                    maxIdRef.set(null);
                    break;
                }
                maxIdRef.set(nextMaxId);

                if (count >= 200) {
                    log.info("達到200个追蹤者資料，跳出循環 count:{}", count);
                    break;
                }
                //請求間暫停五秒
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.info("暂停被中断, 跳出循環");
                    break;
                }
            }
        } catch (Exception e) {
            log.error("取得追蹤者失敗", e);
            throw new ApiException(SysCode.IG_GET_FOLLOWERS_FAILED, "取得追蹤者失敗");
        }

        return FollowersAndMaxIdDTO.builder()
                .followers(followers)
                .maxId(maxIdRef.get())
                .build();
    }
}
