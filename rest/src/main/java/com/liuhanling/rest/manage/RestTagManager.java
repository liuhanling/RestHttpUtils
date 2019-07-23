package com.liuhanling.rest.manage;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class RestTagManager {

    private static RestTagManager instance = null;

    private HashMap<Object, CompositeDisposable> maps;

    public static RestTagManager get() {
        if (instance == null) {
            synchronized (RestTagManager.class) {
                if (instance == null) {
                    instance = new RestTagManager();
                }
            }
        }
        return instance;
    }

    private RestTagManager() {
        maps = new HashMap<>();
    }

    public void add(Object tag, Disposable disposable) {
        if (null == tag) {
            return;
        }
        //tag下的一组或一个请求，用来处理一个页面的所以请求或者某个请求
        //设置一个相同的tag就行就可以取消当前页面所有请求或者某个请求了
        CompositeDisposable compositeDisposable = maps.get(tag);
        if (compositeDisposable == null) {
            CompositeDisposable compositeDisposableNew = new CompositeDisposable();
            compositeDisposableNew.add(disposable);
            maps.put(tag, compositeDisposableNew);
        } else {
            compositeDisposable.add(disposable);
        }
    }

    public void remove(Object tag) {
        if (null == tag) {
            return;
        }
        if (!maps.isEmpty()) {
            maps.remove(tag);
        }
    }

    public void cancel(Object tag) {
        if (null == tag) {
            return;
        }
        if (maps.isEmpty()) {
            return;
        }
        if (null == maps.get(tag)) {
            return;
        }
        if (!maps.get(tag).isDisposed()) {
            maps.get(tag).dispose();
            maps.remove(tag);
        }
    }

    public void cancel(Object... tags) {
        if (null == tags) {
            return;
        }
        for (Object tag : tags) {
            cancel(tag);
        }
    }

    public void cancelAll() {
        if (maps.isEmpty()) {
            return;
        }
        Iterator<Map.Entry<Object, CompositeDisposable>> it = maps.entrySet().iterator();
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
