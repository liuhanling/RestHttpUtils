package com.liuhanling.rest.manage;

import com.liuhanling.rest.RestHttpUtils;
import com.liuhanling.rest.factory.ApiFactory;

import java.util.HashMap;
import java.util.Map;

public class RestUrlManager {

    private volatile static RestUrlManager instance;

    private Map<String, String> urlMap;

    public static String DEFAULT_URL_KEY = "default_url_key";

    public static RestUrlManager getInstance() {
        if (instance == null) {
            synchronized (RestUrlManager.class) {
                if (instance == null) {
                    instance = new RestUrlManager();
                }
            }
        }
        return instance;
    }

    private RestUrlManager() {
        urlMap = new HashMap<>();
    }

    /**
     * 一次性传入urlMap
     *
     * @param urlMap map
     * @return RestUrlManager
     */
    public RestUrlManager addUrl(Map<String, String> urlMap) {
        this.urlMap = urlMap;
        return this;
    }

    /**
     * 向map中添加url
     *
     * @param urlKey   key
     * @param urlValue value
     * @return RestUrlManager
     */
    public RestUrlManager addUrl(String urlKey, String urlValue) {
        urlMap.put(urlKey, urlValue);
        return this;
    }

    /**
     * 动态修改baseUrl信息（单个）
     *
     * @param urlValue url
     * @return RestUrlManager
     */
    public RestUrlManager setUrl(String urlValue) {
        urlMap.put(DEFAULT_URL_KEY, urlValue);
        return this;
    }

    /**
     * 动态修改baseUrl信息（多个）
     *
     * @param urlValue url
     * @return RestUrlManager
     */
    public RestUrlManager setUrl(String urlKey, String urlValue) {
        urlMap.put(urlKey, urlValue);
        return this;
    }

    /**
     * 获取全局唯一的baseUrl
     *
     * @return url
     */
    public String getUrl() {
        return getUrl(DEFAULT_URL_KEY);
    }

    /**
     * 根据key
     *
     * @param urlKey 获取对应的url
     * @return url
     */
    public String getUrl(String urlKey) {
        return urlMap.get(urlKey);
    }

    /**
     * 从map中删除某个url
     *
     * @param urlKey 需要删除的urlKey
     * @return RestUrlManager
     */
    public RestUrlManager clear(String urlKey) {
        urlMap.remove(urlKey);
        RestHttpUtils.removeCookieByUrl(urlKey);
        return this;
    }

    /**
     * 清空设置的url相关的所以信息
     *
     * @return RestUrlManager
     */
    public RestUrlManager clear() {
        urlMap.clear();
        ApiFactory.getInstance().clearAllApi();
        RestHttpUtils.removeAllCookie();
        return this;
    }
}
