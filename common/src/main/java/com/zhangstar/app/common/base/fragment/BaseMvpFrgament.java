package com.zhangstar.app.common.base.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.zhangstar.app.common.base.presenter.BasePresenterImpl;


/**
 * Created by zhangwenxing on 2016/10/28.
 */

public abstract class BaseMvpFrgament<V, P extends BasePresenterImpl> extends BaseFragment {
    public P mPresenter;

    protected abstract P initPresenter();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = initPresenter();
        mPresenter.attach((V) this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.onDestroy();
        }
    }
}
