package org.example;

import com.github.instagram4j.instagram4j.IGClient;
import com.github.instagram4j.instagram4j.actions.feed.FeedIterable;
import com.github.instagram4j.instagram4j.actions.users.UserAction;
import com.github.instagram4j.instagram4j.exceptions.IGResponseException;
import com.github.instagram4j.instagram4j.models.user.Profile;
import com.github.instagram4j.instagram4j.requests.friendships.FriendshipsFeedsRequest;
import com.github.instagram4j.instagram4j.responses.feed.FeedUsersResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;
import java.util.concurrent.CompletionException;

@SpringBootApplication
public class WebScraperTest implements CommandLineRunner {

    @Value("${webdriver.chrome.path}")
    private String chromeDriverPath;
    @Value("${insta.username}")
    private String username;
    @Value("${insta.password}")
    private String password;

    @Override
    public void run(String... args) throws Exception {
        //登入
        IGClient client = IGClient.builder().username(username).password(password).login();
        //搜尋
        UserAction user2 = client.actions().users().findByUsername("marianlinlin").join();
        //打印用戶資訊
        System.out.println("user.getUser() =" + user2.getUser());
        //打印用戶的追蹤者
        user2.followersFeed().stream().limit(1).forEach(response -> {
            // 獲取用戶列表
            List<Profile> users = response.getUsers();
            // 獲取並打印用戶數量
            int numberOfUsers = users.size();
            System.out.println("Number of users in this response: " + numberOfUsers);
            // 如果你想打印每個用戶的信息
            users.forEach(user -> {
                System.out.println("user = " + user);
            });
        });

        FeedIterable<FriendshipsFeedsRequest, FeedUsersResponse> followersFeed = user2.followersFeed();

        int totalFollowers = 0;
        boolean keepFetching = true;
        int retryDelay = 5000;

        while (keepFetching) {
            try {
                for (FeedUsersResponse response : followersFeed) {
                    totalFollowers += response.getUsers().size();
                    System.out.println("Processed a page. Total followers so far: " + totalFollowers);

                    // 暫停一定時間以避免速率限制
                    Thread.sleep(15000);
                }
                // 如果一切順利，退出循環
                keepFetching = false;
            } catch (CompletionException e) {
                if (e.getCause() instanceof IGResponseException) {
                    IGResponseException igException = (IGResponseException) e.getCause();
                    String message = igException.getMessage();
                    if (message != null && message.contains("Please wait a few minutes before you try again.")) {
                        // 这里是根据消息内容判断是否需要重新登录，你可以根据实际情况调整
                        System.out.println("Received rate limit error. Trying to re-login...");
                        client = IGClient.builder().username(username).password(password).login();
                        user2 = client.actions().users().findByUsername("marianlinlin").join();
                    }
                }
                System.err.println("Request failed: " + e.getMessage());
                System.out.println("Retrying in " + retryDelay / 1000 + " seconds...");

            } catch (InterruptedException e) {
                // 恢復中斷狀態
                Thread.currentThread().interrupt();
                // 結束循環
                keepFetching = false;
            }
        }

        System.out.println("Total followers: " + totalFollowers);
    }

    public static void main(String[] args) {
        SpringApplication.run(WebScraperTest.class, args);
    }
}
