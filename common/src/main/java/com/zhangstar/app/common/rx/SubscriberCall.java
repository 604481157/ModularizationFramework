package com.zhangstar.app.common.rx;

import com.zhangstar.app.common.base.view.IBaseView;

import io.reactivex.functions.Consumer;
import io.reactivex.subscribers.ResourceSubscriber;

/**
 * <pre>
 *     author : zhangstar
 *     e-mail : 604481157@qq.com
 *     time   : 2017/05/02
 *     desc   :
 * </pre>
 */

public class SubscriberCall<T, V extends IBaseView> extends ResourceSubscriber<T> {
    private SimpleSubscribe<T> simple;
    private Consumer<T> onNext;
    private Consumer<Throwable> onError;
    private V baseView;
    private boolean isSimple = false;

    public SubscriberCall(V baseView) {
        this.baseView = baseView;
    }

    public SubscriberCall(V baseView, SimpleSubscribe<T> simple) {
        this(baseView);
        this.simple = simple;
        isSimple = true;
    }


    @Override
    protected void onStart() {
        super.onStart();
        baseView.showLoading();
    }

    @Override
    public void onNext(T t) {
        if (isSimple)
            try {
                simple.onNext(t);
            } catch (Exception e) {
                e.printStackTrace();
            }
        else
            baseView.onSuccess(t);
    }

    @Override
    public void onError(Throwable t) {
        baseView.dismissLoading();
        if (isSimple)
            try {
                simple.onError(t);
            } catch (Exception e) {
                e.printStackTrace();
            }
        else {
            baseView.onError();
            ApiExcepition.resolveError(t);
        }
    }

    @Override
    public void onComplete() {
        baseView.dismissLoading();
    }


    interface SimpleSubscripbe<T> {
        void onNext(T data) throws Exception;

        void onError(Throwable throwable) throws Exception;

        void onComplete();

        void onStart();
    }

    public static class SimpleSubscribe<T> implements SimpleSubscripbe<T> {

        @Override
        public void onNext(T data) throws Exception {

        }

        @Override
        public void onError(Throwable throwable) throws Exception {
            ApiExcepition.resolveError(throwable);
        }

        @Override
        public void onComplete() {
        }

        @Override
        public void onStart() {
        }
    }
}
