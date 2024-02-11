package org.example.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SuppressWarnings("unused")
@Configuration
public class SwaggerConfig {
    @Value("${system.version}")
    private String version;

    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("IG爬蟲專案文檔")
                        .description("是一個套用第三方API的爬蟲專案，可用於取得管理IG相關的資料，並依需求進行分析")
                        .version(version));
    }

    // Document Tab
    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("manager")
                .pathsToMatch("/admin/**", "/iguser/**")
                .build();
    }

}