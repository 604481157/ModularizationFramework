package com.zhangstar.app.common.rx;

import org.reactivestreams.Publisher;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * <pre>
 *     author : zhangstar
 *     e-mail : 604481157@qq.com
 *     time   : 2017/04/27
 *     desc   :
 * </pre>
 */

public class TransformerUtil {
    public static FlowableTransformer customFlowableTransformer = null;

    public static ObservableTransformer customObservableTransformer = null;

    static FlowableTransformer sFlowableTransformer = new FlowableTransformer() {
        @Override
        public Publisher apply(Flowable upstream) {
            return upstream.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
        }
    };

    static ObservableTransformer sObservableTransformer = new ObservableTransformer() {
        @Override
        public ObservableSource apply(Observable upstream) {
            return upstream.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    ;
        }
    };

    public static <T> FlowableTransformer<T, T> flowableSchedulers() {
        return sFlowableTransformer;
    }

    public static <T> ObservableTransformer<T, T> observableSchedulers() {
        return sObservableTransformer;
    }

    public static <T> FlowableTransformer<T, T> customFlowableSchedulers() {
        if (customFlowableTransformer == null)
            new RuntimeException("自定义Transformer为null");
        return sFlowableTransformer;
    }

    public static <T> ObservableTransformer<T, T> customObservableSchedulers() {
        if (customObservableTransformer == null)
            new RuntimeException("自定义Transformer为null");
        return sObservableTransformer;
    }

}
