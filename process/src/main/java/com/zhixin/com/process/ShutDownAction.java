package com.zhixin.com.process;

import android.content.Context;

import com.zhangstar.app.common.ActionResult;
import com.zhangstar.app.common.BaseAction;
import com.zhangstar.app.common.RouterRequest;
import com.zhangstar.app.common.manyprocess.application.BaseApplication;
import com.zhangstar.app.common.manyprocess.router.LocalRouter;

/**
 * <pre>
 *     author : zhangstar
 *     e-mail : 604481157@qq.com
 *     time   : 2017/04/13
 *     desc   :
 * </pre>
 */

public class ShutDownAction extends BaseAction {
    @Override
    public ActionResult invoke(Context context, RouterRequest requestData) {
        LocalRouter.getInstance(BaseApplication.getInstance()).stopMyself(ProcessLocalRouterService.class);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.exit(0);
            }
        }).start();
        return new ActionResult(context, ActionResult.ACTION_SUCCESSED, "关闭此进程成功");
    }

    @Override
    public boolean isAsync() {
        return false;
    }
}
