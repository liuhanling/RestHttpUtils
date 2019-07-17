package com.liuhanling.rest.download;

import com.liuhanling.rest.factory.ApiFactory;
import com.liuhanling.rest.manage.RxSchedulers;

import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;

public class DownloadHelper {

    public static final String DEFAULT_DOWNLOAD_KEY = "defaultDownloadUrlKey";
    public static final String DEFAULT_DOWNLOAD_URL = "https://api.github.com/";

    public static Observable<ResponseBody> downloadFile(String url) {

        return ApiFactory.getInstance()
                .setOkClient(new OkHttpClient.Builder().addInterceptor(new DownloadInterceptor()).build())
                .createApi(DEFAULT_DOWNLOAD_KEY, DEFAULT_DOWNLOAD_URL, DownloadApi.class)
                .downloadFile(url)
                .compose(RxSchedulers.apply());
    }

}
