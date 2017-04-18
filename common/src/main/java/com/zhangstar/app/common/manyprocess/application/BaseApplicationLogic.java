package com.zhangstar.app.common.manyprocess.application;

import android.content.res.Configuration;

/**
 * /**
 * <pre>
 *     author : zhangstar
 *     e-mail : 604481157@qq.com
 *     time   : 2017/03/28
 *     desc   : 首先，我们先把所有ApplicationLogic注册到Application中，
 *              然后，Application会根据注册时的进程名信息进行筛选，选择相同进程名的ApplicationLogic，保存到本进程中。
 *              然后，对这些本进程的ApplicationLogic进行实例化，最后，调用ApplicationLogic的onCreate方法，
 *              实现ApplicationLogic与Application生命周期同步
 *              每个module中都有一个此Logic类的实例，模拟每个module中的Application类
 * </pre>
 */

public class BaseApplicationLogic {
    protected BaseApplication mApplication;

    public BaseApplicationLogic() {
    }

    public void setApplication(BaseApplication mApplication) {
        this.mApplication = mApplication;
    }

    public void onCreate() {
    }

    public void onTerminate() {
    }

    public void onLowMemory() {
    }

    public void onTrimMemory(int level) {
    }

    public void onConfigurationChanged(Configuration newConfig) {
    }
}
