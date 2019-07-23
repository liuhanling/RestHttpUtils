package com.liuhanling.rest.interceptor;

import android.text.TextUtils;

import com.liuhanling.rest.constants.RestConstants;
import com.liuhanling.rest.manage.RestUrlManager;

import java.io.IOException;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public abstract class BaseUrlInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        HttpUrl httpUrl = request.url();
        Request.Builder builder = request.newBuilder();

        List<String> urlHeaders = request.headers(getUrlKey());
        if (urlHeaders != null && urlHeaders.size() > 0) {
            builder.removeHeader(getUrlKey());

            String baseKey = urlHeaders.get(0);
            String baseUrl = RestUrlManager.getInstance().getUrl(baseKey);

            if (!TextUtils.isEmpty(baseUrl)) {
                HttpUrl baseURL = HttpUrl.parse(baseUrl);
                HttpUrl newHttpUrl = httpUrl.newBuilder()
                        .scheme(baseURL.scheme())
                        .host(baseURL.host())
                        .port(baseURL.port())
                        .build();

                Request newRequest = builder.url(newHttpUrl)
                        .build();
                return chain.proceed(newRequest);
            }
        }

        return chain.proceed(request);
    }

    public String getUrlKey() {
        return RestConstants.URL_KEY_NAME;
    }
}
