package com.liuhanling.rest.download;

import android.annotation.SuppressLint;

import com.liuhanling.rest.observer.BaseObserver;

import java.io.File;
import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public abstract class DownloadObserver extends BaseObserver<ResponseBody> {

    private String fileName;
    private String destFileDir;

    public DownloadObserver(String fileName) {
        this.fileName = fileName;
    }

    public DownloadObserver(String fileName, String fileDir) {
        this.fileName = fileName;
        this.destFileDir = fileDir;
    }

    @SuppressLint("CheckResult")
    @Override
    public void onSuccess(ResponseBody responseBody) {
        Observable
                .just(responseBody)
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        try {
                            DownloadManager.saveFile(responseBody, fileName, destFileDir, (bytesRead, contentLength, progress, done, filePath) -> Observable
                                    .just(progress)
                                    .distinctUntilChanged()
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(integer -> {
                                        onProgress(bytesRead, contentLength, progress);
                                        if (done) {
                                            onSuccess(new File(filePath));
                                        }
                                    }));
                        } catch (IOException e) {
                            onError(e);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Observable
                                .just(e.getMessage())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(s -> DownloadObserver.this.onError(e));
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 成功回调
     *
     * @param bytesRead     已经下载文件的大小
     * @param contentLength 文件的大小
     * @param progress      当前进度
     */
    protected abstract void onProgress(long bytesRead, long contentLength, float progress);

    protected abstract void onSuccess(File file);
}
