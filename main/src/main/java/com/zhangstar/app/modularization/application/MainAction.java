package com.zhangstar.app.modularization.application;

import android.content.Context;

import com.zhangstar.app.common.ActionResult;
import com.zhangstar.app.common.BaseAction;
import com.zhangstar.app.common.RouterRequest;

import static com.zhangstar.app.common.ActionResult.ACTION_SUCCESSED;

/**
 * <pre>
 *     author : zhangstar
 *     e-mail : 604481157@qq.com
 *     time   : 2017/04/13
 *     desc   :
 * </pre>
 */

public class MainAction extends BaseAction {
    @Override
    public ActionResult invoke(Context context, RouterRequest requestData) {
        return new ActionResult(context, ACTION_SUCCESSED, "sucess");
    }

    @Override
    public boolean isAsync() {
        return false;
    }
}
