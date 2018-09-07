package net.devwiki.client;

import java.io.IOException;

import net.devwiki.manager.UrlConstants;
import net.devwiki.manager.UrlManager;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 请求拦截器, 替换host和path等
 * @author DevWiki
 */
class RequestInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originRequest = chain.request();
        HttpUrl originUrl = originRequest.url();
        HttpUrl.Builder newBuilder;

        String hostType = originRequest.header(UrlConstants.Header.HOST);
        System.out.println("hostType:" + hostType);
        if (hostType != null && hostType.length() > 0) {
            String hostValue = UrlManager.getInstance().getHost(hostType);
            HttpUrl temp = HttpUrl.parse(hostValue);
            if (temp == null) {
                throw new IllegalArgumentException(hostType + "对应的host地址不合法:" + hostValue);
            }
            newBuilder = temp.newBuilder();
        } else {
            newBuilder = new HttpUrl.Builder()
                    .scheme(originUrl.scheme())
                    .host(originUrl.host())
                    .port(originUrl.port());
        }
        String restVersion = originRequest.header(UrlConstants.Header.REST_VERSION);
        System.out.println("restVersion:" + restVersion);
        if (restVersion == null) {
            restVersion = UrlConstants.RestVersionCode.V2;
        }
        String restValue = UrlManager.getInstance().getRest(restVersion);
        if (restValue.contains("/")) {
            String[] paths = restValue.split("/");
            for (String path : paths) {
                newBuilder.addEncodedPathSegment(path);
            }
        } else {
            newBuilder.addEncodedPathSegment(restValue);
        }
        for (int i = 0; i < originUrl.pathSegments().size(); i++) {
            newBuilder.addEncodedPathSegment(originUrl.encodedPathSegments().get(i));
        }

        newBuilder.encodedPassword(originUrl.encodedPassword())
                .encodedUsername(originUrl.encodedUsername())
                .encodedQuery(originUrl.encodedQuery())
                .encodedFragment(originUrl.encodedFragment());

        HttpUrl newUrl = newBuilder.build();
        System.out.println("newUrl:" + newUrl.toString());
        Request newRequest = originRequest.newBuilder().url(newUrl).build();
        return chain.proceed(newRequest);
    }
}
