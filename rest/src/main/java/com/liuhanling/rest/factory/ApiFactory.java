package com.liuhanling.rest.factory;

import com.liuhanling.rest.manage.RxUrlManager;
import com.liuhanling.rest.retrofit.RetrofitBuilder;

import java.util.HashMap;

import okhttp3.OkHttpClient;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;

public class ApiFactory {

    private volatile static ApiFactory instance;

    /**
     * 缓存ApiService
     */
    private static HashMap<String, Object> apiServiceCache;

    private CallAdapter.Factory[] callAdapterFactory;

    private Converter.Factory[] converterFactory;

    private OkHttpClient okHttpClient;

    public static ApiFactory getInstance() {
        if (instance == null) {
            synchronized (ApiFactory.class) {
                if (instance == null) {
                    instance = new ApiFactory();
                }
            }
        }
        return instance;
    }

    private ApiFactory() {
        apiServiceCache = new HashMap<>();
    }

    /**
     * 清空所有api缓存
     */
    public void clearAllApi() {
        apiServiceCache.clear();
    }

    /**
     * 清空所有api缓存
     */
    public void clearApi(String baseUrlKey) {
        apiServiceCache.remove(baseUrlKey);
    }

    public ApiFactory setCallAdapterFactory(CallAdapter.Factory... callAdapterFactory) {
        this.callAdapterFactory = callAdapterFactory;
        return this;
    }

    public ApiFactory setConverterFactory(Converter.Factory... converterFactory) {
        this.converterFactory = converterFactory;
        return this;
    }

    public ApiFactory setOkClient(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
        return this;
    }

    public ApiFactory setBaseUrl(String baseUrl) {
        RxUrlManager.getInstance().setUrl(baseUrl);
        return this;
    }

    public <E> E createApi(Class<E> apiClass) {
        String urlKey = RxUrlManager.DEFAULT_URL_KEY;
        String urlValue = RxUrlManager.getInstance().getUrl();
        return createApi(urlKey, urlValue, apiClass);
    }

    @SuppressWarnings("unchecked")
    public <E> E createApi(String baseUrlKey, String baseUrlValue, Class<E> apiClass) {
        E api = (E) apiServiceCache.get(baseUrlKey);
        if (api == null) {
            Retrofit retrofit = new RetrofitBuilder()
                    .setBaseUrl(baseUrlValue)
                    .setCallAdapterFactory(callAdapterFactory)
                    .setConverterFactory(converterFactory)
                    .setOkHttpClient(okHttpClient)
                    .build();
            api = retrofit.create(apiClass);
            apiServiceCache.put(baseUrlKey, api);
        }
        return api;
    }

}