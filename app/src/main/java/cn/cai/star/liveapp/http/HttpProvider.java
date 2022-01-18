package cn.cai.star.liveapp.http;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class HttpProvider {

    private static HttpProvider provider = null;
    private static OkHttpClient client = null;

    private HttpProvider() {
    }

    public static HttpProvider getInstance() {
        if (provider == null) {
            synchronized (HttpProvider.class) {
                if (provider == null) {
                    provider = new HttpProvider();
                    client = getClient();
                }
            }
        }
        return provider;
    }

    public OkHttpClient get() {
        return client;
    }

    private static OkHttpClient getClient() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
        return new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();
    }
}
