package com.zhangstar.app.common.manyprocess.application;

import com.zhangstar.app.common.manyprocess.router.WideRouter;

/**
 * <pre>
 *     author : zhangstar
 *     e-mail : 604481157@qq.com
 *     time   : 2017/03/29
 *     desc   :
 * </pre>
 */

public class WideApplicationLogic extends BaseApplicationLogic {
    @Override
    public void onCreate() {
        super.onCreate();
        initWideRouter();
    }

    public void initWideRouter() {
        WideRouter.getInstance(mApplication);
        mApplication.registerAllProcessService();
    }
}
