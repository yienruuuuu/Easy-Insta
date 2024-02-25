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
    public static OkHttpClient getBrightDataProxy() {
        return new OkHttpClient.Builder()
                .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("brd.superproxy.io", 22225)))
                .proxyAuthenticator((route, response) -> {
                    String credential = Credentials.basic("brd-customer-hl_f19ba91e-zone-datacenter_proxy_test", "1ugla2u5g249");
                    return response.request().newBuilder()
                            .header("Proxy-Authorization", credential)
                            .build();
                })
                .build();
    }


    public static void main(String[] args) throws Exception {
        System.out.println("To enable your free eval account and get "
                + "CUSTOMER, YOURZONE and YOURPASS, please contact "
                + "sales@brightdata.com");

        OkHttpClient client = new OkHttpClient.Builder()
                .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("brd.superproxy.io", 22225)))
                .proxyAuthenticator((route, response) -> {
                    String credential = Credentials.basic("brd-customer-hl_f19ba91e-zone-datacenter_proxy_test", "1ugla2u5g249");
                    return response.request().newBuilder()
                            .header("Proxy-Authorization", credential)
                            .build();
                })
                .build();

        Request request = new Request.Builder()
                .url("http://lumtest.com/myip.json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            System.out.println(response.body().string());
        }
    }
}
