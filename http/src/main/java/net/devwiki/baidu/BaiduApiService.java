package net.devwiki.baidu;

import net.devwiki.manager.UrlConstants;

import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

/**
 * @author DevWiki
 */
interface BaiduApiService {

    @GET("s")
    Observable<Response<Object>> search(@Query("wd")String wd);

    @GET("s")
    @Headers({UrlConstants.Header.REST_VERSION_V1})
    Observable<Response<Object>> searchChangePath(@Query("wd")String wd);

    @GET("s")
    @Headers({UrlConstants.Header.HOST_DEV})
    Observable<Response<Object>> searchChangeHost(@Query("wd")String wd);

    @Headers({UrlConstants.Header.HOST_PRIVATE, UrlConstants.Header.REST_VERSION_PRIVATE})
    @GET("s")
    Observable<Response<Object>> searchChangeHostPath(@Query("wd")String wd);
}
