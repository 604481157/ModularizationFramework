package com.zhangstar.app.modularization.application;

import com.zhangstar.app.common.manyprocess.application.BaseApplication;
import com.zhangstar.app.common.manyprocess.router.WideRouter;
import com.zhangstar.app.modularization.MainLocalRouterService;

import com.zhixin.com.process.ProcessApplicationLogic;
import com.zhixin.com.process.ProcessLocalRouterService;

/**
 * <pre>
 *     author : zhangstar
 *     e-mail : 604481157@qq.com
 *     time   : 2017/03/29
 *     desc   : 整个程序入口的Application
 * </pre>
 */

public class MyApplication extends BaseApplication {
    @Override
    protected void registerAllProcessService() {
        WideRouter.registerLocalRouter("com.zhixin.app.modularization", MainLocalRouterService.class);
        WideRouter.registerLocalRouter(ProcessLocalRouterService.PROCESS_NAME, ProcessLocalRouterService.class);
    }

    @Override
    protected void registerApplicationLogics() {
        registerApplicationLogic("com.zhixin.app.modularization", 100, MainApplicationLogic.class);
        registerApplicationLogic(ProcessLocalRouterService.PROCESS_NAME, 99, ProcessApplicationLogic.class);
    }

    @Override
    public boolean needMultipleProcess() {
        return true;
    }
}
