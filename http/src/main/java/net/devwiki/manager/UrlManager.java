package net.devwiki.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import okhttp3.HttpUrl;

/**
 * 网址管理类
 * @author DevWiki
 * @date 2018/8/17
 */
public class UrlManager {

    private Map<String, String> hostMap;
    private Map<String, String> restMap;

    public static UrlManager getInstance() {
        return InstanceHolder.INSTANCE;
    }

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

    private static class InstanceHolder {
        private static final UrlManager INSTANCE = new UrlManager();
    }

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

    public String getHost(String name) {
        if (!hostMap.containsKey(name)) {
            throw new NoSuchElementException("没有找到已经定义的Host名称:" + name + ",请先在" +
                    "net.devwiki.manager.UrlConstants.Host中定义!");
        }
        return hostMap.get(name);
    }

    public void setRest(String name, String value) {
        if (restMap.containsKey(name)) {
            restMap.put(name, value);
        } else {
            throw new NoSuchElementException("没有找到已经定义的Rest名称:" + name + ",请先在" +
                    "net.devwiki.manager.UrlConstants.Rest!");
        }
    }

    public String getRest(String name) {
        if (!restMap.containsKey(name)) {
            throw new NoSuchElementException("没有找到已经定义的Rest名称:" + name + ",请先在" +
                    "net.devwiki.manager.UrlConstants.Rest中定义!");
        }
        return restMap.get(name);
    }
}
