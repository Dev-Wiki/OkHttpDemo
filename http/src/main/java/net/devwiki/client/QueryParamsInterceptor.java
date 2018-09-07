package net.devwiki.client;

import java.io.IOException;
import java.util.Map;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 添加通用的Http query params, 比如同一添加uuid防劫持
 * @author DevWiki
 */
public class QueryParamsInterceptor implements Interceptor {

    private Map<String, String> paramsMap;

    public QueryParamsInterceptor() {

    }

    public QueryParamsInterceptor(Map<String, String> paramsMap) {
        this.paramsMap = paramsMap;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originRequest = chain.request();
        if (paramsMap != null) {
            HttpUrl originUrl = originRequest.url();
            HttpUrl.Builder newBuilder = originUrl.newBuilder();
            for (String key : paramsMap.keySet()) {
                newBuilder.addEncodedQueryParameter(key, paramsMap.get(key));
            }
            HttpUrl newUrl = newBuilder.build();
            Request newRequest = originRequest.newBuilder().url(newUrl).build();
            return chain.proceed(newRequest);
        }
        return chain.proceed(originRequest);
    }
}
