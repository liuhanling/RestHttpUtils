package com.liuhanling.rest.exception;

import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializer;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;

import java.io.InterruptedIOException;
import java.io.NotSerializableException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.net.UnknownServiceException;
import java.text.ParseException;

import javax.net.ssl.SSLException;

import retrofit2.HttpException;

public class ApiException extends Exception {

    private int code;
    private String message;

    public ApiException(int code, Throwable cause) {
        super(cause);
        this.message = cause.getMessage();
    }

    public ApiException(int code, String message, Throwable cause) {
        super(cause);
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public static ApiException handleException(Throwable e) {
        ApiException ex;
        if (e instanceof HttpException) {
            HttpException cause = (HttpException) e;
            ex = new ApiException(cause.code(), cause.getMessage(), cause);
        } else if (e instanceof ConnectException) {
            ex = new ApiException(ERROR.TIMEOUT_ERROR, "网络连接异常！", e);
        } else if (e instanceof ConnectTimeoutException) {
            ex = new ApiException(ERROR.TIMEOUT_ERROR, "网络连接超时！", e);
        } else if (e instanceof SocketTimeoutException) {
            ex = new ApiException(ERROR.TIMEOUT_ERROR, "网络连接超时！", e);
        } else if (e instanceof InterruptedIOException) {
            ex = new ApiException(ERROR.TIMEOUT_ERROR, "网络连接超时！", e);
        } else if (e instanceof UnknownServiceException) {
            ex = new ApiException(ERROR.TIMEOUT_ERROR, "未知的服务器错误！", e);
        } else if (e instanceof UnknownHostException) {
            ex = new ApiException(ERROR.TIMEOUT_ERROR, "未知的主机名错误！", e);
        } else if (e instanceof SSLException) {
            ex = new ApiException(ERROR.SSL_ERROR, "SSL证书验证失败！", e);
        } else if (e instanceof ClassCastException) {
            ex = new ApiException(ERROR.CAST_ERROR, "类型转换错误！", e);
        } else if (e instanceof JsonParseException
                || e instanceof JSONException
                || e instanceof JsonSerializer
                || e instanceof NotSerializableException
                || e instanceof ParseException) {
            ex = new ApiException(ERROR.PARSE_ERROR, "解析错误！", e);
        } else if (e instanceof NullPointerException) {
            ex = new ApiException(ERROR.NULL_POINTER_ERROR, "空指针异常！", e);
        } else if (e instanceof IllegalStateException) {
            ex = new ApiException(ERROR.PARAM_ERROR, e);
        } else {
            ex = new ApiException(ERROR.UNKNOWN, e);
        }
        return ex;
    }

    /**
     * 约定异常
     */
    public static class ERROR {
        /**
         * 未知错误
         */
        public static final int UNKNOWN = 1000;
        /**
         * 连接超时
         */
        public static final int TIMEOUT_ERROR = 1001;
        /**
         * 空指针错误
         */
        public static final int NULL_POINTER_ERROR = 1002;

        /**
         * 证书出错
         */
        public static final int SSL_ERROR = 1003;

        /**
         * 类转换错误
         */
        public static final int CAST_ERROR = 1004;

        /**
         * 解析错误
         */
        public static final int PARSE_ERROR = 1005;

        /**
         * 参数异常
         */
        public static final int PARAM_ERROR = 1006;

    }
}
