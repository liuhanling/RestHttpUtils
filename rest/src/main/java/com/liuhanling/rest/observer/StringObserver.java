package com.liuhanling.rest.observer;

public abstract class StringObserver extends BaseObserver<String> {

    @Override
    public void onNext(String s) {
        onSuccess(s);
    }
}
