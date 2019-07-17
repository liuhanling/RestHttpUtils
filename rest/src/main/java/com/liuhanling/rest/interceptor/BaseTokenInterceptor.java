package com.liuhanling.rest.interceptor;

import android.text.TextUtils;

import com.liuhanling.rest.token.TokenStore;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpHeaders;
import okio.Buffer;
import okio.BufferedSource;

public abstract class BaseTokenInterceptor implements Interceptor {

    private static final Charset UTF8 = Charset.forName("utf-8");

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        // 判断tokenApi
        String path = request.url().encodedPath();
        if (path.contains(getTokenApi())) {
            return chain.proceed(request);
        }

        // 添加token
        String tokenKey = getTokenKey();
        String tokenVal = TokenStore.getToken();
        if (!TextUtils.isEmpty(tokenKey) && !TextUtils.isEmpty(tokenVal)) {
            request = request.newBuilder()
                    .header(tokenKey, tokenVal)
                    .build();
        }

        // 判断token失效
        Response response = chain.proceed(request);
        ResponseBody responseBody = response.body();
        if (HttpHeaders.hasBody(response) && !hasUnknownEncoding(response.headers())) {
            BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE);
            Buffer buffer = source.buffer();

            if (isPlaintext(buffer) && responseBody.contentLength() != 0) {
                Charset charset = UTF8;
                MediaType contentType = responseBody.contentType();
                if (contentType != null) {
                    charset = contentType.charset(UTF8);
                }
                String json = buffer.clone().readString(charset);

                if (isTokenInvalid(json)) {
                    refreshToken();
                    tokenVal = TokenStore.getToken();
                    if (!TextUtils.isEmpty(tokenVal)) {
                        Request newRequest = request.newBuilder()
                                .removeHeader(tokenKey)
                                .header(tokenKey, tokenVal)
                                .build();
                        return chain.proceed(newRequest);
                    }
                }
            }
        }

        return response;
    }

    private static boolean hasUnknownEncoding(Headers headers) {
        String contentEncoding = headers.get("Content-Encoding");
        return contentEncoding != null
                && !contentEncoding.equalsIgnoreCase("identity")
                && !contentEncoding.equalsIgnoreCase("gzip");
    }

    private static boolean isPlaintext(Buffer buffer) {
        try {
            Buffer prefix = new Buffer();
            long byteCount = buffer.size() < 64 ? buffer.size() : 64;
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 16; i++) {
                if (prefix.exhausted()) {
                    break;
                }
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }
            return true;
        } catch (EOFException e) {
            return false;
        }
    }

    protected abstract boolean isTokenInvalid(String response);

    protected abstract String getTokenApi();

    protected abstract String getTokenKey();

    protected abstract void refreshToken();
}
