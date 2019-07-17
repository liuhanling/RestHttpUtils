package com.liuhanling.rest.observer;

import com.liuhanling.rest.bean.BaseData;

public abstract class DataObserver<T> extends BaseObserver<BaseData<T>> {

    public boolean isSuccessful(int code) {
        return code == 0;
    }

    @Override
    public void onSuccess(BaseData<T> response) {
        if (isSuccessful(response.getCode())) {
            onResult(response.getData());
        } else {
            onFailure(response.getMsg());
        }
    }

    public abstract void onResult(T t);
}
