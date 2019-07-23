package com.liuhanling.rest.rx;

import com.liuhanling.rest.view.LoadingView;

import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

public class RxSchedulers {

    /**
     * 无参数
     *
     * @param <T> 泛型
     * @return 返回Observable
     */
    public static <T> ObservableTransformer<T, T> apply() {
        return apply(null);
    }

    /**
     * 带参数  显示loading对话框
     *
     * @param loadingView loading
     * @param <T>         泛型
     * @return 返回Observable
     */
    public static <T> ObservableTransformer<T, T> apply(final LoadingView loadingView) {
        return upstream -> upstream
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .doOnSubscribe(disposable -> {
                    if (loadingView != null) {
                        loadingView.showLoadingView();
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally((Action) () -> {
                    if (loadingView != null) {
                        loadingView.hideLoadingView();
                    }
                });
    }

}