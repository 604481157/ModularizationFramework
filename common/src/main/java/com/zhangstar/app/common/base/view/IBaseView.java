package com.zhangstar.app.common.base.view;

/**
 * Created by zhangwenxing on 2016/11/9.
 */

public interface IBaseView<T> {
    void onError();

    void onSuccess(T t);

    void dismissLoading();

    void showLoading();
}
