package net.devwiki.client;

import net.devwiki.manager.UrlConstants;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 接口请求客户端
 * @author DevWiki
 * @date 2018/8/14
 */
public class RestClient {

    private int connectTimeout = 10 * 1000;
    private int writeTimeout = 10 * 1000;
    private int readTimeout = 10 * 1000;
    private String baseUrl = UrlConstants.HostValue.CLOUD;

    private List<Interceptor> interceptors;
    private Retrofit retrofit;

    public RestClient() {
        this(null);
    }

    public RestClient(List<Interceptor> interceptors) {
        this.interceptors = interceptors;
        OkHttpClient okHttpClient = createOkHttpClient();
        retrofit = createRetrofit(baseUrl, okHttpClient);
    }

    private OkHttpClient createOkHttpClient() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
                .writeTimeout(writeTimeout, TimeUnit.MILLISECONDS)
                .readTimeout(readTimeout, TimeUnit.MILLISECONDS)
                .addInterceptor(new HeaderInterceptor())
                .addInterceptor(new RequestInterceptor())
                .addNetworkInterceptor(loggingInterceptor);
        if (interceptors != null) {
            for (Interceptor interceptor : interceptors) {
                builder.addInterceptor(interceptor);
            }
        }
        return builder.build();
    }

    private Retrofit createRetrofit(String baseUrl, OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(baseUrl).client(okHttpClient)
                .build();
    }

    public <T> T createRestService(Class<T> service) {
        return retrofit.create(service);
    }
}
