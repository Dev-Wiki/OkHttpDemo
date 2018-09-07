package net.devwiki.client;

import io.reactivex.functions.Function;
import retrofit2.HttpException;
import retrofit2.Response;

/**
 * 结果返回转换器
 * @author DevWiki
 */
public class ResponseFunction<T> implements Function<Response<T>, T> {

    @Override
    public T apply(Response<T> tResponse) {
        if (!tResponse.isSuccessful()) {
            throw new HttpException(tResponse);
        }
        return tResponse.body();
    }
}
