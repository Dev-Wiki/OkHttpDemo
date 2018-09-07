package net.devwiki.client;

import java.io.IOException;
import java.util.Locale;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author DevWiki
 * @date 2018/8/20
 * 添加通用的Http Header
 */
class HeaderInterceptor implements Interceptor {

    private static final String ENCODING_GZIP = "gzip";
    private static final String CONTENT_TYPE_JSON = "application/json;charset=UTF-8";
    private static final String HEADER_CONTENT_TYPE = "Content-Type";
    private static final String HEADER_ACCEPT_TYPE = "application/json";
    private static final String HEADER_CONTENT_ENCODING = "Content-Encoding";
    private final static String CHARSET = "UTF-8";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originRequest = chain.request();
        Request.Builder newBuilder = originRequest.newBuilder();
        newBuilder.addHeader("Accept", HEADER_ACCEPT_TYPE);
        newBuilder.addHeader("Accept-Charset", CHARSET);
        newBuilder.addHeader("Accept-Encoding", ENCODING_GZIP);
        newBuilder.addHeader("Accept-Language", Locale.getDefault().toString().replace("_", "-"));
        newBuilder.addHeader(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON);
        return chain.proceed(newBuilder.build());
    }
}
