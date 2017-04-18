package com.zhixin.com.process;

import com.zhangstar.app.common.BaseProvider;

/**
 * <pre>
 *     author : zhangstar
 *     e-mail : 604481157@qq.com
 *     time   : 2017/04/13
 *     desc   :
 * </pre>
 */

public class PorcessProvider extends BaseProvider {
    @Override
    protected void registerAllActions() {
        regsiterAction("shutdown", new ShutDownAction());
        regsiterAction("process", new ProcessAction());
    }
}
