package org.example;

import com.github.instagram4j.instagram4j.IGClient;
import com.github.instagram4j.instagram4j.actions.users.UserAction;
import com.github.instagram4j.instagram4j.models.media.timeline.Comment;
import com.github.instagram4j.instagram4j.models.media.timeline.TimelineMedia;
import com.github.instagram4j.instagram4j.models.user.Profile;
import com.github.instagram4j.instagram4j.requests.feed.FeedUserRequest;
import com.github.instagram4j.instagram4j.requests.friendships.FriendshipsFeedsRequest;
import com.github.instagram4j.instagram4j.requests.media.MediaGetCommentsRequest;
import com.github.instagram4j.instagram4j.requests.media.MediaGetLikersRequest;
import com.github.instagram4j.instagram4j.responses.feed.FeedUserResponse;
import com.github.instagram4j.instagram4j.responses.feed.FeedUsersResponse;
import com.github.instagram4j.instagram4j.responses.media.MediaGetCommentsResponse;
import com.google.common.collect.Lists;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@SpringBootApplication
public class WebScraperTest implements CommandLineRunner {

    private String loginAccount = "barbrafyfe383";
    private String loginPassword = "Ha6707mitovuke";

    public static void main(String[] args) {
        SpringApplication.run(WebScraperTest.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        //登入
        IGClient client = IGClient.builder().username(loginAccount).password(loginPassword).login();
        //測試獲取追蹤者
//        testForGetFollowers(client);

        //測試獲取指定用戶的所有文章
//        getPostsByUserName(client, "tomato_yuki_", null);
        //測試獲取指定用戶的指定文章_翻頁
//        getPostsByUserName(client, "tomato_yuki_", "3293773151733047571_63972138771");

        //測試獲取指定文章的資訊
//        getPostDetail(client, "3298906758704944557_59632865840", null);
        //測試獲取指定文章的翻頁留言
//        getPostDetail(client, "3298906758704944557_59632865840", "{\"server_cursor\": \"QVFDSUhiVVRYVGNfS2ZBRVJBNUtqRDBocjczNXdjb3BzZkpfenBiU2UySXRpalJja1pvMGx5R3VfU2lISlFiY3FRRmUzTHNFeVI3VEFaOHVZUXRGMlY3Yg==\", \"is_server_cursor_inverse\": true}");

        //測試獲取指定文章的liker

        List<Profile> firstCallLikers = getPostLiker(client, "3309754731106610154_63972138771", null);
        List<Profile> secondCallLikers = getPostLiker(client, "3309754731106610154_63972138771", "91");
// 从两次调用中获取的 Profile 对象列表中提取 pk
        List<Long> firstCallPks = firstCallLikers.stream().map(Profile::getPk).toList();
        List<Long> secondCallPks = secondCallLikers.stream().map(Profile::getPk).toList();
// 合并两个 pk 列表
        List<Long> combinedPks = new ArrayList<>(firstCallPks);
        combinedPks.addAll(secondCallPks);
// 使用 Set 去重
        Set<Long> uniquePks = new HashSet<>(combinedPks);
// 打印去重后的 pk 数量
        System.out.println("去重后的 Profile pk 数量：" + uniquePks.size());
    }


    //private

    /**
     * 獲取指定文章的liker
     *
     * @param client  已登录的 IGClient 对象
     * @param mediaId 要获取文章的Pk
     * @param maxId   用于分页的最大 ID
     */
    private List<Profile> getPostLiker(IGClient client, String mediaId, String maxId) {
        List<Profile> likers = Lists.newArrayList();
        AtomicReference<String> maxIdRef = new AtomicReference<>(maxId);
        // 計數器，用於追蹤請求到的資料數量
        int count = 0;

        try {
            while (true) {
                // 每次循环使用最新的maxId创建请求
                MediaGetLikersRequest request = new MediaGetLikersRequest(mediaId, maxIdRef.get());
                FeedUsersResponse response = client.sendRequest(request).join();
                List<Profile> commentList = response.getUsers();

                likers.addAll(commentList);
                count += commentList.size();
                System.out.println("目前累計數: " + count);

                // 更新maxId以供下一次请求使用
                String nextMaxId = response.getNext_min_id();
                System.out.println("nextMaxId = " + nextMaxId);
                if (nextMaxId == null || nextMaxId.isEmpty()) {
                    // 没有更多数据，结束循环
                    break;
                }
                maxIdRef.set(nextMaxId);

                // 每当累积到200个用户数据时，暂停一段时间
                if (count >= 200) {
                    System.out.println("達到100個用戶數據，跳出循環");
                    break;
                }
                //請求間暫停五秒
                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("暂停被中断");
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return likers;
    }


    /**
     * 獲取指定文章的資訊
     *
     * @param client  已登录的 IGClient 对象
     * @param mediaId 要获取文章的Pk
     * @param maxId   用于分页的最大 ID
     */
    private void getPostDetail(IGClient client, String mediaId, String maxId) {
        List<Comment> comments = Lists.newArrayList();
        AtomicReference<String> maxIdRef = new AtomicReference<>(maxId);
        // 計數器，用於追蹤請求到的資料數量
        int count = 0;

        try {
            while (true) {
                // 每次循环使用最新的maxId创建请求
                MediaGetCommentsRequest request = new MediaGetCommentsRequest(mediaId, maxIdRef.get());
                MediaGetCommentsResponse response = client.sendRequest(request).join();
                List<Comment> commentList = response.getComments();

                comments.addAll(commentList);
                count += commentList.size();
                System.out.println("目前累計數: " + count);

                // 更新maxId以供下一次请求使用
                String nextMaxId = response.getNext_min_id();
                System.out.println("nextMaxId = " + nextMaxId);
                if (nextMaxId == null || nextMaxId.isEmpty()) {
                    // 没有更多数据，结束循环
                    break;
                }
                maxIdRef.set(nextMaxId);

                // 每当累积到200个用户数据时，暂停一段时间
                if (count >= 50) {
                    System.out.println("達到50個用戶數據，跳出循環");
                    break;
                }
                //請求間暫停五秒
                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("暂停被中断");
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 獲取指定用戶的所有文章
     *
     * @param client   已登录的 IGClient 对象
     * @param username 要获取文章的用户名
     * @param maxId    用于分页的最大 ID
     */
    private void getPostsByUserName(IGClient client, String username, String maxId) {
        List<TimelineMedia> medias = Lists.newArrayList();
        AtomicReference<String> maxIdRef = new AtomicReference<>(maxId);
        // 計數器，用於追蹤請求到的資料數量
        int count = 0;

        try {
            //取得對象Pk
            UserAction userAction = client.actions().users().findByUsername(username).join();
            Long userId = userAction.getUser().getPk();

            while (true) {
                // 每次循环使用最新的maxId创建请求
                FeedUserRequest request = new FeedUserRequest(userId, maxIdRef.get());
                FeedUserResponse response = client.sendRequest(request).join();
                List<TimelineMedia> media = response.getItems();
                medias.addAll(media);
                count += media.size();
                System.out.println("目前累計文章數: " + count);

                // 更新maxId以供下一次请求使用
                String nextMaxId = response.getNext_min_id();
                System.out.println("nextMaxId = " + nextMaxId);
                if (nextMaxId == null || nextMaxId.isEmpty()) {
                    // 没有更多数据，结束循环
                    break;
                }
                maxIdRef.set(nextMaxId);

                // 每当累积到200个用户数据时，暂停一段时间
                if (count >= 50) {
                    System.out.println("達到50個用戶數據，跳出循環");
                    break;
                }
                //請求間暫停五秒
                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("暂停被中断");
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 測試獲取追蹤者
     *
     * @param client 已登录的 IGClient 对象
     */
    private void testForGetFollowers(IGClient client) {
        //获取追踪者
        List<Profile> followers = getFollowersByUserNameAndMaxId(client, "marianlinlin", null);
        //打印追踪者
        followers.forEach(profile -> System.out.println("follower = " + profile));
    }

    /**
     * 获取指定用户的所有追踪者。
     *
     * @param client   已登录的 IGClient 对象
     * @param username 要获取追踪者的用户名
     * @param maxId    用于分页的最大 ID
     * @return 一个包含所有追踪者 Profile 对象的列表
     */
    private static List<Profile> getFollowersByUserNameAndMaxId(IGClient client, String username, String maxId) {
        List<Profile> followers = Lists.newArrayList();
        AtomicReference<String> maxIdRef = new AtomicReference<>(maxId);
        // 計數器，用於追蹤請求到的資料數量
        int count = 0;

        try {
            UserAction userAction = client.actions().users().findByUsername(username).join();
            Long userId = userAction.getUser().getPk();

            while (true) {
                // 每次循环使用最新的maxId创建请求
                FriendshipsFeedsRequest request = new FriendshipsFeedsRequest(userId, FriendshipsFeedsRequest.FriendshipsFeeds.FOLLOWERS, maxIdRef.get());
                FeedUsersResponse response = client.sendRequest(request).join();

                List<Profile> users = response.getUsers();
                followers.addAll(users);
                count += users.size();
                System.out.println("当前累计用户数: " + count);

                // 更新maxId以供下一次请求使用
                String nextMaxId = response.getNext_max_id();
                if (nextMaxId == null || nextMaxId.isEmpty()) {
                    // 没有更多数据，结束循环
                    break;
                }
                maxIdRef.set(nextMaxId);

                // 每当累积到200个用户数据时，暂停一段时间
                if (count >= 150) {
                    System.out.println("达到150个用户数据，跳出循環");
                    break;
                }
                //請求間暫停五秒
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("暂停被中断");
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return followers;
    }
}
