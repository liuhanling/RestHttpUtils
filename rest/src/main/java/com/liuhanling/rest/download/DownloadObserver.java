package com.liuhanling.rest.download;

import android.annotation.SuppressLint;

import com.liuhanling.rest.manage.RxSchedulers;
import com.liuhanling.rest.observer.BaseObserver;

import java.io.File;
import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public abstract class DownloadObserver extends BaseObserver<ResponseBody> {

    private String fileName;
    private String fileDir;

    public DownloadObserver(String fileName) {
        this.fileName = fileName;
    }

    public DownloadObserver(String fileName, String fileDir) {
        this.fileName = fileName;
        this.fileDir = fileDir;
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
                            DownloadManager.saveFile(responseBody, fileName, fileDir, new ProgressListener() {
                                @Override
                                public void onDownloadProgress(long bytes, long total, int progress) {
                                    Observable
                                            .just(progress)
                                            .distinctUntilChanged()
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(p -> onProgress(bytes, total, progress));
                                }

                                @Override
                                public void onDownloadComplete(File file) {
                                    Observable
                                            .just(file)
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(DownloadObserver.this::onSuccess);
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                            onError(e);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Observable
                                .just(e)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(DownloadObserver.this::onError);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 成功回调
     *
     * @param bytes    已下载的大小
     * @param total    文件总的大小
     * @param progress 当前下载进度
     */
    public abstract void onProgress(long bytes, long total, float progress);

    public abstract void onSuccess(File file);
}
