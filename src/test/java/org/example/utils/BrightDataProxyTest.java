package org.example.utils;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.example.Application;
import org.example.bean.enumtype.ConfigEnum;
import org.example.config.ConfigCache;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static org.example.utils.BrightDataProxy.getBrightDataProxy;

/**
 * @author Eric.Lee
 * Date: 2024/2/29
 */
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BrightDataProxyTest {
    @Autowired
    ConfigCache configCache;

    @Test
    public void testMyServiceMethod() throws IOException {
        OkHttpClient client = getBrightDataProxy(
                configCache.get(ConfigEnum.BRIGHT_DATA_ACCOUNT.name()),
                configCache.get(ConfigEnum.BRIGHT_DATA_PASSWORD.name()),
                StringUtils.generateRandomString(8));

        Request request = new Request.Builder()
                .url("http://lumtest.com/myip.json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            System.out.println(response.body().string());
        }
    }

}