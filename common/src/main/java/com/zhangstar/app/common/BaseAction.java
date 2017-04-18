package com.zhangstar.app.common;

import android.content.Context;

/**
 * <pre>
 *     author : zhangstar
 *     e-mail : 604481157@qq.com
 *     time   : 2017/04/10
 *     desc   :
 * </pre>
 */

public abstract class BaseAction {
    public abstract ActionResult invoke(Context context, RouterRequest requestData);

    public abstract boolean isAsync();

}
