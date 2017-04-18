package com.zhangstar.app.modularization.application;

import com.zhangstar.app.modularization.MainProvider;
import com.zhangstar.app.common.manyprocess.application.BaseApplication;
import com.zhangstar.app.common.manyprocess.application.BaseApplicationLogic;
import com.zhangstar.app.common.manyprocess.router.LocalRouter;

/**
 * <pre>
 *     author : zhangstar
 *     e-mail : 604481157@qq.com
 *     time   : 2017/03/29
 *     desc   :
 * </pre>
 */

public class MainApplicationLogic extends BaseApplicationLogic {
    @Override
    public void onCreate() {
        super.onCreate();
        LocalRouter.getInstance(BaseApplication.getInstance()).registerProvider("main", new MainProvider());
    }
}
