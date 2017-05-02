package com.zhangstar.app.common.rx;

import android.content.Context;

import java.util.HashMap;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.processors.FlowableProcessor;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by zhangstar on 2016/12/9.
 */

public class RxBus {
    private static volatile RxBus mInstance;
    private final FlowableProcessor<Object> mSubject;
    private HashMap<String, CompositeDisposable> mDisposableMap;

    private RxBus() {
        mSubject = PublishProcessor.create().toSerialized();
    }

    public static RxBus getInstance() {
        if (mInstance == null) {
            synchronized (RxBus.class) {
                if (mInstance == null) {
                    mInstance = new RxBus();
                }
            }
        }
        return mInstance;
    }

    /**
     * 发送事件
     *
     * @param o
     */
    public void post(Object o) {
        mSubject.onNext(o);
    }

    /**
     * 返回指定类型的Observable实例
     *
     * @param type
     * @param <T>
     * @return
     */
    public <T> Flowable<T> tObservable(final Class<T> type) {


        return mSubject.ofType(type);
    }


    /**
     * 一个默认的订阅方法
     *
     * @param type
     * @param next
     * @param error
     * @param <T>
     * @return
     */
    public <T> Disposable doSubscribe(Class<T> type, Consumer<T> next, Consumer<Throwable> error) {
        return tObservable(type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(next, error);
    }

    /**
     * 保存订阅后的subscription
     *
     * @param context
     * @param disposable
     */
    public void addSubscription(Context context, Disposable disposable) {
        if (mDisposableMap == null) {
            mDisposableMap = new HashMap<>();
        }
        String key = context.getClass().getName();
        if (mDisposableMap.get(key) != null) {
            mDisposableMap.get(key).add(disposable);
        } else {
            CompositeDisposable compositeSubscription = new CompositeDisposable();
            compositeSubscription.add(disposable);
            mDisposableMap.put(key, compositeSubscription);
        }
    }

    /**
     * 取消订阅
     *
     * @param context
     */
    public void unSubscribe(Context context) {
        if (mDisposableMap == null) {
            return;
        }

        String key = context.getClass().getName();
        if (!mDisposableMap.containsKey(key)) {
            return;
        }
        if (mDisposableMap.get(key) != null) {
            mDisposableMap.get(key).dispose();
        }

        mDisposableMap.remove(key);
    }
}
