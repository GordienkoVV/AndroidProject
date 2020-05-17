package com.tracking.observer.ui.utils

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers

fun <T> Observable<T>.threadsToUI(): Observable<T>
        = observeOn(AndroidSchedulers.mainThread())

fun <T> Observable<T>.threadsIOtoUI(): Observable<T>
        = subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

fun <T> Observable<T>.subscribeWithDisposable(): Disposable
        = subscribeWith(SimpleDisposableObserver())

fun <T> Observable<T>.subscribeDisposable()
        = subscribe(SimpleDisposableObserver())

class SimpleDisposableObserver<T> : DisposableObserver<T>() {
    override fun onComplete() {

    }

    override fun onNext(t: T) {

    }

    override fun onError(e: Throwable) {

    }

}
