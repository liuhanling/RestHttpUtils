package com.liuhanling.rest.manage;

import com.liuhanling.rest.RestHttpUtils;
import com.liuhanling.rest.factory.ApiFactory;
import com.liuhanling.rest.upload.UploadFileApi;

import java.util.HashMap;
import java.util.Map;

public class RxUrlManager {

    private volatile static RxUrlManager instance;

    private Map<String, String> urlMap;

    public static String DEFAULT_URL_KEY = "default_url_key";

    public static RxUrlManager getInstance() {
        if (instance == null) {
            synchronized (RxUrlManager.class) {
                if (instance == null) {
                    instance = new RxUrlManager();
                }
            }
        }
        return instance;
    }

    private RxUrlManager() {
        urlMap = new HashMap<>();
    }

    /**
     * 一次性传入urlMap
     *
     * @param urlMap map
     * @return RxUrlManager
     */
    public RxUrlManager addUrl(Map<String, String> urlMap) {
        this.urlMap = urlMap;
        return this;
    }

    /**
     * 向map中添加url
     *
     * @param urlKey   key
     * @param urlValue value
     * @return RxUrlManager
     */
    public RxUrlManager addUrl(String urlKey, String urlValue) {
        urlMap.put(urlKey, urlValue);
        return this;
    }

    /**
     * 针对单个baseUrl切换的时候清空旧baseUrl的所有信息
     *
     * @param urlValue url
     * @return RxUrlManager
     */
    public RxUrlManager setUrl(String urlValue) {
        urlMap.put(DEFAULT_URL_KEY, urlValue);
        ApiFactory.getInstance().clearApi(DEFAULT_URL_KEY);
        return this;
    }

    /**
     * 针对单个baseUrl切换的时候清空旧baseUrl的所有信息
     *
     * @param urlValue url
     * @return RxUrlManager
     */
    public RxUrlManager setUrl(String urlKey, String urlValue) {
        urlMap.put(urlKey, urlValue);
        ApiFactory.getInstance().clearApi(urlKey);
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
     * @return RxUrlManager
     */
    public RxUrlManager clear(String urlKey) {
        urlMap.remove(urlKey);
        ApiFactory.getInstance().clearApi(urlKey);
        return this;
    }

    /**
     * 清空设置的url相关的所以信息
     * 相当于重置url
     * 动态切换生产测试环境时候调用
     *
     * @return RxUrlManager
     */
    public RxUrlManager clear() {
        urlMap.clear();
        ApiFactory.getInstance().clearAllApi();
        RestHttpUtils.removeAllCookie();
        return this;
    }
}
