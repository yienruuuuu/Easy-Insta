package org.example.utils;

import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.net.InetSocketAddress;
import java.net.Proxy;

/**
 * @author Eric.Lee
 * Date: 2024/2/23
 */
public class BrightDataProxy {

    //取得BrightData代理
    public static OkHttpClient getBrightDataProxy(String account, String password, String sessionId) {
        String proxyUser = String.format("%s-country-%s-session-%s", account, "tw", sessionId);
        System.out.println("proxyUser: " + proxyUser);
        return new OkHttpClient.Builder()
                .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("brd.superproxy.io", 22225)))
                .proxyAuthenticator((route, response) -> {
                    String credential = Credentials.basic(proxyUser, password);
                    return response.request().newBuilder()
                            .header("Proxy-Authorization", credential)
                            .build();
                })
                .build();
    }


    //測試 帳蜜自填
    public static void main(String[] args) throws Exception {
        OkHttpClient client = getBrightDataProxy(
                "",
                "",
                StringUtils.generateRandomString(8));

        Request request = new Request.Builder()
                .url("http://lumtest.com/myip.json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            System.out.println(response.body().string());
        }
    }

}
