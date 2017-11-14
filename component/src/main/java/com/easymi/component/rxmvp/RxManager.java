package com.easymi.component.rxmvp;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by xyin on 2016/10/9.
 * 用于管理Rxjava相关代码的生命周期.
 */

public class RxManager {

    private CompositeDisposable compositeDisposable = new CompositeDisposable();    //同一管理subscribe后返回的Disposable

    public void add(Disposable disposable) {
        compositeDisposable.add(disposable);  //增加订阅源
    }

    public void clear() {
        compositeDisposable.clear(); //取消订阅
    }

}
