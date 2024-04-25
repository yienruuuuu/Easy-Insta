package org.example.service.impl;


import com.github.instagram4j.instagram4j.IGClient;
import com.github.instagram4j.instagram4j.actions.users.UserAction;
import com.github.instagram4j.instagram4j.exceptions.IGLoginException;
import com.github.instagram4j.instagram4j.exceptions.IGResponseException;
import com.github.instagram4j.instagram4j.models.media.timeline.Comment;
import com.github.instagram4j.instagram4j.models.media.timeline.TimelineMedia;
import com.github.instagram4j.instagram4j.models.user.Profile;
import com.github.instagram4j.instagram4j.models.user.User;
import com.github.instagram4j.instagram4j.requests.feed.FeedUserRequest;
import com.github.instagram4j.instagram4j.requests.friendships.FriendshipsFeedsRequest;
import com.github.instagram4j.instagram4j.requests.media.MediaGetCommentsRequest;
import com.github.instagram4j.instagram4j.requests.media.MediaGetLikersRequest;
import com.github.instagram4j.instagram4j.responses.feed.FeedUserResponse;
import com.github.instagram4j.instagram4j.responses.feed.FeedUsersResponse;
import com.github.instagram4j.instagram4j.responses.media.MediaGetCommentsResponse;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.example.bean.dto.CommentsAndMaxIdDTO;
import org.example.bean.dto.FollowersAndMaxIdDTO;
import org.example.bean.dto.LikerProfilesAndMaxIdDTO;
import org.example.bean.dto.PostsAndMaxIdDTO;
import org.example.bean.enumtype.ConfigEnum;
import org.example.config.ConfigCache;
import org.example.entity.*;
import org.example.exception.ApiException;
import org.example.exception.SysCode;
import org.example.exception.TaskExecutionException;
import org.example.service.FollowersService;
import org.example.service.InstagramService;
import org.example.service.MediaCommentService;
import org.example.service.MediaLikerService;
import org.example.utils.BrightDataProxy;
import org.example.utils.CrawlingUtil;
import org.example.utils.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.net.SocketTimeoutException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service("instagramService")
public class Instagram4jServiceImpl implements InstagramService {
    private final IgUserServiceImpl igUserService;
    private final FollowersService followersService;
    private final MediaServiceImpl mediaService;
    private final ConfigCache configCache;
    private final MediaCommentService mediaCommentService;
    private final MediaLikerService mediaLikerService;
    private final Map<String, IGClient> clients = new ConcurrentHashMap<>();

    public Instagram4jServiceImpl(IgUserServiceImpl igUserService, FollowersService followersService, MediaServiceImpl mediaService, ConfigCache configCache, MediaCommentService mediaCommentService, MediaLikerService mediaLikerService) {
        this.igUserService = igUserService;
        this.followersService = followersService;
        this.mediaService = mediaService;
        this.configCache = configCache;
        this.mediaCommentService = mediaCommentService;
        this.mediaLikerService = mediaLikerService;
    }

    private IGClient igClient;
    public static final String CHALLENGE_REQUIRED = "challenge_required";
    private static final String LOG_MESSAGE_PATTERN = "出循環 ，目標請求數: {}, maxId: {}, 已取得資料count={}";

    @Override
    public IGClient getClient(String account, String password) {
        IGClient client = clients.get(account);
        if (client == null || !testClient(client, account)) {
            client = loginIg(account, password);
            clients.put(account, client);
        }
        return client;
    }

    @Override
    public void login(String account, String password) {
        try {
            igClient = IGClient.builder()
                    .username(account)
                    .password(password)
                    .client(BrightDataProxy.getBrightDataProxy(
                            configCache.get(ConfigEnum.BRIGHT_DATA_ACCOUNT.name()),
                            configCache.get(ConfigEnum.BRIGHT_DATA_PASSWORD.name()),
                            StringUtils.generateRandomString(8)))
                    .login();
            log.info("登入成功, 帳號:{}", account);
        } catch (IGLoginException | CompletionException e) {
            handleSocketTimeOut(e, SysCode.IG_LOGIN_FAILED);
        } catch (Exception e) {
            log.info("IG登入異常 Exception");
            Throwable cause = e;
            while (cause != null) {
                if (cause instanceof IGResponseException && CHALLENGE_REQUIRED.equals(cause.getMessage())) {
                    throw new ApiException(SysCode.IG_ACCOUNT_CHALLENGE_REQUIRED, cause);
                }
                cause = cause.getCause();
            }
            throw new TaskExecutionException(SysCode.IG_LOGIN_FAILED, e);
        }
    }

    @Override
    public IgUser searchUser(String username, LoginAccount loginAccount) {
        UserAction searchResult;
        try {
            searchResult = igClient.actions().users().findByUsername(username).join();
        } catch (CompletionException e) {
            log.error("IG查詢用戶異常", e);
            throw new ApiException(SysCode.IG_USER_NOT_FOUND);
        }
        User igUser = searchResult.getUser();
        log.info("IG查詢結果,用戶名稱: {} ,查詢用戶PK: {}", igUser.getUsername(), igUser.getPk());
        // 查詢資料庫中是否已存在該用戶
        Optional<IgUser> userOptional = igUserService.findUserByIgPk(igUser.getPk());
        // 建立新的User實體，或是從資料庫中獲取已存在的實體
        return getIgUser(userOptional, igUser);
    }

    @Override
    public void searchFollowersAndSave(TaskQueue task, String maxId) {
        try {
            // 取得追蹤者
            FollowersAndMaxIdDTO followersObjFromIg = getFollowersByUserNameAndMaxId(igClient, task.getIgUser().getUserName(), maxId);
            // 將 Profile 物件轉換為 Followers 實體
            List<Followers> followersList = convertProfilesToFollowerEntities(task.getIgUser(), followersObjFromIg.getFollowers());
            // 保存追蹤者
            followersService.batchInsertFollowers(followersList);
            task.setNextIdForSearch(followersObjFromIg.getMaxId());
        } catch (Exception e) {
            throw new TaskExecutionException(SysCode.IG_GET_FOLLOWERS_FAILED, e);
        }
    }

    @Override
    public void searchUserMediasAndSave(TaskQueue task, String maxId) {
        try {
            // 取得對象貼文
            PostsAndMaxIdDTO postsAndMaxIdDTO = getPostsByUserName(igClient, task.getIgUser().getUserName(), maxId);
            // 將 TimelineMedia 物件轉換為 Media 實體
            List<Media> mediasList = convertTimeLineMediaToMediaEntities(task.getIgUser(), postsAndMaxIdDTO.getMedias());
            // 保存貼文
            mediaService.batchInsertMedias(mediasList);
            task.setNextIdForSearch(postsAndMaxIdDTO.getMaxId());
        } catch (ApiException apiException) {
            throw apiException;
        } catch (CompletionException e) {
            if (e.getCause() instanceof SocketTimeoutException) {
                log.warn("SocketTimeoutException occurred: {}", e.getMessage());
                throw new ApiException(SysCode.SOCKET_TIMEOUT);
            } else {
                log.error("CompletionException occurred: {}", e.getMessage());
                throw new TaskExecutionException(SysCode.IG_GET_MEDIA_FAILED, e);
            }
        } catch (Exception e) {
            throw new TaskExecutionException(SysCode.IG_GET_MEDIA_FAILED, e);
        }
    }

    @Override
    public void searchMediaCommentsAndSave(TaskQueue task, String maxId) {
        try {
            // 取得對象貼文
            CommentsAndMaxIdDTO commentsAndMaxIdDTO = getCommentsByMediaPk(igClient, task, maxId);
            // 將 Comment 物件轉換為 Media_comment 實體
            List<MediaComment> mediasList = convertCommentToMediaCommentEntity(task, commentsAndMaxIdDTO.getComments());
            // 保存
            mediaCommentService.batchInsertMedias(mediasList);
            task.getTaskQueueMediaId().setNextMediaId(commentsAndMaxIdDTO.getMaxId());
        } catch (ApiException apiException) {
            throw apiException;
        } catch (CompletionException e) {
            handleSocketTimeOut(e, SysCode.IG_GET_COMMENTS_FAILED);
        } catch (Exception e) {
            throw new TaskExecutionException(SysCode.IG_GET_COMMENTS_FAILED, e);
        }
    }

    @Override
    public void searchMediaLikersAndSave(TaskQueue task, String maxId) {
        try {
            // 取得對象貼文
            LikerProfilesAndMaxIdDTO likerProfilesAndMaxIdDTO = getLikersByMediaPk(igClient, task, maxId);
            // 將 Comment 物件轉換為 Media_comment 實體
            List<MediaLiker> likerList = convertProfileToMediaLikerEntity(task, likerProfilesAndMaxIdDTO.getLikerProfiles());
            // 保存
            mediaLikerService.batchInsert(likerList);
            task.getTaskQueueMediaId().setNextMediaId(likerProfilesAndMaxIdDTO.getMaxId());
            log.info("Task = {}", task);
        } catch (ApiException apiException) {
            throw apiException;
        } catch (Exception e) {
            throw new TaskExecutionException(SysCode.IG_GET_LIKERS_FAILED, e);
        }
    }

    // private

    /**
     * 登入
     *
     * @param account  IG用戶名
     * @param password IG用戶密碼
     * @return IGClient物件
     */
    private IGClient loginIg(String account, String password) {
        try {
            IGClient client = IGClient.builder()
                    .username(account)
                    .password(password)
                    .client(BrightDataProxy.getBrightDataProxy(
                            configCache.get(ConfigEnum.BRIGHT_DATA_ACCOUNT.name()),
                            configCache.get(ConfigEnum.BRIGHT_DATA_PASSWORD.name()),
                            StringUtils.generateRandomString(8)))
                    .login();
            log.info("登入成功, 帳號:{}", account);
            return client;
        } catch (IGLoginException | CompletionException e) {
            handleSocketTimeOut(e, SysCode.IG_LOGIN_FAILED);
        } catch (Exception e) {
            log.info("IG登入異常 Exception");
            Throwable cause = e;
            while (cause != null) {
                if (cause instanceof IGResponseException && CHALLENGE_REQUIRED.equals(cause.getMessage())) {
                    throw new ApiException(SysCode.IG_ACCOUNT_CHALLENGE_REQUIRED, cause);
                }
                cause = cause.getCause();
            }
            throw new TaskExecutionException(SysCode.IG_LOGIN_FAILED, e);
        }
        return null;
    }

    /**
     * 建立新的User實體，或是從資料庫中獲取已存在的實體，並以ig響應設置或更新用戶信息
     *
     * @param userOptional 資料庫中已存在的用戶
     * @param igUser       IG用戶(實體)
     * @return 用戶實體
     */
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
     * @param igUser             IG用戶名
     * @param followersObjFromIg IG追蹤者物件
     * @return Followers 實體列表
     */
    private static List<Followers> convertProfilesToFollowerEntities(IgUser igUser, List<Profile> followersObjFromIg) {
        return followersObjFromIg.stream().map(profile -> Followers.builder()
                        .igUser(igUser)
                        .followerPk(profile.getPk())
                        .followerUserName(profile.getUsername())
                        .followerFullName(profile.getFull_name())
                        .isPrivate(profile.is_private())
                        .profilePicUrl(profile.getProfile_pic_url())
                        .profilePicId(profile.getProfile_pic_id())
                        .isVerified(profile.is_verified())
                        .hasAnonymousProfilePicture(profile.isHas_anonymous_profile_picture())
                        .latestReelMedia(profile.getLatest_reel_media())
                        .build())
                .toList();
    }

    /**
     * 將 TimelineMedia 物件轉換為 Media 實體
     *
     * @param igUser IG用戶名
     */
    private static List<Media> convertTimeLineMediaToMediaEntities(IgUser igUser, List<TimelineMedia> timeLineMediaObjFromIg) {
        return timeLineMediaObjFromIg.stream().map(timelineMedia -> {
            LocalDateTime takenAt = Instant.ofEpochSecond(timelineMedia.getTaken_at())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
            return Media.builder()
                    .igUserId(igUser)
                    .mediaPk(timelineMedia.getPk())
                    .mediaId(timelineMedia.getId())
                    .playCount(timelineMedia.getPlay_count())
                    .fbPlayCount(timelineMedia.getFb_play_count())
                    .likeCount(timelineMedia.getLike_count())
                    .fbLikeCount(timelineMedia.getFb_like_count())
                    .reshareCount(timelineMedia.getReshare_count())
                    .commentCount(timelineMedia.getComment_count())
                    .numberOfQualities(timelineMedia.getNumber_of_qualities())
                    .takenAt(takenAt) // 设置转换后的LocalDateTime
                    .text(timelineMedia.getCaption() != null ? timelineMedia.getCaption().getText() : null) // 添加空值检查
                    .build();
        }).toList();
    }

    /**
     * 將 Comment 物件轉換為 MediaComment 實體
     *
     * @param taskQueue     任務
     * @param commentFromIg IG留言物件
     */
    private static List<MediaComment> convertCommentToMediaCommentEntity(TaskQueue taskQueue, List<Comment> commentFromIg) {
        return commentFromIg.stream().map(comment -> MediaComment.builder()
                        .media(taskQueue.getTaskQueueMediaId().getMedia())
                        .text(comment.getText())
                        .commenterUserId(comment.getUser().getPk())
                        .commenterFullName(comment.getUser().getFull_name())
                        .commenterUserName(comment.getUser().getUsername())
                        .commentPk(comment.getPk())
                        .commenterIsPrivate(comment.getUser().is_private())
                        .commenterIsVerified(comment.getUser().is_verified())
                        .commenterProfilePicId(comment.getUser().getProfile_pic_id())
                        .commenterProfilePicUrl(comment.getUser().getProfile_pic_url())
                        .commenterLatestReelMedia(comment.getUser().getLatest_reel_media())
                        .contentType(comment.getContent_type())
                        .status(comment.getStatus())
                        .commentLikeCount(comment.getComment_like_count())
                        .build())
                .toList();
    }

    /**
     * 將 Comment 物件轉換為 MediaComment 實體
     *
     * @param taskQueue 任務
     * @param profiles  IG按讚者物件
     */
    private static List<MediaLiker> convertProfileToMediaLikerEntity(TaskQueue taskQueue, List<Profile> profiles) {
        return profiles.stream().map(profile -> MediaLiker.builder()
                        .media(taskQueue.getTaskQueueMediaId().getMedia())
                        .likerUserName(profile.getUsername())
                        .likerFullName(profile.getFull_name())
                        .likerPk(profile.getPk())
                        .likerIsPrivate(profile.is_private())
                        .likerIsVerified(profile.is_verified())
                        .likerProfilePicId(profile.getProfile_pic_id())
                        .likerProfilePicUrl(profile.getProfile_pic_url())
                        .likerLatestReelMedia(profile.getLatest_reel_media())
                        .build())
                .toList();
    }

    /**
     * 向ig爬取指定用户的所有追踪者。
     *
     * @param client   已登入的 IGClient 對象
     * @param username 要取得追蹤者的用戶名
     * @param maxId    用於分頁的最大 ID
     * @return 一個包含所有追蹤者 Profile 物件的列表
     */
    private FollowersAndMaxIdDTO getFollowersByUserNameAndMaxId(IGClient client, String username, String maxId) {
        List<Profile> followers = Lists.newArrayList();
        AtomicReference<String> maxIdRef = new AtomicReference<>(maxId);
        // 計數器，用於追蹤請求到的資料數量
        int count = 0;
        // 是否第一次迭代
        boolean isFirstIteration = true;
        int maxRequestTimes = Integer.parseInt(configCache.get(ConfigEnum.MAX_FOLLOWERS_PER_REQUEST.name()));
        try {
            // 向IG取得用戶PK
            Long userPkFromIg = getUserIdByUsername(client, username);
            while (shouldContinueFetching(count, maxIdRef.get(), isFirstIteration, maxRequestTimes)) {
                // 之后的迭代不再是第一次
                isFirstIteration = false;
                // 每次循環使用最新的max Id建立請求
                FeedUsersResponse response = fetchFollowers(client, userPkFromIg, maxIdRef.get());
                // 處理請求結果
                processFollowersResponse(followers, response, maxIdRef);
                // 更新計數器
                count += response.getUsers().size();
                log.info("目前查詢累計用戶數: " + count);
                //請求間暫停
                CrawlingUtil.pauseBetweenRequests(5, 15);
            }
            log.info("達到{}個追蹤者資料，跳出循環 count:{}", maxRequestTimes, count);
        } catch (CompletionException completionException) {
            if (completionException.getCause() instanceof IGResponseException && CHALLENGE_REQUIRED.equals(completionException.getCause().getMessage())) {
                throw new ApiException(SysCode.IG_ACCOUNT_CHALLENGE_REQUIRED, completionException);
            } else {
                throw completionException;
            }
        }
        return FollowersAndMaxIdDTO.builder()
                .followers(followers)
                .maxId(maxIdRef.get())
                .build();
    }

    /**
     * 獲取指定用戶的所有文章資訊
     *
     * @param client   已登录的 IGClient 对象
     * @param username 要获取文章的用户名
     * @param maxId    用于分页的最大 ID
     */
    private PostsAndMaxIdDTO getPostsByUserName(IGClient client, String username, String maxId) {
        List<TimelineMedia> medias = Lists.newArrayList();
        AtomicReference<String> maxIdRef = new AtomicReference<>(maxId);
        // 計數器，用於追蹤請求到的資料數量
        int count = 0;
        boolean isFirstIteration = true;
        int maxRequestTimes = Integer.parseInt(configCache.get(ConfigEnum.MAX_POSTS_PER_REQUEST.name()));

        try {
            //取得對象Pk
            Long userPkFromIg = getUserIdByUsername(client, username);
            while (shouldContinueFetching(count, maxIdRef.get(), isFirstIteration, maxRequestTimes)) {
                isFirstIteration = false;

                // 每次循環使用最新的max Id建立請求
                FeedUserResponse response = fetchPosts(client, userPkFromIg, maxIdRef.get());
                // 處理請求結果
                processPostsResponse(medias, response, maxIdRef);
                count += response.getItems().size();
                log.info("目前累計文章數: " + count);
                //請求間暫停
                CrawlingUtil.pauseBetweenRequests(5, 15);
            }
            log.info(LOG_MESSAGE_PATTERN, maxRequestTimes, maxIdRef.get(), count);
        } catch (CompletionException completionException) {
            handleCompletionException(completionException);
        }
        return PostsAndMaxIdDTO.builder()
                .medias(medias)
                .maxId(maxIdRef.get())
                .build();
    }

    private CommentsAndMaxIdDTO getCommentsByMediaPk(IGClient client, TaskQueue task, String maxId) {
        String mediaId = task.getTaskQueueMediaId().getMedia().getMediaId();
        List<Comment> comments = Lists.newArrayList();
        AtomicReference<String> maxIdRef = new AtomicReference<>(maxId);
        // 計數器，用於追蹤請求到的資料數量
        int count = 0;
        boolean isFirstIteration = true;
        int maxRequestTimes = Integer.parseInt(configCache.get(ConfigEnum.MAX_COMMENTS_PER_REQUEST.name()));

        try {
            while (shouldContinueFetching(count, maxIdRef.get(), isFirstIteration, maxRequestTimes)) {
                isFirstIteration = false;
                // 每次循环使用最新的maxId创建请求
                MediaGetCommentsResponse response = fetchComments(client, mediaId, maxIdRef.get());
                // 處理請求結果
                processCommentsResponse(comments, response, maxIdRef);
                count += response.getComments().size();
                log.info("目前累計數: " + count);
                //請求間暫停
                CrawlingUtil.pauseBetweenRequests(5, 15);
            }
            log.info(LOG_MESSAGE_PATTERN, maxRequestTimes, maxIdRef.get(), count);
        } catch (CompletionException completionException) {
            handleCompletionException(completionException);
        }
        return CommentsAndMaxIdDTO.builder()
                .comments(comments)
                .maxId(maxIdRef.get())
                .build();
    }


    private LikerProfilesAndMaxIdDTO getLikersByMediaPk(IGClient client, TaskQueue task, String maxId) {
        String mediaId = task.getTaskQueueMediaId().getMedia().getMediaId();
        List<Profile> profiles = Lists.newArrayList();
        AtomicReference<String> maxIdRef = new AtomicReference<>(maxId);
        // 計數器，用於追蹤請求到的資料數量
        int count = 0;
        boolean isFirstIteration = true;
        int maxRequestTimes = Integer.parseInt(configCache.get(ConfigEnum.MAX_COMMENTS_PER_REQUEST.name()));

        try {
            while (shouldContinueFetching(count, maxIdRef.get(), isFirstIteration, maxRequestTimes)) {
                isFirstIteration = false;
                // 每次循环使用最新的maxId创建请求
                FeedUsersResponse response = fetchLikers(client, mediaId, maxIdRef.get());
                // 處理請求結果
                processFollowersResponse(profiles, response, maxIdRef);
                count += response.getUsers().size();
                log.info("目前累計數: " + count);
                //請求間暫停
                CrawlingUtil.pauseBetweenRequests(5, 15);
            }
            log.info(LOG_MESSAGE_PATTERN, maxRequestTimes, maxIdRef.get(), count);
        } catch (CompletionException completionException) {
            handleCompletionException(completionException);
        }
        return LikerProfilesAndMaxIdDTO.builder()
                .likerProfiles(profiles)
                .maxId(maxIdRef.get())
                .build();
    }

    private Long getUserIdByUsername(IGClient client, String username) {
        return client.actions().users().findByUsername(username).join().getUser().getPk();
    }

    private FeedUsersResponse fetchFollowers(IGClient client, Long userId, String maxId) {
        return client.sendRequest(new FriendshipsFeedsRequest(userId, FriendshipsFeedsRequest.FriendshipsFeeds.FOLLOWERS, maxId)).join();
    }

    private FeedUserResponse fetchPosts(IGClient client, Long userPkFromIg, String maxId) {
        return client.sendRequest(new FeedUserRequest(userPkFromIg, maxId)).join();
    }

    private MediaGetCommentsResponse fetchComments(IGClient client, String mediaId, String maxId) {
        return client.sendRequest(new MediaGetCommentsRequest(mediaId, maxId)).join();
    }

    private FeedUsersResponse fetchLikers(IGClient client, String mediaId, String maxId) {
        return client.sendRequest(new MediaGetLikersRequest(mediaId, maxId)).join();
    }

    private void processFollowersResponse(List<Profile> followers, FeedUsersResponse response, AtomicReference<String> maxIdRef) {
        followers.addAll(response.getUsers());
        String nextMaxId = response.getNext_max_id();
        maxIdRef.set(nextMaxId);
    }

    private void processPostsResponse(List<TimelineMedia> medias, FeedUserResponse response, AtomicReference<String> maxIdRef) {
        medias.addAll(response.getItems());
        String nextMaxId = response.getNext_max_id();
        log.info("下一個maxId:{}", nextMaxId);
        maxIdRef.set(nextMaxId);
    }

    private void processCommentsResponse(List<Comment> comments, MediaGetCommentsResponse response, AtomicReference<String> maxIdRef) {
        comments.addAll(response.getComments());
        String nextMaxId = response.getNext_min_id();
        log.info("下一個maxId:{}", nextMaxId);
        maxIdRef.set(nextMaxId);
    }

    private boolean shouldContinueFetching(int count, String maxId, boolean isFirstIteration, int requestLimit) {
        //條件: 計數器小於規定 && (maxId不為空或是第一次迭代)
        return count < requestLimit && (maxId != null || isFirstIteration);
    }

    private void handleCompletionException(CompletionException completionException) {
        if (completionException.getCause() instanceof IGResponseException && CHALLENGE_REQUIRED.equals(completionException.getCause().getMessage())) {
            log.error("處理 challenge_required", completionException);
            throw new ApiException(SysCode.IG_ACCOUNT_CHALLENGE_REQUIRED, completionException);
        } else {
            throw completionException;
        }
    }

    private void handleSocketTimeOut(Exception e, SysCode sysCode) {
        Throwable cause = e;
        while (cause != null) {
            if (cause.getMessage() != null && cause.getMessage().contains("SocketTimeoutException")) {
                throw new ApiException(SysCode.SOCKET_TIMEOUT, cause);
            }
            cause = cause.getCause();
        }
        throw new TaskExecutionException(sysCode, e);
    }

    private boolean testClient(IGClient client, String username) {
        try {
            // 實施一個小的API動作來測試client是否正常
            getUserIdByUsername(client, username);
            return true;
        } catch (Exception e) {
            log.info("客戶端失效，需要重新登入");
            return false;
        }
    }
}
