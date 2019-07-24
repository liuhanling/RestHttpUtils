package com.liuhanling.rest.interceptor;

import android.text.TextUtils;

import com.liuhanling.rest.consts.URLConstants;
import com.liuhanling.rest.manage.RestUrlManager;

import java.io.IOException;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class UrlInterceptor implements Interceptor {

    public static UrlInterceptor create() {
        return new UrlInterceptor();
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        HttpUrl httpUrl = request.url();
        Request.Builder builder = request.newBuilder();

        String baseUrl;
        List<String> urlHeaders = request.headers(URLConstants.BASE_URL_KEY);
        if (urlHeaders != null && urlHeaders.size() > 0) {
            builder.removeHeader(URLConstants.BASE_URL_KEY);
            baseUrl = RestUrlManager.getInstance().getUrl(urlHeaders.get(0));
        } else {
            baseUrl = RestUrlManager.getInstance().getUrl();
        }

        if (!TextUtils.isEmpty(baseUrl)) {
            HttpUrl baseURL = HttpUrl.parse(baseUrl);
            HttpUrl newHttpUrl = httpUrl.newBuilder()
                    .scheme(baseURL.scheme())
                    .host(baseURL.host())
                    .port(baseURL.port())
                    .build();
            request = builder.url(newHttpUrl).build();
        }

        return chain.proceed(request);
    }


}
