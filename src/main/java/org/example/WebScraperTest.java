package org.example;

import com.github.instagram4j.instagram4j.IGClient;
import com.github.instagram4j.instagram4j.actions.feed.FeedIterable;
import com.github.instagram4j.instagram4j.actions.users.UserAction;
import com.github.instagram4j.instagram4j.models.user.Profile;
import com.github.instagram4j.instagram4j.requests.friendships.FriendshipsFeedsRequest;
import com.github.instagram4j.instagram4j.responses.feed.FeedUsersResponse;
import com.google.common.collect.Lists;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

@SpringBootApplication
public class WebScraperTest implements CommandLineRunner {

    private String username = "ericlee09578";
    private String password = "s8903132";

    @Override
    public void run(String... args) throws Exception {
        //登入
        IGClient client = IGClient.builder().username(username).password(password).login();
        //获取追踪者
        List<Profile> followers = getFollowersByUserNameAndMaxId(client, "marianlinlin", null);
        //打印追踪者
        followers.forEach(profile -> {
            System.out.println("follower = " + profile.getUsername());
        });
    }

    public static void main(String[] args) {
        SpringApplication.run(WebScraperTest.class, args);
    }

    /**
     * 获取指定用户的所有追踪者。
     *
     * @param client   已登录的 IGClient 对象
     * @param username 要获取追踪者的用户名
     * @param maxId    用于分页的最大 ID
     * @return 一个包含所有追踪者 Profile 对象的列表
     */
    public static List<Profile> getFollowersByUserNameAndMaxId(IGClient client, String username, String maxId) {
        List<Profile> followers = Lists.newArrayList();
        AtomicReference<String> maxIdRef = new AtomicReference<>(maxId);

        try {
            UserAction userAction = client.actions().users().findByUsername(username).join();
            Long userId = userAction.getUser().getPk();

            Supplier<FriendshipsFeedsRequest> requestSupplier = () ->
                    new FriendshipsFeedsRequest(userId, FriendshipsFeedsRequest.FriendshipsFeeds.FOLLOWERS, maxIdRef.get());

            while (true) {
                FeedIterable<FriendshipsFeedsRequest, FeedUsersResponse> iterable = new FeedIterable<>(client, requestSupplier);
                boolean hasMore = false;

                for (FeedUsersResponse response : iterable) {
                    followers.addAll(response.getUsers());
                    maxIdRef.set(response.getNext_max_id());
                    System.out.println("nextMaxId = " + maxIdRef.get());
                    Thread.sleep(10000);

                    if (response.getNext_max_id() != null && !response.getNext_max_id().isEmpty()) {
                        hasMore = true;
                        break; // 跳出 for-each 循環，進行下一次 while 循環
                    }
                }
                if (!hasMore) {
                    break; // 如果沒有更多頁面，結束 while 循環
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return followers;
    }
}
