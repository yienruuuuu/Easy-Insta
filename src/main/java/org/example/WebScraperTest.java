package org.example;

import com.github.instagram4j.instagram4j.IGClient;
import com.github.instagram4j.instagram4j.actions.users.UserAction;
import com.github.instagram4j.instagram4j.models.user.Profile;
import com.github.instagram4j.instagram4j.responses.feed.FeedUsersResponse;
import com.google.common.collect.Lists;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;
import java.util.concurrent.CompletionException;

@SpringBootApplication
public class WebScraperTest implements CommandLineRunner {

    //    @Value("${webdriver.chrome.path}")
//    private String chromeDriverPath;
    private String username = "ericlee09578";
    private String password = "s8903132";

    @Override
    public void run(String... args) throws Exception {
        //登入
        IGClient client = IGClient.builder().username(username).password(password).login();
        //获取追踪者
        List<Profile> followers = getAllFollowers(client, "marianlinlin");
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
     * @return 一个包含所有追踪者 Profile 对象的列表
     */
    public static List<Profile> getAllFollowers(IGClient client, String username) {
        try {
            // 通过用户名获取 UserAction，以便进行后续操作
            UserAction userAction = client.actions().users().findByUsername(username).join();

            // 初始化追踪者列表
            List<Profile> followers = Lists.newArrayList();
            String nextMaxId = null;

            do {
                // 获取追踪者分页响应
                FeedUsersResponse response = userAction.followersFeed().stream()
                        .findFirst()
                        .orElse(null);
                System.out.println("response = " + response);
                if (response != null) {
                    // 添加当前批次的追踪者到列表
                    followers.addAll(response.getUsers());
                    // 使用 response 中的 next_max_id 作为下一次请求的参数
                    nextMaxId = response.getNext_max_id();
                }
            } while (nextMaxId != null && !nextMaxId.isEmpty());

            return followers;
        } catch (CompletionException e) {
            e.printStackTrace();
            return Lists.newArrayList();
        }
    }

    public void backupLegacyCode() {
//        //打印用戶資訊
//        System.out.println("user.getUser() =" + user2.getUser());
//        //打印用戶的追蹤者
//        user2.followersFeed().stream().limit(2).forEach(response -> {
//            // 獲取用戶列表
//            List<Profile> users = response.getUsers();
//            // 獲取並打印用戶數量
//            int numberOfUsers = users.size();
//            System.out.println("Number of users in this response: " + numberOfUsers);
//            // 如果你想打印每個用戶的信息
//            users.forEach(user -> {
//                System.out.println("user = " + user);
//            });
//        });
//
//        FeedIterable<FriendshipsFeedsRequest, FeedUsersResponse> followersFeed = user2.followersFeed();
//
//        int totalFollowers = 0;
//        boolean keepFetching = true;
//        int retryDelay = 5000;
//
//        while (keepFetching) {
//            try {
//                for (FeedUsersResponse response : followersFeed) {
//                    totalFollowers += response.getUsers().size();
//                    System.out.println("Processed a page. Total followers so far: " + totalFollowers);
//
//                    // 暫停一定時間以避免速率限制
//                    Thread.sleep(15000);
//                }
//                // 如果一切順利，退出循環
//                keepFetching = false;
//            } catch (CompletionException e) {
//                if (e.getCause() instanceof IGResponseException) {
//                    IGResponseException igException = (IGResponseException) e.getCause();
//                    String message = igException.getMessage();
//                    if (message != null && message.contains("Please wait a few minutes before you try again.")) {
//                        // 这里是根据消息内容判断是否需要重新登录，你可以根据实际情况调整
//                        System.out.println("Received rate limit error. Trying to re-login...");
//                        client = IGClient.builder().username(username).password(password).login();
//                        user2 = client.actions().users().findByUsername("marianlinlin").join();
//                    }
//                }
//                System.err.println("Request failed: " + e.getMessage());
//                System.out.println("Retrying in " + retryDelay / 1000 + " seconds...");
//
//            } catch (InterruptedException e) {
//                // 恢復中斷狀態
//                Thread.currentThread().interrupt();
//                // 結束循環
//                keepFetching = false;
//            }
//        }
//
//        System.out.println("Total followers: " + totalFollowers);
    }
}
