package com.liuhanling.rest.factory;

import com.liuhanling.rest.manage.RestUrlManager;
import com.liuhanling.rest.retrofit.RetrofitBuilder;

import java.util.HashMap;

import okhttp3.OkHttpClient;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;

public class ApiFactory {

    private volatile static ApiFactory instance;

    private static HashMap<String, Object> apiServiceCache;

    private CallAdapter.Factory[] callAdapterFactory;

    private Converter.Factory[] converterFactory;

    private OkHttpClient okHttpClient;

    private String baseUrl;

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

    public void clearApi(String key) {
        apiServiceCache.remove(key);
    }

    public void clearAllApi() {
        apiServiceCache.clear();
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
        this.baseUrl = baseUrl;
        RestUrlManager.getInstance().setUrl(baseUrl);
        return this;
    }

    public ApiFactory setBaseUrl(String urlKey, String urlValue) {
        RestUrlManager.getInstance().addUrl(urlKey, urlValue);
        return this;
    }

    public ApiFactory setBaseUrl(HashMap<String, String> urlMap) {
        RestUrlManager.getInstance().addUrl(urlMap);
        return this;
    }

    /**
     * 默认APIService
     */
    public <E> E getApi(Class<E> apiClass) {
        String baseUrlKey = RestUrlManager.DEFAULT_URL_KEY;
        String baseUrlVal = RestUrlManager.getInstance().getUrl(baseUrlKey);
        return createApi(baseUrlKey, baseUrlVal, apiClass);
    }

    public <E> E createApi(Class<E> apiClass) {
        String baseUrlKey = apiClass.getSimpleName();
        String baseUrlVal = RestUrlManager.getInstance().getUrl(baseUrlKey);
        return createApi(baseUrlKey, baseUrlVal, apiClass);
    }

    public <E> E createApi(String baseKey, Class<E> apiClass) {
        String baseUrl = RestUrlManager.getInstance().getUrl(baseKey);
        return createApi(baseKey, baseUrl, apiClass);
    }

    @SuppressWarnings("unchecked")
    public <E> E createApi(String baseKey, String baseUrl, Class<E> apiClass) {
        E api = (E) apiServiceCache.get(baseKey);
        if (api == null) {
            Retrofit retrofit = new RetrofitBuilder()
                    .setBaseUrl(baseUrl)
                    .setCallAdapterFactory(callAdapterFactory)
                    .setConverterFactory(converterFactory)
                    .setOkHttpClient(okHttpClient)
                    .build();
            api = retrofit.create(apiClass);
            apiServiceCache.put(baseKey, api);
        }
        return api;
    }

}