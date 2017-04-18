package com.zhixin.com.process;

import com.zhangstar.app.common.manyprocess.application.BaseApplication;
import com.zhangstar.app.common.manyprocess.application.BaseApplicationLogic;
import com.zhangstar.app.common.manyprocess.router.LocalRouter;

/**
 * <pre>
 *     author : zhangstar
 *     e-mail : 604481157@qq.com
 *     time   : 2017/04/13
 *     desc   :
 * </pre>
 */

public class ProcessApplicationLogic extends BaseApplicationLogic {
    @Override
    public void onCreate() {
        super.onCreate();
        LocalRouter.getInstance(BaseApplication.getInstance()).registerProvider("process", new PorcessProvider());
    }
}
