package com.tracking.treking_gps.utils;

import com.tracking.treking_gps.utils.Logger;

public class SimpleObserver<T> extends io.reactivex.observers.DisposableObserver<T> {

    private String tag;


    public SimpleObserver() {
        this("SimpleObserver");
    }

    public SimpleObserver(String tag) {
        this.tag = tag;
    }


    @Override
    public void onNext(T o) {
        Logger.log(tag, "onNext: "+o);
    }

    @Override
    public void onError(Throwable e) {
        Logger.log(tag, "onError", e);
    }

    @Override
    public void onComplete() {
        Logger.log(tag, "onComplete");
    }
}
