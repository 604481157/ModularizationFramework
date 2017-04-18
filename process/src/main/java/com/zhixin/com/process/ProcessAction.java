package com.zhixin.com.process;

import android.content.Context;
import android.content.Intent;

import com.zhangstar.app.common.ActionResult;
import com.zhangstar.app.common.BaseAction;
import com.zhangstar.app.common.RouterRequest;

/**
 * <pre>
 *     author : zhangstar
 *     e-mail : 604481157@qq.com
 *     time   : 2017/04/13
 *     desc   :
 * </pre>
 */

public class ProcessAction extends BaseAction {
    @Override
    public ActionResult invoke(Context context, RouterRequest requestData) {
        Intent i = new Intent(context, ProcessActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
        return new ActionResult(context, ActionResult.ACTION_SUCCESSED, "Success");
    }

    @Override
    public boolean isAsync() {
        return false;
    }
}
