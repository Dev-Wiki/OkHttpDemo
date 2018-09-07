package net.devwiki.client;

import java.util.List;

import okhttp3.Interceptor;

/**
 * 网络请求代理
 * @author DevWiki
 * @date 2018/8/16
 */
public abstract class RestServiceProxy {

    protected RestClient httpClient;

    public RestServiceProxy() {
        this(null);
    }

    public RestServiceProxy(List<Interceptor> interceptors) {
        httpClient = new RestClient(interceptors);
    }
}
