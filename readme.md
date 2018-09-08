## 动态改变Retrofit的 base url和 path

### 1. 需求与前提

#### base url

默认base url: `https://cloud.devwiki.net`
     
测试版 url : `https://dev.devwiki.net`
     
私有云版本url: `https://private.devwiki.net`


#### rest 版本

- `/rest/v1/`
- `/rest/v2/`
- `/rest/v3/`

#### 需求点

1. 大部分接口使用 cloud host, 部分接口使用 private host
2. 大部分接口使用 rest/v3 版本, 部分接口使用 v2, v1版本.
3. 每个host 都有可能存在 rest v1, v2, v3的接口


### 2. 实现思路

okhttp 可以添加拦截器, 可在发起访问前进行拦截, 通常我们会在 拦截器中统一添加 header, 比如:

```java
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
```

同理我们也可以在所有请求中添加统一的uuid 或者 key 进行防劫持或者认证. 比如:

```java
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
```

那么, 同样我们可以再拦截器中进行host 和 path的替换, 那么怎么替换呢?

### 3. 实现过程

#### 3.1 定义host 类型和 rest 版本

host类型:

```java
interface HostName {
    String CLOUD = "CLOUD";
    String PRIVATE = "PRIVATE";
    String DEV = "DEV";
}

interface HostValue {
    String CLOUD = "https://www.baidu.com";
    String PRIVATE = "https://private.bidu.com";
    String DEV = "https://dev.baidu.com";
}
```

rest 版本:

```java
interface RestVersionCode {
    String EMPTY = "EMPTY";
    String V1 = "V1";
    String V2 = "V2";
    String PRIVATE = "PRIVATE";
}

/**
 * path 前缀值
 */
interface RestVersionValue {
    String EMPTY = "";
    String V1 = "rest/v1";
    String V2 = "rest/v2";
    String PRIVATE = "rest/private";
}
```

设置一个默认的 host 和 rest 版本, 然后在需要更改host和rest 版本的请求接口处添header, 根据header设置来变更.

```java
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
```

header 的可选值:
```java
interface Header {
    String SPLIT_COLON = ":";
    String HOST = "HostName";
    String HOST_CLOUD = HOST + SPLIT_COLON + HostName.CLOUD;
    String HOST_PRIVATE = HOST + SPLIT_COLON + HostName.PRIVATE;
    String HOST_DEV = HOST + SPLIT_COLON + HostName.DEV;
    String REST_VERSION = "RestVersion";
    String REST_VERSION_V1 = REST_VERSION + SPLIT_COLON + RestVersionCode.V1;
    String REST_VERSION_V2 = REST_VERSION + SPLIT_COLON + RestVersionCode.V2;
    String REST_VERSION_PRIVATE = REST_VERSION + SPLIT_COLON + RestVersionCode.PRIVATE;
    String REST_VERSION_EMPTY = REST_VERSION + SPLIT_COLON + RestVersionCode.EMPTY;
}
```

然后是解析:

```java
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
```

为了能动态设置host, 我们需要一个map来存储host 类型和值.

```java
private Map<String, String> hostMap;
private Map<String, String> restMap;

private UrlManager() {
    hostMap = new HashMap<>(16);
    for (UrlConstants.Host host : UrlConstants.Host.values()) {
        hostMap.put(host.getName(), host.getValue());
    }
    restMap = new HashMap<>();
    for (UrlConstants.Rest rest : UrlConstants.Rest.values()) {
        restMap.put(rest.getVersion(), rest.getValue());
    }
}

//更新host 的值
public void setHost(String name, String value) {
    if (hostMap.containsKey(name)) {
        HttpUrl httpUrl = HttpUrl.parse(value);
        if (httpUrl == null) {
            throw new IllegalArgumentException("要存入的Host " + name + "对应的value:"
                    + value + "不合法!");
        }
        hostMap.put(name, value);
    } else {
        throw new NoSuchElementException("没有找到已经定义的Host名称:" + name + ",请先在" +
                "net.devwiki.manager.UrlConstants.Host中定义!");
    }
}

//根据host 获取值
public String getHost(String name) {
    if (!hostMap.containsKey(name)) {
        throw new NoSuchElementException("没有找到已经定义的Host名称:" + name + ",请先在" +
                "net.devwiki.manager.UrlConstants.Host中定义!");
    }
    return hostMap.get(name);
}
```

这样就可以动态替换host 和 rest版本了. 

### 4.测试运行

测试代码:

```java
private static void testRequest() {
    BaiduRest rest = new BaiduRest();

    testDefault(rest);

    testChangeHost(rest);

    testChangePath(rest);

    testChangeHostPath(rest);
}
```

测试运行结果:

```log
ostType:null
restVersion:null
newUrl:https://www.baidu.com/rest/v2/s?wd=123
九月 07, 2018 11:36:58 上午 okhttp3.internal.platform.Platform log
信息: --> GET https://www.baidu.com/rest/v2/s?wd=123 http/1.1
九月 07, 2018 11:36:58 上午 okhttp3.internal.platform.Platform log
信息: <-- 302 Found https://www.baidu.com/rest/v2/s?wd=123 (83ms, 154-byte body)
九月 07, 2018 11:36:58 上午 okhttp3.internal.platform.Platform log
信息: --> GET http://www.baidu.com/s?wd=123&tn=SE_PSStatistics_p1d9m0nf http/1.1
九月 07, 2018 11:36:58 上午 okhttp3.internal.platform.Platform log
信息: <-- 200 OK http://www.baidu.com/s?wd=123&tn=SE_PSStatistics_p1d9m0nf (46ms, unknown-length body)
hostType:DEV
restVersion:null
newUrl:https://dev.baidu.com/rest/v2/s?wd=123
九月 07, 2018 11:36:58 上午 okhttp3.internal.platform.Platform log
信息: --> GET https://dev.baidu.com/rest/v2/s?wd=123 http/1.1
九月 07, 2018 11:36:59 上午 okhttp3.internal.platform.Platform log
信息: <-- 302 Found https://dev.baidu.com/rest/v2/s?wd=123 (154ms, 154-byte body)
九月 07, 2018 11:36:59 上午 okhttp3.internal.platform.Platform log
信息: --> GET http://developer.baidu.com/error.html http/1.1
九月 07, 2018 11:36:59 上午 okhttp3.internal.platform.Platform log
信息: <-- 301 Moved Permanently http://developer.baidu.com/error.html (18ms, 73-byte body)
九月 07, 2018 11:36:59 上午 okhttp3.internal.platform.Platform log
信息: --> GET https://developer.baidu.com/error.html http/1.1
hostType:null
restVersion:V1
newUrl:https://www.baidu.com/rest/v1/s?wd=123
九月 07, 2018 11:36:59 上午 okhttp3.internal.platform.Platform log
信息: <-- 200 OK https://developer.baidu.com/error.html (157ms, unknown-length body)
九月 07, 2018 11:36:59 上午 okhttp3.internal.platform.Platform log
信息: --> GET https://www.baidu.com/rest/v1/s?wd=123 http/1.1
九月 07, 2018 11:36:59 上午 okhttp3.internal.platform.Platform log
信息: <-- 302 Found https://www.baidu.com/rest/v1/s?wd=123 (46ms, 154-byte body)
九月 07, 2018 11:36:59 上午 okhttp3.internal.platform.Platform log
信息: --> GET http://www.baidu.com/s?wd=123&tn=SE_PSStatistics_p1d9m0nf http/1.1
九月 07, 2018 11:36:59 上午 okhttp3.internal.platform.Platform log
信息: <-- 200 OK http://www.baidu.com/s?wd=123&tn=SE_PSStatistics_p1d9m0nf (54ms, unknown-length body)
hostType:PRIVATE
restVersion:PRIVATE
newUrl:https://private.bidu.com/rest/private/s?wd=123
```

结果按照设置进行了host 和 rest 的变更.

### 5. 项目代码

项目代码地址: [Dev-Wiki/OkHttpDemo](https://github.com/Dev-Wiki/OkHttpDemo)

更多文章,请移步访问我的博客: [DevWiki](http://blog.devwiki.net/)