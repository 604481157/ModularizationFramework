package com.zhangstar.app.modularization;

import com.zhangstar.app.modularization.application.MainAction;
import com.zhangstar.app.common.BaseProvider;

/**
 * <pre>
 *     author : zhangstar
 *     e-mail : 604481157@qq.com
 *     time   : 2017/04/13
 *     desc   :
 * </pre>
 */

public class MainProvider extends BaseProvider {
    @Override
    protected void registerAllActions() {
        regsiterAction("main", new MainAction());
    }
}
