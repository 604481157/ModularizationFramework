package com.zhangstar.app.common.base.presenter;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by zhangwenxing on 2016/10/28.
 */

public abstract class BasePresenterImpl<V> {
    public V view;
    private CompositeDisposable mCompositeDisposable;

    public void attach(V view) {
        this.view = view;
    }

    public void addDisposable(Disposable mSubscription) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = new CompositeDisposable();
        }
        mCompositeDisposable.add(mSubscription);
    }

    ;

    public void onDestroy() {
        if (mCompositeDisposable != null) {
            mCompositeDisposable.dispose();
            mCompositeDisposable = null;
        }
    }
}
