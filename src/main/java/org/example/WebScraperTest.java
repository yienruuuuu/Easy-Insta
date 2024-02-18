package org.example;

import com.github.instagram4j.instagram4j.IGClient;
import com.github.instagram4j.instagram4j.actions.users.UserAction;
import com.github.instagram4j.instagram4j.models.user.Profile;
import com.github.instagram4j.instagram4j.requests.friendships.FriendshipsFeedsRequest;
import com.github.instagram4j.instagram4j.responses.feed.FeedUsersResponse;
import com.google.common.collect.Lists;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@SpringBootApplication
public class WebScraperTest implements CommandLineRunner {

    private String username = "bellebarker487";
    private String password = "haluho4157Hy";

    @Override
    public void run(String... args) throws Exception {
        //登入
        IGClient client = IGClient.builder().username(username).password(password).login();
        //获取追踪者
        List<Profile> followers = getFollowersByUserNameAndMaxId(client, "marianlinlin", null);
        //打印追踪者
        followers.forEach(profile -> System.out.println("follower = " + profile));
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
