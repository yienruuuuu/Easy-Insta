package org.example;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackages = {"org.example.*"})
@EnableTransactionManagement
@EnableScheduling
@EnableCaching
@EnableAspectJAutoProxy
@EnableAsync
@Slf4j
public class ApplicationTest implements CommandLineRunner {
    private final OpenAiChatClient chatClient;

    public ApplicationTest(OpenAiChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public static void main(String[] args) {
        SpringApplication.run(ApplicationTest.class, args);
    }

    @Override
    public void run(String... args) {
        log.info(chatClient.call("跟我說說有哪些鎖 怎麼實作 像是樂觀鎖 死鎖等等"));
    }
}
