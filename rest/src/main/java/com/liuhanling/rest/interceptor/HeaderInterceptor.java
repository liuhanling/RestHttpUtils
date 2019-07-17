package com.liuhanling.rest.interceptor;

import java.io.IOException;
import java.util.Map;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public abstract class HeaderInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Headers headers = request.headers();
        Map<String, String> headerMap = buildHeaders();
        if (headers == null || headerMap == null || headerMap.isEmpty()) {
            return chain.proceed(request);
        } else {
            return chain.proceed(request.newBuilder()
                    .headers(buildHeaders(headers, headerMap))
                    .build());
        }
    }

    private Headers buildHeaders(Headers headers, Map<String, String> headerMap) {
        Headers.Builder builder = headers.newBuilder();
        for (Map.Entry<String, String> entry : headerMap.entrySet()) {
            builder.add(entry.getKey(), entry.getValue());
        }
        return builder.build();
    }

    public abstract Map<String, String> buildHeaders();
}
