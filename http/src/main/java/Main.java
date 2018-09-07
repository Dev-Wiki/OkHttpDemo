import net.devwiki.baidu.BaiduRest;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DefaultObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * @author zhangyazhou
 * @date 2018/8/20
 */
class Main {

    public static void main(String[] args) {
        testRequest();
    }

    private static void testRequest() {
        BaiduRest rest = new BaiduRest();

        testDefault(rest);

        testChangeHost(rest);

        testChangePath(rest);

        testChangeHostPath(rest);
    }

    private static void testDefault(BaiduRest rest) {
        rest.search("123").subscribe(new DefaultObserver<Object>() {
            @Override
            public void onNext(Object o) {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private static void testChangeHost(BaiduRest rest) {
        rest.searchChangeHost("123").subscribe(new DefaultObserver<Object>() {
            @Override
            public void onNext(Object o) {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private static void testChangePath(BaiduRest rest) {
        rest.searchChangePath("123").subscribe(new DefaultObserver<Object>() {
            @Override
            public void onNext(Object o) {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private static void testChangeHostPath(BaiduRest rest) {
        rest.searchChangeHostPath("123").subscribe(new DefaultObserver<Object>() {
            @Override
            public void onNext(Object o) {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }
}
