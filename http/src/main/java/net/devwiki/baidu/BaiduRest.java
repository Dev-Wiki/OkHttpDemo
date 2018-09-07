package net.devwiki.baidu;

import net.devwiki.client.ResponseFunction;
import net.devwiki.client.RestServiceProxy;
import io.reactivex.Observable;

/**
 * @author DevWiki
 */
public class BaiduRest extends RestServiceProxy {

    private BaiduApiService apiService;

    public BaiduRest() {
        apiService = httpClient.createRestService(BaiduApiService.class);
    }

    public Observable<Object> search(String key) {
        return apiService.search(key).map(new ResponseFunction<>());
    }

    public Observable<Object> searchChangeHost(String key) {
        return apiService.searchChangeHost(key).map(new ResponseFunction<>());
    }

    public Observable<Object> searchChangePath(String key) {
        return apiService.searchChangePath(key).map(new ResponseFunction<>());
    }

    public Observable<Object> searchChangeHostPath(String key) {
        return apiService.searchChangeHostPath(key).map(new ResponseFunction<>());
    }
}
