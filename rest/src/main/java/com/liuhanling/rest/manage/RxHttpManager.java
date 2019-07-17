package com.liuhanling.rest.manage;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class RxHttpManager implements IRxHttpManager<Object> {

    private static RxHttpManager instance = null;

    private HashMap<Object, CompositeDisposable> mMaps;

    public static RxHttpManager get() {
        if (instance == null) {
            synchronized (RxHttpManager.class) {
                if (instance == null) {
                    instance = new RxHttpManager();
                }
            }
        }
        return instance;
    }

    private RxHttpManager() {
        mMaps = new HashMap<>();
    }

    @Override
    public void add(Object tag, Disposable disposable) {
        if (null == tag) {
            return;
        }
        //tag下的一组或一个请求，用来处理一个页面的所以请求或者某个请求
        //设置一个相同的tag就行就可以取消当前页面所有请求或者某个请求了
        CompositeDisposable compositeDisposable = mMaps.get(tag);
        if (compositeDisposable == null) {
            CompositeDisposable compositeDisposableNew = new CompositeDisposable();
            compositeDisposableNew.add(disposable);
            mMaps.put(tag, compositeDisposableNew);
        } else {
            compositeDisposable.add(disposable);
        }
    }

    @Override
    public void remove(Object tag) {
        if (null == tag) {
            return;
        }
        if (!mMaps.isEmpty()) {
            mMaps.remove(tag);
        }
    }

    @Override
    public void cancel(Object tag) {
        if (null == tag) {
            return;
        }
        if (mMaps.isEmpty()) {
            return;
        }
        if (null == mMaps.get(tag)) {
            return;
        }
        if (!mMaps.get(tag).isDisposed()) {
            mMaps.get(tag).dispose();
            mMaps.remove(tag);
        }
    }

    @Override
    public void cancel(Object... tags) {
        if (null == tags) {
            return;
        }
        for (Object tag : tags) {
            cancel(tag);
        }
    }

    @Override
    public void cancelAll() {
        if (mMaps.isEmpty()) {
            return;
        }
        Iterator<Map.Entry<Object, CompositeDisposable>> it = mMaps.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Object, CompositeDisposable> entry = it.next();
            CompositeDisposable disposable = entry.getValue();
            //如果直接使用map的remove方法会报这个错误java.util.ConcurrentModificationException
            //所以要使用迭代器的方法remove
            if (null != disposable) {
                if (!disposable.isDisposed()) {
                    disposable.dispose();
                    it.remove();
                }
            }
        }
    }
}
