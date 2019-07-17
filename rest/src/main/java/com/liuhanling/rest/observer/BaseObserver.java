package com.liuhanling.rest.observer;

import com.liuhanling.rest.exception.ApiException;
import com.liuhanling.rest.manage.RxHttpManager;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;


/**
 * BaseObserver
 *
 * @author liuhanling
 * @date 2019-02-22 11:18
 */
public abstract class BaseObserver<T> implements Observer<T> {

    protected String setTag(){
        return null;
    }

    protected boolean isHideToast() {
        return false;
    }

    @Override
    public void onSubscribe(@NonNull Disposable d) {
        RxHttpManager.get().add(setTag(), d);
    }

    @Override
    public void onNext(@NonNull T t) {
        onSuccess(t);
    }

    @Override
    public void onError(@NonNull Throwable e) {
        onFailure(ApiException.handleException(e));
    }

    @Override
    public void onComplete() {

    }

    public void onFailure(ApiException e){
        onFailure(e.getMessage());
    }

    public abstract void onSuccess(T response);

    public abstract void onFailure(String msg);

}
