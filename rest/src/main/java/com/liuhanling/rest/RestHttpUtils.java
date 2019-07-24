package com.liuhanling.rest;

import android.annotation.SuppressLint;
import android.content.Context;

import com.liuhanling.rest.config.OkHttpConfig;
import com.liuhanling.rest.cookie.CookieJarImpl;
import com.liuhanling.rest.cookie.store.CookieStore;
import com.liuhanling.rest.download.DownloadHelper;
import com.liuhanling.rest.factory.ApiFactory;
import com.liuhanling.rest.manage.RestTagManager;
import com.liuhanling.rest.manage.RestUrlManager;
import com.liuhanling.rest.token.TokenStore;
import com.liuhanling.rest.upload.UploadHelper;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.Cookie;
import okhttp3.HttpUrl;
import okhttp3.ResponseBody;


/**
 * Rest网络请求
 *
 * @author liuhanling
 * @date 2019-02-22 11:18
 */
public class RestHttpUtils {

    @SuppressLint("StaticFieldLeak")
    private static Context context;
    @SuppressLint("StaticFieldLeak")
    private static RestHttpUtils instance;

    public static RestHttpUtils getInstance() {
        if (instance == null) {
            synchronized (RestHttpUtils.class) {
                if (instance == null) {
                    instance = new RestHttpUtils();
                }
            }
        }
        return instance;
    }

    /**
     * Application中调用
     *
     * @param app Application
     */
    public RestHttpUtils init(Context app) {
        context = app.getApplicationContext();
        return this;
    }

    /**
     * 获取全局上下文
     */
    public static Context getContext() {
        checkInit();
        return context;
    }

    /**
     * 检测是否初始化
     */
    private static void checkInit() {
        if (context == null) {
            throw new ExceptionInInitializerError("请先在全局Application中调用 RestHttpUtils.getInstance().init(this) ！");
        }
    }


    public ApiFactory config() {
        checkInit();
        return ApiFactory.getInstance();
    }

    /**
     * 使用全局参数创建请求
     *
     * @param apiClass api
     * @param <T>      T
     * @return T
     */
    public static <T> T createApi(Class<T> apiClass) {
        return ApiFactory.getInstance().createApi(apiClass);
    }

    /**
     * 切换baseUrl
     *
     * @param baseKey  域名的key
     * @param apiClass api
     * @param <T>      T
     * @return T
     */
    public static <T> T createApi(String baseKey, Class<T> apiClass) {
        return ApiFactory.getInstance().createApi(baseKey, apiClass);
    }

    /**
     * 切换baseUrl
     *
     * @param baseUrlKey 域名的key
     * @param baseUrl    域名的url
     * @param api        class
     * @param <T>        T
     * @return T
     */
    public static <T> T createApi(String baseUrlKey, String baseUrl, Class<T> api) {
        return ApiFactory.getInstance().createApi(baseUrlKey, baseUrl, api);
    }


    /**
     * 下载文件
     *
     * @param fileUrl 地址
     * @return ResponseBody
     */
    public static Observable<ResponseBody> download(String fileUrl) {
        return DownloadHelper.downloadFile(fileUrl);
    }

    /**
     * 上传单张图片
     *
     * @param url      地址
     * @param filePath 文件路径
     * @return ResponseBody
     */
    public static Observable<ResponseBody> uploadFile(String url, String filePath) {
        return UploadHelper.uploadFile(url, filePath);
    }

    /**
     * 上传多张图片
     *
     * @param url       地址
     * @param filePaths 文件路径
     * @return ResponseBody
     */
    public static Observable<ResponseBody> uploadFiles(String url, List<String> filePaths) {
        return UploadHelper.uploadFiles(url, filePaths);
    }

    /**
     * 上传多张图片
     *
     * @param url       地址
     * @param paramsMap 参数
     * @param filePaths 文件路径
     * @return ResponseBody
     */
    public static Observable<ResponseBody> uploadFiles(String url, Map<String, Object> paramsMap, List<String> filePaths) {
        return UploadHelper.uploadFiles(url, paramsMap, filePaths);
    }

    /**
     * 动态修改baseUrl信息（单个）
     *
     * @param url url
     */
    public static void setBaseUrl(String url) {
        RestUrlManager.getInstance().setUrl(url);
    }

    /**
     * 动态修改baseUrl信息（多个）
     *
     * @param urlValue url
     */
    public static void setBaseUrl(String urlKey, String urlValue) {
        RestUrlManager.getInstance().setUrl(urlKey, urlValue);
    }

    /**
     * 获取默认token
     *
     * @return token
     */
    public static String getToken() {
        return TokenStore.getInstance().getToken();
    }

    /**
     * 根据key获取Token
     *
     * @param key key
     * @return token
     */
    public static String getToken(String key) {
        return TokenStore.getInstance().getToken(key);
    }

    /**
     * 设置默认token
     *
     * @param token token
     */
    public static void setToken(String token) {
        TokenStore.getInstance().setToken(token);
    }

    /**
     * 设置键值key-token
     *
     * @param key   key
     * @param token token
     */
    public static void setToken(String key, String token) {
        TokenStore.getInstance().setToken(key, token);
    }

    /**
     * 获取全局的CookieJarImpl实例
     */
    private static CookieJarImpl getCookieJar() {
        return (CookieJarImpl) OkHttpConfig.getInstance().getOkHttpClient().cookieJar();
    }

    /**
     * 获取全局的CookieStore实例
     */
    private static CookieStore getCookieStore() {
        return getCookieJar().getCookieStore();
    }

    /**
     * 获取所有cookie
     */
    public static List<Cookie> getAllCookie() {
        CookieStore cookieStore = getCookieStore();
        return cookieStore.getAllCookie();
    }

    /**
     * 获取某个url所对应的全部cookie
     */
    public static List<Cookie> getCookieByUrl(String url) {
        CookieStore cookieStore = getCookieStore();
        HttpUrl httpUrl = HttpUrl.parse(url);
        return cookieStore.getCookie(httpUrl);
    }


    /**
     * 移除全部cookie
     */
    public static void removeAllCookie() {
        CookieStore cookieStore = getCookieStore();
        cookieStore.removeAllCookie();
    }

    /**
     * 移除某个url下的全部cookie
     */
    public static void removeCookieByUrl(String url) {
        HttpUrl httpUrl = HttpUrl.parse(url);
        CookieStore cookieStore = getCookieStore();
        cookieStore.removeCookie(httpUrl);
    }

    /**
     * 取消所有请求
     */
    public static void cancelAll() {
        RestTagManager.get().cancelAll();
    }

    /**
     * 取消某个或某些请求
     */
    public static void cancel(Object... tag) {
        RestTagManager.get().cancel(tag);
    }
}
